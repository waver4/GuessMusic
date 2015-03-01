package com.guessmusic.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * 音乐播放类
 * 
 * @author waver
 * 
 */
public class MyPlayer {

	// 歌曲播放
	private static MediaPlayer mMusicMediaPlayer;

	/**
	 * 播放歌曲
	 * 
	 * @param context
	 * @param fileName
	 */
	public static void playSong(Context context, String fileName) {
		if (mMusicMediaPlayer == null) {
			mMusicMediaPlayer = new MediaPlayer();
		}

		// 强制重置
		mMusicMediaPlayer.reset();

		// 加载声音
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			mMusicMediaPlayer
					.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());
			
			mMusicMediaPlayer.prepare();
			//声音播放
			mMusicMediaPlayer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 停止当前歌曲播放
	 * @param context
	 */
	public static void stopTheSong(Context context){
		if(mMusicMediaPlayer != null){
			mMusicMediaPlayer.stop();
		}
	}

}
