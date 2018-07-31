package org.magicalwater.mgkotlin.mgprojectconstructorkt.fgtHelper

/**
 * Created by 志朋 on 2017/12/11.
 * 幫助 Fgt 在顯示, 隱藏時的調用
 */
interface MGFgtStatusHelper {

    //即將顯示或隱藏
    fun willStatus(show: Boolean)

    //已經顯示或隱藏
    fun didStatus(show: Boolean)
}