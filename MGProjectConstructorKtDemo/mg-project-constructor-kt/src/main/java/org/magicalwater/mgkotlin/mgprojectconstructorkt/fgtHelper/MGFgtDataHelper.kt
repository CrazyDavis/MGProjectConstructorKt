package org.magicalwater.mgkotlin.mgprojectconstructorkt.fgtHelper

import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGPageData

/**
 * Created by 志朋 on 2017/12/11.
 * 讓 FGT 繼承, 以便讓 FgtManager 呼叫設定資料
 */
interface MGFgtDataHelper {
    fun pageData(data: MGPageData, isFgtInit: Boolean = true)
}