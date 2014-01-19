package com.jingfm.lyrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jingfm.api.helper.StringHelper;
import com.jingfm.tools.JingTools;

public class LyricsMaker {
	
	public static LyricsInfo MakeLyricInfoByLyricFile(File lyricFile) {
		if (lyricFile == null || !lyricFile.exists() || lyricFile.isDirectory()
				|| !lyricFile.canRead()) {
			return null;
		} else {
			LyricsInfo lyricsInfo = null;
			FileInputStream fileInputStream  = null;
			InputStreamReader inputStreamReader  = null;
			BufferedReader bufferedReader  = null;
			try {
				lyricsInfo = new LyricsInfo();
				fileInputStream = new FileInputStream(lyricFile);
				inputStreamReader = new InputStreamReader(
						fileInputStream, "utf-8");
				bufferedReader = new BufferedReader(
						inputStreamReader);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					parserLine(lyricsInfo, line);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if (bufferedReader != null) {
						bufferedReader.close();
					}
					if (inputStreamReader != null) {
						inputStreamReader.close();
					}
					if (fileInputStream != null) {
						fileInputStream.close();
					}
				} catch (Exception e2) {
				}
			}
			return lyricsInfo;
		}
	}

	private static void parserLine(LyricsInfo lyricsInfo, String str) {
		if (str.startsWith("[ti:")) {
			String title = str.substring(4, str.length() - 1);
			lyricsInfo.setTitle(title);
		} else if (str.startsWith("[ar:")) {
			String singer = str.substring(4, str.length() - 1);
			lyricsInfo.setSinger(singer);
		} else if (str.startsWith("[al:")) {
			String album = str.substring(4, str.length() - 1);
			lyricsInfo.setAlbum(album);
		} else if (str.startsWith("[by:")) {
			String by = str.substring(4, str.length() - 1);
			lyricsInfo.setBy(by);
		} else if (str.startsWith("[re:")) {
			String re = str.substring(4, str.length() - 1);
			lyricsInfo.setRe(re);
		} else if (str.startsWith("[ve:")) {
			String ve = str.substring(4, str.length() - 1);
			lyricsInfo.setVe(ve);
		} else {
			// 设置正则规则
			Long currentTimeMS = -1L;
			String currentContent = "";
			HashMap<Long, String> mapsMS = lyricsInfo.getLineOfContentMS();
			String reg = "\\[(\\d{2}:\\d{2}\\.\\d{2})\\]";
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(str);

			while (matcher.find()) {
				// 得到匹配的所有内容
				String msg = matcher.group();
				// 得到这个匹配项开始的索引
				int start = matcher.start();
				// 得到这个匹配项结束的索引
				int end = matcher.end();

				// 得到这个匹配项中的组数
				int groupCount = matcher.groupCount();
				// 得到每个组中内容
				for (int i = 0; i <= groupCount; i++) {
					String timeStr = matcher.group(i);
					if (i == 1) {
						// 将第二组中的内容设置为当前的一个时间点
						currentTimeMS = strToLong(timeStr);
					}
				}

				// 得到时间点后的内容
				String[] content = pattern.split(str);
				// 输出数组内容
				for (int i = 0; i < content.length; i++) {
					if (i == content.length - 1) {
						currentContent = content[i];
					}
				}
				String hasContent = mapsMS.get(currentTimeMS);
				if (JingTools.isValidString(hasContent)) {
					mapsMS.put(currentTimeMS, hasContent + "\n" + currentContent);
				}else{
					mapsMS.put(currentTimeMS, currentContent);
				}
				// System.out.println("put---currentTimeMS--->" + currentTimeMS
				// + "----currentContent---->" + currentContent);
			}
		}
	}

	private static long strToLong(String timeStr) {
		// 因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单位
		// 1:使用：分割 2：使用.分割
		String[] stringArray = timeStr.split(":");
		int min = Integer.parseInt(stringArray[0]);
		String[] ss = stringArray[1].split("\\.");
		int sec = Integer.parseInt(ss[0]);
		int mill = Integer.parseInt(ss[1]);
		return min * 60 * 1000 + sec * 1000 + mill * 10;
	}
}
