package com.ccenglish.ccplayerdemo

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import com.ccenglish.ccplayerlib.CCPlayer

/**
 * Created by yangqc on 2019-09-28.
 * describe:
 */
class APP : Application() {


    override fun onCreate() {
        super.onCreate()

        CCPlayer.instance.register(this)
    }

    override fun onTerminate() {
        super.onTerminate()

        CCPlayer.instance.unRegister()
    }





}