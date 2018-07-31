package org.magicalwater.mgkotlin.mgprojectconstructorkt.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgprojectconstructorkt.fgtHelper.MGFgtDataHelper

/**
 * Created by 志朋 on 2017/12/3.
 * 最基本的上層 Fgt
 */
open abstract class MGBaseFgt: Fragment(), MGFgtDataHelper, MGApiHelperDelegate {

    private var apiHelper: MGBaseApiHelper? = null

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(contentLayout(), container, false)
    }

    final override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settingApiHelper()
        setupView()
    }

    //抓取是否啟用 api 輔助物件的工具
    protected fun settingApiHelper() {
        apiHelper = if (enableApiHelper()) MGBaseApiHelper(this) else null
    }

    //是否啟用 api 輔助 class
    protected fun enableApiHelper(): Boolean = true

    abstract fun setupView()

    abstract fun contentLayout() : Int

    /**
     * @param pageInfo  跳轉進入此Fgt的 頁面資料
     * @param isFgtInit   此頁面是否正要初始化, 或者頁面已經存在只是傳入資料
     * */
//    override fun pageData(data: MGPageData, isFgtInit: Boolean)

    //子類別設定倒數計時狀態
    protected fun timerAction(action: MGBaseApiHelper.TimerAction) {
        apiHelper?.timerAction(action)
    }

    //設定倒數計時預設時間
    protected fun setTimerTime(time: Long) {
        apiHelper?.timerTime = time
    }

    //發送MGUrlRequest
    protected fun sendRequest(request: MGUrlRequest, requestCode: Int? = null) {
        if (requestCode != null) apiHelper?.sendRequest(request, requestCode)
        else apiHelper?.sendRequest(request)
    }

}