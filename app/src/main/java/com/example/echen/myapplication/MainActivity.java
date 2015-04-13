package com.example.echen.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final int PICK_CONTACT_SUBACTIVITY = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this,"MainActivity onCreate", Toast.LENGTH_SHORT).show();

        TextView textView = (TextView)findViewById(R.id.txtMain);
        if (null != textView) {
            textView.setText("Text has been changed!");
        }

        Button btnActivity1 = (Button)findViewById(R.id.btnGoActivity1);
        if (null != btnActivity1)
            btnActivity1.setOnClickListener(btnActivity1Listener);

        Button btnContact = (Button)findViewById(R.id.btnContact);
        if (null != btnContact)
            btnContact.setOnClickListener(new Button.OnClickListener(){
                public void onClick(View view)
                {
//                    Uri uri=Uri.parse("content://contacts/people");
//                    Intent intent = new Intent(Intent.ACTION_PICK,uri);
//                    startActivityForResult(intent, PICK_CONTACT_SUBACTIVITY);
                    Intent intent = new Intent(MainActivity.this,ImagesActivity.class);
                    startActivity(intent);
                }
            });

//        Intent intent = getIntent();
//        String dataPath = intent.getData().toString();
//        final Uri data = Uri.parse(dataPath+"people/");
//        final Uri data = Uri.parse("content://contacts/people");
//        final Cursor c = managedQuery(data,null,null,null,null);
//        String[] from = new String[]{Contacts.People.NAME};
//        int[] to = new int[]{R.id.itemTextView};
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.contactlistitem,c,from,to);
//        ListView lst = (ListView)findViewById(R.id.contactListView);
//        lst.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Button.OnClickListener btnActivity1Listener = new Button.OnClickListener(){
        public void onClick(View v)
        {
            Intent intent = new Intent(MainActivity.this, Activity1.class);
            startActivity(intent);
        }
    };

    public void btnDialOnClick(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:13516818216"));
        startActivity(intent);
    }

    public void btnShowDialogOnClick(View view){
        showDialog(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    CharSequence[] items = { "Google", "Apple", "Microsoft" };
    boolean[] itemsChecked = new boolean [items.length];
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id)
        {
            case 0:
            {
                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle("This is a dialog with some simple text...")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(),
                                        "OK clicked!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(),
                                        "Cancel clicked!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setMultiChoiceItems(items, itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                Toast.makeText(getBaseContext(),
                                        items[which] + (isChecked ? " checked!":" unchecked!"),
                                Toast.LENGTH_SHORT).show();
                            }
                        }).create();
            }
        }
        return null;
    }
}
