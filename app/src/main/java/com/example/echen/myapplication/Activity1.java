package com.example.echen.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.echen.androidcommon.Media.AudioProvider;
import com.echen.androidcommon.Media.IMediaProvider;
import com.echen.androidcommon.Media.ImageProvider;
import com.echen.androidcommon.Media.VideoProvider;
import com.example.echen.myapplication.Model.People;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/1/19.
 */
public class Activity1 extends Activity {
    private ArrayList<People> peoples;
    private ListView myListView;
    private ArrayList<String> list=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_1);
//        myListView = (ListView)findViewById(R.id.myListView);
//        peoples = new ArrayList<People>();
//        People eric = new People();
//        eric.setName("Eric");
//        eric.setAge(32);
//        peoples.add(eric);
//        People alan = new People();
//        alan.setName("Alan");
//        alan.setAge(12);
//        peoples.add(alan);
//        ArrayAdapter<People> adapter = new ArrayAdapter<People>(this, android.R.layout.simple_expandable_list_item_1, peoples);
////        peoples<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,getListData());
//        myListView.setAdapter(adapter);

        IMediaProvider providerImage = new ImageProvider(this);
        List<?> list1 = providerImage.getList();
        IMediaProvider providerVideo = new VideoProvider(this);
        List<?> list2 = providerVideo.getList();
        IMediaProvider providerAudio = new AudioProvider(this);
        List<?> list3 = providerAudio.getList();
        int i =0;
    }

    public List<String> getListData(){
        list.add("Item 1");
        list.add("Item 2");
        list.add("Item 3");
        list.add("Item 4");
        list.add("Item 5");
        return list;
    }
}
