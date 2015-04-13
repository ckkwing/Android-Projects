package com.jecfbagsx.android.camera;

public final class CaptureJob {
	public enum State {
		capturing, storing, finished,
	};
	
	public String filename;
	public int speed;
	public int quality;
	public boolean result;
	public State state;
	public CaptureType type;
};
