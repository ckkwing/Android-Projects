package com.jecfbagsx.android.data;

import java.util.List;

import com.jecfbagsx.android.utils.FileHelper;

public class HighQualityConverter extends BaseConverter {

	public HighQualityConverter(List<String> imageList,
			ResolutionInfo settingRes) {
		super(imageList, settingRes);
	}

	@Override
	protected boolean init() {
		this.mResolutionInfo = new ResolutionInfo(Quality.High);
		this.mIsNeedScaleSize = true;
		if (mImageList.size() < 10) {
			tenPicSolution();
		} else if (mImageList.size() < 20) {
			twentuyPicSolution();
		} else if (mImageList.size() < 30) {
			thirtyPicSolution();
		} else {
			otherPicSolution();
		}
		return true;
	}

	private void tenPicSolution() {
		long defMaxSize = 1 * 1024 * 1024;
		int defMaxWidth = 440;
		int maxWidth = getFilesMaxRes();
		boolean isExceed = isFileSizeExceed(defMaxSize);
		ResolutionInfo imageInfo = FileHelper.getImageResolution(mImageList.iterator().next());
		if (maxWidth > defMaxWidth) {
			mIsNeedScaleSize = true;
			this.mResolutionInfo = FileHelper.getRefinedResolutionInfo(imageInfo, 440);
		} else {
			if (isExceed) {
				mIsNeedScaleSize = true;
				this.mResolutionInfo = imageInfo;
			} else {
				mIsNeedScaleSize = false;
				this.mResolutionInfo = imageInfo;
			}
		}
	}

	private void twentuyPicSolution() {
		long defMaxSize = 2 * 1024 * 1024;
		int defMaxWidth = 440;
		int maxWidth = getFilesMaxRes();
		boolean isExceed = isFileSizeExceed(defMaxSize);
		ResolutionInfo imageInfo = FileHelper.getImageResolution(mImageList.iterator().next());
		if (maxWidth > defMaxWidth) {
			mIsNeedScaleSize = true;
			this.mResolutionInfo = FileHelper.getRefinedResolutionInfo(imageInfo, 440);
		} else {
			mIsNeedScaleSize = false;
			if (isExceed) {
				this.mResolutionInfo = imageInfo;
			} else {
				this.mResolutionInfo = imageInfo;
			}
		}
	}

	private void thirtyPicSolution() {
		long defMaxSize = 3 * 1024 * 1024;
		int defMaxWidth = 440;
		int maxWidth = getFilesMaxRes();
		boolean isExceed = isFileSizeExceed(defMaxSize);
		ResolutionInfo imageInfo = FileHelper.getImageResolution(mImageList.iterator().next());
		if (maxWidth > defMaxWidth) {
			mIsNeedScaleSize = true;
			this.mResolutionInfo = FileHelper.getRefinedResolutionInfo(imageInfo, mCurrentSettingRes.getWidth());
		} else {
			mIsNeedScaleSize = false;
			if (isExceed) {
				this.mResolutionInfo = imageInfo;
			} else {
				this.mResolutionInfo = imageInfo;
			}
		}
	}

	private void otherPicSolution() {
		long defMaxSize = 5 * 1024 * 1024;
		int defMaxWidth = 440;
		int maxWidth = getFilesMaxRes();
		boolean isExceed = isFileSizeExceed(defMaxSize);
		ResolutionInfo imageInfo = FileHelper.getImageResolution(mImageList.iterator().next());
		if (maxWidth > defMaxWidth) {
			mIsNeedScaleSize = true;
			this.mResolutionInfo = FileHelper.getRefinedResolutionInfo(imageInfo, mCurrentSettingRes.getWidth());
		} else {
			mIsNeedScaleSize = false;
			if (isExceed) {
				this.mResolutionInfo = imageInfo;
			} else {
				this.mResolutionInfo = imageInfo;
			}
		}
	}

}
