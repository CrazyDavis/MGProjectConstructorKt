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
abstract class MGBaseFgt: Fragment(), MGFgtDataHelper, MGBaseFgtHelperFeature, MGBaseApiHelperFeature {

    override var mApiHelper: MGBaseApiHelper? = null
    override var mFgtHelper: MGBaseFgtHelper? = null

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(contentLayout(), container, false)
    }

    final override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settingApiHelper()
        settingFgtManager()
        setupView()
    }

    abstract fun setupView()

    abstract fun contentLayout() : Int

    /**
     * @param pageInfo  跳轉進入此Fgt的 頁面資料
     * @param isFgtInit   此頁面是否正要初始化, 或者頁面已經存在只是傳入資料
     * */
//    override fun pageData(data: MGPageData, isFgtInit: Boolean)

}