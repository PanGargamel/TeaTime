package pl.piotrskiba.teatime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeaPropertiesSelectionActivity extends AppCompatActivity {

    @BindView(R.id.tv_tea_description)
    TextView mTeaDescription;

    int mTeaIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tea_properties_selection);

        ButterKnife.bind(this);

        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra(Constants.EXTRA_INDEX)){
            mTeaIndex = parentIntent.getIntExtra(Constants.EXTRA_INDEX, -1);
            populateUi();
        }
    }

    private void populateUi(){
        String tea_id = getResources().getStringArray(R.array.tea_ids)[mTeaIndex];
        String tea_name = getResources().getStringArray(R.array.tea_names)[mTeaIndex];
        String tea_description = getResources().getStringArray(R.array.tea_descriptions)[mTeaIndex];

        getSupportActionBar().setTitle(tea_name);

        mTeaDescription.setText(tea_description);
    }
}
