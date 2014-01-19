package com.jingfm.background_model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RecoverySystem.ProgressListener;
import android.util.Log;

import com.jingfm.Constants.Constants;
import com.jingfm.api.SecureCustomerAudioRule;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.lyrics.LyricsInfo;
import com.jingfm.lyrics.LyricsMaker;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.HttpDownloader;
import com.jingfm.tools.HttpDownloader.SizeObtainer;
import com.jingfm.tools.JingTools;

public class LocalCacheManager {
	private static LocalCacheManager instance = new LocalCacheManager();

	public static LocalCacheManager getInstance() {
		return instance;
	}

	private boolean isReady;
	private String mBasePath;
	private String mImageCachePath = Constants.CACHE_PATH_UPDATE_IMAGE;
	private String mImageUpdateCachePath = Constants.CACHE_PATH_UPDATE_IMAGE;
	private String mFileCachePath = Constants.DOWNLOAD_PATH;
	private HttpDownloader mHttpDownloader;
	private int retryCount;

	public boolean isReady() {
		return isReady;
	}

	private LocalCacheManager() {
		mHttpDownloader = HttpDownloader.getInstance();
	}

	public void clearTempFiles() {
		if (!isReady) {
			return;
		}
		try {
			File newCacheFile = new File(mBasePath +Constants.CACHE_PATH_UPDATE_IMAGE + Constants.CACHE_PATH_TEMP_IMAGE);
			if (!newCacheFile.exists()) {
				return;
			}
			File bakCacheFile = new File(mBasePath +Constants.CACHE_PATH_UPDATE_IMAGE + Constants.CACHE_PATH_TEMP_IMAGE_BAK);
			if (bakCacheFile.exists()) {
				if (bakCacheFile.isDirectory()) {
					JingTools.delFolder(bakCacheFile.toString());
				}else{
					bakCacheFile.delete();
				}
				Log.i("kid_debug","file delete :  " + !bakCacheFile.exists());
			}
			newCacheFile.renameTo(bakCacheFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try {
//			File file = new File(mBasePath + Constants.CACHE_PATH_TEMP_IMAGE);
//			if (file.exists()) {
//				if (file.isDirectory()) {
//					JingTools.delFolder(file.toString());
//				}else{
//					file.delete();
//				}
//				Log.i("kid_debug","file delete :  " + !file.exists());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public void clearCacheFiles() {
		if (!isReady) {
			return;
		}
		try {
			File file = new File(mBasePath + Constants.DELETABLE_PATH_STATEABLE);
			if (file.exists() && file.isDirectory()) {
				file.delete();
			}
		} catch (Exception e) {
		}
	}

	public void requestImageUrl(String imageUrl, int type,
			ImageCallback callback) throws MalformedURLException {
		requestImageUrl(imageUrl, type, Constants.DELETABLE_PATH_STATEABLE,
				callback);
		// requestImageUrl("http://192.168.101.73/1.jpg",type,"",callback);
	}

	public void requestImageUrl(final String imageUrl, final int type,
			String cacheDir, final ImageCallback callback)
			throws MalformedURLException {
			URL url = new URL(imageUrl);
			String path = Constants.DOWNLOAD_PATH + url.getPath();
			// 先去下载文件夹查找是否存在
			File fileDownloaded = createImageCacheFile(imageUrlToFullFilePath(path));
			Bitmap rsBitmp;
			if (fileDownloaded.exists()) {
				callBackImageFile(imageUrl,fileDownloaded,callback,type);
				return;
			}
			// 然后去可删除文件夹查找文件
			path = cacheDir + url.getPath();
			final File file = createImageCacheFile(imageUrlToFullFilePath(path));
			if (file.exists()) {
				callBackImageFile(imageUrl,file,callback,type);
			} else {
				mHttpDownloader.addDownLoadTask(file, new URL(imageUrl),
						new ProgressListener() {
					@Override
					public void onProgress(int progress) {
						if (progress >= 100) {
							callBackImageFile(imageUrl,file,callback,type);
						}
					}
				});
			}
	}

	private void callBackImageFile(String imageUrl,File file, ImageCallback callback,int type) {
		if (file == null || !file.exists() || !file.canRead()) {
			return;
		}
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(
					file);
			Bitmap rsBitmap = AsyncImageLoader.makeImageByType(imageUrl,
					BitmapFactory.decodeStream(new FileInputStream(
							file)), type);
			callback.imageLoaded(rsBitmap, imageUrl);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	public void requestUpdateImageUrl(final String imageUrl, final int type,
			final ImageCallback callback) throws MalformedURLException {
		try {
			if (!JingTools.isValidString(imageUrl)) {
				return;
			}
			URL url = new URL(imageUrl);
			String[] s = imageUrl.split("\\?");
			String param = "d";
			try {
				param = s[1];
			} catch (Exception e) {
			}
			String path = url.getPath();
			path += "/" + param + JingTools.getFileExtension(path);
			final File file = createImageCacheFile(updateimageUrlToUpdateFullFilePath(path));
			if (file.exists()) {
				callBackImageFile(imageUrl,file,callback,type);
			} else {
				File parentfile = file.getParentFile();
				if (parentfile.exists()) {
					parentfile.delete();
				}
				mHttpDownloader.addDownLoadTask(file, new URL(imageUrl),
						new ProgressListener() {

							@Override
							public void onProgress(int progress) {
								if (progress >= 100) {
									callBackImageFile(imageUrl,file,callback,type);
								}
							}
						});
			}
		} catch (Exception e) {
			Log.e("kid_debug","imageUrl: " + imageUrl);
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
		}
	}

	public long getDownloadedFileSize(String qualitiy,String tid, String cacheDir) {
		if (!isReady) {
			return 0;
		}
		try {
			File file = new File(fileUrlToFullFilePath(qualitiy+tid+Constants.DOWNLOAD_MUSIC_EXTENSION));
			return mHttpDownloader.getFileDownloadedSize(file);
		} catch (Exception e) {
		}
		return 0;
	}

	public void downMusicAlbum(final String imageUrl) {
		URL url;
		try {
			url = new URL(imageUrl);
			String path = Constants.DOWNLOAD_PATH + url.getPath();
			// 先去下载文件夹查找是否存在
			File fileDownloaded = createImageCacheFile(imageUrlToFullFilePath(path));
			Bitmap rsBitmp;
			if (fileDownloaded.exists()) {
				return;
			} else {
				final File file = createImageCacheFile(imageUrlToFullFilePath(path));
				if (file.exists()) {
					CopySdcardFile(file.toString(), fileDownloaded.toString());
				} else {
					requestImageUrl(imageUrl,
							AsyncImageLoader.IMAGE_TYPE_ORIGINAL,
							Constants.DOWNLOAD_PATH, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int CopySdcardFile(String fromFile, String toFile) {
		InputStream fosfrom = null;
		OutputStream fosto = null;
		try {
			fosfrom = new FileInputStream(fromFile);
			fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;
		} catch (Exception ex) {
			return -1;
		}finally{
			try {
				if (fosfrom != null) {
					fosfrom.close();
				}
				if (fosto != null) {
					fosto.close();
				}
			} catch (Exception e) {
			}
		}
	}
	
	public void delDownloadedFile(String tid) {
		delDownloadedFile(tid,Constants.DOWNLOAD_DEFAULT_QUALITIY);
	}
	public void delDownloadedFile(String tid,String qualitiy) {
		File file = createFileCacheFile(muiscUrlToFullFilePath(qualitiy + tid + Constants.DOWNLOAD_MUSIC_EXTENSION));
		if (file.exists()) {
			file.delete();
		}else{
			file = new File(file + HttpDownloader.RE_TRANSFER_FILE_NAME_EXTENSION);
			if (file.exists()){
				file.delete();
			}
		}
	}

	public void downMusicFile(final MusicDTO musicDTO,ProgressListener listener,SizeObtainer sizeObtainer) {
		downMusicFile(musicDTO, Constants.DOWNLOAD_DEFAULT_QUALITIY,listener,sizeObtainer);
	}
	public void downMusicFile(final MusicDTO musicDTO,String qualitiy,ProgressListener listener,SizeObtainer sizeObtainer) {
		try {
			final File file = createFileCacheFile(muiscUrlToFullFilePath(qualitiy + musicDTO.getTid() + Constants.DOWNLOAD_MUSIC_EXTENSION));
			URL tmpUrl = new URL(SecureCustomerAudioRule.ID2URL(musicDTO.getMid()));
			if (file.exists()) {
				listener.onProgress(100);
				sizeObtainer.onSizeObtain(file.length());
			} else {
				mHttpDownloader.addDownLoadMuiscTask(file, tmpUrl, listener,sizeObtainer);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void downLyricsFile(final String mid,final LrcCallBacker lrcCallbacker) {
		if (!isReady) {
			retryCount++;
			if (retryCount > 3) {
				return;
			}
			Log.e("kid_debug","downLyricsFile !isReady");
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					downLyricsFile(mid,lrcCallbacker);
				}
			},2000);
			return;
		}
		try {
			final File file = createFileCacheFile(lyricsUrlToFullFilePath(mid + Constants.DOWNLOAD_LYRICS_EXTENSION));
			if (file.exists()) {
				lrcCallbacker.onCallBack(mid,LyricsMaker.MakeLyricInfoByLyricFile(file));
			} else {
				URL url = new URL(SecureCustomerAudioRule.ID2Lyrics(mid));
//				url = new URL("http://192.168.101.32/1.lrc");
				mHttpDownloader.addDownLoadLyricsTask(file, url, new ProgressListener() {
					
					@Override
					public void onProgress(int progress) {
						if (progress >= 100) {
							lrcCallbacker.onCallBack(mid,LyricsMaker.MakeLyricInfoByLyricFile(file));
						}
					}
				},null);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private File createImageCacheFile(String imageUrlToFullFilePath) {
		return new File(imageUrlToFullFilePath);
	}

	private File createFileCacheFile(String fileTidToFullFilePath) {
		return new File(fileTidToFullFilePath);
	}

	private String imageUrlToFullFilePath(String path) {
		return mBasePath + mImageCachePath + path;
	}
	private String updateimageUrlToUpdateFullFilePath(String path) {
		return mBasePath + mImageUpdateCachePath + path;
	}

	private String fileUrlToFullFilePath(String path) {
		return mBasePath + mFileCachePath + path;
	}
	private String muiscUrlToFullFilePath(String path) {
		return mBasePath + mFileCachePath + path;
	}
	private String lyricsUrlToFullFilePath(String path) {
		return mBasePath + Constants.DOWNLOAD_LYRICS_PATH +  path;
	}

	public boolean initPath(String basePath) {
		mBasePath = basePath;
		isReady = true;
		try {
			File baseFolder = new File(basePath);
			if (!baseFolder.exists()) {
				baseFolder.mkdirs();
			} else {
				if (!baseFolder.isDirectory()) {
					baseFolder.delete();
				} else {
					try {
						baseFolder.mkdirs();
					} catch (Exception e) {
					}
				}
			}
			File nomedia = new File(baseFolder, ".nomedia");
			nomedia.createNewFile();
			if (!baseFolder.canWrite()) {
				isReady = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			isReady = false;
		}
		return isReady;
	}

	public String getBasePath() {
		return mBasePath;
	}

	public File musicFileByTid(Integer tid) {
		if (isReady) {
			String path = fileUrlToFullFilePath(Constants.DOWNLOAD_HIGH_QUALITIY+tid+Constants.DOWNLOAD_MUSIC_EXTENSION);
			return new File(path);
		}
		return null;
	}
	
	public void saveCacheData(List src,String nameOfClass) throws IOException, ClassNotFoundException{
		if (!isReady) {
			return;
		}
		File file = new File(mBasePath+Constants.DATA_CACHE_PATH+nameOfClass);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
        out.writeObject(src);
        out.flush();
        out.close();
        fileOutputStream.close();
    }
	
	public List loadCacheData(List src,String nameOfClass) throws IOException, ClassNotFoundException{
		if (!isReady) {
			return src;
		}
		File file = new File(mBasePath+Constants.DATA_CACHE_PATH+nameOfClass);
		if (!file.exists() || !file.canRead()) {
			return src;
		}
		FileInputStream fileInputStream = new FileInputStream(file);
		ObjectInputStream in =new ObjectInputStream(fileInputStream);
		List dest = (List)in.readObject();
		in.close();
		fileInputStream.close();
		return dest;
	}

	public interface LrcCallBacker {
		public void onCallBack(String mid,LyricsInfo lyricsInfo);
	}

	public void getLocalLrcByMid(String mid,LrcCallBacker lrcCallbacker) {
		final File file = createFileCacheFile(lyricsUrlToFullFilePath(mid + Constants.DOWNLOAD_LYRICS_EXTENSION));
		if (file.exists()) {
			lrcCallbacker.onCallBack(mid,LyricsMaker.MakeLyricInfoByLyricFile(file));
		} 
	}
	
}
