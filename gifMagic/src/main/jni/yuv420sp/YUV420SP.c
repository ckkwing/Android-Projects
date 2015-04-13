#include <string.h>
#include <jni.h>

#define PIXEL_FORMAT_YUV_420_SP	17
#define PIXEL_FORMAT_RGBA_8888	1

void decodeYUV420SP(
		int* rgb,
		signed char* yuv420sp,
		int width,
		int height
		)
{
	int frameSize = width * height;
	int j = 0;
	int i = 0;
	int yp = 0;

    for (j = 0, yp = 0; j < height; j++) {
    	int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
        for (i = 0; i < width; i++, yp++) {
        	int y = (0xff & ((int)yuv420sp[yp])) - 16;
            if (y < 0) y = 0;
            if ((i & 1) == 0) {
                v = (0xff & yuv420sp[uvp++]) - 128;
                u = (0xff & yuv420sp[uvp++]) - 128;
            }
            int y1192 = 1192 * y;
            int r = (y1192 + 1634 * v);
            int g = (y1192 - 833 * v - 400 * u);
            int b = (y1192 + 2066 * u);

            if (r < 0) r = 0; else if (r > 262143) r = 262143;
            if (g < 0) g = 0; else if (g > 262143) g = 262143;
            if (b < 0) b = 0; else if (b > 262143) b = 262143;

            rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
        }
    }
}

void Java_com_jecfbagsx_android_utils_YUV420SP_decode(
		JNIEnv* env,
		jobject thiz,
		jintArray rgbArray,
		jbyteArray yuv420spArray,
		jint width,
		jint height
		)
{
    jint* rgb = (*env)->GetIntArrayElements(env, rgbArray, NULL);
    jbyte* yuv420sp = (*env)->GetByteArrayElements(env, yuv420spArray, NULL);

    decodeYUV420SP(rgb, yuv420sp, width, height);

	(*env)->ReleaseIntArrayElements(env, rgbArray, rgb, 0);
	(*env)->ReleaseByteArrayElements(env, yuv420spArray, yuv420sp, 0);
}
