package org.magicalwater.mgkotlin.mgprojectconstructorktdemo

import android.Manifest
import kotlinx.android.synthetic.main.activity_main.*
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGCodeScanManager
import org.magicalwater.mgkotlin.mgprojectconstructorkt.ui.MGBase3Aty
import org.magicalwater.mgkotlin.mgutilskt.util.MGPermissionUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGSettingUtils

class MainActivity : MGBase3Aty() {

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
