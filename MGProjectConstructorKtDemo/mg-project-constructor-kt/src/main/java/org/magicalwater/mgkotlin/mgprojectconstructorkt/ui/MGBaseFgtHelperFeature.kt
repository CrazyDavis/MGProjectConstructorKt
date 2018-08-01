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
        val manager = fragmentManager()
        if (manager != null) {
            mFgtHelper = if (enableFgtManager()) MGBaseFgtHelper() else null
            mFgtHelper?.setFragmentManager(manager)
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

    //回退上一頁fragment, 回傳代表是否處理 back
    fun backPage(back: Int = 1): Boolean {
        return mFgtHelper?.backPage() ?: false
    }
}