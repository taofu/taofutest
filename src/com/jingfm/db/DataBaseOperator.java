package com.jingfm.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jingfm.api.model.MusicDTO;

public class DataBaseOperator {
	private  DatabaseHelper databaseHelper;
	private  Context context;
	private static DataBaseOperator instants;
	public void close(){
		databaseHelper.close();
	}
	
	private DataBaseOperator(Context context) {
		this.context = context;
		databaseHelper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		boolean exits = databaseHelper.isTableExits(db, DatabaseHelper.DOWNLOAD_TAB);
		if (!exits) {
			databaseHelper.onCreate(db);
		}
	}
	
	public static DataBaseOperator getInstants(Context context) {
		if(instants == null){
			return new DataBaseOperator(context);
		}else{
			return instants;
		}
	}
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	//查询
	public synchronized ArrayList<MusicDTO> qryOfflineMusicByUid(int uid){
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select " + "*" + " from "+DatabaseHelper.DOWNLOAD_TAB+" where " +DatabaseHelper.USER_ID+"= ?",
				new String[] {""+uid});
		ArrayList<MusicDTO> musicDTOList = new ArrayList<MusicDTO>();
		if (cursor.getCount()>0) {
			cursor.moveToFirst();
			do{
				MusicDTO musicDTO  = new MusicDTO();
				musicDTO.setTid(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TID)));
				musicDTO.setAbid(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ABID)));
				musicDTO.setAid(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.AID)));
				musicDTO.setAn(cursor.getString(cursor.getColumnIndex(DatabaseHelper.AN)));
				musicDTO.setAtn(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ATN)));
				musicDTO.setD(cursor.getString(cursor.getColumnIndex(DatabaseHelper.D)));
				musicDTO.setFid(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FID)));
				musicDTO.setFs(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FS)));
				musicDTO.setMid(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MID)));
				musicDTO.setN(cursor.getString(cursor.getColumnIndex(DatabaseHelper.N)));
				musicDTO.setY(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Y)));
				if (!musicDTOList.contains(musicDTO)) {
					musicDTOList.add(musicDTO);
				}
			}while(cursor.moveToNext());
		}
		cursor.close();// 游标关闭
		return musicDTOList;
	}
	
	
	 /** 
     * 更新数据库 
     * @param rowId 行标识 
     * @param title 内容 
     * @param body 内容 
     * @return 是否更新成功 
     */  
	//写入文件大小
    public boolean updateFS(MusicDTO dto) {  
    		if (dto.getFs() <= 0) {
				return false;
		}
    		SQLiteDatabase db = databaseHelper.getReadableDatabase();
    		ContentValues content = new ContentValues();
    		content.put(DatabaseHelper.FS, dto.getFs()); 
        //第一个参数:数据库表名,第二个参数更新的内容,第三个参数更新的条件,第四个参数条件带?号的替代者  
    		boolean rs = db.update(DatabaseHelper.DOWNLOAD_TAB, content, DatabaseHelper.TID + " = ? ", new String[]{""+dto.getTid()}) > 0;  
    		return rs;
    }  
	
	//添加下载歌曲
	public synchronized void addNewDownloadTask(MusicDTO musicDTO,int uid,int index) {
		long data = (new java.util.Date()).getTime();
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		if (isExist(musicDTO.getTid(), String.valueOf(uid), db)) {
			return;
		}
		musicDTO.setFs(0);
		db.execSQL(
				"insert into "+DatabaseHelper.DOWNLOAD_TAB+"("
					+DatabaseHelper.ABID+","
					+DatabaseHelper.AID+","
					+DatabaseHelper.AN+","
					+DatabaseHelper.ATN+","
					+DatabaseHelper.D+","
					+DatabaseHelper.DOWNLOAD_TAB_LOADING_INDEX+","
					+DatabaseHelper.FID+","
					+DatabaseHelper.FS+","
					+DatabaseHelper.MID+","
					+DatabaseHelper.N+","
					+DatabaseHelper.TID+","
					+DatabaseHelper.USER_ID+","
					+DatabaseHelper.Y+","
					+DatabaseHelper.DOWNLOAD_TIME+") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				new Object[] {
						musicDTO.getAbid()== null?Integer.valueOf(0):musicDTO.getAbid(),
						musicDTO.getAid() == null?Integer.valueOf(0):musicDTO.getAid(),
						musicDTO.getAn(),
						musicDTO.getAtn(),
						musicDTO.getD(),
						index,
						musicDTO.getFid(),
						musicDTO.getFs(),
						musicDTO.getMid(),
						musicDTO.getN(),
						musicDTO.getTid(),
						uid,
						musicDTO.getY(),
						data
		});
	}

	//删除缓存
	public boolean deleteFileDownload(Integer tid,String uid) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		database.delete(DatabaseHelper.DOWNLOAD_TAB, 
				DatabaseHelper.TID + " = ? and " 
						+ DatabaseHelper.USER_ID + " = ? ", 
				new String[] {""+tid,uid});
//		boolean rs = isExist(tid, uid, database);
		return false;
	}
	
	//删除个人全部数据
	public void deleteALLFileDownloadInDatabase(String uid) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		database.delete(DatabaseHelper.DOWNLOAD_TAB, DatabaseHelper.USER_ID + " = ? ", 
						new String[] {uid});
		return;
	}
	
	//查询
	public static boolean isExist(Integer tid,String uid,SQLiteDatabase database) {
		try {
			Cursor cursor = database.rawQuery(
					"select " + "*" + " from "+DatabaseHelper.DOWNLOAD_TAB+" where "+DatabaseHelper.TID+"= ?" + " AND " + DatabaseHelper.USER_ID + "= ?" ,
					new String[] {""+tid,uid});
			boolean rs = cursor.getCount() > 0;
			cursor.close();
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
