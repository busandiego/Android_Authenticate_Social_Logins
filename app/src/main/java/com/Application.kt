package com

import android.app.Application
import com.navercorp.nid.NaverIdLoginSDK

class Application: Application() {


    override fun onCreate() {
        super.onCreate()

        NaverIdLoginSDK.initialize(this, "", "", "")
    }

}