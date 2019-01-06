package pl.piotrskiba.teatime;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.teatime.utils.LanguageUtils;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        // set language
        if(!LanguageUtils.getAppLanguage().equals(LanguageUtils.getLanguageFromSettings(this)))
            LanguageUtils.setAppLanguage(getBaseContext(), LanguageUtils.getLanguageFromSettings(this));

        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.settings);
    }
}
