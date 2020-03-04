package pl.piotrskiba.teatime

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.navigation.NavigationView
import pl.piotrskiba.teatime.adapters.TeaListAdapter
import pl.piotrskiba.teatime.interfaces.TeaSelectedListener
import pl.piotrskiba.teatime.utils.LanguageUtils.appLanguage
import pl.piotrskiba.teatime.utils.LanguageUtils.getLanguageFromSettings
import pl.piotrskiba.teatime.utils.LanguageUtils.setAppLanguage
import pl.piotrskiba.teatime.utils.NotificationUtils.createNotificationChannel

class TeaSelectionActivity : AppCompatActivity(), TeaSelectedListener {

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar

    @BindView(R.id.drawer_layout)
    lateinit var mDrawerLayout: DrawerLayout

    @BindView(R.id.navigation_view)
    lateinit var mNavigationView: NavigationView

    @BindView(R.id.rv_tea_list)
    lateinit var mTeaList: RecyclerView

    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        // set language
        if (appLanguage != getLanguageFromSettings(this)) setAppLanguage(baseContext, getLanguageFromSettings(this)!!)

        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        mContext = this

        setSupportActionBar(mToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        val layoutManager = GridLayoutManager(this, 3)
        mTeaList.layoutManager = layoutManager
        mTeaList.setHasFixedSize(true)

        val adapter = TeaListAdapter(this, this)
        mTeaList.adapter = adapter

        // make first menu item selected by default
        mNavigationView.menu.getItem(0).isChecked = true
        mNavigationView.setNavigationItemSelectedListener { item ->
            mDrawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.nav_settings -> {
                    val intent = Intent(mContext, SettingsActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                }
            }
            true
        }

        // create notification channel
        createNotificationChannel(this)
    }

    override fun onResume() {
        super.onResume()

        // set language
        if (appLanguage != getLanguageFromSettings(this)) {
            recreate()
        }
    }

    override fun onTeaSelected(tea_index: Int) {
        val intent = Intent(applicationContext, TeaDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_INDEX, tea_index)

        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}