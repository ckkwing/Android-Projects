package com.echen.wisereminder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.echen.androidcommon.Utility.LanguageUtility;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import be.webelite.ion.Icon;
import be.webelite.ion.IconView;
import uicommon.customcontrol.PreferenceUtility;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return (GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || LanguagePreferenceFragment.class.getName().equals(fragmentName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }
    }

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class LanguagePreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//        }
//    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LanguagePreferenceFragment extends PreferenceFragment {

        class LanguageInfo {
            private String displayName = "";

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            private String languageCode = "";

            public String getLanguageCode() {
                return languageCode;
            }

            public void setLanguageCode(String languageCode) {
                this.languageCode = languageCode;
            }

            private boolean isSelected = false;
            public boolean getIsSelected() {return  isSelected; }
            public void setIsSelected(boolean isSelected) { this.isSelected = isSelected; }

            public LanguageInfo(String displayName, String languageCode) {
                this.displayName = displayName;
                this.languageCode = languageCode;
            }
        }

        class ViewHolder {
            TextView txtDisplayName;
            IconView imgSelected;
        }

        class LanguageAdapter extends BaseAdapter {

            private static final String TAG = "LanguageAdapter";

            private Context m_context = null;
            private LayoutInflater m_layoutInflater = null;
            private List<LanguageInfo> m_languageItems = new ArrayList<LanguageInfo>();

            private String m_globeLanguageCode = "";

            LanguageAdapter(Context context, List<LanguageInfo> languageItems) {
                m_context = context;
                m_layoutInflater = (LayoutInflater) m_context.getSystemService(LAYOUT_INFLATER_SERVICE);
                m_languageItems = languageItems;
                m_globeLanguageCode = PreferenceUtility.getString(PreferenceUtility.KEY_LANGUAGE, LanguageUtility.LANGUAGE_CODE_CHINESE);
                for (LanguageInfo languageInfo : m_languageItems)
                {
                    if (languageInfo.getLanguageCode().equalsIgnoreCase(m_globeLanguageCode))
                    {
                        languageInfo.setIsSelected(true);
                        return;
                    }
                }
            }

            public void changeSelectedLanguage(int position)
            {
                for(int i =0; i<m_languageItems.size();i++)
                {
                    LanguageInfo languageInfo = m_languageItems.get(i);
                    if (position != i)
                    {
                        languageInfo.setIsSelected(false);
                    }
                    else
                    {
                        languageInfo.setIsSelected(true);
                    }
                }
            }

            @Override
            public int getCount() {
                return m_languageItems.size();
            }

            @Override
            public Object getItem(int position) {
                return m_languageItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                try {
                    if (m_languageItems.isEmpty())
                        return convertView;
                    LanguageInfo languageInfo = m_languageItems.get(position);
                    ViewHolder viewHolder;
                    if (null == convertView) {
                        convertView = m_layoutInflater.inflate(R.layout.language_list_item_view, null);
                        viewHolder = new ViewHolder();
                        viewHolder.txtDisplayName = (TextView) convertView.findViewById(R.id.languageName);
                        viewHolder.imgSelected = (IconView) convertView.findViewById(R.id.imgSelectedLGFlag);
                        convertView.setTag(viewHolder);
                    } else {
                        viewHolder = (ViewHolder) convertView.getTag();
                    }

                    viewHolder.txtDisplayName.setText(languageInfo.getDisplayName());
                    if (languageInfo.getIsSelected()) {
                        viewHolder.imgSelected.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.imgSelected.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }

                return convertView;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.language_selection_view, container, false);
            ListView listView = (ListView) view.findViewById(R.id.lvLanguages);
            final ProgressBar loadingBar = (ProgressBar)view.findViewById(R.id.loadingChangeLanguage);
            final List<LanguageInfo> languageItems = new ArrayList<LanguageInfo>();
            languageItems.add(new LanguageInfo(getResources().getString(R.string.pref_content_language_chinese), "zh"));
            languageItems.add(new LanguageInfo(getResources().getString(R.string.pref_content_language_english), "en"));
            final LanguageAdapter languageAdapter = new LanguageAdapter(getActivity(), languageItems);
            listView.setAdapter(languageAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    loadingBar.setVisibility(View.VISIBLE);
                    parent.setEnabled(false);

                    LanguageInfo languageInfo = languageItems.get(position);
                    Resources resources = getResources();
                    Configuration configuration = resources.getConfiguration();
                    DisplayMetrics displayMetrics = resources.getDisplayMetrics();

                    Locale locale = LanguageUtility.getMappedLanguageLocale(languageInfo.getLanguageCode());
                    if (locale != configuration.locale) {
                        configuration.locale = locale;
                        resources.updateConfiguration(configuration, displayMetrics);
                    }

                    PreferenceUtility.commitString(PreferenceUtility.KEY_LANGUAGE, languageInfo.getLanguageCode());
                    languageAdapter.changeSelectedLanguage(position);
                    languageAdapter.notifyDataSetChanged();

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
                    startActivity(intent);
                    getActivity().finish();//关掉自己
                }
            });
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
    }
}
