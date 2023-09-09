package com.example.cartemplate2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.cartemplate2.databinding.ActivityMain2Binding
import com.example.cartemplate2.databinding.BottomNavigationBarMainBinding
import com.example.cartemplate2.databinding.DrawerLayoutBinding
import com.example.cartemplate2.ui.auth.LoginActivity
import com.example.cartemplate2.ui.auth.ProfileFragment
import com.example.cartemplate2.ui.details.BookingListFragment
import com.example.cartemplate2.ui.details.ProductListActivity
import com.example.cartemplate2.ui.details.TestDriveBookingListFragment
import com.example.cartemplate2.ui.home.AboutUsActivity
import com.example.cartemplate2.ui.home.HomeFragment
import com.example.cartemplate2.utils.PreferenceKey
import com.example.cartemplate2.utils.PreferenceProvider
import com.example.cartemplate2.utils.log

class MainActivity2 : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var bottomNavBar: BottomNavigationBarMainBinding
    private lateinit var navigationDrawerBinding: DrawerLayoutBinding

    var doubleBackToExitPressedOnce: Boolean = false
    //val onBackPressedDispatcher = getOnBackPressedDispatcher()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_main2)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        bottomNavBar = binding.bottomNav
        navigationDrawerBinding = binding.includeNavView

        findViewById<ImageView>(R.id.img_back).setOnClickListener(this)

        navigationDrawerBinding.navLogin.setOnClickListener(this)
        navigationDrawerBinding.navLogout.setOnClickListener(this)
        navigationDrawerBinding.navAbout.setOnClickListener(this)

        bottomNavBar.idSV.visibility = View.VISIBLE
        bottomNavBar.icMenu.visibility = View.GONE
        bottomNavBar.txtHeading.text = "Home"

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount == 0) {
                    if (doubleBackToExitPressedOnce) {
                        finish()
                        return
                    } else {
                        doubleBackToExitPressedOnce = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            doubleBackToExitPressedOnce = false
                        }, 2000)
                    }
                }
            }
        })

        init()
        initSearchView()
        bottomNav()
    }

    @SuppressLint("SetTextI18n")
    private fun bottomNav() {
        if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)
                .equals("0")
        ) {
            bottomNavBar.navigation.findViewById<View>(R.id.m_item_Booking).visibility = View.GONE
            bottomNavBar.navigation.findViewById<View>(R.id.m_item_test_drive).visibility =
                View.GONE
            bottomNavBar.navigation.findViewById<View>(R.id.m_item_profile).visibility = View.GONE
        } else {
            bottomNavBar.navigation.findViewById<View>(R.id.m_item_Booking).visibility =
                View.VISIBLE
            bottomNavBar.navigation.findViewById<View>(R.id.m_item_test_drive).visibility =
                View.VISIBLE
            bottomNavBar.navigation.findViewById<View>(R.id.m_item_profile).visibility =
                View.VISIBLE
        }
        bottomNavBar.navigation.setOnItemSelectedListener { menuItem ->
            var selectedFragment: Fragment? = null
            when (menuItem.itemId) {
                R.id.m_item_home -> {
                    bottomNavBar.txtHeading.text = "Home"
                    bottomNavBar.idSV.visibility = View.VISIBLE
                    selectedFragment = HomeFragment()
                }
                R.id.m_item_Booking -> {
                    bottomNavBar.txtHeading.text = "Booking List"
                    bottomNavBar.idSV.visibility = View.GONE
                    selectedFragment = BookingListFragment()
                }
                R.id.m_item_test_drive -> {
                    bottomNavBar.txtHeading.text = "Test Drive Booking List"
                    bottomNavBar.idSV.visibility = View.GONE
                    selectedFragment = TestDriveBookingListFragment()
                }
                R.id.m_item_profile -> {
                    bottomNavBar.txtHeading.text = "Profile"
                    bottomNavBar.idSV.visibility = View.GONE
                    selectedFragment = ProfileFragment()
                }
                R.id.m_item_more -> {
                    if (!binding.drawerLayout.isDrawerOpen(GravityCompat.END)) binding.drawerLayout.openDrawer(
                        GravityCompat.END
                    ) else binding.drawerLayout.closeDrawer(
                        GravityCompat.END
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
        //String backStateName = fragment.getClass().getName();
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


    private fun init() {
        bottomNav()
        log(
            "Home_User_Profile",
            "${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)}"
        )
        if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)
                .equals("1")
        ) {
            navigationDrawerBinding.navLogout.visibility = View.VISIBLE
            navigationDrawerBinding.navLogin.visibility = View.GONE
        } else if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)
                .equals("2")
        ) {

            navigationDrawerBinding.navLogout.visibility = View.VISIBLE
            navigationDrawerBinding.navLogin.visibility = View.GONE
        } else {
            navigationDrawerBinding.navLogout.visibility = View.GONE
            navigationDrawerBinding.navLogin.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.img_back -> {
                if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.openDrawer(
                    GravityCompat.START
                ) else binding.drawerLayout.closeDrawer(
                    GravityCompat.START
                )
            }
            R.id.nav_login -> {
                val intent = Intent(this@MainActivity2, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                binding.drawerLayout.closeDrawers()
            }
            R.id.nav_about -> {
                val intent = Intent(this@MainActivity2, AboutUsActivity::class.java)
                // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                binding.drawerLayout.closeDrawers()
            }
            R.id.nav_logout -> {
                binding.drawerLayout.closeDrawers()
                PreferenceProvider(applicationContext).setBooleanValue(
                    false,
                    PreferenceKey.LOGIN_STATUS
                )
                PreferenceProvider(applicationContext).setStringValue(
                    "0",
                    PreferenceKey.USER_PROFILE
                )
                // PreferenceProvider(applicationContext).clear()
                val intent = Intent(this@MainActivity2, MainActivity2::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

    }

    private fun initSearchView() {
        bottomNavBar.idSV.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = v.text.toString().trim()
                if (searchText.length > 2) {
                    val imm =
                        applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(bottomNavBar.idSV.windowToken, 0)
                    val intent = Intent(applicationContext, ProductListActivity::class.java)
                    intent.putExtra("type", "Search")
                    intent.putExtra("name", bottomNavBar.idSV.text.toString())
                    intent.putExtra("id", "1")
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }
    }

    override fun onDestroy() {
        Log.d("onDestroy ", "onDestroy: Called")
        super.onDestroy()
        Log.d("onDestroy ", "onDestroy: Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("onDestroy ", "onStop: Called")
    }

}