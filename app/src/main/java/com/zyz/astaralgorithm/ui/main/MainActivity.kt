package com.zyz.astaralgorithm.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zyz.astaralgorithm.R
import com.zyz.astaralgorithm.ui.astar.activity.AStarActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_show_a_star.setOnClickListener{
            startActivity(Intent(this, AStarActivity::class.java))
        }
    }
}