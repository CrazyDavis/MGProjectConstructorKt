package org.magicalwater.mgkotlin.mgprojectconstructorkt.connect

import org.magicalwater.mgkotlin.mgprojectconstructorkt.ui.MGBaseFgt
import kotlin.reflect.KClass

/**
 * Created by 志朋 on 2017/12/3.
 * 頁面相關資訊, 通常與 MGUrlRequest 綁一起
 */
class MGPageInfo(
        page: KClass<out MGBaseFgt>,
        containerId: Int,
        pageTag: String,
        pageTitle: String,
        inHistory: Boolean,
        needLogin: Boolean,
        isNode: Boolean,
        dataReuse: Boolean
) {

    //頁面實體化的 class
    var page: KClass<out MGBaseFgt> = page

    //頁面的類型, 為裝載 Fragment 的 view id
    var containerId: Int = containerId

    //頁面的標籤, 會設置到 FragmentManager 裡
    //因此每個頁面都必須設置
    var pageTag: String = pageTag

    //頁面的標題
    var pageTitle: String = pageTitle

    //此次跳轉是否加入歷史紀錄, 歷史紀錄用來返回上一頁
    var inHistory: Boolean = inHistory

    //是否為返回上一頁, 不可主動設定, 此參數給 FgtManager 在跳回上一頁時設定
    var isPageBack: Boolean = false

    //此頁面是否需要登入
    var needLogin: Boolean = needLogin

    //頁面是否為節點, 若是節點則會清除掉之前所有的歷史跳轉
    var isChainNode: Boolean = isNode

    //此次跳轉資料是否可重複使用
    var dataReuse: Boolean = dataReuse

    //有任何東西需要攜帶的直接放入此array
    private var attachData: MutableMap<String, Any> = mutableMapOf()


    fun addAttachData(key: String, data: Any) {
        attachData[key] = data
    }

    fun <T> getAttachData(key: String): T? {
        return attachData[key] as T
    }

    class MGPageInfoBuilder {
        private var page: KClass<out MGBaseFgt>? = null
        private var containerId: Int = 0
        private var pageTag: String = ""
        private var pageTitle: String = ""
        private var inHistory: Boolean = true
        private var needLogin: Boolean = false
        private var isChainNode: Boolean = false
        private var dataReuse: Boolean = true

        fun setPage(page: KClass<out MGBaseFgt>): MGPageInfoBuilder {
            this.page = page
            return this
        }

        fun setPageTag(tag: String): MGPageInfoBuilder {
            this.pageTag = tag
            return this
        }

        fun setContainer(viewId: Int): MGPageInfoBuilder {
            this.containerId = viewId
            return this
        }

        fun setPageTitle(title: String): MGPageInfoBuilder {
            this.pageTitle = title
            return this
        }

        fun setHistory(inHistory: Boolean): MGPageInfoBuilder {
            this.inHistory = inHistory
            return this
        }

        fun setNeedLogin(login: Boolean): MGPageInfoBuilder {
            this.needLogin = login
            return this
        }

        fun setChainNode(isNode: Boolean): MGPageInfoBuilder {
            this.isChainNode = isNode
            return this
        }

        fun setDataReuse(reusable: Boolean): MGPageInfoBuilder {
            this.dataReuse = reusable
            return this
        }


        fun build(): MGPageInfo {
            val ins = MGPageInfo(page!!,
                    containerId,
                    pageTag,
                    pageTitle,
                    inHistory,
                    needLogin,
                    isChainNode,
                    dataReuse)
            return ins
        }

    }

}