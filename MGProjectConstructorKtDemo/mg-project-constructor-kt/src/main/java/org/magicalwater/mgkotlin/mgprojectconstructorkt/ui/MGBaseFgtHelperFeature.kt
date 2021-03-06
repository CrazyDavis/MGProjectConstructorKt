package org.magicalwater.mgkotlin.mgprojectconstructorkt.ui

import android.support.v4.app.FragmentManager
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGPageInfo
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGFgtManager
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGPageData

interface MGBaseFgtHelperFeature {
    var mFgtHelper: MGBaseFgtHelper?

    //是否啟用 fgt管理 輔助 class
    fun enableFgtManager(): Boolean = false
    fun fragmentManager(): FragmentManager? = null

    //抓取是否啟用 api 輔助物件的工具
    fun settingFgtManager() {
        //重新設定FgtManager需要先清除原先存在的page(假如有的話)
        removeAllPage()
        val manager = fragmentManager()
        if (manager != null) {
            //設定之前同樣清除所有歷史
            mFgtHelper = if (enableFgtManager()) MGBaseFgtHelper() else null
            mFgtHelper?.setFragmentManager(manager)
            removeAllPage()
        } else {
            mFgtHelper = null
        }
    }

    //設定 fgt manager 的 root page
    fun setRootPage(page: MGPageData?) {
        mFgtHelper?.setRootPage(page)
    }

    //跳轉到某個 Fragment, 可供複寫, 為的在跳轉前的執行動作
    fun fgtShow(request: MGUrlRequest) {
        mFgtHelper?.show(request)
    }

    //顯示某個頁面, 不用經過網路
    fun fgtShow(pageInfo: MGPageInfo) {
        mFgtHelper?.show(pageInfo)
    }

    //顯示某個頁面, 直接帶入Any類型的class
    //在這裡面自動判斷是否為 request 或者 pageInfo類型的跳轉
    fun fgtShow(data: Any) {
        when (data) {
            is MGPageInfo -> fgtShow(data)
            is MGUrlRequest -> fgtShow(data)
            else -> println("跳轉頁面帶入資料必須是 MGPageInfo 或者 MGUrlRequest 類型")
        }
    }

    //隱藏某個頁面
    fun fgtHide(fgtTag: String) {
        mFgtHelper?.hide(fgtTag)
    }

    //得到目前最頂層的page
    fun getTopPage(): MGPageData? {
        return mFgtHelper?.getTopPage()
    }

    fun setFgtDelegate(delegate: MGFgtManager.TurnDelegate) {
        mFgtHelper?.setTurnPageDelegate(delegate)
    }

    fun toRootPage() {
        mFgtHelper?.toRootPage()
    }

    //清除所有畫面
    fun removeAllPage() {
        mFgtHelper?.removeAllPage()
    }

    //回退上一頁fragment, 回傳代表是否處理 back
    fun backPage(back: Int = 1): Boolean {
        return mFgtHelper?.backPage() ?: false
    }
}