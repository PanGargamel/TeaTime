package pl.piotrskiba.teatime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.teatime.adapters.TeaListAdapter;

public class TeaSelectionActivity extends AppCompatActivity {

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

        TeaListAdapter adapter = new TeaListAdapter(this);
        mTeaList.setAdapter(adapter);
    }
}
