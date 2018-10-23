package pl.piotrskiba.teatime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.teatime.R;

public class TeaListAdapter extends RecyclerView.Adapter<TeaListAdapter.TeaListAdapterViewHolder> {

    private Context context;
    private String[] mTeaIds;
    private String[] mTeaNames;

    public TeaListAdapter(Context context){
        this.context = context;
        mTeaIds = context.getResources().getStringArray(R.array.tea_ids);
        mTeaNames = context.getResources().getStringArray(R.array.tea_names);
    }

    @NonNull
    @Override
    public TeaListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tea_list_item, parent, false);

        return new TeaListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeaListAdapterViewHolder holder, int position) {
        int resourceId = context.getResources().getIdentifier(mTeaIds[position], context.getString(R.string.drawable), context.getPackageName());
        holder.mTeaImage.setImageResource(resourceId);
        holder.mTeaName.setText(mTeaNames[position]);
        Log.d("onBindViewHolder", "Binded " + mTeaNames[position] + " (" + (position+1) +"/" + getItemCount() + ")");
    }

    @Override
    public int getItemCount() {
        return mTeaIds.length;
    }

    public class TeaListAdapterViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_tea_image)
        ImageView mTeaImage;

        @BindView(R.id.tv_tea_name)
        TextView mTeaName;

        public TeaListAdapterViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}