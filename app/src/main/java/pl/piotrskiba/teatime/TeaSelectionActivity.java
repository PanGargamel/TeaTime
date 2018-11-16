package pl.piotrskiba.teatime;

import android.content.Intent;
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

public class TeaSelectionActivity extends AppCompatActivity implements TeaSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.rv_tea_list)
    RecyclerView mTeaList;

    GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mLayoutManager = new GridLayoutManager(this, 3);

        mTeaList.setLayoutManager(mLayoutManager);
        mTeaList.setHasFixedSize(true);

        TeaListAdapter adapter = new TeaListAdapter(this, this);
        mTeaList.setAdapter(adapter);
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
