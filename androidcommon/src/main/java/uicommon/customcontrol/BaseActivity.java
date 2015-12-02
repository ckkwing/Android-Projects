package uicommon.customcontrol;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.echen.androidcommon.Utility.LanguageUtility;

import java.util.Locale;

/**
 * Created by echen on 2015/11/16.
 */
public class BaseActivity extends Activity {


//    protected String LANGUAGE_CODE_ENGLISH = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceUtility.init(this);
        switchLanguage(PreferenceUtility.getString(PreferenceUtility.KEY_LANGUAGE, LanguageUtility.LANGUAGE_CODE_CHINESE));
    }

    protected void switchLanguage(String languageCode) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        Locale locale = LanguageUtility.getMappedLanguageLocale(languageCode);
        if (locale != configuration.locale) {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, displayMetrics);
        }

        PreferenceUtility.commitString(PreferenceUtility.KEY_LANGUAGE, languageCode);
    }
}
