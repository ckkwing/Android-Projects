package com.echen.arthur.Data;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.Objects;

/**
 * Created by echen on 2015/2/15.
 */
public class LoadDataAsyncTask extends AsyncTask<String, Integer, Boolean> {

    private ProgressDialog progressDialog = null;
    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     */
    public LoadDataAsyncTask(ProgressDialog dialog) {
        super();
        progressDialog = dialog;
    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Boolean doInBackground(String... params) {
        DataManager.getInstance().getImagesByForce();
        publishProgress(33);
        DataManager.getInstance().getVideosByForce();
        publishProgress(66);
        DataManager.getInstance().getAudiosByForce();
        publishProgress(3100);
        return true;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param aBoolean The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked.
     * The specified values are the values passed to {@link #publishProgress}.
     *
     * @param values The values indicating progress.
     * @see #publishProgress
     * @see #doInBackground
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
