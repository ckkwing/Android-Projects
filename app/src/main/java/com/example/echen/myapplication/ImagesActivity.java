package com.example.echen.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.echen.androidcommon.Media.IMediaProvider;
import com.echen.androidcommon.Media.Image;
import com.echen.androidcommon.Media.ImageProvider;
import com.example.echen.myapplication.Adapter.ImageAdapter;

import java.util.List;

/**
 * Created by echen on 2015/1/30.
 */
public class ImagesActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_images);
        ListView lvImage = (ListView)findViewById(R.id.lvImage);
        IMediaProvider providerImage = new ImageProvider(this);
        List<?> list = providerImage.getList();
//        ArrayAdapter<?> imageArrayAdapter = new ArrayAdapter<Image>(this, android.R.layout.simple_expandable_list_item_1,(List<Image>)list);
//        lvImage.setAdapter(imageArrayAdapter);

        ImageAdapter adapter = new ImageAdapter(this, (List<Image>)list);
        lvImage.setAdapter(adapter);

//        lvImage.setOnItemClickListener(onItemClickListener);
//        lvImage.setOnItemSelectedListener(onItemSelectedListener);


    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setTitle(parent.getItemAtPosition(position).toString());
        }
    };

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setTitle(parent.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            setTitle("");
        }
    };
}
