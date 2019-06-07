package pl.piotrskiba.teatime;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
