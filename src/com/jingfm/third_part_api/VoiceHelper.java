package com.jingfm.third_part_api;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;

import com.jingfm.MainActivity;
import com.jingfm.background_model.LocalCacheManager;

public class VoiceHelper {
	private static VoiceHelper instance;
	private static boolean isGoogleVoiceExist;
	private static MediaRecorder sMediaRecorder;
	private static String TEMP_FILE_PATH;
	private static String GOOGLE_SPEECH_API_URL = "http://www.google.com/speech-api/v1/recognize?xjerr=1&client=chromium&lang=zh-CN";

	private VoiceHelper() {
	}

	public static VoiceHelper getInstance() {
		if (instance == null) {
			instance = new VoiceHelper();
		}
		return instance;
	}

	public static void init(MainActivity mActivity) {
		List<ResolveInfo> activities = mActivity
				.getPackageManager()
				.queryIntentActivities(
						new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		isGoogleVoiceExist = (activities.size() != 0);
		TEMP_FILE_PATH = LocalCacheManager.getInstance().getBasePath()
				+ "/record_temp.amr";
	}

	public static void startVoiceRecognitionGv(MainActivity mActivity) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "JingFM Search");
		// mActivity.startActivityForResult(intent,
		// MainActivity.REQUEST_CODE_VOICE_RECOGNITION);
	}

	public static void startVoiceRecognition(MainActivity mActivity) {
		// if (isGoogleVoiceExist) {
		// startVoiceRecognitionGv(mActivity);
		// }else{
		startVoiceRecognitionDiy(mActivity);
		// }
	}

	public synchronized static void startVoiceRecognitionDiy(
			final MainActivity mActivity) {
		if (sMediaRecorder != null) {
			cancelVoiceRecognitionDiy();
		}
		sMediaRecorder = new MediaRecorder();
		sMediaRecorder.setAudioSource(AudioSource.MIC);
		// 设置音源,这里是来自麦克风,虽然有VOICE_CALL,但经真机测试,不行
		sMediaRecorder.setOutputFormat(OutputFormat.RAW_AMR);
		// 输出格式
		sMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// 编码
		File outputFile = new File(TEMP_FILE_PATH);
		sMediaRecorder.setOutputFile(TEMP_FILE_PATH);
		if (outputFile.exists()) {
			outputFile.delete();
		}
		// 输出文件路径,貌似文件必须是不存在的,不会自己清空
		try {
			sMediaRecorder.prepare();
			// 做些准备工作
			sMediaRecorder.start();
			// 开始
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static void cancelVoiceRecognitionDiy() {
		if (sMediaRecorder == null) {
			return;
		} else {
			try {
				sMediaRecorder.stop();
				sMediaRecorder.reset();
				sMediaRecorder.release();
			} catch (Exception e) {
			}
			sMediaRecorder = null;
		}
	}

	public synchronized static void stopVoiceRecognitionDiy(
			final MainActivity mActivity, final VoiceCallbacker callbacker) {
		cancelVoiceRecognitionDiy();
		new VoiceAsyncTask().startAsyncTask(null, new VoiceCallbacker() {
			@Override
			public Object onCallBack(Object obj) {
				HashMap<String, String> params = new HashMap<String, String>();
				// xjerr=1&client=chromium&lang=zh-CN"
				params.put("xjerr", "1");
				params.put("client", "chromium");
				params.put("lang", "zh-CN");
				HashMap<String, File> files = new HashMap<String, File>();
				files.put("TEMP_FILE_PATH", new File(TEMP_FILE_PATH));
				String rs = null;
				try {
					rs = post(GOOGLE_SPEECH_API_URL, params, files);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return rs;
			}
		}, callbacker);
	}

	public static String post(String actionUrl, Map<String, String> params,
			Map<String, File> files) throws IOException {

		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(5 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", "audio/amr; rate=8000");

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		// 发送文件数据
		if (files != null)
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
						+ file.getKey() + "\"" + LINEND);
				sb1.append("Content-Type: audio/amr; rate=8000");
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}

		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// 得到响应码
		int res = conn.getResponseCode();
		String str = conn.getResponseMessage();
		String rs = null;
		InputStream in;
		if (res == 200) {
			in = conn.getInputStream();
			StringBuilder sb2 = new StringBuilder();
			byte[] buffer = new byte[100 * 1024];
			while (true) {
				int count = in.read(buffer);
				if (count < 0) {
					break;
				} else {
					sb2.append(new String(buffer));
				}
			}
			try {
				JSONObject jsonObject = new JSONObject(sb2.toString().trim());
				JSONArray jsonArray = jsonObject.getJSONArray("hypotheses");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = (JSONObject) jsonArray.get(i);
					if (object instanceof JSONObject) {
						rs = object.getString("utterance");
						if (rs != null) {
							break;
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		outStream.close();
		conn.disconnect();
		return rs;
	}

	private static class  VoiceAsyncTask extends AsyncTask<String, Integer, String> {

		private VoiceCallbacker preCallbacker;
		private VoiceCallbacker backCallbacker;
		private VoiceCallbacker afterCallbacker;

		public void startAsyncTask(VoiceCallbacker preCallbacker, VoiceCallbacker backCallbacker, VoiceCallbacker afterCallbacker) {
			this.preCallbacker = preCallbacker;
			this.backCallbacker = backCallbacker;
			this.afterCallbacker = afterCallbacker;
			this.execute(null,null);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (preCallbacker != null) {
				preCallbacker.onCallBack(null);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (afterCallbacker != null) {
				afterCallbacker.onCallBack(result);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected String doInBackground(String... params) {
			if (backCallbacker != null) {
				return (String) backCallbacker.onCallBack(null);
			}
			return null;
		}
	}
}
