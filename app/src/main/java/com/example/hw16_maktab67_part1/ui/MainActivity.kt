package com.example.hw16_maktab67_part1.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.example.hw16_maktab67_part1.R
import com.example.hw16_maktab67_part1.ui.main.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            replace(R.id.container, MainFragment())
            setReorderingAllowed(true)
        }

    }
}