/*
 * ImageFile.cpp
 *
 *  Created on: 2011-9-13
 *      Author: gshe
 */

#include "ImageFile.h"
#include <jni.h>
#include <stdio.h>
#include <android/log.h>

#define LOG_TAG "GifMagicImageScale"

#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

void ImageFile::fill_bitmap(int displayW, int displayH, AVFrame *pFrameRGB,
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

ImageFile::ImageFile(const char *szFilePath) {
	mpFormatCtx = NULL;
	mpCodecCtx = NULL;
	mpCodec = NULL;
	if (szFilePath != NULL) {
		strcpy(mszFilePath, szFilePath);
	} else {
		memset(mszFilePath, 0, sizeof(mszFilePath));
	}
	mIndexUnknownStream = -1;
	mPixelFormat = PIX_FMT_RGB24;
}

ImageFile::~ImageFile() {
}

bool ImageFile::init() {
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

	LOGI("find stream info Count[%d]", mpFormatCtx->nb_streams);
	for (int i = 0; i < mpFormatCtx->nb_streams; i++) {
		if (mpFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_UNKNOWN) {
			LOGI("open file Successful, codec_type =[%d], codec_id[%d]", mpFormatCtx->streams[i]->codec->codec_type, mpFormatCtx->streams[i]->codec->codec_id);
			mIndexUnknownStream = i;
			break;
		}
	}
	mIndexUnknownStream = 0;
	if (mIndexUnknownStream == -1) {
		LOGE("Unable to find video stream");
		return false;
	}

	mpCodecCtx = mpFormatCtx->streams[mIndexUnknownStream]->codec;

	mpCodec = avcodec_find_decoder(mpCodecCtx->codec_id);
	if (mpCodec == NULL) {
		LOGE("Unsupported codec");
		return false;
	}

	if (avcodec_open(mpCodecCtx, mpCodec) < 0) {
		LOGE("Unable to open codec");
		return false;
	}

	return true;
}

long ImageFile::scaleImage(AndroidBitmapInfo *pTargetInfo, void* pTargetPixels) {
	if (!pTargetInfo || !pTargetPixels) {
		return -1;
	}

	uint32_t targetWidth = pTargetInfo->width;
	uint32_t targetHeight = pTargetInfo->height;
	LOGI("Target Image size is [%d x %d]", targetWidth, targetHeight);

	AVFrame *pFrame = NULL;
	AVFrame *pFrameRGB = NULL;
	long ret = -1;
	int frameFinished = 0;
	AVPacket packet;
	struct SwsContext *img_convert_ctx;
	uint8_t *buffer;
	pFrame = avcodec_alloc_frame();
	pFrameRGB = avcodec_alloc_frame();
	LOGI("Src Image size is [%d x %d]", mpCodecCtx->width, mpCodecCtx->height);

	int numBytes = avpicture_get_size(mPixelFormat, mpCodecCtx->width,
			mpCodecCtx->height);
	buffer = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));
	LOGI("avpicture_get_size");
	avpicture_fill((AVPicture *) pFrameRGB, buffer, mPixelFormat,
			mpCodecCtx->width, mpCodecCtx->height);
	LOGI("avpicture_fill");
	img_convert_ctx = sws_getContext(mpCodecCtx->width, mpCodecCtx->height,
			mpCodecCtx->pix_fmt, targetWidth, targetHeight, mPixelFormat,
			SWS_BICUBIC, NULL, NULL, NULL);
	LOGI("sws_getContext");
	int readSize = -1;
	int i = 0;
	while ((i == 0) && (readSize = av_read_frame(mpFormatCtx, &packet)) >= 0) {
		LOGI("av_read_frame");
		if (packet.stream_index == mIndexUnknownStream) {
			int retBytes = avcodec_decode_video(mpCodecCtx, pFrame,
					&frameFinished, packet.data, packet.size);
			LOGI("avcodec_decode_video");
			if (frameFinished) {
				LOGI("sws_scale");
				sws_scale(img_convert_ctx, pFrame->data, pFrame->linesize, 0,
						mpCodecCtx->height, pFrameRGB->data,
						pFrameRGB->linesize);
				LOGI("fill_bitmap");
				fill_bitmap(targetWidth, targetHeight, pFrameRGB, pTargetInfo, pTargetPixels);
				i = 1;
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
