package com.echen.androidcommon.Utility;

import java.util.Locale;

/**
 * Created by echen on 2015/11/24.
 */
public class LanguageUtility {

    public static String LANGUAGE_CODE_CHINESE = "zh";
    public static String LANGUAGE_CODE_ENGLISH = "en";

    public static Locale getMappedLanguageLocale(String languageCode) {
        Locale locale = Locale.ENGLISH;
        if (languageCode.equalsIgnoreCase(LANGUAGE_CODE_ENGLISH)) {
            locale = Locale.ENGLISH;
        } else if (languageCode.equalsIgnoreCase(LANGUAGE_CODE_CHINESE)) {
            locale = Locale.SIMPLIFIED_CHINESE;
        }
        return locale;
    }
}
