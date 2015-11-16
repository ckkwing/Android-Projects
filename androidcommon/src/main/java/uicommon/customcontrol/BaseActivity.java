package uicommon.customcontrol;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by echen on 2015/11/16.
 */
public class BaseActivity extends Activity {

    protected String KEY_LANGUAGE = "language";
    protected String LANGUAGE_CODE_CHINESE = "zh";
    protected String LANGUAGE_CODE_ENGLISH = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceUtility.init(this);
        switchLanguage(PreferenceUtility.getString(KEY_LANGUAGE, LANGUAGE_CODE_CHINESE));
    }

    protected void switchLanguage(String languageCode) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        Locale locale = getMappedLanguageLocale(languageCode);
        if (locale != configuration.locale) {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, displayMetrics);
        }

        PreferenceUtility.commitString(KEY_LANGUAGE, languageCode);
    }

    protected Locale getMappedLanguageLocale(String languageCode) {
        Locale locale = Locale.ENGLISH;
        if (languageCode.equalsIgnoreCase(LANGUAGE_CODE_ENGLISH)) {
            locale = Locale.ENGLISH;
        } else if (languageCode.equalsIgnoreCase(LANGUAGE_CODE_CHINESE)) {
            locale = Locale.SIMPLIFIED_CHINESE;
        }
        return locale;
    }
}
