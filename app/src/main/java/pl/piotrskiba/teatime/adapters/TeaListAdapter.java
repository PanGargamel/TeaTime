package pl.piotrskiba.teatime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.teatime.R;
import pl.piotrskiba.teatime.interfaces.TeaSelectedListener;

public class TeaListAdapter extends RecyclerView.Adapter<TeaListAdapter.TeaListAdapterViewHolder> {

    private final Context context;
    private final String[] mTeaIds;
    private final String[] mTeaNames;

    private final TeaSelectedListener teaSelectedListener;

    public TeaListAdapter(Context context, TeaSelectedListener teaSelectedListener){
        this.context = context;
        mTeaIds = context.getResources().getStringArray(R.array.tea_ids);
        mTeaNames = context.getResources().getStringArray(R.array.tea_names);

        this.teaSelectedListener = teaSelectedListener;
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
    }

    @Override
    public int getItemCount() {
        return mTeaIds.length;
    }

    public class TeaListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_tea_image)
        ImageView mTeaImage;

        @BindView(R.id.tv_tea_name)
        TextView mTeaName;

        public TeaListAdapterViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            teaSelectedListener.onTeaSelected(clickedPosition);
        }
    }
}
