package com.jecfbagsx.android.utils;

import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.jecfbagsx.android.gifmagic.GalleryShowcase;

public class GestureProcessor implements OnGestureListener {

	private static final float FLING_MIN_DISTANCE = 100;
	private static final float FLING_MIN_VELOCITY = 100;
	private Context context;
	private Activity activity;

	public GestureProcessor(Context context, Activity activity) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.activity = activity;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// Toast.makeText(context, "enter onFling", Toast.LENGTH_SHORT).show();
		// TODO Auto-generated method stub
		// 参数解释：
		// e1：第1个ACTION_DOWN MotionEvent
		// e2：最后一个ACTION_MOVE MotionEvent
		// velocityX：X轴上的移动速度，像素/秒
		// velocityY：Y轴上的移动速度，像素/秒
		// 触发条件 ：
		// X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {

			// Fling left
			((GalleryShowcase) activity).setNextPage();
			// Toast.makeText(context, "Fling Left", Toast.LENGTH_SHORT).show();
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {

			// Fling right
			((GalleryShowcase) activity).setPreviousPage();
			// Toast.makeText(context, "Fling Right",
			// Toast.LENGTH_SHORT).show();
		}

		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
