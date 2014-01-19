package com.jingfm.tools;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.RecoverySystem.ProgressListener;
import android.util.Log;

public class HttpDownloader {

	private static HttpDownloader httpDownloader;

	public static HttpDownloader getInstance() {
		if (httpDownloader == null) {
			httpDownloader = new HttpDownloader();
		}
		return httpDownloader;
	}

	public static final int MAX_THREAD_NUM = 1;
	public static final int MAX_MUISC_THREAD_NUM = 1;
	public static final int MAX_LYRIC_THREAD_NUM = 10;
	public static final String RE_TRANSFER_FILE_NAME_EXTENSION = ".JingDownload";
	private ExecutorService executorMuiscService;
	private ExecutorService executorLyricsService;

	public HttpDownloader() {
		executorService = Executors.newFixedThreadPool(MAX_THREAD_NUM);
		executorMuiscService = Executors.newFixedThreadPool(MAX_MUISC_THREAD_NUM);
		executorLyricsService = Executors.newFixedThreadPool(MAX_LYRIC_THREAD_NUM);
	}

	// 线程池，用于管理多个下载线程
	private ExecutorService executorService;
	public boolean isAllDownloadNeedStop;

	public void setAllDownloadNeedStop(boolean b){
		isAllDownloadNeedStop = b;
	}
	
	public void addDownLoadTask(File file, URL url,
			ProgressListener progressListener) {
		executorService.submit(new DownLoadTask(file, url, progressListener));
	}
	
	public void addDownLoadMuiscTask(File file, URL url,
			ProgressListener progressListener,SizeObtainer sizeObtainer) {
		executorMuiscService.submit(new DownLoadTask(file, url, progressListener,sizeObtainer));
	}
	
	public void addDownLoadLyricsTask(File file, URL url,
			ProgressListener progressListener,SizeObtainer sizeObtainer) {
		Log.i("kid_debug","DownLoadLyricsTask: " + file);
		executorLyricsService.submit(new DownLoadTask(file, url, progressListener));
	}

	public long getFileDownloadedSize(File file) {
		if (file.exists()) {
			return file.length();
		}else{
			file = new File(file.toString() + RE_TRANSFER_FILE_NAME_EXTENSION);
			if (file.exists()) {
				return file.length();
			}else{
				return 0;
			}
		}
	}
	
	public void stop() {
		executorService.shutdownNow();
	}

	// 判断线程池是否停止
	public boolean isStop() {
		return !(executorService != null && !executorService.isShutdown());
	}

	class DownLoadTask implements Runnable {

		private File file;
		private URL url;
		private ProgressListener progressListener;
		private SizeObtainer sizeObtainer;

		public DownLoadTask(File file, URL url,
				ProgressListener progressListener) {
			this.file = file;
			this.url = url;
			this.progressListener = progressListener;
		}

		public DownLoadTask(File file2, URL url2,
				ProgressListener progressListener2, SizeObtainer sizeObtainer2) {
			this.file = file2;
			this.url = url2;
			this.progressListener = progressListener2;
			this.sizeObtainer = sizeObtainer2;
		}

		@Override
		public void run() {
			try {
				doDownFile(file, url, progressListener);
			} catch (Exception e) {
				e.printStackTrace();
				if (progressListener != null) {
					progressListener.onProgress(-1);
				}
			}
		}

		public void doDownFile(File file, URL url,
				ProgressListener progressListener) throws Exception {
			url = new URL(url.getProtocol(), url.getHost(),url.getPort(), url.getPath());
			if (file.exists() && file.length() > 0) {
				if (progressListener != null) {
					progressListener.onProgress(100);
				}
				if (sizeObtainer != null) {
					sizeObtainer.onSizeObtain(file.length());
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
				doDownFile(file, url,progressListener);
				return;
			}
			if (fileSize > 0 && sizeObtainer != null) {
				sizeObtainer.onSizeObtain(fileSize);
			}
			String as = httpConnection.getRequestMethod();
			Log.i("kid_debug","RequestMethod: " + as);
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
			while (!isAllDownloadNeedStop) {
				int result = (int) (downLoadedLength * 100 / fileSize);
				
				if (progressListener != null) {
					progressListener.onProgress(result);
				}
				
				if (result >= 100) {
					downLoadCompleted(file, downFile, progressListener);
					is.close();
					rf.close();
					return;
				}
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
	
	public interface SizeObtainer{
		public void onSizeObtain(long size);
	}
}
