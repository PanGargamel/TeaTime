package pl.piotrskiba.teatime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import pl.piotrskiba.teatime.utils.LanguageUtils

class SettingsActivity : AppCompatActivity() {

    @JvmField
    @BindView(R.id.toolbar)
    var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        // set language
        if (LanguageUtils.appLanguage != LanguageUtils.getLanguageFromSettings(this))
            LanguageUtils.setAppLanguage(baseContext, LanguageUtils.getLanguageFromSettings(this)!!)

        setContentView(R.layout.activity_settings)

        ButterKnife.bind(this)

        setSupportActionBar(mToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.settings)
    }
}