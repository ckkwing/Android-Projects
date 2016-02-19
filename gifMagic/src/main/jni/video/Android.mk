LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ffmpegutils
LOCAL_SRC_FILES := VideoFile.cpp \
                 VideoFileManager.cpp \
                 VideoFrame.cpp \
                 ImageFile.cpp

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS := -L$(NDK_PLATFORMS_ROOT)/android-9/arch-arm/usr/lib -L$(LOCAL_PATH) -ljnigraphics -lavformat -lavcodec -lavdevice -lavfilter -lavcore -lavutil -lswscale -llog -lz -ldl -lgcc

include $(BUILD_SHARED_LIBRARY)
