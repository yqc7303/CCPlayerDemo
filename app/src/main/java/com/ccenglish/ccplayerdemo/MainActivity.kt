package com.ccenglish.ccplayerdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ccenglish.ccplayerlib.activity.SimpleMediaPlayActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_simple_player.setOnClickListener {


            startActivity(Intent(this,SimpleMediaPlayActivity::class.java).apply {
                putExtra("videoId","83CFD26BAB4E1FE49C33DC5901307461")
            })

        }
    }


}
