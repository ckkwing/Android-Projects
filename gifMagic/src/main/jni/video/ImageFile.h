/*
 * ImageFile.h
 *
 *  Created on: 2011-9-13
 *      Author: gshe
 */

#ifndef IMAGEFILE_H_
#define IMAGEFILE_H_
#include <android/bitmap.h>

#ifdef __cplusplus
extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
}
#endif

class ImageFile {
public:
	ImageFile(const char *szFilePath);
	virtual ~ImageFile();
	bool init();
	long scaleImage(AndroidBitmapInfo *pTargetInfo, void* pTargetPixels);

private:
	void fill_bitmap(int displayW, int displayH, AVFrame *pFrameRGB,
				AndroidBitmapInfo *info, void* pixels);
private:
	AVFormatContext *mpFormatCtx;
	AVCodecContext *mpCodecCtx;
	AVCodec *mpCodec;
	char mszFilePath[256];
	int mIndexUnknownStream;
	PixelFormat mPixelFormat;
};

#endif /* IMAGEFILE_H_ */
