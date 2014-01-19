package com.jingfm.background_model;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RecoverySystem.ProgressListener;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.ConfigRequestApi;
import com.jingfm.api.model.ConfigDTO;

public class UpdateManager {
	public final static int UPDATE_NOTIFICATION_ID = 5556666;
	public static final String RE_TRANSFER_FILE_NAME_EXTENSION = ".JingDownload";
	public final static String UPDATE_INTENT_KEYWORD = "update_be_clicked";
	private static final String DOWNLOAD_TEXT = "Jing已下载更新 ";
	private MainActivity mContext;
	private Handler mHandler;
	protected boolean isNeedUpdate;
	private static UpdateManager instance;
	private Notification notification;
	private NotificationManager mNotificationManager;
	private UpdateAsyncTask mUpdateAsyncTask;
	protected boolean hasUpdateResult;
	
	private UpdateManager() {
	}

	public static void startCheckUpdate(MainActivity context) {
		if (instance == null) {
			instance = new UpdateManager();
		}
		instance.setContext(context);
		instance.doCheckUpdate();
	}

	public static void cancel() {
		if (instance == null) {
			return;
		}
		instance.cancelUpdate();
	}

	private void setContext(MainActivity context) {
		mContext = context;
		mHandler = new Handler();
	}

	private void doCheckUpdate() {
		if (hasUpdateResult || mContext == null) {
			return;
		}
		new Thread() {
			public void run() {
				sendCheckVersionRequest();
			};
		}.start();
	}

	protected synchronized void sendCheckVersionRequest() {
		if (hasUpdateResult) {
			return;
		}
		HashMap<Object, Object> params = new HashMap<Object, Object>();
		String versionName = "";
		int versionCode = 0;
		try {
			versionName = mContext.getPackageManager().getPackageInfo("com.jingfm", 0).versionName;
			versionCode = mContext.getPackageManager().getPackageInfo("com.jingfm", 0).versionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		params.put("cid", Constants.CHANNELID);
		params.put("vn", versionName);
		params.put("vc", versionCode);
		ResultResponse<ConfigDTO> rs = ConfigRequestApi.fetch(params);
		if (rs == null || !rs.isSuccess()) {
			return;
		}
		hasUpdateResult = true;
//		isNeedUpdate = !versionName.equals(rs.getResult().getVersion());
		isNeedUpdate = true;
		final String updateMessage = rs.getResult().getUpdate_message();
		final String updateUrl = rs.getResult().getApp_url();
		if (isNeedUpdate) {
			isNeedUpdate = false;
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
					alertDialog.setIcon(android.R.drawable.stat_sys_download);
					alertDialog.setTitle("升级");
					alertDialog.setMessage(updateMessage);
					alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "升级", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startUpdateAction(updateUrl);
						}
					});
					alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "下次再说", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					alertDialog.show();
				}
			});
		}
	}

	protected void startUpdateAction(String updateUrl) {
		mNotificationManager = (NotificationManager) mContext
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		notification = new Notification(
				R.drawable.ic_notification, "Jing正在下载更新...",
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_NO_CLEAR;
		// notification = new Notification();

		// 通过RemoteViews 设置notification中View 的属性
		notification.contentView = new RemoteViews(mContext
				.getApplication().getPackageName(),
				R.layout.notification_update);
		notification.contentView.setImageViewResource(
				R.id.update_image, R.drawable.ic_launcher);
		notification.contentView.setProgressBar(
				R.id.update_progress, 100, 0, false);
		notification.contentView.setTextViewText(
				R.id.update_text, DOWNLOAD_TEXT + 0 + "%");
		// 通过PendingIntetn
		// 设置要跳往的Activity，这里也可以设置发送一个服务或者广播，
		// 不过在这里的操作都必须是用户点击notification之后才触发的
		Intent intent = new Intent(mContext,
				MainActivity.class);
		intent.setAction(UPDATE_INTENT_KEYWORD);
		notification.contentIntent = PendingIntent
				.getActivity(
						mContext,
						MainActivity.REQUEST_CODE_CANCEL_UPDATE,
						intent, 0);
		mNotificationManager.notify(UPDATE_NOTIFICATION_ID,
				notification);
		mUpdateAsyncTask = new UpdateAsyncTask();
		mUpdateAsyncTask.execute(updateUrl);
	}

	public class UpdateAsyncTask extends AsyncTask<String, Integer, Boolean>
			implements OnCancelListener {

		private File file;
		private Integer mProgress = 0;

		public UpdateAsyncTask() {

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (mNotificationManager != null) {
				if(!mProgress.equals(values[0])){
					notification.contentView.setTextViewText(R.id.update_text,
							DOWNLOAD_TEXT + values[0] + "%");
					notification.contentView.setProgressBar(R.id.update_progress,
							100, values[0], false);
					mNotificationManager.notify(UPDATE_NOTIFICATION_ID,
							notification);
				}
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				file = new File(LocalCacheManager.getInstance().getBasePath() + "update" + url.getPath());
				doUpdateFile(file, url,new ProgressListener() {
					
					@Override
					public void onProgress(int progress) {
						if (mProgress != progress && progress%4 == 0) {
							publishProgress(progress);
						}
					}
				});
				return true;
			} catch (Exception e) {
				// TODO: handle exception
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result && !isCancelled()) {
				Intent intent = new Intent(Intent.ACTION_VIEW);  
		        intent.setDataAndType(Uri.fromFile(file),  
		                "application/vnd.android.package-archive"); 
		        mNotificationManager.cancel(UPDATE_NOTIFICATION_ID);
		        mContext.startActivity(intent);  
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			this.cancel(true);
		}
	}

	private void cancelUpdate() {
		if (mUpdateAsyncTask != null) {
			mUpdateAsyncTask.cancel(true);
		}
		if (mNotificationManager != null) {
			mNotificationManager.cancel(UPDATE_NOTIFICATION_ID);
		}
	}
	
	public void doUpdateFile(File file, URL url,
			ProgressListener progressListener) throws Exception {
		url = new URL(url.getProtocol(), url.getHost(),url.getPort(), url.getPath());
		if (file.exists()) {
			if (progressListener != null) {
				progressListener.onProgress(100);
			}
			return;
		}
		File downFile = new File(file.toString()
				+ RE_TRANSFER_FILE_NAME_EXTENSION);
		long downLoadedLength = 0;
		if (downFile.exists()) {
			downLoadedLength = downFile.length();
		}
		HttpURLConnection httpConnection = (HttpURLConnection) url
				.openConnection();
		if (downLoadedLength  > 0) {
			httpConnection.setRequestProperty("content-type", "*/*");
			httpConnection.setRequestProperty("User-Agent", "Android Jing");// 设置User-Agent
			httpConnection.setRequestProperty("RANGE", "bytes="
					+ downLoadedLength + "-");// 设置断点续传的开始位置
			httpConnection.setRequestMethod("GET"); // 设置请求方法为POST, 也可以为GET
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
		}
		long fileSize = httpConnection.getContentLength();
		if (httpConnection.getRequestMethod().equals("POST")) {
			if (downFile.exists()) {
				downFile.delete();
			}
			doUpdateFile(file, url,progressListener);
			return;
		}
		if (fileSize <= 0) {
			if (downFile.exists()) {
				downLoadCompleted(file, downFile, progressListener);
			}else{
				progressListener.onProgress(-1);
			}
			return;
		}else{
			if (!downFile.exists()) {
				File parentFile = downFile.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				downFile.createNewFile();
			}
		}
		fileSize	 += downLoadedLength;// 根据响应获取文件大小
		String a = httpConnection.getResponseMessage().toString();
		Log.i("kid_debug","httpConnection Content: " + a);
		InputStream is = httpConnection.getInputStream();
		if (is == null) {
			progressListener.onProgress(-1);
			return;
		}
		RandomAccessFile rf = new RandomAccessFile(downFile, "rw");
		if (fileSize <= 0) {
			progressListener.onProgress(-1);
			rf.close();
			is.close();
			return;
		}
		if (downLoadedLength == fileSize) {
			downLoadCompleted(file, downFile, progressListener);
			rf.close();
			is.close();
			return;
		} else if (downLoadedLength > fileSize) {
			downFile.delete();
			downLoadedLength = 0;
		}
		rf.seek(downLoadedLength);
		byte buf[] = new byte[1024 * 10];
		while (true) {
			SystemClock.sleep(100);
			int length = is.read(buf);
			if (length < 0) {
				is.close();
				rf.close();
				return;
			}else{
				downLoadedLength += length;
				if (downFile.exists()) {
					rf.write(buf, 0, length);
				}else{
					progressListener.onProgress(0);
					is.close();
					rf.close();
					return;
				}
				int result = (int) (downLoadedLength * 100 / fileSize);
				
				if (result >= 100) {
					downLoadCompleted(file, downFile, progressListener);
					progressListener.onProgress(result);
					is.close();
					rf.close();
					return;
				}
				if (progressListener != null) {
					progressListener.onProgress(result);
				}
			}
		}
	}

	private void downLoadCompleted(File file, File downFile,
			ProgressListener progressListener2) {
		if (file.exists()) {
			file.delete();
		}
		downFile.renameTo(file);
		if (progressListener2 != null) {
			progressListener2.onProgress(100);
		}
	}

}
