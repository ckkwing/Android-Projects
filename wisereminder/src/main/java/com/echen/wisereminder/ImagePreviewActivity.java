package com.echen.wisereminder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.echen.androidcommon.Utility.ImageUtility;
import com.echen.wisereminder.Utility.AppPathHelper;

/**
 * Created by echen on 2015/10/30.
 */
public class ImagePreviewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview);
        Intent intent = getIntent();
        ImageView imageView = (ImageView)findViewById(R.id.imgPreview);
        Bitmap bitmapAvatar = ImageUtility.getDiskBitmap(AppPathHelper.getAvatarFilePath());
        imageView.setImageBitmap(bitmapAvatar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
