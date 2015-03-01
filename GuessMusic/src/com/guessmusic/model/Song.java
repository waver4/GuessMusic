package com.guessmusic.model;

public class Song {
	//定义歌曲名
	private String mSongName;
	//定义歌曲文件夹名字
	private String mSongFileName;
	//定义歌曲长度
	private int mNameLength;
	
	public char[] getNameCharacters(){
		return mSongName.toCharArray();
	}
	public String getSongName() {
		return mSongName;
	}
	public void setSongName(String songName) {
		this.mSongName = songName;
		
		this.mNameLength = songName.length();
	}
	public String getSongFileName() {
		return mSongFileName;
	}
	public void setSongFileName(String songFileName) {
		this.mSongFileName = songFileName;
	}
	public int getNameLength() {
		return mNameLength;
	}



}
