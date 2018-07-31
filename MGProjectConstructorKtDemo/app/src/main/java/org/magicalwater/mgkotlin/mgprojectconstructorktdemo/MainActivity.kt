package org.magicalwater.mgkotlin.mgprojectconstructorktdemo

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGCodeScanManager
import org.magicalwater.mgkotlin.mgprojectconstructorkt.ui.MGBaseAty
import org.magicalwater.mgkotlin.mgprojectconstructorktdemo.R
import org.magicalwater.mgkotlin.mgutilskt.util.MGLogUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGPermissionUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGSettingUtils

class MainActivity : MGBaseAty() {

    val mCodeScanManager: MGCodeScanManager by lazy {
        MGCodeScanManager(mCodeScanLayout)
    }

    override fun setupView() {
        MGSettingUtils.initPrefs(this)

        val delegate = object : MGPermissionUtils.PermissionCheckDelegate {
            override fun permissionStatus(status: MGPermissionUtils.PermissionStatus, permission: String, requestCode: Int) {
                when (status) {
                    MGPermissionUtils.PermissionStatus.GRANTED -> {
                        //賦予權限
                        mCodeScanManager.startScan()
                    }
                }
            }
        }
        //相機權限
        requestPermission(Manifest.permission.CAMERA, 100, delegate)
    }

    override fun activityToBackground() {
        super.activityToBackground()
        mCodeScanManager.stopScan()
    }

    override fun activityToForeground() {
        super.activityToForeground()
        mCodeScanManager.startScan()
    }

    override fun contentLayout(): Int = R.layout.activity_main

    override fun response(request: MGUrlRequest, success: Boolean, requestCode: Int) {}
    override fun timesUp() {}

}
