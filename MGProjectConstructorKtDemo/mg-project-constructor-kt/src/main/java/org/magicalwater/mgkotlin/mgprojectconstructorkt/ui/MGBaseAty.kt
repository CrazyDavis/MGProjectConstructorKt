package org.magicalwater.mgkotlin.mgprojectconstructorkt.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGPageInfo
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGFgtManager
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGPageData
import org.magicalwater.mgkotlin.mgutilskt.util.MGLogUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGPermissionUtils

/**
 * Created by 志朋 on 2017/11/11.
 * 最基本的上層 Aty
 */
abstract class MGBaseAty: AppCompatActivity(), MGApiHelperDelegate {

    private var apiHelper: MGBaseApiHelper? = null
    private var fgtManager: MGFgtManager? = null
    private var permissionUtil: MGPermissionUtils = MGPermissionUtils()

    //將呼叫 requestPermission 的 delegate 暫時儲存起來
    private var requestPermissionDelegate: MGPermissionUtils.PermissionCheckDelegate? = null

    //這個數字代表了現在activity的狀態
    // <= 0 - 後台
    //  > 0 - 前台
    //第一次進入頁面(頁面剛初始化時不呼叫進入前台)
    private var foregroundCount: Int = 0
        set(value) {
            field = value
            if (foregroundCount <= 0) activityToBackground()
            else {
                if (!isActivtyFirstInside) activityToForeground()
                else isActivtyFirstInside = false
            }
        }

    //幫助判斷是否為第一次進入此頁面(作為判斷是否呼叫 activityToForeground 的依據)
    private var isActivtyFirstInside: Boolean = true

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentLayout())
        settingApiHelper()
        settingFgtManager()
        setupView()
    }

    final override fun onStart() {
        super.onStart()
        foregroundCount += 1
    }


    override fun onStop() {
        super.onStop()
        foregroundCount -= 1
    }



    //抓取是否啟用 api 輔助物件的工具
    private fun settingApiHelper() {
        apiHelper = if (enableApiHelper()) MGBaseApiHelper(this) else null
    }

    //抓取是否啟用 api 輔助物件的工具
    private fun settingFgtManager() {
        fgtManager = if (enableFgtManager()) MGFgtManager() else null
        fgtManager?.setFgtManager(supportFragmentManager)
        fgtManager?.setRoot(rootFgt())
    }

    //是否啟用 api 輔助 class
    protected open fun enableApiHelper(): Boolean = true

    //是否啟用 fgt管理 輔助 class
    protected open fun enableFgtManager(): Boolean = true

    //如果啟fgt管理則此項必須設置
    protected open fun rootFgt(): MGPageData? = null

    //activity從後臺跳到前台
    protected open fun activityToForeground() {}

    //activity從前臺跳到後台
    protected open fun activityToBackground() {}

    //初始化設置view
    abstract fun setupView()

    abstract fun contentLayout() : Int

    //子類別設定倒數計時狀態
    protected fun timerAction(action: MGBaseApiHelper.TimerAction) {
        apiHelper?.timerAction(action)
    }

    //設定倒數計時預設時間
    protected fun setTimerTime(time: Long) {
        apiHelper?.timerTime = time
    }

    //設定 fgt manager 的 root page
    protected fun setRootPage(page: MGPageData) {
        fgtManager?.setRoot(page)
    }

    //跳轉到某個 Fragment, 可供複寫, 為的在跳轉前的執行動作
    open fun fgtShow(request: MGUrlRequest) {
        fgtManager?.pageJump(request)
    }

    //顯示某個頁面, 不用經過網路
    fun fgtShow(pageInfo: MGPageInfo) {
        fgtManager?.pageJump(pageInfo)
    }

    //隱藏某個頁面
    fun fgtHide(fgtTag: String) {
        fgtManager?.hideFgt(fgtTag)
    }

    //得到目前最頂層的page
    fun getTopPage(): MGPageData? {
        return if (fgtManager?.totalHistory?.isNotEmpty() == true) {
            fgtManager?.totalHistory?.last()
        } else {
            null
        }
    }

    //要求某個權限
    protected fun requestPermission(permission: String, requestCode: Int,
                                    delegate: MGPermissionUtils.PermissionCheckDelegate) {
        permissionUtil.requestPermissions(
                this, permission, requestCode,
                object : MGPermissionUtils.PermissionCheckDelegate{
                    override fun permissionStatus(status: MGPermissionUtils.PermissionStatus,
                                                  permission: String, requestCode: Int) {
                        //如果回傳的狀態是使用者正在選擇的話, 將delegate暫時儲存起來, 其餘的直接傳回去
                        requestPermissionDelegate = when (status) {
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
                    requestPermissionDelegate?.permissionStatus(
                            MGPermissionUtils.PermissionStatus.GRANTED,
                            permissions.first(), requestCode)
                }

                PackageManager.PERMISSION_DENIED -> {
                    requestPermissionDelegate?.permissionStatus(
                            MGPermissionUtils.PermissionStatus.REJECT,
                            permissions.first(), requestCode)
                }
            }
        } else {
            MGLogUtils.d("權限要求頁面回來為空值, 默認reject, 但字串是什麼不知道, 無法回傳")
        }
        requestPermissionDelegate = null
    }

    protected fun setFgtDelegate(delegate: MGFgtManager.TurnDelegate) {
        fgtManager?.delegate = delegate
    }

    protected fun toRootPage() {
        fgtManager?.toRootPage()
    }

    //回退上一頁fragment, 回傳代表是否處理 back
    protected fun backPage(back: Int = 1): Boolean {
        //先檢查最上層的fgt是否處理back的動作, 當back數量等於1時
        return if (back == 1 && fgtManager?.backAction() == true) {
            true
        } else {
            fgtManager?.backPage(back) ?: false
        }

    }

    //發送MGUrlRequest
    protected open fun sendRequest(request: MGUrlRequest, requestCode: Int? = null) {
        if (requestCode != null) apiHelper?.sendRequest(request, requestCode)
        else apiHelper?.sendRequest(request)
    }

}