package com.echen.arthur;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.echen.androidcommon.Media.Image;
import com.echen.arthur.ActivityAdapter.ImageAdapter;
import com.echen.arthur.Utility.StringConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/2/17.
 */
public class FilesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        Intent intent = getIntent();
        GridView gridView = (GridView)findViewById(R.id.imagesContainer);
        try {
            // Get the Bundle Object
            Bundle bundleObject = getIntent().getExtras();

            // Get ArrayList Bundle
            List<Image> images = (ArrayList<Image>) bundleObject.getSerializable(StringConstant.IMAGES);
//            ImageAdapter adapter = new ImageAdapter(this, DataManager.getInstance().getImages());
            ImageAdapter adapter = new ImageAdapter(this, images);
            gridView.setAdapter(adapter);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }


}
