package org.magicalwater.mgkotlin.mgprojectconstructorkt.connect

import android.graphics.Bitmap
import kotlin.reflect.KClass

/**
 * Created by 志朋 on 2017/12/10.
 * 構件網路要求的內容
 * 使用的連接類型 (GET, POST)
 * 連接的網址 (URL)
 */
class MGRequestContent(
        scheme: MGRequestContent.Scheme,
        host: String,
        path: String,
        method: MGRequestContent.Method = MGRequestContent.Method.GET
) {

    var scheme: String = scheme.value // http 或者 https
    var host: String = host
    var path: String = path
    var method: Method = method

    //要求頭
    var headers: MutableList<Pair<String, String>>? = null

    //要求的參數
    var params: List<Pair<String, String>>? = null
    get() {
        if (paramSource.isEmpty()) {
            return null
        }
        var l = mutableListOf<Pair<String,String>>()
        for ((k,v) in paramSource) {
            when(v) {
                is String -> {
                    val pair = Pair(k,v)
                    l.add(pair)
                }

                else -> {
                    val map = v as MutableMap<String,String>
                    for ((num, innerV) in map) {
                        val pair = Pair("$k[$num]", innerV)
                        l.add(pair)
                    }
                }
            }
        }
        return l
    }

    //需要上傳的檔案
    var uploadDatas: List<Pair<String, Any>>? = null
        get() {
            if (uploadSource.isEmpty()) {
                return null
            }
            var l = mutableListOf<Pair<String,Any>>()
            for ((k,v) in uploadSource) {
                when(v) {
                    is String -> {
                        var pair = Pair(k,v)
                        l.add(pair)
                    }

                    is Bitmap -> {
                        var pair = Pair(k,v)
                        l.add(pair)
                    }

                    else -> {
                        val map = v as MutableMap<String,Any>
                        for ((num, innerV) in map) {
                            var pair = Pair("$k[$num]", innerV)
                            l.add(pair)
                        }
                    }
                }
            }
            return l
        }

    //通常搭配資料庫lib, 是否從本地資料庫拉出相對應的 class 所儲存的資料
    var locale: MGLocalCache = MGLocalCache() //本地的快取設定, 默認關閉

    //發起 request 時是否要快取
    var network: Boolean = true //網路的快取設定, 默認開啟

    var contentHandler: MGContentHandler = MGContentHandler() //得到回傳後需要做的動作, 需要反序列化的 class 或者 下載到目的路徑

    //這邊處存內部已有的 param key, 對應到已經加入多少個, 方便取出時加入陣列字串
    private var paramSource: MutableMap<String, Any> = mutableMapOf()

    //同 paramSource, 差別在於此參數專給 paramSource
    private var uploadSource: MutableMap<String, Any> = mutableMapOf()

    //設定下載到哪個地方(包含檔名)
    fun setSaveInPath(path: String): MGRequestContent {
        contentHandler.saveInPath = path
        return this
    }

    //設定反序列化成哪個class
    fun setDeserialize(deserialize: KClass<out Any>): MGRequestContent {
        contentHandler.deserialize = deserialize
        return this
    }

    //加入參數
    fun addParam(key: String, value: String, array: Boolean): MGRequestContent {

        if (array) {
            var innerArray: MutableMap<String,Any> =
                    if (paramSource.containsKey(key)) paramSource[key] as MutableMap<String, Any>
                    else mutableMapOf()
            innerArray[innerArray.size.toString()] = value
            paramSource[key] = innerArray
        } else {
            paramSource[key] = value
        }

        return this
    }

    fun addParams(key: String, value: List<String>): MGRequestContent {
        value.forEach {
            addParam(key, it, true)
        }
        return this
    }


    fun addUpload(key: String, value: Any, array: Boolean): MGRequestContent {
        if (array) {
            var innerArray: MutableMap<String,Any> =
                    if (uploadSource.containsKey(key)) uploadSource[key] as MutableMap<String, Any>
                    else mutableMapOf()
            innerArray[innerArray.size.toString()] = value
            uploadSource[key] = innerArray
        } else {
            uploadSource[key] = value
        }
        return this
    }

    fun addUpload(key: String, value: List<Any>): MGRequestContent {
        value.forEach {
            addUpload(key, it, true)
        }
        return this
    }


    override fun toString(): String {
        return "連線($method) - 位址: $scheme://$host$path, 參數: $params"
    }

    //是 http 還是 https
    enum class Scheme constructor(val value: String) {
        HTTP("http"),
        HTTPS("https")
    }

    //Requst Method
    enum class Method {
        GET, POST
    }

    //得到文件後是 下載/反序列化
    class MGContentHandler {
        var saveInPath: String? = null //包含檔名
        var deserialize: KClass<out Any>? = null

        constructor()

        constructor(saveInPath: String) {
            this.saveInPath = saveInPath
        }
        constructor(deserialize: KClass<out Any>?) {
            this.deserialize = deserialize
        }
    }

    //本地的快取設定
    data class MGLocalCache(private val loadLocal: Boolean = false, private val saveLocal: Boolean = false) {
        var load: Boolean = loadLocal
        var save: Boolean = saveLocal
    }

}