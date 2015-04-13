package com.jecfbagsx.android.data;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.jecfbagsx.android.utils.FileHelper;

public abstract class BaseConverter implements IImageConverter {
	protected ResolutionInfo mResolutionInfo;
	protected boolean mIsNeedScaleSize;
	protected List<String> mImageList;
	protected ResolutionInfo mCurrentSettingRes;
	
	public BaseConverter(List<String> imageList, ResolutionInfo settingRes) {
		mImageList = imageList;
		mCurrentSettingRes = settingRes;
	}

	@Override
	public ResolutionInfo getTargetResolution() {
		return mResolutionInfo;
	}

	@Override
	public boolean isNeedAdjust() {
		return mIsNeedScaleSize;
	}

	protected abstract boolean init();

	protected boolean isFileSizeExceed(long maxSize) {
		boolean isExceedMax = false;
		long totalSize = 0;
		for (Iterator<String> iterator = mImageList.iterator(); iterator.hasNext();) {
			String url = iterator.next();
			File file = new File(url);
			if (file.exists() && file.isFile() && file.canRead()) {
				totalSize += file.length();
				if (totalSize > maxSize) {
					isExceedMax = true;
					break;
				}
			}
		}
		return isExceedMax;
	}

	protected int getFilesMaxRes() {
		int maxWidth = 0;
		for (Iterator<String> iterator = mImageList.iterator(); iterator.hasNext();) {
			String url = iterator.next();

			ResolutionInfo imageInfo = FileHelper.getImageResolution(url);
			if (imageInfo.getWidth() > maxWidth) {
				maxWidth = imageInfo.getWidth();
			}

			if (imageInfo.getHeight() > maxWidth) {
				maxWidth = imageInfo.getHeight();
			}

		}
		return maxWidth;
	}
}
