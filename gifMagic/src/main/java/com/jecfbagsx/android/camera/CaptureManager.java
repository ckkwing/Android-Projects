package com.jecfbagsx.android.camera;

import java.util.LinkedList;

import android.os.Handler;
import android.os.Message;

public class CaptureManager {
	private static final String TAG = CaptureManager.class.getSimpleName();
	private static final int RESPONSE_MESSAGE = 219;
	
	public interface Notification {
		public void reportState(int capturing, int storing, int finished);
		public void reportFinish(String path, boolean capture);
	};
	
	private LinkedList<CaptureJob> mCapturingJobs = new LinkedList<CaptureJob>();
	private LinkedList<CaptureJob> mStoringJobs = new LinkedList<CaptureJob>();
	private LinkedList<CaptureJob> mFinishedJobs = new LinkedList<CaptureJob>();

	private CaptureStore mCaptureStore = new CaptureStore(
			new CaptureStore.Notification() {
				public void reportFinish(CaptureJob job) {
					if (mHandler != null) {
						Message message = Message.obtain();
						message.what = RESPONSE_MESSAGE;
						message.obj = job;
						mHandler.sendMessage(message);
					}
				}
			});
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		public boolean handleMessage(Message msg) {
			if (msg.what != RESPONSE_MESSAGE || msg.obj == null)
				return false;

			finishJob((CaptureJob)msg.obj);

			return true;
		}
	});
	
	private Notification mNotification = null;
	
	public CaptureManager(Notification notification) {
		mNotification = notification;
	}
	
	public void startStore() {
		mCaptureStore.startup();
	}
	
	public void stopStore() {
		mCaptureStore.shutdown();
	}
		
	public void addJob(CaptureType type, String filename, int speed, int quality) {
		CaptureJob job = new CaptureJob();
		job.type = type;
		job.filename = filename;
		job.speed = speed;
		job.quality = quality;
		
		addJob(job);
	}
	
	public void abort() {
		doAbort();
	}
	
	public void init() {
		onInit();
	}
	
	public boolean hasCapturingJobs() {
		return !mCapturingJobs.isEmpty();
	}
	
	public boolean receiveData(CaptureData data) {
		if (data == null)
			return false;

		CaptureJob job = nextCapturingJob();

		if (job == null)
			return false;

		mCaptureStore.request(data, job);

		return true;
	}
	
	private boolean mAborted;
	
	private void addJob(CaptureJob job) {
		mAborted = false;
		
		job.state = CaptureJob.State.capturing;
		mCapturingJobs.addLast(job);

		onChanged();
	}
	
	private CaptureJob nextCapturingJob() {
		if (mCapturingJobs.isEmpty())
			return null;

		CaptureJob job = mCapturingJobs.getFirst();
		job.speed--;

		if (job.speed != 0)
			return null;

		job.state = CaptureJob.State.storing;
		mCapturingJobs.removeFirst();
		mStoringJobs.addLast(job);

		onChanged();
		
		onCaptured(job.filename);

		if (mCapturingJobs.isEmpty())
			onFinished(true);

		return job;
	}

	private void finishJob(CaptureJob job) {
		if (mAborted)
			return;
		
		job.state = CaptureJob.State.finished;
		mFinishedJobs.addLast(job);
		mStoringJobs.remove(job);

		onChanged();
		
		onStored(job.filename);

		if (mCapturingJobs.isEmpty() && mStoringJobs.isEmpty())
			onFinished(false);
	}
	
	private void doAbort() {
		mAborted = true;
		clearAllJobs();
	}
	
	private void onInit() {
		if (mNotification != null)
			mNotification.reportState(mCapturingJobs.size(), mStoringJobs.size(), mFinishedJobs.size());
	}	

	private void onChanged() {
		if (mNotification != null)
			mNotification.reportState(mCapturingJobs.size(), mStoringJobs.size(), mFinishedJobs.size());
	}

	private void onFinished(boolean capture) {
		if (mNotification != null)
			mNotification.reportFinish(null, capture);
	}
	
	private void onCaptured(String path) {
		if (mNotification != null)
			mNotification.reportFinish(path, true);
	}

	private void onStored(String path) {
		if (mNotification != null)
			mNotification.reportFinish(path, false);
	}	

	private void clearAllJobs() {
		clearCaptureJobs();
		clearStoreJobs();
	}

	private void clearCaptureJobs() {
		mCapturingJobs.clear();
	}

	private void clearStoreJobs() {
		mStoringJobs.clear();
		mFinishedJobs.clear();
	}
};
