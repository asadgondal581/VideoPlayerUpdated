package com.example.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.videoplayer.view.GridViewFragment
import com.example.videoplayer.view.LinearViewFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var navView: BottomNavigationView? = null
    private var fragment1: LinearViewFragment? = null
    private var fragment2: GridViewFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment1 = LinearViewFragment()
        fragment2 = GridViewFragment()

        navView = findViewById(R.id.bottomNavigationView)
        navView?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.pdfFragment -> {
                    setFragment(fragment1)
                    true
                }
                R.id.videoFragment -> {
                    setFragment(fragment2)
                    true

                }
                else -> false
            }
        }
    }

    private fun setFragment(fragment: Fragment?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activity_main_nav_host_fragment, fragment!!)
        fragmentTransaction.commit()
    }
}