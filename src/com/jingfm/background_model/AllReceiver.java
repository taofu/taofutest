package com.jingfm.background_model;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class AllReceiver extends BroadcastReceiver {
	private boolean mMediaButtonEnabled = true;
	private static AllReceiver instance;

	public static AllReceiver getInstance() {
		return instance;
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			// Toast.makeText(context, "PowerOnReceiver onReceive", 1).show();
			final Intent newIntent = new Intent(context, JingService.class);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 注意，必须添加这个标记，否则启动会失败
			instance = this;
			new Thread() {
				public void run() {
					context.startService(newIntent);
				};
			}.start();
		} else if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
		} else if (intent.getAction().equals(
				TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
		} else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
			if (!mMediaButtonEnabled) {
				return;
			}
			KeyEvent event = (KeyEvent) intent
					.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if (event == null)
				return;

			switch (event.getAction()) {
			case KeyEvent.ACTION_UP:
				break;
			case KeyEvent.ACTION_DOWN:
				return;
			default:
				return;
			}
			int keyCode = event.getKeyCode();
			long eventTime = event.getEventTime() - event.getDownTime();// 按键按下到松开的时长
			if (eventTime < 1000) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
					Log.i("kid_debug","KEYCODE_MEDIA_PLAY_PAUSE");
					sendMusicSwitch(context);
					abortBroadcast();
					break;
				case KeyEvent.KEYCODE_HEADSETHOOK: // 耳机的中键被按下
					Log.i("kid_debug","KEYCODE_HEADSETHOOK");
					sendMusicCenterButton(context);
					abortBroadcast();
					break;
				case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
					Log.i("kid_debug","KEYCODE_MEDIA_PREVIOUS");
					break;
				case KeyEvent.KEYCODE_MEDIA_STOP:
					Log.i("kid_debug","KEYCODE_MEDIA_STOP");
					sendMusicPause(context);
					abortBroadcast();
					break;
				case KeyEvent.KEYCODE_MEDIA_NEXT:
					Log.i("kid_debug","KEYCODE_MEDIA_NEXT");
					sendMusicNext(context);
					abortBroadcast();
					break;
				}
			}
		}
	}

	private void sendMusicCenterButton(Context context) {
		Intent intent = new Intent(
				PlayerManager.MUSIC_CONTROLLOR_ACTION);
		Bundle bundle = new Bundle();
		bundle.putInt(PlayerManager.MUSIC_CONTROLLOR_ACTION_Key,
				PlayerManager.MUSIC_CONTROLLOR_ACTION_CenterButton);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}

	private void sendMusicSwitch(Context context) {
		Intent intent = new Intent(
				PlayerManager.MUSIC_CONTROLLOR_ACTION);
		Bundle bundle = new Bundle();
		bundle.putInt(PlayerManager.MUSIC_CONTROLLOR_ACTION_Key,
				PlayerManager.MUSIC_CONTROLLOR_ACTION_Play);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}
	private void sendMusicPause(Context context) {
		Intent intent = new Intent(
				PlayerManager.MUSIC_CONTROLLOR_ACTION);
		Bundle bundle = new Bundle();
		bundle.putInt(PlayerManager.MUSIC_CONTROLLOR_ACTION_Key,
				PlayerManager.MUSIC_CONTROLLOR_ACTION_Pause);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}
	private void sendMusicNext(Context context) {
		Intent intent = new Intent(
				PlayerManager.MUSIC_CONTROLLOR_ACTION);
		Bundle bundle = new Bundle();
		bundle.putInt(PlayerManager.MUSIC_CONTROLLOR_ACTION_Key,
				PlayerManager.MUSIC_CONTROLLOR_ACTION_Next);
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}
}