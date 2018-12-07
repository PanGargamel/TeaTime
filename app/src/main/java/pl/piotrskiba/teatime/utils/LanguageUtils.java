package pl.piotrskiba.teatime.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

import pl.piotrskiba.teatime.R;

public class LanguageUtils {
    public static void setAppLanguage(Context baseContext, String lang_code){
        // update language
        Locale locale = new Locale(lang_code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        baseContext.getResources().updateConfiguration(config,
                baseContext.getResources().getDisplayMetrics());
    }

    public static String getLanguageFromSettings(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String language = sharedPreferences.getString(context.getString(R.string.pref_language_key), getPhoneLanguage());

        if(language.equals(context.getString(R.string.pref_language_value_default)))
            return getPhoneLanguage();
        else
            return language;
    }

    public static String getAppLanguage(){
        return Locale.getDefault().getLanguage();
    }

    public static String getPhoneLanguage(){
        Locale globalLocale = Resources.getSystem().getConfiguration().locale;
        return globalLocale.getLanguage();
    }
}
