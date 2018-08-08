package org.magicalwater.mgkotlin.mgprojectconstructorktdemo

import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgprojectconstructorkt.manager.MGPageData
import org.magicalwater.mgkotlin.mgprojectconstructorkt.ui.MGBaseFgt

class HomeFgt: MGBaseFgt() {
    override fun setupView() {
    }

    override fun contentLayout(): Int = R.layout.fgt_main

    override fun pageData(data: MGPageData, isFgtInit: Boolean) {
    }

    override fun response(request: MGUrlRequest, success: Boolean, requestCode: Int) {
    }
}