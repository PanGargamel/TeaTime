package pl.piotrskiba.teatime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeaInfoFragment extends Fragment {

    private int mTeaIndex;

    @BindView(R.id.tv_tea_name)
    TextView mTeaName;

    @BindView(R.id.tv_tea_description)
    TextView mTeaDescription;

    @BindView(R.id.tv_tea_brewing_instructions)
    TextView mTeaBrewingInstructions;

    @BindView(R.id.tea_instructions_temperature)
    ImageView mTeaBrewingTemperatureImageView;

    @BindView(R.id.tea_instructions_time)
    ImageView mTeaBrewingTimeImageView;

    public TeaInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tea_info, container, false);

        ButterKnife.bind(this, rootView);

        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.EXTRA_INDEX)){
            mTeaIndex = savedInstanceState.getInt(Constants.EXTRA_INDEX);
        }

        populateFragment();

        return rootView;
    }

    public void setTeaIndex(int index){
        mTeaIndex = index;
    }

    private void populateFragment(){
        String tea_name = getResources().getStringArray(R.array.tea_names)[mTeaIndex];
        String tea_description = getResources().getStringArray(R.array.tea_descriptions)[mTeaIndex];
        String tea_brewing_instructions = getResources().getStringArray(R.array.tea_brewing_instructions)[mTeaIndex];
        String tea_brewing_temperature_image = getResources().getStringArray(R.array.tea_brewing_temperature_images)[mTeaIndex];
        String tea_brewing_time_image = getResources().getStringArray(R.array.tea_brewing_time_images)[mTeaIndex];


        mTeaName.setText(tea_name);
        mTeaDescription.setText(tea_description);

        mTeaBrewingInstructions.setText(tea_brewing_instructions);

        if(tea_brewing_temperature_image != null) {
            int temperature_image = getResources().getIdentifier(tea_brewing_temperature_image, "drawable", getContext().getPackageName());
            mTeaBrewingTemperatureImageView.setImageResource(temperature_image);
        }
        if(tea_brewing_time_image != null) {
            int time_image = getResources().getIdentifier(tea_brewing_time_image, "drawable", getContext().getPackageName());
            mTeaBrewingTimeImageView.setImageResource(time_image);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(Constants.EXTRA_INDEX, mTeaIndex);
    }
}
