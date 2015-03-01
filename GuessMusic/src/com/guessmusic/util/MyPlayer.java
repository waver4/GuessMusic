package com.guessmusic.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * ���ֲ�����
 * 
 * @author waver
 * 
 */
public class MyPlayer {

	// ��������
	private static MediaPlayer mMusicMediaPlayer;

	/**
	 * ���Ÿ���
	 * 
	 * @param context
	 * @param fileName
	 */
	public static void playSong(Context context, String fileName) {
		if (mMusicMediaPlayer == null) {
			mMusicMediaPlayer = new MediaPlayer();
		}

		// ǿ������
		mMusicMediaPlayer.reset();

		// ��������
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			mMusicMediaPlayer
					.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());
			
			mMusicMediaPlayer.prepare();
			//��������
			mMusicMediaPlayer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * ֹͣ��ǰ��������
	 * @param context
	 */
	public static void stopTheSong(Context context){
		if(mMusicMediaPlayer != null){
			mMusicMediaPlayer.stop();
		}
	}

}
