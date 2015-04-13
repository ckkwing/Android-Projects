package com.jecfbagsx.android.data;

import java.util.List;

import com.jecfbagsx.android.utils.FileHelper;

public class MiddleQualityConverter extends BaseConverter {

	public MiddleQualityConverter(List<String> imageList,
			ResolutionInfo settingRes) {
		super(imageList, settingRes);
	}

	@Override
	protected boolean init() {
		ResolutionInfo imageInfo = FileHelper.getImageResolution(mImageList
				.iterator().next());

		int maxWidth = getFilesMaxRes();
		if (maxWidth > mCurrentSettingRes.getWidth()) {
			this.mIsNeedScaleSize = true;
			this.mResolutionInfo = FileHelper.getRefinedResolutionInfo(
					imageInfo, mCurrentSettingRes.getWidth());
		} else {
			this.mIsNeedScaleSize = false;
			this.mResolutionInfo = imageInfo;
		}
		return true;
	}
}
