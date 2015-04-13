/*
 * VideoFile.h
 *
 *  Created on: 2011-9-11
 *      Author: Mary
 */

#ifndef VIDEOFILE_H_
#define VIDEOFILE_H_
#include <android/bitmap.h>
#ifdef __cplusplus
extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
}
#endif

class VideoFile {
public:
	VideoFile(const char *szFilePath);
	virtual ~VideoFile();
	bool init();
	int getWidth();
	int getHeight();
	long getDurationMs();
	long getNextFrame(int targetWidth, int targetHeight, int nskipFrame,
			AndroidBitmapInfo *pInfo, void* pixels);
	long seekTo(int targetWidth, int targetHeight, long nTimeMs,
			AndroidBitmapInfo *pInfo, void* pixels);
	void freeFrame();

private:
	void fill_bitmap(int displayW, int displayH, AVFrame *pFrameRGB,
			AndroidBitmapInfo *info, void* pixels);
	int seek_frame(long tsms);

private:
	AVFormatContext *mpFormatCtx;
	AVCodecContext *mpCodecCtx;
	AVCodec *mpCodec;
	char mszFilePath[256];
	int mIndexVideoStream;
	int mWidth;
	int mHeight;
	long mDurationMs;
	PixelFormat mPixelFormat;
};

#endif /* VIDEOFILE_H_ */
