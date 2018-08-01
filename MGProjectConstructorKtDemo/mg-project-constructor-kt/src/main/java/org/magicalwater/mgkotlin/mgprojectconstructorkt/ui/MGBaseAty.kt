package org.magicalwater.mgkotlin.mgprojectconstructorkt.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.magicalwater.mgkotlin.mgutilskt.util.MGLogUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGPermissionUtils

/**
 * Created by 志朋 on 2017/11/11.
 * 最基本的上層 Aty
 */
abstract class MGBaseAty: AppCompatActivity(), MGBaseApiHelperFeature, MGBaseFgtHelperFeature {

    override var mApiHelper: MGBaseApiHelper? = null
    override var mFgtHelper: MGBaseFgtHelper? = null

    private var mPermissionUtil: MGPermissionUtils = MGPermissionUtils()

    //將呼叫 requestPermission 的 delegate 暫時儲存起來
    private var mRequestPermissionDelegate: MGPermissionUtils.PermissionCheckDelegate? = null

    //這個數字代表了現在activity的狀態
    // <= 0 - 後台
    //  > 0 - 前台
    //第一次進入頁面(頁面剛初始化時不呼叫進入前台)
    private var mForegroundCount: Int = 0
        set(value) {
            field = value
            if (mForegroundCount <= 0) activityToBackground()
            else {
                if (!mIsActivtyFirstInside) activityToForeground()
                else mIsActivtyFirstInside = false
            }
        }

    //幫助判斷是否為第一次進入此頁面(作為判斷是否呼叫 activityToForeground 的依據)
    private var mIsActivtyFirstInside: Boolean = true

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentLayout())
        settingApiHelper()
        settingFgtManager()
        setupView()
    }

    final override fun onStart() {
        super.onStart()
        mForegroundCount += 1
    }

    override fun onStop() {
        super.onStop()
        mForegroundCount -= 1
    }

    //activity從後臺跳到前台
    protected open fun activityToForeground() {}

    //activity從前臺跳到後台
    protected open fun activityToBackground() {}

    //初始化設置view
    abstract fun setupView()

    abstract fun contentLayout() : Int

    //要求某個權限
    protected fun requestPermission(permission: String, requestCode: Int,
                                    delegate: MGPermissionUtils.PermissionCheckDelegate) {
        mPermissionUtil.requestPermissions(
                this, permission, requestCode,
                object : MGPermissionUtils.PermissionCheckDelegate{
                    override fun permissionStatus(status: MGPermissionUtils.PermissionStatus,
                                                  permission: String, requestCode: Int) {
                        //如果回傳的狀態是使用者正在選擇的話, 將delegate暫時儲存起來, 其餘的直接傳回去
                        mRequestPermissionDelegate = when (status) {
                            MGPermissionUtils.PermissionStatus.WAIT_USER -> delegate
                            else -> {
                                delegate.permissionStatus(status, permission, requestCode)
                                null
                            }
                        }
                    }

                } )
    }

    //6.0以上獲得權限的回調接口
    final override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && permissions.isNotEmpty()) {
            when (grantResults.first()) {
                PackageManager.PERMISSION_GRANTED -> {
                    mRequestPermissionDelegate?.permissionStatus(
                            MGPermissionUtils.PermissionStatus.GRANTED,
                            permissions.first(), requestCode)
                }

                PackageManager.PERMISSION_DENIED -> {
                    mRequestPermissionDelegate?.permissionStatus(
                            MGPermissionUtils.PermissionStatus.REJECT,
                            permissions.first(), requestCode)
                }
            }
        } else {
            MGLogUtils.d("權限要求頁面回來為空值, 默認reject, 但字串是什麼不知道, 無法回傳")
        }
        mRequestPermissionDelegate = null
    }

}