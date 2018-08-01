package org.magicalwater.mgkotlin.mgprojectconstructorkt.ui

import android.support.v4.app.FragmentManager
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGPageInfo
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGFgtManager
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGPageData

class MGBaseFgtHelper {
    private var fgtManager: MGFgtManager = MGFgtManager()

    fun setFragmentManager(manager: FragmentManager): MGBaseFgtHelper {
        fgtManager.setFgtManager(manager)
        return this
    }

    fun setRootPage(page: MGPageData?): MGBaseFgtHelper {
        fgtManager.setRoot(page)
        return this
    }

    fun setTurnPageDelegate(delegate: MGFgtManager.TurnDelegate): MGBaseFgtHelper {
        fgtManager.delegate = delegate
        return this
    }

    fun toRootPage() {
        fgtManager.toRootPage()
    }

    //回退上一頁fragment, 回傳代表是否處理 back
    //目前默認回退一頁, 請別傳入參數
    fun backPage(back: Int = 1): Boolean {
        //先檢查最上層的fgt是否處理back的動作, 當back數量等於1時
        return if (back == 1 && fgtManager.backAction()) {
            true
        } else {
            fgtManager.backPage(back)
        }
    }

    //得到目前最頂層的page
    fun getTopPage(): MGPageData? {
        return if (fgtManager.totalHistory.isNotEmpty()) {
            fgtManager.totalHistory.last()
        } else {
            null
        }
    }

    //跳轉到某個 Fragment, vm ru/ eji4jp;3xj4

    fun show(request: MGUrlRequest) {
        fgtManager.pageJump(request)
    }

    //顯示某個頁面, 不用經過網路
    fun show(pageInfo: MGPageInfo) {
        fgtManager.pageJump(pageInfo)
    }

    //隱藏某個頁面
    fun hide(fgtTag: String) {
        fgtManager.hideFgt(fgtTag)
    }

}