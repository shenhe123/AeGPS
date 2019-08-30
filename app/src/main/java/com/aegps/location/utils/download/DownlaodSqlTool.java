package com.aegps.location.utils.download;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aegps.location.bean.net.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vread on 2016/8/29.
 */
public class DownlaodSqlTool {
    private DownloadHelper dbHelper;

    public DownlaodSqlTool(Context context) {
        dbHelper = new DownloadHelper(context);
    }

    public void insertInfos(List<DownloadInfo> infos) {
        SQLiteDatabase database = null;
        try{
            database = dbHelper.getWritableDatabase();
            for (DownloadInfo info : infos) {
                String sql = "insert into download_info(thread_id,start_pos, end_pos,compelete_size,url) values (?,?,?,?,?)";
                Object[] bindArgs = { info.getThreadId(), info.getStartPos(),
                        info.getEndPos(), info.getCompeleteSize(), info.getUrl() };
                database.execSQL(sql, bindArgs);
            }
        }catch (Exception e){

        }finally {
            try {
                if (database != null && database.inTransaction()) {
                    database.endTransaction();
                }
            } catch (Exception e) {
            }
        }
    }

    public List<DownloadInfo> getInfos(String urlstr) {
        List<DownloadInfo> list = new ArrayList<DownloadInfo>();
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try{
            database = dbHelper.getWritableDatabase();
            String sql = "select thread_id, start_pos, end_pos,compelete_size,url from download_info where url=?";
            cursor = database.rawQuery(sql, new String[] { urlstr });
            while (cursor.moveToNext()) {
                DownloadInfo info = new DownloadInfo(cursor.getInt(0),
                        cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
                        cursor.getString(4));
                list.add(info);
            }
        }catch (Exception e){

        }finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
                if (database != null && database.inTransaction()) {
                    database.endTransaction();
                }
            } catch (Exception e) {
            }
        }

        return list;
    }

    public void updataInfos(int threadId, int compeleteSize, String urlstr) {
        SQLiteDatabase database = null;
        try{
            database = dbHelper.getWritableDatabase();
            String sql = "update download_info set compelete_size=? where thread_id=? and url=?";
            Object[] bindArgs = { compeleteSize, threadId, urlstr };
            database.execSQL(sql, bindArgs);
        }catch (Exception e){}finally {
            try {
                if (database != null && database.inTransaction()) {
                    database.endTransaction();
                }
            }catch (Exception e){}
        }
    }

    public void closeDb() {
        dbHelper.close();
    }

    public void delete(String url) {
        SQLiteDatabase database = null;
        try{
            database = dbHelper.getWritableDatabase();
            database.delete("download_info", "url=?", new String[] { url });
        }catch (Exception e){}finally {
            try {
                if (database != null && database.inTransaction()) {
                    database.endTransaction();
                }
            }catch (Exception e){}
        }
    }
}
