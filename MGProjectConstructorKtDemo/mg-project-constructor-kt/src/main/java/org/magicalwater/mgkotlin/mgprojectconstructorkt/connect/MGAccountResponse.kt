package org.magicalwater.mgkotlin.mgprojectconstructorkt.connect

import android.support.annotation.Keep
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by 志朋 on 2017/12/9.
 * 網路上的朋友使用 kotlin 對於 okhttp3 的封裝
 * 原封裝作者: ngudream
 * 網址: http://ngudream.com/2017/06/20/kotlin-okhttp/
 * 這個是返回資料的部分
 *
 * 此類 與 MGAccountHttpClient 一套
 */

@Keep //不混淆此類別
class MGAccountResponse {
    var statusCode: Int = 0
        private set
    private var responseAsString: String? = null
    private var responseAsBytes: ByteArray? = null
    private var headers: MutableMap<String, MutableList<String>>? = null
    /**
     * 獲取InputStream中的數據結構
     * 注意, 這個建構子會直接讀取並且關閉InputStream, 後續無法再對InputStream進行操作
     */
    @Throws(IOException::class)
    constructor(statusCode: Int, datas: ByteArray?, headers: MutableMap<String, MutableList<String>>?) {
        this.statusCode = statusCode
        // 在这里获取stream的数据，因为该方法之后stream会close掉
        this.responseAsBytes = datas
        this.headers = headers
    }
    constructor(content: String, responseCode: Int) {
        responseAsString = content
        statusCode = responseCode
    }
    fun getResponseAsString(): String? {
        responseAsString = responseAsBytes?.toString(Charset.forName("utf-8"))
        return responseAsString
    }
    fun getHeadersByName(name: String): MutableList<String>? {
        if (headers != null) {
            return headers?.get(name)
        }
        return null
    }

    fun getHeaders(): MutableMap<String, MutableList<String>>? {
        return headers
    }

    override fun toString(): String {
        if (null != getResponseAsString()) {
            return responseAsString?: ""
        }
        return "Response{" +
                "statusCode=" + statusCode +
                ", responseString='" + responseAsString +
                '}'
    }
}