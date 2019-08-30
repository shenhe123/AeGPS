package com.aegps.location.utils.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vread on 2016/8/29.
 */
public class DownloadHelper extends SQLiteOpenHelper {
    private static final String SQL_NAME = "download.db";
    private static final int DOWNLOAD_VERSION = 1;

    public DownloadHelper(Context context) {
        super(context, SQL_NAME, null, DOWNLOAD_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table download_info(_id integer PRIMARY KEY AUTOINCREMENT, thread_id integer, "
                + "start_pos integer, end_pos integer, compelete_size integer,url char)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
