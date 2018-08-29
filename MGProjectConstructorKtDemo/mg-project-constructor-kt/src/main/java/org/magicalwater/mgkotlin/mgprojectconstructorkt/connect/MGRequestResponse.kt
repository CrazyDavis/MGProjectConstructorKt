package org.magicalwater.mgkotlin.mgprojectconstructorkt.connect

import android.support.annotation.Keep
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Created by 志朋 on 2017/12/9.
 * 網路上的朋友使用 kotlin 對於 okhttp3 的封裝
 * 原封裝作者: ngudream
 * 網址: http://ngudream.com/2017/06/20/kotlin-okhttp/
 * 這個是返回資料的部分
 *
 * 此類 與 MGOkHttpClient 一套
 */

@Keep //不混淆此類別
class MGRequestResponse {
    var statusCode: Int = 0
        private set
    private var responseAsString: String? = null
    private var responseAsBytes: ByteArray? = null
    private var headers: MutableMap<String, MutableList<String>>? = null

    @Throws(IOException::class)
    constructor(statusCode: Int, datas: ByteArray?, headers: MutableMap<String, MutableList<String>>?) {
        this.statusCode = statusCode
        this.responseAsBytes = datas
        this.headers = headers
    }

    //當目的是下載檔案時, 呼叫此建構式, 因為不會有資料回傳
    constructor(statusCode: Int, headers: MutableMap<String, MutableList<String>>?) {
        this.statusCode = statusCode
        this.headers = headers
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