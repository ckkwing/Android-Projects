package com.echen.wisereminder;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.echen.androidcommon.DeviceHelper;
import com.echen.androidcommon.FileHelper;
import com.echen.androidcommon.Utility.ImageUtility;
import com.echen.wisereminder.Adapter.ExpandableSubjectListAdapter;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Subject;
import com.echen.wisereminder.Profile.DefaultUser;
import com.echen.wisereminder.Profile.ProfileManager;
import com.echen.wisereminder.Profile.User;
import com.echen.wisereminder.Utility.AppPathHelper;

import java.util.ArrayList;
import java.util.List;

import be.webelite.ion.Icon;
import uicommon.customcontrol.Listview.ExpandableListViewForScrollView;
import uicommon.customcontrol.Utility;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerExpandableListView;
    private LinearLayout mProfileWrapper;
    private de.hdodenhof.circleimageview.CircleImageView m_circleImageView;
    private TextView mUserName;
    private View mFragmentContainerView;
    private View mSettings;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private ExpandableSubjectListAdapter subjectListAdapter = null;
    private List<Subject> mSubjectList = new ArrayList<>();

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSubjectList = DataManager.getInstance().getSubjects();
        subjectListAdapter = new ExpandableSubjectListAdapter(getActionBar().getThemedContext(), mSubjectList);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition, -1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;//Fix click pass event to activity which under this fragment
//            }
//        });

        mProfileWrapper = (LinearLayout) view.findViewById(R.id.profileWrapper);
        mProfileWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != ProfileManager.getInstance().getUser()) {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(intent, ConsistentParameter.REQUEST_CODE_DRAWERFRAMENT);
                }
            }
        });

        m_circleImageView = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.profile_image);
        m_circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
                startActivity(intent);
            }
        });
        updateAvatar();

        mUserName = (TextView) view.findViewById(R.id.txtUserName);
        String userName = getString(R.string.profile_press_to_login);
        if (null != ProfileManager.getInstance().getUser())
            userName = ProfileManager.getInstance().getUser().getName();
        mUserName.setText(userName);

        mSettings = (TextView)view.findViewById(R.id.settings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        mDrawerExpandableListView = (ExpandableListView) view.findViewById(R.id.lstSubjects);
        mDrawerExpandableListView.setAdapter(subjectListAdapter);
        Utility.setListViewHeightBasedOnChildren(mDrawerExpandableListView);
        mDrawerExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < mSubjectList.size(); i++) {
                    if (groupPosition != i) {
                        mDrawerExpandableListView.collapseGroup(i);
                    }
                }
                Utility.setListViewHeightBasedOnChildren(mDrawerExpandableListView);
            }
        });
        mDrawerExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Utility.setListViewHeightBasedOnChildren(mDrawerExpandableListView);
            }
        });
        mDrawerExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Subject subject = mSubjectList.get(groupPosition);
                if (subject.getType() == Subject.Type.Categories)
                    return false;
                else {
                    selectItem(groupPosition, -1);
                    return true;
                }
            }
        });
        mDrawerExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                selectItem(groupPosition, childPosition);
                return false;
            }
        });
//        DisplayMetrics displayMetrics = DeviceHelper.getDisplayMetrics(this.getActivity());
//        ViewGroup.LayoutParams layoutParams = mDrawerExpandableListView.getLayoutParams();
//        layoutParams.height = displayMetrics.heightPixels/2;
//        mDrawerExpandableListView.setLayoutParams(layoutParams);


        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                ExpandableListView elv = (ExpandableListView)drawerView.findViewById(R.id.lstSubjects);
                elv.setFocusable(false);
                drawerView.scrollTo(0,0);

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int groupPosition, int childPosition) {
        mCurrentSelectedPosition = groupPosition;
        if (mDrawerExpandableListView != null) {
//            mDrawerExpandableListView.setItemChecked(groupPosition, true);
            mDrawerExpandableListView.setSelectedGroup(groupPosition);
            mDrawerExpandableListView.setSelectedChild(groupPosition, childPosition, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
//            mCallbacks.onNavigationDrawerItemSelected(DataManager.getInstance().getCategories().get(position-1));
            mCallbacks.onNavigationDrawerItemSelected(groupPosition, childPosition);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.common_black));
//        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(R.string.app_name);

    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int groupPosition, int childPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data)
            return;
        switch (resultCode) {
            case ConsistentParameter.RESULT_CODE_CREATEAVATARACTIVITY: {
                updateAvatar();
            }
            break;
            default: {
                Intent intent = new Intent(getActivity().getBaseContext(), CreateAvatarActivityEX.class);
                intent.putExtra(ConsistentString.PARAM_URI, data.getDataString());
                startActivityForResult(intent, ConsistentParameter.REQUEST_CODE_DRAWERFRAMENT);
            }
            break;
        }
    }

    private void updateAvatar()
    {
        Bitmap bitmapAvatar = null;
        if(FileHelper.isExist(AppPathHelper.getAvatarFilePath()))
        {
            bitmapAvatar = ImageUtility.getDiskBitmap(AppPathHelper.getAvatarFilePath());
        }
        if(null == bitmapAvatar)
        {
            m_circleImageView.setImageResource(R.drawable.avatar_default);
        }
        else
        {
            m_circleImageView.setImageBitmap(bitmapAvatar);
        }
    }
}
