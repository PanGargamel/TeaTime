package pl.piotrskiba.teatime.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.preference.PreferenceManager
import pl.piotrskiba.teatime.R
import java.util.*

object LanguageUtils {
    @JvmStatic
    fun setAppLanguage(baseContext: Context, lang_code: String) { // update language
        val locale = Locale(lang_code)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale

        baseContext.resources.updateConfiguration(config,
                baseContext.resources.displayMetrics)
    }

    @JvmStatic
    fun getLanguageFromSettings(context: Context): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val language = sharedPreferences.getString(context.getString(R.string.pref_language_key), phoneLanguage)
        return if (language == context.getString(R.string.pref_language_value_default)) phoneLanguage else language
    }

    @JvmStatic
    val appLanguage: String
        get() = Locale.getDefault().language

    val phoneLanguage: String
        get() {
            val globalLocale = Resources.getSystem().configuration.locale
            return globalLocale.language
        }
}