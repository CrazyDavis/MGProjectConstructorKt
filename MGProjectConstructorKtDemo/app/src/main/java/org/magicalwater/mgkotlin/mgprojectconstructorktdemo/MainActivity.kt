package org.magicalwater.mgkotlin.mgprojectconstructorktdemo

import android.Manifest
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGCodeScanManager
import org.magicalwater.mgkotlin.mgprojectconstructorkt.ui.MGBaseAty
import org.magicalwater.mgkotlin.mgutilskt.util.MGPermissionUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGSettingUtils

class MainActivity : MGBaseAty() {

    override fun enableApiHelper(): Boolean = true
    override fun enableFgtManager(): Boolean = true
    override fun fragmentManager(): FragmentManager? = supportFragmentManager

    override fun setupView() {

    }

    override fun contentLayout(): Int = R.layout.activity_main

    override fun response(request: MGUrlRequest, success: Boolean, requestCode: Int) {}
    override fun timesUp() {}

    fun onClick(view: View) {
        println("按下按鈕")
        val pi = PageBuilder.buildHome()
        fgtShow(pi)
    }

}
