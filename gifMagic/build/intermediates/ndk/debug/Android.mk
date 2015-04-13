LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := gifMagic
LOCAL_SRC_FILES := \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\Android.mk \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\Android.mk \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\ImageFile.cpp \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\libavcodec.a \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\libavcore.a \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\libavdevice.a \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\libavfilter.a \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\libavformat.a \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\libavutil.a \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\libswscale.a \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\VideoFile.cpp \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\VideoFileManager.cpp \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\video\VideoFrame.cpp \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\yuv420sp\Android.mk \
	E:\GitHub\Android-Projects\gifMagic\src\main\jni\yuv420sp\YUV420SP.c \

LOCAL_C_INCLUDES += E:\GitHub\Android-Projects\gifMagic\src\main\jni
LOCAL_C_INCLUDES += E:\GitHub\Android-Projects\gifMagic\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
