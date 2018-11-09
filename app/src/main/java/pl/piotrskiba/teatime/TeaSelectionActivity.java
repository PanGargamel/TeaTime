package pl.piotrskiba.teatime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.teatime.adapters.TeaListAdapter;
import pl.piotrskiba.teatime.interfaces.TeaSelectedListener;

public class TeaSelectionActivity extends AppCompatActivity implements TeaSelectedListener {

    @BindView(R.id.rv_tea_list)
    RecyclerView mTeaList;

    GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mLayoutManager = new GridLayoutManager(this, 3);

        mTeaList.setLayoutManager(mLayoutManager);
        mTeaList.setHasFixedSize(true);

        TeaListAdapter adapter = new TeaListAdapter(this, this);
        mTeaList.setAdapter(adapter);
    }

    @Override
    public void onTeaSelected(int tea_index) {
        Intent intent = new Intent(getApplicationContext(), TeaPropertiesSelectionActivity.class);
        intent.putExtra(Constants.EXTRA_INDEX, tea_index);
        startActivity(intent);
    }
}
