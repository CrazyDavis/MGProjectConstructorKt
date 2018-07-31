package org.magicalwater.mgkotlin.mgprojectconstructorkt.fgtHelper

/**
 * Created by magicalwater on 2018/1/5.
 * 當fgt繼承 此 helper 時, 點選backpressed會呼叫此方法
 * 回傳代表是否處理backpressed
 */
interface MGFgtBackHelper {
    fun backPress(): Boolean
}