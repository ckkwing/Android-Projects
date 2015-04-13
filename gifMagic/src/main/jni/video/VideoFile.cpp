/*
 * VideoFile.cpp
 *
 *  Created on: 2011-9-11
 *      Author: Mary
 */

#include "VideoFile.h"
#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <android/log.h>

#ifdef __cplusplus
extern "C" {
#include <libswscale/swscale.h>
}
#endif

#define  LOG_TAG    "VideoFrameCapture"

#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static void ts_str(char buffer[60], int64_t ts, AVRational base)
{
    double tsval;
    if (ts == AV_NOPTS_VALUE) {
        strcpy(buffer, " NOPTS   ");
        return;
    }
    tsval = ts * av_q2d(base);
    snprintf(buffer, 60, "%9f", tsval);
}

static long ts_to_long_ms(int64_t ts, AVRational base)
{
    double tsval;
    if (ts == AV_NOPTS_VALUE) {
        return 0.0;
    }
   return  (ts * av_q2d(base)*1000);
}

void VideoFile::fill_bitmap(int displayW, int displayH, AVFrame *pFrameRGB,
		AndroidBitmapInfo *info, void* pixels) {
	if (pFrameRGB == NULL || pixels == NULL || pFrameRGB->data[0] == NULL) {
		return;
	}

	if (info->format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
		uint8_t *frameLine = NULL;
		int yy;
		for (yy = 0; yy < info->height; yy++) {
			uint8_t* line = (uint8_t*) pixels;
			frameLine = (uint8_t *) pFrameRGB->data[0] + (yy
					* pFrameRGB->linesize[0]);

			int xx;
			for (xx = 0; xx < info->width; xx++) {
				int out_offset = xx * 4;
				int in_offset = xx * 3;

				line[out_offset] = frameLine[in_offset];
				line[out_offset + 1] = frameLine[in_offset + 1];
				line[out_offset + 2] = frameLine[in_offset + 2];
				line[out_offset + 3] = 0;
			}
			pixels = (char*) pixels + info->stride;
		}
	} else if (info->format == ANDROID_BITMAP_FORMAT_RGB_565) {

	} else if (info->format == ANDROID_BITMAP_FORMAT_RGBA_4444) {

	} else {

	}

}

VideoFile::VideoFile(const char *szFilePath) {
	mpFormatCtx = NULL;
	mpCodecCtx = NULL;
	mpCodec = NULL;
	if (szFilePath != NULL) {
		strcpy(mszFilePath, szFilePath);
	} else {
		memset(mszFilePath, 0, sizeof(mszFilePath));
	}
	mIndexVideoStream = -1;
	mWidth = 0;
	mHeight = 0;
	mDurationMs = 0;
	mPixelFormat = PIX_FMT_RGB24;
}

VideoFile::~VideoFile() {
	if (mpCodecCtx) {
		avcodec_close( mpCodecCtx);
	}
	mpCodecCtx = NULL;
	if (mpFormatCtx) {
		av_close_input_file( mpFormatCtx);
	}
	mpFormatCtx = NULL;
}

bool VideoFile::init() {
	if (mszFilePath[0] == '\0') {
		LOGE("File path is empty");
		return false;
	}

	int ret = -1;
	AVCodec *pCodec = NULL;
	LOGI("open file %s", mszFilePath);
	ret = av_open_input_file(&mpFormatCtx, mszFilePath, NULL, 0, NULL);
	LOGI("open file Successful, ret =[%d]", ret);
	if (ret != 0) {
		LOGE("Couldn't open file");
		return false;
	}
	LOGI("find stream info");
	if (av_find_stream_info(mpFormatCtx) < 0) {
		LOGE("Unable to get stream info");
		return false;
	}
	LOGI("find stream info successful");

	for (int i = 0; i < mpFormatCtx->nb_streams; i++) {
		if (mpFormatCtx->streams[i]->codec->codec_type == CODEC_TYPE_VIDEO) {
			mIndexVideoStream = i;
			break;
		}
	}

	if (mIndexVideoStream == -1) {
		LOGE("Unable to find video stream");
		return false;
	}

	LOGI("Video stream is [%d]", mIndexVideoStream);

	mpCodecCtx = mpFormatCtx->streams[mIndexVideoStream]->codec;

	mpCodec = avcodec_find_decoder(mpCodecCtx->codec_id);
	if (mpCodec == NULL) {
		LOGE("Unsupported codec");
		return false;
	}

	if (avcodec_open(mpCodecCtx, mpCodec) < 0) {
		LOGE("Unable to open codec");
		return false;
	}

	mWidth = mpCodecCtx->width;
	mHeight = mpCodecCtx->height;
	if (mpFormatCtx->duration >0)
	{
		mDurationMs = mpFormatCtx->duration/1000;
	}else
	{
		AVStream *pStream = mpFormatCtx->streams[mIndexVideoStream];
		mDurationMs = pStream->duration * pStream->time_base.num/pStream->time_base.den;
		mDurationMs = mDurationMs * 1000;
	}
	LOGI("Video Width is [%d]", mWidth);
	LOGI("Video Height is [%d]", mHeight);
	LOGI("Video Duration(ms)[%d]",mDurationMs);
	LOGI("mpFormatCtx->duration[%d]", mpFormatCtx->duration);
	return true;
}

int VideoFile::getWidth() {
	return mWidth;
}

int VideoFile::getHeight() {
	return mHeight;
}

long VideoFile::getDurationMs() {
	return mDurationMs;
}

long VideoFile::getNextFrame(int targetWidth, int targetHeight, int nskipFrame,
		AndroidBitmapInfo *pInfo, void* pixels) {
	AVFrame *pFrame = NULL;
	AVFrame *pFrameRGB = NULL;
	long ret = -1;
	int frameFinished = 0;
	AVPacket packet;
	struct SwsContext *img_convert_ctx;
	uint8_t *buffer;
	pFrame = avcodec_alloc_frame();
	pFrameRGB = avcodec_alloc_frame();
	LOGI("Video size is [%d x %d]", mpCodecCtx->width, mpCodecCtx->height);

	int numBytes = avpicture_get_size(mPixelFormat, mpCodecCtx->width,
			mpCodecCtx->height);
	buffer = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));

	avpicture_fill((AVPicture *) pFrameRGB, buffer, mPixelFormat,
			mpCodecCtx->width, mpCodecCtx->height);
	img_convert_ctx = sws_getContext(mpCodecCtx->width, mpCodecCtx->height,
			mpCodecCtx->pix_fmt, targetWidth, targetHeight, mPixelFormat,
			SWS_BICUBIC, NULL, NULL, NULL);

	int readSize = -1;
	int i = 0;
	int nSkip = 0;
	while ((i == 0) && (readSize = av_read_frame(mpFormatCtx, &packet)) >= 0) {
		if (packet.stream_index == mIndexVideoStream) {
			int retBytes = avcodec_decode_video(mpCodecCtx, pFrame,
					&frameFinished, packet.data, packet.size);
			char dts_buf[60];
			char ts_buf[60];
			AVStream *pStream = mpFormatCtx->streams[mIndexVideoStream];
			ts_str(dts_buf, packet.dts, pStream->time_base);
			ts_str(ts_buf,  packet.pts, pStream->time_base);
			LOGI("Debug package Info:");
			LOGI("dts:%s pts:%s size:%6d", dts_buf, ts_buf, packet.size);

			if (frameFinished) {
				if (nskipFrame > 0 && nSkip < nskipFrame) {
					nSkip++;
					LOGI("av_read_frame nskip Frame[%d]", nSkip);
					av_free_packet(&packet);
					continue;
				}

				sws_scale(img_convert_ctx, pFrame->data, pFrame->linesize, 0,
						mpCodecCtx->height, pFrameRGB->data,
						pFrameRGB->linesize);

				fill_bitmap(targetWidth, targetHeight, pFrameRGB, pInfo, pixels);
				i = 1;
				if (packet.pts != AV_NOPTS_VALUE)
				{
					ret = ts_to_long_ms(packet.pts, pStream->time_base);
				}else
				{
					ret = ts_to_long_ms(packet.dts, pStream->time_base);
				}
			}
		}
		av_free_packet(&packet);
	}

	sws_freeContext(img_convert_ctx);
	av_free(buffer);
	av_free(pFrameRGB);
	av_free(pFrame);
	return ret;
}

long VideoFile::seekTo(int targetWidth, int targetHeight, long nTimeMs,
		AndroidBitmapInfo *pInfo, void* pixels) {
	seek_frame(nTimeMs);
	return getNextFrame(targetWidth, targetHeight, 0, pInfo, pixels);
}

int VideoFile::seek_frame(long tsms) {
	LOGI("seek_frame");
	int64_t frame;
	frame = tsms * AV_TIME_BASE / 1000;
	frame = av_rescale_q(frame, AV_TIME_BASE_Q,
			mpFormatCtx->streams[mIndexVideoStream]->time_base);
	if (avformat_seek_file(mpFormatCtx, mIndexVideoStream, 0, frame, frame,
			AVSEEK_FLAG_FRAME) < 0) {
		return 0;
	}

	LOGE("seek_frame:pos:%d", frame);
	avcodec_flush_buffers( mpCodecCtx);
	LOGI("flush buffers");
	return 1;
}
