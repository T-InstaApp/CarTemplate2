package com.example.cartemplate2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.cartemplate2.databinding.ActivityMainBinding
import com.example.cartemplate2.databinding.CustomToolbarBinding
import com.example.cartemplate2.databinding.HeaderNavigationDrawerBinding
import com.example.cartemplate2.ui.auth.LoginActivity
import com.example.cartemplate2.ui.details.ProductListActivity
import com.example.cartemplate2.ui.home.HomeFragment
import com.example.cartemplate2.utils.PreferenceKey
import com.example.cartemplate2.utils.PreferenceProvider
import com.example.cartemplate2.utils.log
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), View.OnClickListener, UpdateUi {
    var doubleBackToExitPressedOnce: Boolean = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationDrawerBinding: HeaderNavigationDrawerBinding
    private lateinit var customToolbarBinding: CustomToolbarBinding
    lateinit var toolbarBinding: CustomToolbarBinding

    lateinit var navigation: BottomNavigationView
    lateinit var drawer_layout: androidx.drawerlayout.widget.DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        customToolbarBinding = binding.toolBar
        navigationDrawerBinding = binding.includeNavView
        toolbarBinding = binding.toolBar

        navigation = findViewById(R.id.navigation)
        drawer_layout = findViewById(R.id.drawer_layout)

        findViewById<ImageView>(R.id.img_back).setOnClickListener(this)

        navigationDrawerBinding.navLogin.setOnClickListener(this)
        navigationDrawerBinding.navPro.setOnClickListener(this)
        navigationDrawerBinding.navLogout.setOnClickListener(this)
        navigationDrawerBinding.navTestBooking.setOnClickListener(this)
        navigationDrawerBinding.navBooking.setOnClickListener(this)

        customToolbarBinding.icMenu.setOnClickListener {
            if (!drawer_layout.isDrawerOpen(GravityCompat.START)) drawer_layout.openDrawer(
                GravityCompat.START
            ) else drawer_layout.closeDrawer(
                GravityCompat.START
            )
        }
        toolbarBinding.idSV.visibility = View.VISIBLE
        toolbarBinding.icMenu.visibility = View.GONE
        toolbarBinding.txtHeading.text = "Home"
        init()
        initSearchView()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.img_back -> {
                if (!drawer_layout.isDrawerOpen(GravityCompat.START)) drawer_layout.openDrawer(
                    GravityCompat.START
                ) else drawer_layout.closeDrawer(
                    GravityCompat.START
                )
            }
            /* R.id.nav_pro -> {
                 val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                 startActivity(intent)
                 drawer_layout.closeDrawers()
             }*/
            /*R.id.nav_test_booking -> {
                val intent = Intent(this@MainActivity, TestDriveBookingListActivity::class.java)
                startActivity(intent)
                drawer_layout.closeDrawers()
            }*/
            /* R.id.nav_booking -> {
                 val intent = Intent(this@MainActivity, BookingListActivity::class.java)
                 startActivity(intent)
                 drawer_layout.closeDrawers()
             }*/
            R.id.nav_login -> {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                drawer_layout.closeDrawers()
            }
            R.id.nav_logout -> {
                drawer_layout.closeDrawers()
                PreferenceProvider(applicationContext).setBooleanValue(
                    false,
                    PreferenceKey.LOGIN_STATUS
                )
                PreferenceProvider(applicationContext).setStringValue(
                    "0",
                    PreferenceKey.USER_PROFILE
                )
                PreferenceProvider(applicationContext).clear()
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun updateIMG(url: Boolean) {
        navigation.menu.findItem(R.id.m_item_cart).isEnabled = !url
    }

    private fun init() {
        bottomNav()
        log(
            "Home_User_Profile",
            "${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)}"
        )
        if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)
                .equals("1")
        ) {
            navigationDrawerBinding.navPro.visibility = View.VISIBLE
            navigationDrawerBinding.navLogout.visibility = View.VISIBLE
            navigationDrawerBinding.navTestBooking.visibility = View.VISIBLE
            navigationDrawerBinding.navBooking.visibility = View.VISIBLE
            navigationDrawerBinding.navLogin.visibility = View.GONE
        } else if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)
                .equals("2")
        ) {
            navigationDrawerBinding.navPro.visibility = View.GONE
            navigationDrawerBinding.navLogout.visibility = View.VISIBLE
            navigationDrawerBinding.navTestBooking.visibility = View.VISIBLE
            navigationDrawerBinding.navBooking.visibility = View.VISIBLE
            navigationDrawerBinding.navLogin.visibility = View.GONE
        } else {
            navigationDrawerBinding.navPro.visibility = View.GONE
            navigationDrawerBinding.navLogout.visibility = View.GONE
            navigationDrawerBinding.navTestBooking.visibility = View.GONE
            navigationDrawerBinding.navBooking.visibility = View.GONE
            navigationDrawerBinding.navLogin.visibility = View.VISIBLE
        }
    }

    private fun bottomNav() {
        navigation.setOnItemSelectedListener { menuItem ->
            var selectedFragment: Fragment? = null
            when (menuItem.itemId) {
                R.id.m_item_home -> selectedFragment = HomeFragment()
                R.id.m_item_more -> {
                    if (!drawer_layout.isDrawerOpen(GravityCompat.START)) drawer_layout.openDrawer(
                        GravityCompat.START
                    ) else drawer_layout.closeDrawer(
                        GravityCompat.START
                    )
                    return@setOnItemSelectedListener false
                }
            }
            replaceFragment(selectedFragment!!)
            true
        }
        replaceFragment(HomeFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        val manager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.replace(R.id.parent_foter, fragment)
            ft.commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                finish()
                return
            } else {
                this.doubleBackToExitPressedOnce = true
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun initSearchView() {
        toolbarBinding.idSV.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = v.text.toString().trim()
                if (searchText.length > 2) {
                    val imm =
                        applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(toolbarBinding.idSV.windowToken, 0)
                    val intent = Intent(applicationContext, ProductListActivity::class.java)
                    intent.putExtra("type", "Search")
                    intent.putExtra("name", toolbarBinding.idSV.text.toString())
                    intent.putExtra("id", "1")
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }
    }
}