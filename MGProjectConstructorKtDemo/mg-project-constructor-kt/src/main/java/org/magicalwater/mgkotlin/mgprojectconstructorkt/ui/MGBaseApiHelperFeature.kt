package org.magicalwater.mgkotlin.mgprojectconstructorkt.ui

import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest

interface MGBaseApiHelperFeature: MGApiHelperDelegate {
    var mApiHelper: MGBaseApiHelper?

    //抓取是否啟用 api 輔助物件的工具
    fun settingApiHelper() {
        mApiHelper = if (enableApiHelper()) MGBaseApiHelper(this) else null
    }

    //是否啟用 api 輔助 class
    fun enableApiHelper(): Boolean = false

    //子類別設定倒數計時狀態
    fun timerAction(action: MGBaseApiHelper.TimerAction) {
        mApiHelper?.timerAction(action)
    }

    //設定倒數計時預設時間
    fun setTimerTime(time: Long) {
        mApiHelper?.timerTime = time
    }

    override fun timesUp() {}

    //發送MGUrlRequest
    fun sendRequest(request: MGUrlRequest, requestCode: Int? = null) {
        if (requestCode != null) mApiHelper?.sendRequest(request, requestCode)
        else mApiHelper?.sendRequest(request)
    }
}