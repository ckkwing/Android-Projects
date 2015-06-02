package com.echen.wisereminder;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.echen.wisereminder.Adapter.ReminderListAdapter;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.Model.ReminderActionType;

import java.util.List;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Category currentCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        createFloatAddButtonView();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        if (number <= 0)
            return;
        List<Category> categories = DataManager.getInstance().getCategories(false);
        if (number > categories.size())
            return;
        currentCategory = categories.get(number - 1);
        if (null == currentCategory)
            return;
        mTitle = currentCategory.getName();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private LinearLayout mFloatLayout;
    private Button mFloatView;
    private void createFloatAddButtonView()
    {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = this.getWindowManager();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        wmParams.x = 20;
        wmParams.y = 20;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = this.getLayoutInflater();
        mFloatLayout = (LinearLayout)inflater.inflate(R.layout.float_additem_view, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        mFloatView = (Button)mFloatLayout.findViewById(R.id.float_btn_add);
//        //绑定触摸移动监听
//        mFloatView.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                wmParams.x = (int) event.getRawX() - mFloatLayout.getWidth() / 2;
//                //25为状态栏高度
//                wmParams.y = (int) event.getRawY() - mFloatLayout.getHeight() / 2 - 40;
//                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
//                return false;
//            }
//        });


        mFloatView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                if (null != currentCategory) {
                    Intent intent = new Intent(MainActivity.this, ReminderCreationActivity.class);
                    intent.putExtra(ConsistentString.PARAM_CATEGORY_ID, currentCategory.getId());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final int REFRESH_COMPLETE = 0X110;
        private ReminderListAdapter listAdapter = null;
        private SwipeRefreshLayout swipeLayout;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();
            if (bundle != null) {
//                mArgument = bundle.getString(ARGUMENT);
//                Intent intent = new Intent();
//                intent.putExtra(RESPONSE, "good");
//                getActivity().setResult(ListTitleFragment.REQUEST_DETAIL, intent);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            swipeLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_ly);
            swipeLayout.setOnRefreshListener(this);
            swipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                    android.R.color.holo_orange_light, android.R.color.holo_red_light);

            Bundle bundle = getArguments();

            if (null != bundle) {
                int position = bundle.getInt(ARG_SECTION_NUMBER);
                if (position > 0 && position <= DataManager.getInstance().getCategories(false).size()) {
                    Category category = DataManager.getInstance().getCategories(false).get(position - 1);
                    if (null != category) {
//                        TextView txtName = (TextView) rootView.findViewById(R.id.section_label);
//                        txtName.setText(category.getName());
                        List<Reminder> reminders =DataManager.getInstance().getRemindersByCategoryID(category.getId());
                        ListView reminderList = (ListView)rootView.findViewById(R.id.reminderList);
                        listAdapter = new ReminderListAdapter(inflater, reminders);
                        reminderList.setAdapter(listAdapter);
                        reminderList.setOnItemClickListener(onItemClickListener);
                    }
                }
            }
            return rootView;
        }

        private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                final TextView content=(TextView) view.findViewById(R.id.txtReminderName);
//                if (null == content)
//                    return;
//                content.setText("哈哈， 你选中我了");
                Reminder reminder = (Reminder)parent.getItemAtPosition(position);
                if (null == reminder)
                    return;
                    Intent intent = new Intent(PlaceholderFragment.this.getActivity(), ReminderEditActivity.class);
                    intent.putExtra(ConsistentString.PARAM_REMINDER_ID, reminder.getId());
                    getActivity().startActivity(intent);
            }
        };

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void onRefresh() {
            mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
        }

        private Handler mHandler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                switch (msg.what)
                {
                    case REFRESH_COMPLETE:
                        listAdapter.notifyDataSetChanged();
                        swipeLayout.setRefreshing(false);
                        break;

                }
            };
        };
    }

}
