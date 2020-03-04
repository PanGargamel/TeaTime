package pl.piotrskiba.teatime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife

class TeaInfoFragment : Fragment() {
    private var mTeaIndex = 0

    @BindView(R.id.tv_tea_name)
    lateinit var mTeaName: TextView

    @BindView(R.id.tv_tea_description)
    lateinit var mTeaDescription: TextView

    @BindView(R.id.tv_tea_brewing_instructions)
    lateinit var mTeaBrewingInstructions: TextView

    @BindView(R.id.tea_instructions_temperature)
    lateinit var mTeaBrewingTemperatureImageView: ImageView

    @BindView(R.id.tea_instructions_time)
    lateinit var mTeaBrewingTimeImageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_tea_info, container, false)

        ButterKnife.bind(this, rootView)

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.EXTRA_INDEX)) {
            mTeaIndex = savedInstanceState.getInt(Constants.EXTRA_INDEX)
        }

        populateFragment()

        return rootView
    }

    fun setTeaIndex(index: Int) {
        mTeaIndex = index
    }

    private fun populateFragment() {
        val teaName = resources.getStringArray(R.array.tea_names)[mTeaIndex]
        val teaDescription = resources.getStringArray(R.array.tea_descriptions)[mTeaIndex]
        val teaBrewingInstructions = resources.getStringArray(R.array.tea_brewing_instructions)[mTeaIndex]
        val teaBrewingTemperatureImage = resources.getStringArray(R.array.tea_brewing_temperature_images)[mTeaIndex]
        val teaBrewingTimeImage = resources.getStringArray(R.array.tea_brewing_time_images)[mTeaIndex]

        mTeaName.text = teaName
        mTeaDescription.text = teaDescription
        mTeaBrewingInstructions.text = teaBrewingInstructions

        if (teaBrewingTemperatureImage != null) {
            val temperatureImage = resources.getIdentifier(teaBrewingTemperatureImage, "drawable", context!!.packageName)
            mTeaBrewingTemperatureImageView.setImageResource(temperatureImage)
        }

        if (teaBrewingTimeImage != null) {
            val timeImage = resources.getIdentifier(teaBrewingTimeImage, "drawable", context!!.packageName)
            mTeaBrewingTimeImageView.setImageResource(timeImage)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.EXTRA_INDEX, mTeaIndex)
    }
}