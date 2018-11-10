package pl.piotrskiba.teatime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class TeaTimerFragment extends Fragment {

    private int mTeaIndex;

    public TeaTimerFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_tea_timer, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    public void setTeaIndex(int index){
        mTeaIndex = index;
    }
}
