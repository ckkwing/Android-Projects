//============================================================================
// Name        : VideoFrame.cpp
// Author      : George She
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================
#include <stdio.h>
#include "jni.h"
#include <android/log.h>
#include <android/bitmap.h>
#include "VideoFile.h"
#include "VideoFileManager.h"
#include "ImageFile.h"
#define LOG_TAG "GifMagicVideoFrame"

#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

struct fields_t {
	jfieldID context;
};

static fields_t fields;

static const char* const kClassPathName =
		"com/jecfbagsx/android/video/FrameCapture";

static VideoFileManager* getManager(JNIEnv* env, jobject thiz) {
	VideoFileManager* manager = (VideoFileManager*) env->GetIntField(thiz,
			fields.context);
	LOGI("Jni getManager from env[%d]", manager);
	return manager;
}

static void setManager(JNIEnv* env, jobject thiz, int manager) {
	LOGI("Jni setManager into env");
	VideoFileManager *old = (VideoFileManager*) env->GetIntField(thiz,
			fields.context);
	env->SetIntField(thiz, fields.context, manager);
}

JNIEXPORT jint JNICALL com_jecfbagsx_android_video_FrameCapture_openFile(
		JNIEnv * env, jobject thiz, jstring filename) {
	LOGI("Jni FrameCapture openFile");
	jboolean isCopy;
	VideoFileManager* manager = getManager(env, thiz);
	const char *strFilePath = env->GetStringUTFChars(filename, &isCopy);
	VideoFile *pFile = new VideoFile(strFilePath);
	int index = -1;
	if (pFile && pFile->init()) {
		index = manager->putVideoFile(pFile);
	}
	if (isCopy) {
		env->ReleaseStringUTFChars(filename, strFilePath);
	}
	LOGI("Jni FrameCapture openFile, ret [%d]", index);
	return index;
}

JNIEXPORT jint JNICALL com_jecfbagsx_android_video_FrameCapture_getFileResolutionWidth(
		JNIEnv * env, jobject thiz, jint videoIndex) {
	int w = 0;
	VideoFileManager* manager = getManager(env, thiz);
	VideoFile *pFile = manager->getVideoFile(videoIndex);
	if (pFile != NULL) {
		w = pFile->getWidth();
	}

	return w;
}

JNIEXPORT jint JNICALL com_jecfbagsx_android_video_FrameCapture_getFileResolutionHeight(
		JNIEnv * env, jobject thiz, jint videoIndex) {
	LOGI("get File Resolution Height");
	int h = 0;
	VideoFileManager* manager = getManager(env, thiz);
	VideoFile *pFile = manager->getVideoFile(videoIndex);
	if (pFile != NULL) {
		h = pFile->getHeight();
	}

	return h;
}

JNIEXPORT jlong JNICALL com_jecfbagsx_android_video_FrameCapture_getFileDuration(
		JNIEnv * env, jobject thiz, jint videoIndex) {
	LOGI("get File Duration");
	long dur = 0;
	VideoFileManager* manager = getManager(env, thiz);
	VideoFile *pFile = manager->getVideoFile(videoIndex);
	if (pFile != NULL) {
		dur = pFile->getDurationMs();
	}

	LOGI("Video Duration is [%d]", dur);
	return dur;
}

JNIEXPORT jlong JNICALL com_jecfbagsx_android_video_FrameCapture_getNextFrame(
		JNIEnv * env, jobject thiz, jint videoIndex, jint targetW,
		jint targetH, jint nskipFrame, jobject bitmap) {
	AndroidBitmapInfo info;
	int ret = -1;
	void* pixels = NULL;
	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return NULL;
	}
	LOGE("Checked on the bitmap");

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	VideoFileManager* manager = getManager(env, thiz);
	VideoFile *pFile = manager->getVideoFile(videoIndex);
	if (pFile != NULL) {
		ret = pFile->getNextFrame(targetW, targetH, nskipFrame, &info, pixels);
	}

	AndroidBitmap_unlockPixels(env, bitmap);
	LOGI("seek to ret[%d]", ret);
	return ret;
}

JNIEXPORT jlong JNICALL com_jecfbagsx_android_video_FrameCapture_seekTo(
		JNIEnv * env, jobject thiz, jint videoIndex, jint targetW,
		jint targetH, jlong timeMs, jobject bitmap) {
	AndroidBitmapInfo info;
	int ret;
	void* pixels = NULL;
	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return NULL;
	}
	LOGI("Checked on the bitmap");

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}
	VideoFileManager* manager = getManager(env, thiz);
	VideoFile *pFile = manager->getVideoFile(videoIndex);
	if (pFile != NULL) {
		ret = pFile->seekTo(targetW, targetH, timeMs, &info, pixels);
	}
	AndroidBitmap_unlockPixels(env, bitmap);
	LOGI("seek to ret[%d]", ret);
	return ret;
}

JNIEXPORT jlong JNICALL com_jecfbagsx_android_video_FrameCapture_scaleImage(
		JNIEnv * env, jobject thiz, jstring filename, jobject targetBmp) {
	LOGI("scale Image xxxxxx");
	AndroidBitmapInfo targetInfo;
	int ret;
	jboolean isCopy;
	void* targetPixel = NULL;
	const char *strFilePath = env->GetStringUTFChars(filename, &isCopy);
	if ((ret = AndroidBitmap_getInfo(env, targetBmp, &targetInfo)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
	}

	if ((ret = AndroidBitmap_lockPixels(env, targetBmp, &targetPixel)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}
	ImageFile image(strFilePath);
	image.init();
	image.scaleImage(&targetInfo, targetPixel);
	AndroidBitmap_unlockPixels(env, targetBmp);
	if (isCopy) {
		env->ReleaseStringUTFChars(filename, strFilePath);
	}
	return 0;
}

static void com_jecfbagsx_android_video_FrameCapture_native_init(JNIEnv *env) {
	LOGI("register all codec");
	avcodec_init();
	avcodec_register_all();
	av_register_all();

	LOGI("FindClass,[%s]", kClassPathName);
	jclass clazz = env->FindClass(kClassPathName);
	if (clazz == NULL) {
		return;
	}
	LOGI("Find mNativeContext");
	fields.context = env->GetFieldID(clazz, "mNativeContext", "I");
	if (fields.context == NULL) {
		return;
	}
}

static void com_jecfbagsx_android_video_FrameCapture_native_setup(JNIEnv *env,
		jobject thiz) {
	LOGI("native_setup");
	VideoFileManager* pManager = new VideoFileManager();
	if (pManager == 0) {
		return;
	}

	bool bRet = pManager->init();
	LOGI("pManager->init ret [%d]", bRet);
	setManager(env, thiz, (int) pManager);
}

static void com_jecfbagsx_android_video_FrameCapture_release(JNIEnv *env,
		jobject thiz) {
	LOGI("release");
	VideoFileManager* manager = getManager(env, thiz);
	delete manager;
	setManager(env, thiz, 0);
}

static void com_jecfbagsx_android_video_FrameCapture_native_finalize(
		JNIEnv *env, jobject thiz) {
	LOGI("native_finalize");

	com_jecfbagsx_android_video_FrameCapture_release(env, thiz);
}

static JNINativeMethod
		nativeMethods[] =
				{
						{
								"openFile",
								"(Ljava/lang/String;)I",
								(void *) com_jecfbagsx_android_video_FrameCapture_openFile },
						{
								"getNextFrame",
								"(IIIILandroid/graphics/Bitmap;)J",
								(void *) com_jecfbagsx_android_video_FrameCapture_getNextFrame },
						{
								"scaleImage",
								"(Ljava/lang/String;Landroid/graphics/Bitmap;)J",
								(void *) com_jecfbagsx_android_video_FrameCapture_scaleImage },
						{
								"seekTo",
								"(IIIJLandroid/graphics/Bitmap;)J",
								(void *) com_jecfbagsx_android_video_FrameCapture_seekTo },
						{
								"getFileDuration",
								"(I)J",
								(void *) com_jecfbagsx_android_video_FrameCapture_getFileDuration },
						{
								"getFileResolutionWidth",
								"(I)I",
								(void *) com_jecfbagsx_android_video_FrameCapture_getFileResolutionWidth },
						{
								"getFileResolutionHeight",
								"(I)I",
								(void *) com_jecfbagsx_android_video_FrameCapture_getFileResolutionHeight },
						{
								"release",
								"()V",
								(void *) com_jecfbagsx_android_video_FrameCapture_release },
						{
								"native_finalize",
								"()V",
								(void *) com_jecfbagsx_android_video_FrameCapture_native_finalize },
						{
								"native_setup",
								"()V",
								(void *) com_jecfbagsx_android_video_FrameCapture_native_setup },
						{
								"native_init",
								"()V",
								(void *) com_jecfbagsx_android_video_FrameCapture_native_init }, };

// This function only registers the native methods, and is called from
// JNI_OnLoad in android_media_MediaPlayer.cpp
int register_native_method(JNIEnv *env) {
	LOGI("Jni register_native_method");
	jclass clazz;

	clazz = env->FindClass(kClassPathName);
	if (clazz == NULL) {
		return JNI_FALSE;
	}
	if (env->RegisterNatives(clazz, nativeMethods,
			sizeof(nativeMethods) / sizeof(nativeMethods[0])) < 0) {
		return JNI_FALSE;
	}
	LOGI("Jni register_native_method successful");
	return JNI_TRUE;
}

/* This function will be call when the library first be loaded */
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	LOGI("JNI_OnLoad!");

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("ERROR: GetEnv failed");
		return -1;
	}

	if (register_native_method(env) != JNI_TRUE) {
		LOGE("ERROR: registerNatives failed");
		return -1;
	}
	LOGI("JNI_OnLoad finished");
	return JNI_VERSION_1_4;
}

