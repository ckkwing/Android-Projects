package com.echen.wisereminder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

import com.echen.androidcommon.DeviceHelper;
import com.echen.androidcommon.Utility.ImageUtility;
import com.echen.wisereminder.Utility.AppPathHelper;
import com.edmodo.cropper.CropImageView;

import be.webelite.ion.IconView;

/**
 * Created by echen on 2015/11/3.
 */
public class CreateAvatarActivityEX extends Activity {

    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final int ROTATE_NEGATIVE_NINETY_DEGREES = -90;
    private final int MINSIDELENGTH = -1;
    private int MAXNUMOFPIXELS = 360 * 360;
    private Bitmap croppedImage;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_createavatar_ex);
        Intent intent = getIntent();
        String uriString = intent.getStringExtra(ConsistentString.PARAM_URI);
        Uri uri = Uri.parse(uriString);

        // Initialize components of the app
        final CropImageView cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        try {
            DisplayMetrics displayMetrics = DeviceHelper.getDisplayMetrics(this);
            int baseLinePixels = displayMetrics.heightPixels / 2;
            MAXNUMOFPIXELS = baseLinePixels * baseLinePixels;
            String imageUrl = ImageUtility.getPath(this.getContentResolver(), uri);
            Bitmap bmp = ImageUtility.autoLoadImageFromUrl(imageUrl, MINSIDELENGTH, MAXNUMOFPIXELS);
            cropImageView.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        IconView rotateLeft = (IconView) findViewById(R.id.rotateLeft);
        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_NEGATIVE_NINETY_DEGREES);
            }
        });
        IconView rotateRight = (IconView) findViewById(R.id.rotateRight);
        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
            }
        });

        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);

        IconView cropOK = (IconView) findViewById(R.id.cropOK);
        cropOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                croppedImage = cropImageView.getCroppedImage();
                boolean bRel = ImageUtility.saveBitmapAsPng(croppedImage, AppPathHelper.getAvatarFilePath());
                Bundle bundle = new Bundle();
                bundle.putBoolean(ConsistentString.RESULT_BOOLEAN, bRel);
                Intent intent = getIntent();
                intent.putExtra(ConsistentString.BUNDLE_UNIT, bundle);
                setResult(ConsistentParameter.RESULT_CODE_CREATEAVATARACTIVITY, intent); //set resultCode
                finish();
            }
        });

        IconView iconBack = (IconView) findViewById(R.id.iconBack);
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ConsistentString.RESULT_BOOLEAN, false);
                Intent intent = getIntent();
                intent.putExtra(ConsistentString.BUNDLE_UNIT, bundle);
                setResult(ConsistentParameter.RESULT_CODE_CREATEAVATARACTIVITY, intent); //set resultCode
                finish();
            }
        });

    }
}
