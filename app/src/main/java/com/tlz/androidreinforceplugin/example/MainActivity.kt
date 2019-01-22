package com.tlz.androidreinforceplugin.example

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_hello.text = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)?.metaData?.getString("UMENG_CHANNEL").toString()
    }
}
