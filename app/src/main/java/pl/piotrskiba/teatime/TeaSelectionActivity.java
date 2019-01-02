package pl.piotrskiba.teatime;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.teatime.adapters.TeaListAdapter;
import pl.piotrskiba.teatime.interfaces.TeaSelectedListener;
import pl.piotrskiba.teatime.utils.LanguageUtils;

public class TeaSelectionActivity extends AppCompatActivity implements TeaSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.rv_tea_list)
    RecyclerView mTeaList;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set language
        if(!LanguageUtils.getAppLanguage().equals(LanguageUtils.getLanguageFromSettings(this)))
            LanguageUtils.setAppLanguage(getBaseContext(), LanguageUtils.getLanguageFromSettings(this));

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mContext = this;

        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mTeaList.setLayoutManager(layoutManager);
        mTeaList.setHasFixedSize(true);

        TeaListAdapter adapter = new TeaListAdapter(this, this);
        mTeaList.setAdapter(adapter);

        // make first menu item selected by default
        mNavigationView.getMenu().getItem(0).setChecked(true);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();

                switch (item.getItemId()){
                    case R.id.nav_make_tea:
                        break;
                    case R.id.nav_settings:
                        Intent intent = new Intent(mContext, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // set language
        if(!LanguageUtils.getAppLanguage().equals(LanguageUtils.getLanguageFromSettings(this))) {
            recreate();
        }
    }

    @Override
    public void onTeaSelected(int tea_index) {
        Intent intent = new Intent(getApplicationContext(), TeaDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_INDEX, tea_index);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
