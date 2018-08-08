package org.magicalwater.mgkotlin.mgprojectconstructorktdemo

import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGPageInfo

class PageBuilder {
    companion object {
        fun buildHome(): MGPageInfo {
            val pi = MGPageInfo.MGPageInfoBuilder()
                    .setHistory(true)
                    .setContainer(PageContainer.MAIN)
                    .setPage(HomeFgt::class)
                    .setPageTag(Page.HOME)
                    .build()
            return pi
        }
    }
}

fun MGPageInfo.MGPageInfoBuilder.setPageTag(tag: PageTag): MGPageInfo.MGPageInfoBuilder {
    return setPageTag(tag.tag)
            .setPageTitle(tag.title)
}