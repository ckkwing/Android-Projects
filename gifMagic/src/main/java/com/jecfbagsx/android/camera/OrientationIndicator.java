package com.jecfbagsx.android.camera;

import android.content.Context;
import android.util.Log;
import android.view.OrientationEventListener;

public class OrientationIndicator extends OrientationEventListener {
	private static final String TAG = OrientationIndicator.class.getSimpleName();

	public interface Notification {
		public void reportOrientationChange(boolean compensation, int previous, int current);
	};
	
	public interface Callback {
		public int getStardardOrientation();
	};
	
	private Notification mNotification;
	private Callback mCallback;

	private int mOrientation = ORIENTATION_UNKNOWN;
    private int mCompensation = 0;
	
	public OrientationIndicator(Context context, Notification notification, Callback callback) {
		super(context);
		
		mNotification = notification;
		mCallback = callback;
	}
	
	public int getOrientation() {
		return mOrientation;
	}
	
	public int getCompensation() {
		return mCompensation;
	}

	public void onOrientationChanged(int orientation) {
        if (orientation == ORIENTATION_UNKNOWN) 
        	return;
        
        orientation = roundOrientation(orientation);

    	int previousOrientation = mOrientation;
    	int currentOrientation = orientation;

        if (previousOrientation != currentOrientation) {
        	mOrientation = currentOrientation;
        	
    		Log.i(TAG, "orientation change from " + previousOrientation + " to " + currentOrientation);

        	mNotification.reportOrientationChange(false, previousOrientation, currentOrientation);
        
            int previousCompensation = mCompensation;
            int currentCompensation = mOrientation + mCallback.getStardardOrientation();        
            if (previousCompensation != currentCompensation) {
                mCompensation = currentCompensation;
                
        		Log.i(TAG, "compensation change from " + previousCompensation + " to " + currentCompensation);

            	mNotification.reportOrientationChange(true, previousCompensation, currentCompensation);
            }
        }
	}
	
    private static int roundOrientation(int orientation) {
        return ((orientation + 45) / 90 * 90) % 360;
    }
}