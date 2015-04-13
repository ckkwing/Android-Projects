package com.jecfbagsx.android.gifmanage;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GifView extends View implements GifAction {

	
	private GifDecoder gifDecoder = null;
	
	private Bitmap currentImage = null;

	private boolean isRun = true;

	private boolean pause = false;

	private int showWidth = -1;
	private int showHeight = -1;
	private Rect rect = null;

	private DrawThread drawThread = null;

	private GifImageType animationType = GifImageType.SYNC_DECODER;

	
	public enum GifImageType {
		
		WAIT_FINISH(0),
		
		SYNC_DECODER(1),
		
		COVER(2);

		GifImageType(int i) {
			nativeInt = i;
		}

		final int nativeInt;
	}

	public GifView(Context context) {
		super(context);

	}

	public GifView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	
	private void setGifDecoderImage(byte[] gif) {
		if (gifDecoder != null) {
			gifDecoder.free();
			gifDecoder = null;
		}
		gifDecoder = new GifDecoder(gif, this);
		gifDecoder.start();
	}

	
	private void setGifDecoderImage(InputStream is) {
		if (gifDecoder != null) {
			gifDecoder.free();
			gifDecoder = null;
		}
		gifDecoder = new GifDecoder(is, this);
		gifDecoder.start();
	}

	
	public void setGifImage(byte[] gif) {
		setGifDecoderImage(gif);
	}

	
	public void setGifImage(InputStream is) {
		setGifDecoderImage(is);
	}

	
	public void setGifImage(int resId) {
		Resources r = this.getResources();
		InputStream is = r.openRawResource(resId);
		setGifDecoderImage(is);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (gifDecoder == null)
			return;
		if (currentImage == null) {
			currentImage = gifDecoder.getImage();
		}
		if (currentImage == null) {
			return;
		}
		int saveCount = canvas.getSaveCount();
		canvas.save();
		canvas.translate(getPaddingLeft(), getPaddingTop());
		if (showWidth == -1) {
			canvas.drawBitmap(currentImage, 0, 0, null);
		} else {
			canvas.drawBitmap(currentImage, null, rect, null);
		}
		canvas.restoreToCount(saveCount);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int pleft = getPaddingLeft();
		int pright = getPaddingRight();
		int ptop = getPaddingTop();
		int pbottom = getPaddingBottom();

		int widthSize;
		int heightSize;

		int w;
		int h;

		if (gifDecoder == null) {
			w = 1;
			h = 1;
		} else {
			w = gifDecoder.width;
			h = gifDecoder.height;
		}

		w += pleft + pright;
		h += ptop + pbottom;

		w = Math.max(w, getSuggestedMinimumWidth());
		h = Math.max(h, getSuggestedMinimumHeight());

		widthSize = resolveSize(w, widthMeasureSpec);
		heightSize = resolveSize(h, heightMeasureSpec);

		setMeasuredDimension(widthSize, heightSize);
	}

	
	public void showCover() {
		if (gifDecoder == null)
			return;
		pause = true;
		currentImage = gifDecoder.getImage();
		invalidate();
	}

	public void showAnimation() {
		if (pause) {
			pause = false;
		}
	}

	
	public void setGifImageType(GifImageType type) {
		if (gifDecoder == null)
			animationType = type;
	}

	
	public void setShowDimension(int width, int height) {
		if (width > 0 && height > 0) {
			showWidth = width;
			showHeight = height;
			rect = new Rect();
			rect.left = 0;
			rect.top = 0;
			rect.right = width;
			rect.bottom = height;
		}
	}
	
	public void stopGifDecoder()
	{
		isRun = false;
		if (gifDecoder != null)
		{
			gifDecoder.stopRequest();
		}
	}

	public void parseOk(boolean parseStatus, int frameIndex) {
		if (parseStatus) {
			if (gifDecoder != null) {
				Log.e("----parseOk func----", "parseing....");
				switch (animationType) {
				case WAIT_FINISH:
					if (frameIndex == -1) {
						if (gifDecoder.getFrameCount() > 1) { // 当帧数大于1时，�?�动动画线程
							DrawThread dt = new DrawThread();
							dt.start();
						} else {
							reDraw();
						}
					}
					break;
				case COVER:
					if (frameIndex == 1) {
						currentImage = gifDecoder.getImage();
						reDraw();
					} else if (frameIndex == -1) {
						if (gifDecoder.getFrameCount() > 1) {
							if (drawThread == null) {
								drawThread = new DrawThread();
								drawThread.start();
							}
						} else {
							reDraw();
						}
					}
					break;
				case SYNC_DECODER:
					if (frameIndex == 1) {
						currentImage = gifDecoder.getImage();
						reDraw();
					} else if (frameIndex == -1) {
						reDraw();
					} else {
						if (drawThread == null) {
							drawThread = new DrawThread();
							drawThread.start();
						}
					}
					break;
				}

			} else {
				Log.e("gif", "parse error");
			}

		}
	}

	private void reDraw() {
		if (redrawHandler != null) {
			Message msg = redrawHandler.obtainMessage();
			redrawHandler.sendMessage(msg);
		}
	}

	private Handler redrawHandler = new Handler() {
		public void handleMessage(Message msg) {
			invalidate();
		}
	};

	
	private class DrawThread extends Thread {
		public void run() {
			if (gifDecoder == null) {
				return;
			}
			while (isRun) {
				if (pause == false) {
					// if(gifDecoder.parseOk()){
					GifFrame frame = gifDecoder.next();
					currentImage = frame.image;
					long sp = frame.delay;
					if (redrawHandler != null) {
						Message msg = redrawHandler.obtainMessage();
						redrawHandler.sendMessage(msg);
						SystemClock.sleep(sp);
					} else {
						break;
					}
					// }else{
					// currentImage = gifDecoder.getImage();
					// break;
					// }
				} else {
					SystemClock.sleep(10);
				}
				
			}
		}
	}

}
