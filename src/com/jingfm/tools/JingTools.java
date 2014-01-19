package com.jingfm.tools;

import java.io.File;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.UserPlayingDTO;

public class JingTools {

	private static int cShowWidth;
	private static int cShowHeight;
	private static float proportion;
	private static long UiThreadId;
	public static int screenHeight = 0;
	public static int screenWidth = 0;
	public static float screenDensity = 0;
	public static int windowHeight;
	public static int windowWidth;

	public static final int VIBRATE_DURATION = 35;

	public static void init(Activity context) {
		UiThreadId = Thread.currentThread().getId();
		if (screenDensity == 0 || screenWidth == 0 || screenHeight == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			Display display = context.getWindowManager().getDefaultDisplay();
			display.getMetrics(dm);
			JingTools.screenDensity = dm.density;
			JingTools.windowHeight = display.getHeight();
			JingTools.windowWidth = display.getWidth();
			JingTools.screenHeight = dm.heightPixels;
			JingTools.screenWidth = dm.widthPixels;
		}
	}
	
	public static Rect getRectOnScreen(View view) {
		Rect rect = new Rect();
		int[] location = new int[2];
		View parent = view;
		if (view.getParent() instanceof View) {
			parent = (View) view.getParent();
		}
		parent.getLocationInWindow(location);
		view.getHitRect(rect);
		rect.offset(location[0], location[1]);
		return rect;
	}
	
	public static boolean isValidString(Object str) {
		if (str == null) {
			return false;
		} else {
			return !("" + str).isEmpty();
		}
	}
	
	public static boolean isUiThread() {
		return UiThreadId == Thread.currentThread().getId();
	}

	public static boolean isEmailAddress(final Object value) {
		String namePattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(namePattern);
		Matcher m = p.matcher(value.toString());
		return m.find();
	}

	public static boolean judgePassword(TextView arg0) {
		int textLen = arg0.getText().toString().length();
		if (textLen < 6) {
			arg0.setError("密码必须6位以上");
			return false;
		}
		return true;
	}

	public static boolean judgeNickName(TextView arg0) {
		int textLen = arg0.getText().length();
		if (textLen > 14 || textLen < 2) {
			arg0.setError("昵称只能2-14个字符");
			return false;
		} else {
			String namePattern = "^[\u4E00-\u9FA5A-Za-z0-9_]+$";// "^[\u4E00-\u9FA5A-Za-z0-9]+$";
			Pattern p = Pattern.compile(namePattern);
			Matcher m = p.matcher(arg0.getText().toString());
			boolean rs = m.find();
			if (!rs) {
				arg0.setError("昵称不能含有特殊字符");
			}
			return rs;
		}
	}

	public static boolean judgeEmailAddress(TextView arg0) {
		if (!JingTools.isEmailAddress(arg0.getText())) {
			arg0.setError("邮箱格式错误");
			return false;
		}
		return true;
	}
	
	public static boolean judgeSignature(TextView arg0) {
		if (arg0.length() > 1000) {
			((TextView) arg0).setError("签名不能超过1000字");
			return false;
		}
		return false;
	} 

	public static int getShowHeight(Activity activity) {
		if (cShowHeight == 0) {
//			View view = activity.getWindow()
//					.findViewById(Window.ID_ANDROID_CONTENT);
//			cShowHeight = view.getBottom() - view.getTop();
			Display display = activity.getWindowManager().getDefaultDisplay();
			cShowWidth = display.getWidth();
			cShowHeight = display.getHeight();
		}
		return cShowHeight;
	}

	public static int getShowWidth(Activity activity) {
		if (cShowWidth == 0) {
//			View view = activity.getWindow()
//					.findViewById(Window.ID_ANDROID_CONTENT);
//			cShowWidth = view.getWidth();
			Display display = activity.getWindowManager().getDefaultDisplay();
			cShowWidth = display.getWidth();
			cShowHeight = display.getHeight();
		}
		return cShowWidth;
	}

	public static int getOldInfo(String startDay) throws ParseException {
		if (!isValidString(startDay)) {
			return 0;
		}
		int year = 0;
		String endDay = getCurrentDate("yyyy-MM-dd");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(df.parse(startDay));
		c2.setTime(df.parse(endDay));
		while (!c1.after(c2)) { // 循环对比，直到相等，n 就是所要的结果
			year++;
			c1.add(Calendar.YEAR, 1);
		}
		return year;
	}

	public static String formatSeconds(long seconds) {
		String rs = seconds + "秒";
		if (seconds > 0) {
			long s = seconds%60;
			rs = ""+s+"秒";
			seconds /= 60;
			if (seconds > 0) {
				long m = seconds%60;
				rs = ""+ m + "分" + rs;
				seconds /= 60;
				if (seconds > 0) {
//					long h = seconds%24;
//					seconds /= 24;
					long h = seconds;
					rs = ""+ h + "小时" + rs;
//					if (seconds > 0) {
//						rs = ""+ seconds + "天" + rs;
//					}
				}
			}
		}
		return rs;
	}
	
	public static String formatSecondsShort(long seconds) {
		String rs = seconds + "秒";
		if (seconds > 0) {
			long s = Math.abs(seconds%60);
			rs = ""+s+"秒";
			seconds /= 60;
			if (seconds > 0) {
				long m = Math.abs(seconds%60);
				rs = ""+ m + "分";
				seconds /= 60;
				if (seconds > 0) {
					long h = Math.abs(seconds%24);
					rs = ""+ h + "小时";
					seconds /= 24;
					if (seconds > 0) {
						rs = ""+ seconds + "天";
					}
				}
			}
		}
		return rs;
	}
	
	public static String getCurrentDate(String format) {
		Calendar day = Calendar.getInstance();
		day.add(Calendar.DATE, 0);
		SimpleDateFormat sdf = new SimpleDateFormat(format);// "yyyy-MM-dd"
		String date = sdf.format(day.getTime());
		return date;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static int px2sp(Context context,float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static float sp2px(Context context, float spValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return spValue * scale + 0.5f;
	}

	public static String getFileExtension(String path) {
		int index = path.lastIndexOf(".");
		if (index < 0 || index >= path.length()) {
			return "";
		} else {
			return path.substring(index);
		}
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
			}
		}
	}

	public static String getDateString(long currentTimeMillis) {
		String dateTime = MessageFormat.format("{0,date,yyyy-MM-dd HH:mm:ss}",
				new Object[] { new java.sql.Date(currentTimeMillis) });
		return dateTime;
	}

	public static void listReverse(List mRankList) {
		if (mRankList == null) {
			return;
		}
		for (int i = 0; i < mRankList.size() / 2; i++) {
			Object tmp = mRankList.get(i);
			mRankList.set(i, mRankList.get(mRankList.size() - 1 - i));
			mRankList.set(mRankList.size() - 1 - i, tmp);
		}
	}

	public static float getDeviceAspectRatio(Activity mContext) {
		if (proportion == 0) {
			Display display = mContext.getWindowManager().getDefaultDisplay();
			proportion = (float) display.getHeight()
					/ (float) display.getWidth();
		}
		return proportion;
	}

	public static NetworkInfo getActiveNetwork(Context context){
		if (context == null)
			return null;
		ConnectivityManager mConnMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mConnMgr == null)
			return null;
		NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo(); // 获取活动网络连接信息
		return aActiveInfo;
	}
	
	public static boolean checkListEqual(List listA,
			List listB) {
		if (listA == null || listB == null ||listA.size() != listB.size()) {
			return false;
		}
		for (int i = 0; i < listA.size(); i++) {
			if (!listB.contains(listA.get(i))) {
				return false;
			}
		}
		return true;
	}

	public static String trimSign(String keyWord) {
		try {
			while (keyWord.endsWith("+")) {
				keyWord = keyWord.substring(0,keyWord.length()-1);
			}
			while (keyWord.startsWith("+")) {
				keyWord = keyWord.substring(1,keyWord.length());
			}
		} catch (Exception e) {
		}
		return keyWord;
	}
	
	 /** 
     * get the field value in aObject by aFieldName 
     *  
     * @param aObject 
     * @param aFieldName 
     * @return 
     */  
	public static Object getFieldValue(Object aObject, String aFieldName) {
		Field field = getClassField(aObject.getClass(), aFieldName);
		if (field != null) {
			field.setAccessible(true);
			try {
				return field.get(aObject);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
  
    /** 
     * 这个方法，是最重要的，关键的实现在这里面 
     *  
     * @param aClazz 
     * @param aFieldName 
     * @return 
     */  
	private static Field getClassField(Class aClazz, String aFieldName) {
		Field[] declaredFields = aClazz.getDeclaredFields();
		for (Field field : declaredFields) {
			// 注意：这里判断的方式，是用字符串的比较。很傻瓜，但能跑。要直接返回Field。我试验中，尝试返回Class，然后用getDeclaredField(String
			// fieldName)，但是，失败了
			if (field.getName().equals(aFieldName)) {
				return field;// define in this class
			}
		}

		Class superclass = aClazz.getSuperclass();
		if (superclass != null) {// 简单的递归一下
			return getClassField(superclass, aFieldName);
		}
		return null;
	}

	public static UserPlayingDTO musicDto2UserPlayingDTO(MusicDTO currentMusicDTO,String cmbt,int ct,int uid) {
		UserPlayingDTO upld = new UserPlayingDTO();
		upld.setAn(currentMusicDTO.getAn());
		upld.setAtn(currentMusicDTO.getAtn());
		upld.setCmbt(cmbt);
		upld.setCt(String.valueOf(ct));
		upld.setD(currentMusicDTO.getD());
		upld.setFid(currentMusicDTO.getFid());
		upld.setMid(currentMusicDTO.getMid());
		upld.setN(currentMusicDTO.getN());
		upld.setTid(currentMusicDTO.getTid());
		upld.setUid(uid);
		return upld;
	}

}
