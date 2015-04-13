package com.jecfbagsx.android.data;

import java.util.List;

import com.jecfbagsx.android.utils.SettingsHelper;

public class ImageSolutionFactory {
	public static IImageConverter getImageSolution(List<String> imageList) {
		ResolutionInfo resSetting = SettingsHelper.getResolutionInfo();
		BaseConverter conv = null;
		switch (resSetting.getType()) {
		case Low:
			conv = new LowQualityConverter(imageList, resSetting);
			break;
		case Middle:
			conv = new MiddleQualityConverter(imageList, resSetting);
			break;
		case High:
			conv = new HighQualityConverter(imageList, resSetting);
			break;
		}
		
		conv.init();
		return conv;
	}
}
