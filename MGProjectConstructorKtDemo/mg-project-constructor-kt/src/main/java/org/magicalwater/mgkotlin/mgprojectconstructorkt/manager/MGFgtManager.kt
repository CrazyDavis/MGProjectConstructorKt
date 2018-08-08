package org.magicalwater.mgkotlin.mgprojectconstructorkt.manager

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGPageInfo
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGRequestConnect
import org.magicalwater.mgkotlin.mgprojectconstructorkt.fgtHelper.MGFgtBackHelper
import org.magicalwater.mgkotlin.mgprojectconstructorkt.fgtHelper.MGFgtDataHelper
import org.magicalwater.mgkotlin.mgprojectconstructorkt.fgtHelper.MGFgtStatusHelper
import org.magicalwater.mgkotlin.mgextensionkt.removeLast
import org.magicalwater.mgkotlin.mgprojectconstructorkt.R
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgutilskt.util.MGFgtUtils

/**
 * Created by magicalwater on 2017/11/30.
 * 管理所有 fragment, 跳轉 歷史
 */

//因為有時候跳轉 Page 並不用網路 request
//所以將兩者分開
typealias MGPageData = Pair<MGUrlRequest?, MGPageInfo>

class MGFgtManager: MGRequestConnect.MGRequestCallback {

    private lateinit var manager: FragmentManager

    //最基本的fgt跳轉, 每次將歷史紀錄清除後都需要回到地俗稱首頁的頁面
    private var rootPage: MGPageData? = null

    //儲存所有的頁面跳轉歷史
    var totalHistory: MutableList<MGPageData> = mutableListOf()

    //針對每種類型頁面的 history, key 為頁面類型, value 為 MGPageData
    var pageHistory: MutableMap<Int, MutableList<MGPageData>> = mutableMapOf()

    //跳轉時的相關回調
    var delegate: TurnDelegate? = null

    //儲存 某個 layout 裡面裝載的最上層 fgt
    //不一定是顯示狀態, 但一定是最上層
    //以map的方式儲存, key: view id, value: pageInfo
    //pageInfo 是此layout id正在顯示的 fragment
    //讓外部已註冊的方式加入
    private var containerMap: MutableMap<Int, Fragment> = mutableMapOf()

    /*****************************外部接口 以下**********************************/

    //傳入 FGT 的管理器, 通常由 Activity 傳入
    fun setFgtManager(manager: FragmentManager) {
        this.manager = manager
    }

    fun setRoot(page: MGPageData?) {
        this.rootPage = page
    }

    //回到根fgt
    fun toRootPage() {
        if (rootPage != null) pageJump(rootPage!!)
    }

    //回傳最上方的page是否處理back的動作
    fun backAction(): Boolean {
        //先檢查是否擁有頁面可以檢查是否處理
        if (totalHistory.size == 0) return false

        val last = totalHistory.last()
        val fgt = MGFgtUtils.getNowShowFgt(manager, last.second.pageTag)
        return if (fgt != null && fgt is MGFgtBackHelper) {
            fgt.backPress()
        } else {
            false
        }
    }

    //回上一個 fgt, 參數回到退多少頁
    //回傳可否回上一頁, 例如: 如果當頁已經是首頁, 即無法回上頁
    fun backPage(back: Int = 1): Boolean {

        //先檢查是否擁有頁面可以跳轉
        if (totalHistory.size == 0) return false

        var pageData: MGPageData? = null

        //檢查是否還擁有頁面可回退, 如果沒有的話直接跳回首頁
        fun isNeedToRoot(): Boolean {
            if (totalHistory.size == 0) {
                toRootPage()
                return true
            }
            return false
        }

        //因為是退回上一頁, 所以我們要拿上一頁的資料進行跳轉
        //因此當回退一頁的時候, 就要刪除兩頁
        for (i in 0..back) {
            if (isNeedToRoot()) return true

            pageData = totalHistory.removeLast()

            //藉由刪除 total 最後一筆, 我們就能知道container id是多少
            //並且 pageHistory 內此 container id的最後面一筆就是我們要刪除的
            //因此直接刪除即可
            pageHistory[pageData.second.containerId]!!.removeLast()

            if (i < back) {
                hideFgt(pageData.second.pageTag)
            }
        }

        pageJump(pageData!!)
        return true
    }

    //清除所有的fragment, 通常會在此類別重新創建時使用
    fun removeAllFragment() {
        manager.inTransaction {
            manager.fragments.forEach {
                remove(it)
            }
        }
        totalHistory.clear()
        pageHistory.clear()
        containerMap.clear()
    }

    //需經過網路要求資料, 再跳頁面
    fun pageJump(request: MGUrlRequest) {
        MGRequestConnect.getData(request, 0, this)
    }

    //不用經過網路要求資料, 直接跳頁面
    fun pageJump(page: MGPageInfo) {
        pageJump(MGPageData(null, page))
    }

    //直接隱藏某個 Fragment, 並且將之從歷史紀錄移除
    fun hideFgt(fgtTag: String) {
        val fgt = MGFgtUtils.getNowShowFgt(manager, fgtTag) ?: return

        manager.inTransaction {
            setCustomAnimations(
                    R.anim.anim_fgt_alpha_in, R.anim.anim_fgt_stay,
                    R.anim.anim_fgt_alpha_in, R.anim.anim_fgt_stay
            )
            hide(fgt)
        }
        totalHistory.filter { pair ->
            val page = pair.second.page
            if (fgt.javaClass.isAssignableFrom(page.java)) {
                return@filter false
            }
            return@filter true
        }

        for ((_,v) in pageHistory) {
            v.filter { pair ->
                val pageTag = pair.second.pageTag
                if (fgtTag == pageTag) {
                    return@filter false
                }
                return@filter true
            }
        }
    }

//    //直接隱藏某個container, 檢查裡面是否擁有fgt存在, 有直接隱藏
//    fun hideContainer(@LayoutRes container: Int) {
//        page
//    }

    /*****************************外部接口 以上**********************************/

    /**
     * 功能: 跳轉頁面
     * 1. 檢查資料是否過期
     * 2. 頁面是否已有相同的 Fgt 顯示當中
     *      - 無 - 直接進行顯示
     *      - 有 - 直接送入 data
     * */
    private fun pageJump(data: MGPageData) {

        val request = data.first
        if (request != null && request.isExpired) {
            //資料過期, 需要重新發起網路 request
            pageJump(request)
            return
        }

        val pageInfo = data.second

        //檢查layout id上是否已有fragment顯示中
        //有 -> 檢查 fgt 是否相同
        //      - 是 -> 將 data 傳入, 不做其餘動作
        //      - 否 -> 初始化 fgt 並替換
        //無 -> 直接 add fgt
        var fgt: Fragment? = getFgt(pageInfo.pageTag)

        if (fgt != null) {
            //fgt 已存在
            replaceFgt(fgt, pageInfo.containerId, pageInfo.pageTag, data)

            //檢查設定, 若為節點則清除以前的歷史紀錄
            //若不加入歷史紀錄則不加入
            if (pageInfo.isChainNode) {
                totalHistory.clear()
                pageHistory.clear()
            }

            //將已有的 fgt 做置換, 若存在歷史的話
            var fgtArray = pageHistory[pageInfo.containerId]

            if (fgtArray != null) {
                //將已存在歷史的fgt拉到最上層, 若無歷史存在則直接加入
                (0 until fgtArray.size)
                        .filter { fgtArray[it].second.pageTag == pageInfo.pageTag }
                        .forEach { fgtArray.removeAt(it) }

                //total history同理
                (0 until totalHistory.size)
                        .filter { totalHistory[it].second.pageTag == data.second.pageTag }
                        .forEach { totalHistory.removeAt(it) }
            }

        } else {
            //直接生產fgt置換顯示
            fgt = MGFgtUtils.getFgt(manager, pageInfo.pageTag, pageInfo.page)

            //開始置換 fgt 顯示
            replaceFgt(fgt!!, pageInfo.containerId, pageInfo.pageTag, data)

            //檢查設定, 若為節點則清除以前的歷史紀錄
            //若不加入歷史紀錄則不加入
            if (pageInfo.isChainNode) {
                totalHistory.clear()
                pageHistory.clear()
            }

        }

        //是否將本次頁面跳轉寫入歷史
        if (pageInfo.inHistory && pageInfo.pageTag != rootPage?.second?.pageTag) {
            totalHistory.add(data)
            if (pageHistory.containsKey(pageInfo.containerId)) {
                pageHistory[pageInfo.containerId]?.add(data)
            } else {
                pageHistory[pageInfo.containerId] = mutableListOf(data)
            }
        }

        containerMap[data.second.containerId] = fgt!!

        //頁面以跳轉, 回調
        delegate?.fgtChange(data)
    }

    //將 pageData 傳入 fgt 中, 假如有需要
    private fun putPageDataIfNeed(to: Fragment, data: MGPageData, isInit: Boolean) {
        if (to is MGFgtDataHelper) {
            //fgt 繼承了 dataHelper, 因此可以接收資料
            to.pageData(data, isInit)
        }
    }

    //置換 fgt 顯示, 回傳顯示的fgt是否初次初始化加入
    private fun replaceFgt(fgt: Fragment, layoutId: Int, tag: String, data: MGPageData): Boolean {
        var isInit = false
        manager.inTransaction {

            setCustomAnimations(
                    R.anim.anim_fgt_alpha_in, R.anim.anim_fgt_alpha_out,
                    R.anim.anim_fgt_alpha_in, R.anim.anim_fgt_alpha_out
            )

            //先檢查是否有原存在的 fgt 顯示當中
            //若有則隱藏, 當然也要檢查是否為同一個fgt
            val sourceFgt = containerMap[layoutId]
            if (sourceFgt != null && !sourceFgt.isHidden && sourceFgt != fgt) {

                //假如要被隱藏的fgt有繼承 MGFgtStatusHelper 監聽狀態
                //則傳入即將關閉的訊息
                if (sourceFgt is MGFgtStatusHelper) sourceFgt.willStatus(false)
                hide(sourceFgt)
            }

            val isFgtAdded = fgt.isAdded

            //在最後決定置換/顯示fgt之前, 將fgt需要的資料傳入
            putPageDataIfNeed(fgt!!, data, !isFgtAdded)

            if (isFgtAdded) {
                if (fgt.isHidden) show(fgt)
            } else {
                add(layoutId, fgt, tag)
            }
        }
        return isInit
    }

    //得到 某個 tag 對應的 fgt, 只有當其正在顯示當中才會回傳
    private fun getFgt(tag: String): Fragment? {
        return MGFgtUtils.getNowShowFgt(manager, tag)
    }

    //封裝 FGT 交易跳轉, 以 lambda 的方式編寫 fgt 的每次交易
    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    /**
     * 跳轉頁面的 request 回調
     * */
    override fun response(request: MGUrlRequest, requestCode: Int, success: Boolean) {
        if (delegate?.jumpResponse(request, requestCode, success) == true) return

        if (success) {
            pageJump(MGPageData(request, request.pageInfo!!))
        } else {
//            MGToastUtils.show(this, "跳轉失敗")
        }
    }

    //跳轉頁面相關回調
    interface TurnDelegate {
        //跳轉頁面回調
        fun fgtChange(pageData: MGPageData)

        //跳轉頁面包含撈取api, 得到response之後, 跳轉頁面之前回調
        //回傳代表是否攔截回調
        fun jumpResponse(request: MGUrlRequest, requestCode: Int, success: Boolean): Boolean
    }
}

