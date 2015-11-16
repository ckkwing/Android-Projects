package com.echen.wisereminder;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.echen.androidcommon.DateTime;
import com.echen.androidcommon.DeviceHelper;
import com.echen.wisereminder.Adapter.ReminderListAdapter;
import com.echen.wisereminder.Adapter.SwipeReminderListAdapter;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.IListItem;
import com.echen.wisereminder.Model.IReminderParent;
import com.echen.wisereminder.Model.Subject;
import com.echen.wisereminder.Receiver.AlarmReceiver;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;

import java.util.List;

import uicommon.customcontrol.BaseActivity;


public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment m_NavigationDrawerFragment;
    private int m_currentGroupPosition = -1;
    private int m_currentChildPosition = -1;
    private int m_exitTimes = 0;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence m_Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_NavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        m_Title = getTitle();


        // Set up the drawer.
        m_NavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        createFloatAddButtonView();
        createPeriodicAlarm();
    }

    @Override
    protected void onDestroy() {
        m_exitTimes = 0;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Fragment fragmentMain = getFragmentManager().findFragmentById(R.id.container);
        if (null != fragmentMain)
        {
            View rootView = fragmentMain.getView();
            if (null != rootView)
            {
                SwipeListView swipeListView = (SwipeListView)rootView.findViewById(R.id.swipeReminderList);
                swipeListView.closeOpenedItems();
                if (0 == m_exitTimes) {
                    m_exitTimes++;
                    Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    private void createPeriodicAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(ConsistentString.ACTION_BROADCAST_PERIODICALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        DateTime triggerDate = null;
        DateTime today = DateTime.today();
        today.setHour(9);
        DateTime now = DateTime.now();
        if (today.toUTCLong() < now.toUTCLong())
            triggerDate = today.addDays(1);
        else
            triggerDate = today;

        //for test
        triggerDate = DateTime.now();
        triggerDate = triggerDate.addSeconds(30);
        //for test


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerDate.getLocalCalendar().getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onNavigationDrawerItemSelected(int groupPosition, int childPosition) {
        m_currentGroupPosition = groupPosition;
        m_currentChildPosition = childPosition;
        navigateMainActivity(m_currentGroupPosition, m_currentChildPosition);
    }

    private void navigateMainActivity(int groupPosition, int childPosition) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(this, groupPosition, childPosition))
                .commit();
    }

    public void onSectionAttached(int groupPosition, int childPosition) {
        if (groupPosition < 0)
            return;

        Subject currentSubject = DataManager.getInstance().getSubjects().get(groupPosition);
        if (null != currentSubject) {
            if (-1 == childPosition)
                m_Title = currentSubject.getName();
            else {
                IListItem item = currentSubject.getChildren().get(childPosition);
                m_Title = item.getName();
            }
        } else
            m_Title = "";
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(m_Title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!m_NavigationDrawerFragment.isDrawerOpen()) {
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

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == R.id.action_notification) {
            Intent intent = new Intent(MainActivity.this, AppNotificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            this.startActivityForResult(intent, ConsistentParameter.REQUEST_CODE_MAINACTIVITY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragmentMain = getFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (null == fragmentMain)
            return;
        Activity rootActivity = fragmentMain.getActivity();
        if (null == rootActivity)
            return;
//        SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) rootActivity.findViewById(R.id.swipe_ly);
//        if (null == swipeLayout)
//            return;
        if (requestCode != ConsistentParameter.REQUEST_CODE_MAINACTIVITY)
            return;
        switch (resultCode) {
            case ConsistentParameter.RESULT_CODE_REMINDERCREATIONACTIVITY: {
                Bundle bundle = data.getBundleExtra(ConsistentString.BUNDLE_UNIT);
                if (null != bundle) {
                    boolean bRel = bundle.getBoolean(ConsistentString.RESULT_BOOLEAN);
                    if (bRel) {
//                        swipeLayout.setRefreshing(true);
                        navigateMainActivity(m_currentGroupPosition, m_currentChildPosition);
                    }
                }
            }
            break;
            case ConsistentParameter.RESULT_CODE_REMINDEREDITIONACTIVITY: {
                Bundle bundle = data.getBundleExtra(ConsistentString.BUNDLE_UNIT);
                if (null != bundle) {
                    boolean bRel = bundle.getBoolean(ConsistentString.RESULT_BOOLEAN);
                    if (bRel) {
//                        swipeLayout.setRefreshing(true);
                        navigateMainActivity(m_currentGroupPosition, m_currentChildPosition);
                    }
                }
            }
            break;
        }
    }

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private LinearLayout mFloatLayout;
    private Button mFloatView;

    private void createFloatAddButtonView() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = this.getWindowManager();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        wmParams.x = 20;
        wmParams.y = 20;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = this.getLayoutInflater();
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_additem_view, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        mFloatView = (Button) mFloatLayout.findViewById(R.id.float_btn_add);
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


        mFloatView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_rotate_addbutton));
        mFloatView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, ReminderCreationActivity.class);
                startActivityForResult(intent, ConsistentParameter.REQUEST_CODE_MAINACTIVITY);

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
        private static final String ARG_SECTION_GROUP_NUMBER = "section_group_number";
        private static final String ARG_SECTION_CHILD_NUMBER = "section_child_number";
        private static final int REFRESH_COMPLETE = 0X110;
        private ReminderListAdapter m_listAdapter = null;
        private SwipeReminderListAdapter m_swipeListAdapter = null;
        private SwipeRefreshLayout m_swipeLayout;
        private IReminderParent m_selectedItem;
        private static Context m_context;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(Context context, int groupPosition, int childPosition) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            m_context = context;
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_GROUP_NUMBER, groupPosition);
            args.putInt(ARG_SECTION_CHILD_NUMBER, childPosition);
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
//            m_swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_ly);
//            m_swipeLayout.setOnRefreshListener(this);
//            m_swipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
//                    android.R.color.holo_orange_light, android.R.color.holo_red_light);

            Bundle bundle = getArguments();
            if (null != bundle) {
                int groupPosition = bundle.getInt(ARG_SECTION_GROUP_NUMBER);
                int childPosition = bundle.getInt(ARG_SECTION_CHILD_NUMBER);
                m_selectedItem = null;
                List<Subject> subjects = DataManager.getInstance().getSubjects();
                Subject currentSubject = subjects.get(groupPosition);
                if (null == currentSubject)
                    return rootView;

                if (childPosition >= 0) {
                    m_selectedItem = (IReminderParent) currentSubject.getChildren().get(childPosition);
                } else {
                    m_selectedItem = currentSubject;
                }

                if (null != m_selectedItem) {
//                    ListView reminderList = (ListView) rootView.findViewById(R.id.reminderList);
//                    m_listAdapter = new ReminderListAdapter(m_context, m_selectedItem.getReminders());
//                    reminderList.setAdapter(m_listAdapter);

                    SwipeListView swipeListView = (SwipeListView)rootView.findViewById(R.id.swipeReminderList);
                    m_swipeListAdapter = new SwipeReminderListAdapter(m_context, m_selectedItem.getReminders(), swipeListView);
                    swipeListView.setAdapter(m_swipeListAdapter);
                    swipeListView.setSwipeListViewListener(new RemindersSwipeListViewListener());
                    int deviceWidth = DeviceHelper.getDisplayMetrics(m_context).widthPixels;
                    int offSet = deviceWidth/3;
                    swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
                    swipeListView.setOffsetLeft(offSet);
//                    swipeListView.setOffsetRight(offSet);
                    swipeListView.setAnimationTime(1);
                    swipeListView.setSwipeOpenOnLongPress(false);
                }
            }


            return rootView;
        }

        class RemindersSwipeListViewListener extends BaseSwipeListViewListener
        {

            @Override
            public void onMove(int position, float x) {
                super.onMove(position, x);
            }

            @Override
            public void onClickFrontView(int position) {
                super.onClickFrontView(position);
            }

            @Override
            public void onClickBackView(int position) {
                super.onClickBackView(position);
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                super.onDismiss(reverseSortedPositions);
                if (reverseSortedPositions.length > 0)
                    m_swipeListAdapter.notifyDataSetChanged();
//                for (int position : reverseSortedPositions) {
//                    Log.i("lenve", "position--:"+position);
//                    testData.remove(position);
//                }
//                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_GROUP_NUMBER),
                    getArguments().getInt(ARG_SECTION_CHILD_NUMBER));
        }

        @Override
        public void onRefresh() {
//            mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);

//            m_listAdapter.updateSource(m_selectedItem.getReminders());
            m_swipeListAdapter.updateSource(m_selectedItem.getReminders());
            mHandler.sendEmptyMessage(REFRESH_COMPLETE);
        }

        private Handler mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case REFRESH_COMPLETE: {
                        m_swipeLayout.setRefreshing(false);
                    }
                    break;

                }
            }

            ;
        };
    }

}
