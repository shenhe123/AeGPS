package com.aegps.location.utils.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.aegps.location.bean.net.DownloadInfo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vread on 2016/8/29.
 */
public class DownloadHttpTool {
    private int threadCount;
    private String urlstr;
    private Context mContext;
    private Handler mHandler;
    private List<DownloadInfo> downloadInfos;
    private String localPath;
    private String fileName;
    private int fileSize;
    private DownlaodSqlTool sqlTool;

    private enum Download_State {
        Downloading, Pause, Ready, Delete
    }

    private Download_State state = Download_State.Ready;
    private int globalCompelete = 0;

    public DownloadHttpTool(int threadCount, String urlString,
                            String localPath, String fileName, Context context, Handler handler) {
        super();
        this.threadCount = threadCount;
        this.urlstr = urlString;
        this.localPath = localPath;
        this.mContext = context;
        this.mHandler = handler;
        this.fileName = fileName;
        sqlTool = new DownlaodSqlTool(mContext);
    }

    public void ready() {
        globalCompelete = 0;
        downloadInfos = sqlTool.getInfos(urlstr);
        if (downloadInfos.size() == 0) {
            initFirst();
        } else {
            File file = new File(localPath + "/" + fileName);
            if (!file.exists()) {
                sqlTool.delete(urlstr);
                initFirst();
            } else {
                fileSize = downloadInfos.get(downloadInfos.size() - 1)
                        .getEndPos();
                for (DownloadInfo info : downloadInfos) {
                    globalCompelete += info.getCompeleteSize();
                }
            }
        }
    }

    public void start() {
        if (downloadInfos != null) {
            if (state == Download_State.Downloading) {
                return;
            }
            state = Download_State.Downloading;
            for (DownloadInfo info : downloadInfos) {
                new DownloadThread(info.getThreadId(), info.getStartPos(),
                        info.getEndPos(), info.getCompeleteSize(),
                        info.getUrl()).start();
            }
        }
    }

    public void pause() {
        state = Download_State.Pause;
        sqlTool.closeDb();
    }

    public void delete() {
        state = Download_State.Delete;
        compelete();
        new File(localPath + File.separator + fileName).delete();
    }

    public void compelete() {
        sqlTool.delete(urlstr);
        sqlTool.closeDb();
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getCompeleteSize() {
        return globalCompelete;
    }


    private void initFirst() {
        try {
            URL url = new URL(urlstr);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            fileSize = connection.getContentLength();
            File fileParent = new File(localPath);
            if (!fileParent.exists()) {
                fileParent.mkdir();
            }
            File file = new File(fileParent, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
            accessFile.setLength(fileSize);
            accessFile.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int range = fileSize / threadCount;
        downloadInfos = new ArrayList<DownloadInfo>();
        for (int i = 0; i < threadCount - 1; i++) {
            DownloadInfo info = new DownloadInfo(i, i * range, (i + 1) * range
                    - 1, 0, urlstr);
            downloadInfos.add(info);
        }
        DownloadInfo info = new DownloadInfo(threadCount - 1, (threadCount - 1)
                * range, fileSize - 1, 0, urlstr);
        downloadInfos.add(info);
        sqlTool.insertInfos(downloadInfos);
    }

    private class DownloadThread extends Thread {

        private int threadId;
        private int startPos;
        private int endPos;
        private int compeleteSize;
        private String urlstr;
        private int totalThreadSize;

        public DownloadThread(int threadId, int startPos, int endPos,
                              int compeleteSize, String urlstr) {
            this.threadId = threadId;
            this.startPos = startPos;
            this.endPos = endPos;
            totalThreadSize = endPos - startPos + 1;
            this.urlstr = urlstr;
            this.compeleteSize = compeleteSize;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile randomAccessFile = null;
            InputStream is = null;
            try {
                randomAccessFile = new RandomAccessFile(localPath
                        + File.separator + fileName, "rwd");
                randomAccessFile.seek(startPos + compeleteSize);
                URL url = new URL(urlstr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Range", "bytes="
                        + (startPos + compeleteSize) + "-" + endPos);
                is = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int length = -1;
                while ((length = is.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, length);
                    compeleteSize += length;
                    Message message = Message.obtain();
                    message.what = threadId;
                    message.obj = urlstr;
                    message.arg1 = length;
                    mHandler.sendMessage(message);
                    if ((state != Download_State.Downloading)
                            || (compeleteSize >= totalThreadSize)) {
                        sqlTool.updataInfos(threadId, compeleteSize, urlstr);
                        break;
                    }
                }
                Message message = Message.obtain();
                message.arg1 = -1;
                mHandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.arg1 = -2;
                mHandler.sendMessage(message);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    randomAccessFile.close();
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
