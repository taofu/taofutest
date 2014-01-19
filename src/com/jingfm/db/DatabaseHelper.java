package com.jingfm.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String DB_ARTCHINA = "jingfm.db";

	public final static String DOWNLOAD_TAB = "download_music_table";

	public final static String TID = "tid"; // 歌曲ID
	public final static String N = "n"; // 歌曲名称
	public final static String ATN = "atn"; // 艺人名字
	public final static String FS = "fs"; // 文件大小
	public final static String D = "d"; // 歌曲时长，单位：秒

	public final static String AN = "an"; // 专辑名
	public final static String MID = "mid"; // media fid Media
											// URL，返回对应的媒体文件mid地址
	public final static String FID = "fid"; // cover fid
											// 返回对应封面专辑图片的url地址。大小为：300x300
	public final static String AID = "aid"; // 作为艺人交叉排序时使用
	public final static String ABID = "abid"; // 专辑id
	public final static String Y = "y"; // 歌曲距离当前时间相差的年

	public final static String USER_ID = "user_id"; // 用户ID
	public final static String DOWNLOAD_TAB_LOADING_INDEX = "loading_index"; // 歌曲所在列表中的的Index

	public final static String DOWNLOAD_TIME = "download_time"; // 歌曲所在列表中的的Index

	private static DatabaseHelper instance;

	// Factory:代表记录集游标工厂，是专门用来生成记录集游标，记录集游标是对查询结果进行迭代的，后面我们会继续介绍。
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public DatabaseHelper(Context context) {
		super(context, DB_ARTCHINA, null, 1);
	}

	public static DatabaseHelper getHelper(Context context) {
		if (instance == null)
			instance = new DatabaseHelper(context);
		return instance;
	}

	public boolean isTableExits(SQLiteDatabase dbInfo, String tabName) {
		boolean result = false;
		if (tabName == null) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ tabName.trim() + "' ";
			cursor = dbInfo.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
	/**
	 * 用户第一次使用软件时调用，实现数据库的操作
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + DOWNLOAD_TAB + "(" + " " + TID
				+ " INTEGER," + " " + USER_ID + " INTEGER," + " " + N
				+ " varchar(100)," + " " + ATN + " varchar(100)," + " " + FS
				+ " INTEGER," + " " + D + " varchar(100)," + " " + AN
				+ " varchar(100)," + " " + FID + " varchar(100)," + " " + AID
				+ " INTEGER," + " " + MID + " varchar(100)," + " " + ABID
				+ " varchar(100)," + " " + Y + " varchar(100)," + " "
				+ DOWNLOAD_TAB_LOADING_INDEX + " INTEGER," + " "
				+ DOWNLOAD_TIME + " LONG) ");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists data_cache");
		db.execSQL("drop index if exists index_cache");
		db.execSQL("drop table if exists download_table");
		onCreate(db);
	}

	/**
	 * 根据版本号进行更新
	 * 
	 * @param db
	 * @param mNewVersion
	 */
	public void checkVersionCreate(SQLiteDatabase db, int mNewVersion) {
		int version = db.getVersion();
		if (version != mNewVersion) {
			db.beginTransaction();
			try {
				if (version == 0) {
					onCreate(db);
				} else {
					onUpgrade(db, version, mNewVersion);
				}
				db.setVersion(mNewVersion); // 设置为新的版本号
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}
	}

}