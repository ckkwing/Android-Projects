package com.jecfbagsx.android.data;

public class ResolutionInfo {
	private int width;
	private int height;
	private Quality type;

	public ResolutionInfo(Quality quality) {
		super();
		this.type = quality;
		switch (quality) {
		case Low:
			width = 180;
			height = 180;
			break;
		case Middle:
			width = 256;
			height = 256;
			break;
		case High:
			width = 320;
			height = 320;
			break;
		}
	}

	public ResolutionInfo(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public ResolutionInfo() {
		this.width = 0;
		this.height = 0;
	}
	
	public Quality getType() {
		return type;
	}

	public void setType(Quality type) {
		this.type = type;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String FormatRes() {
		return width + "x" + height;
	}

	@Override
	public String toString() {
		return "Res [width=" + width + ", height=" + height + "]";
	}
}
