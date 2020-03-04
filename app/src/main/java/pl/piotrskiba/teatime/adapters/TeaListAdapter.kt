package pl.piotrskiba.teatime.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import pl.piotrskiba.teatime.R
import pl.piotrskiba.teatime.adapters.TeaListAdapter.TeaListAdapterViewHolder
import pl.piotrskiba.teatime.interfaces.TeaSelectedListener

class TeaListAdapter(private val context: Context, private val teaSelectedListener: TeaSelectedListener) : RecyclerView.Adapter<TeaListAdapterViewHolder>() {

    private val mTeaIds: Array<String> = context.resources.getStringArray(R.array.tea_ids)
    private val mTeaNames: Array<String> = context.resources.getStringArray(R.array.tea_names)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeaListAdapterViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.tea_list_item, parent, false)

        return TeaListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeaListAdapterViewHolder, position: Int) {
        val resourceId = context.resources.getIdentifier(
                mTeaIds[position],
                context.getString(R.string.drawable),
                context.packageName)

        holder.mTeaImage.setImageResource(resourceId)
        holder.mTeaName.text = mTeaNames[position]
    }

    override fun getItemCount(): Int {
        return mTeaIds.size
    }

    inner class TeaListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        @BindView(R.id.iv_tea_image)
        lateinit var mTeaImage: ImageView

        @BindView(R.id.tv_tea_name)
        lateinit var mTeaName: TextView

        override fun onClick(v: View) {
            val clickedPosition = adapterPosition
            teaSelectedListener.onTeaSelected(clickedPosition)
        }

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener(this)
        }
    }

}