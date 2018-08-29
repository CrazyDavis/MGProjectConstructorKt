package org.magicalwater.mgkotlin.mgprojectconstructorktdemo

import android.Manifest
import android.support.v4.app.FragmentManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.contentView
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGCodeScanManager
import org.magicalwater.mgkotlin.mgprojectconstructorkt.ui.MGBaseAty
import org.magicalwater.mgkotlin.mgutilskt.util.MGPermissionUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGSettingUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGTimerUtils

class MainActivity : MGBaseAty() {

    override fun enableApiHelper(): Boolean = true
    override fun enableFgtManager(): Boolean = true
    override fun fragmentManager(): FragmentManager? = supportFragmentManager

    override fun setupView() {
        println("按下按鈕")
        val pi = PageBuilder.buildHome()
        fgtShow(pi)

        MGTimerUtils.countDown(3000) {
            println("已經裝載入 fgt, 那麼 child = ${mContainer.childCount}")
            loopForChild(mContainer)
        }.start()
    }

    fun loopForChild(view: View) {
        println("類型: ${view.javaClass.simpleName}")
        if (view is ViewGroup) {
            (0 until view.childCount).forEach {
                loopForChild(view.getChildAt(it))
            }
        }
    }

    override fun contentLayout(): Int = R.layout.activity_main

    override fun response(request: MGUrlRequest, success: Boolean, requestCode: Int) {}
    override fun timesUp() {}

    fun onClick(view: View) {
    }

}
