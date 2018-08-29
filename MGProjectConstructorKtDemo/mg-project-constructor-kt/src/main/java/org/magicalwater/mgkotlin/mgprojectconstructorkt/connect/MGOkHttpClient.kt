package org.magicalwater.mgkotlin.mgprojectconstructorkt.connect

import android.graphics.Bitmap
import okhttp3.*
import okhttp3.internal.tls.OkHostnameVerifier
import java.io.*
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLSession


/**
 * Created by 志朋 on 2017/12/9.
 * 原封裝作者: ngudream
 * 網址: http://ngudream.com/2017/06/20/kotlin-okhttp/
 *
 * 此類 與 AccountRespnse 一套
 */
class MGOkHttpClient private constructor() {

    private val mOkHttpCilent: OkHttpClient

    companion object {
        private var CONNECTION_TIME_OUT = 30 * 1000
        private var READ_TIME_OUT = 30 * 1000
        private var WRITE_TIME_OUT = 30 * 1000
        val SHARED: MGOkHttpClient by lazy { MGOkHttpClient() }
    }

    /**
     * 1. 在此初始化相關參數
     * 2. 保持 cookie
    */
    init {
        mOkHttpCilent = OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .hostnameVerifier(AccountHostnameVerifier())
                .cookieJar(object : CookieJar {
                    private var cookieStore: MutableMap<String, List<Cookie>> = mutableMapOf()
                    override fun loadForRequest(url: HttpUrl?): MutableList<Cookie> {
                        if (url != null) {
                            val cookies: List<Cookie>? = cookieStore[url.host()]
                            return cookies?.toMutableList() ?: mutableListOf()
                        }
                        return mutableListOf()
                    }

                    override fun saveFromResponse(url: HttpUrl?, cookies: MutableList<Cookie>?) {
                        if (url != null && cookies != null)
                            cookieStore.put(url.host(), cookies)
                    }

                })
                .build()
    }

    /**
     * 執行請求, 依照 MGRequestContent 進行的封裝
     */
    fun execute(content: MGRequestContent): MGRequestResponse? {
        var result: MGRequestResponse? = null
        try {
            var okRequest = MGOKRequest(content)
            val okResponse = performRequest(okRequest) //進行參數封裝, 發起網路請求
            if (okResponse != null) {
                //依照需求, 假如是下載檔案的話, 就將 inputStream 直接寫入下載位置
                when {
                    content.contentHandler.saveInPath != null -> {
                        outputData(okResponse.getBodyAsInputStream(), content.contentHandler.saveInPath!!)
                        result = MGRequestResponse(okResponse.responseCode, okResponse.headers)
                    }
                    content.contentHandler.deserialize != null -> {
                        result = MGRequestResponse(okResponse.responseCode, okResponse.getBodyAsByteArray(), okResponse.headers)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    //從inputstream輸出資料到目的儲存
    private fun outputData(inputStream: InputStream?, target: String) {
        if (inputStream != null) {
            val outputStream = FileOutputStream(target)
            val byteBuffer = ByteArray(1024)
            var bytesRead: Int = inputStream.read(byteBuffer)
            while (bytesRead != -1) {
                outputStream.write(byteBuffer, 0, bytesRead)
                bytesRead = inputStream.read(byteBuffer)
            }
            inputStream.close()
            outputStream.flush()
            outputStream.close()
        }
    }

    @Throws(IOException::class)
    private fun performRequest(request: MGOKRequest): OKResponse? {
        val call = mOkHttpCilent.newCall(request.getDataRequest())
        return OKResponse(call.execute())
    }

    private inner class MGOKRequest(private var content: MGRequestContent) {

        fun getDataRequest(): Request {
            val request: Request
            val requestBuilder = Request.Builder()

            requestBuilder.header("Connection", "Close")

            //先加入header
            if (content.headers?.isNotEmpty() == true) {
                for (header in content.headers!!) {
                    requestBuilder.addHeader(header.first, header.second)
                }
            }

            //創建 url builder 設定相關參數
            val httpBuilder = HttpUrl.Builder()
                    .scheme(content.scheme)
                    .host(content.host)
                    .encodedPath(content.path)

            request = when (content.method) {
                MGRequestContent.Method.GET -> buildGetRequest(requestBuilder, httpBuilder)
                MGRequestContent.Method.POST -> buildPostRequest(requestBuilder, httpBuilder)
            }

            return request
        }

        private fun buildGetRequest(requestBuilder: Request.Builder, httpBuilder: HttpUrl.Builder): Request {
            content.params?.let {
                for (param in it) {
                    httpBuilder.addQueryParameter(param.first, param.second)
                }
            }
            val httpUrl = httpBuilder.build()
            requestBuilder.url(httpUrl)
            requestBuilder.get()
            return requestBuilder.build()
        }


        //post需要再判斷是否有檔案需要上傳
        private fun buildPostRequest(requestBuilder: Request.Builder, httpBuilder: HttpUrl.Builder): Request {
            //POST需要檢測是否有檔案需要上傳
            val httpUrl = httpBuilder.build()
            requestBuilder.url(httpUrl)

            val uploadDatas = content.uploadDatas
            if (uploadDatas != null) {
                //有檔案需要上傳
                val bodyBuilder = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)

                uploadDatas.forEach {
                    val uploadName = it.first
                    val uploadData = it.second
                    var requestBody: RequestBody? = null

                    //目前只支持Bitmap, String, byte Array的上傳
                    when (uploadData) {
                        is Bitmap -> { //圖檔直接轉為byte array
                            val stream = ByteArrayOutputStream()
                            uploadData.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            val byteArray = stream.toByteArray()
                            val type = MediaType.parse("image/png")
                            requestBody = RequestBody.create(type, byteArray)
                        }
                        is String -> {
                            requestBody = RequestBody.create(null, uploadData)
                        }
                        is ByteArray -> {
                            requestBody = RequestBody.create(null, uploadData)
                        }
                    }

                    if (requestBody != null)
                        bodyBuilder.addFormDataPart(uploadName, "file", requestBody!!)
                }

                //加入參數
                content.params?.let {
                    for (param in it) {
                        bodyBuilder.addFormDataPart(param.first, param.second)
                    }
                    requestBuilder.post(bodyBuilder.build())
                }

                requestBuilder.header("Content-Type", "multipart/form-data")
            } else {
                //加入post參數
                content.params?.let {
                    val formBuilder = FormBody.Builder()
                    for (param in it) {
                        formBuilder.add(param.first, param.second)
                    }
                    requestBuilder.post(formBuilder.build())
                }
            }

            return requestBuilder.build()
        }
    }

    /**
     * 響應
     */
    private inner class OKResponse(private val response: Response?) {
        val responseCode: Int
            get() = response?.code() ?: -1
        private var body: ResponseBody? = response?.body()

        //bodyByteArray呼叫了之後就會直接關閉 InputStream
        //在這邊直接把body設置為null表示不可再取
        private var bodyByteArray: ByteArray? = null

        val headers: MutableMap<String, MutableList<String>>?
            get() {
                var headers: MutableMap<String, MutableList<String>>? = null
                if (response?.headers() != null) {
                    try {
                        headers = response.headers().toMultimap()
                    } catch (e: Exception) { }
                }
                return headers
            }

        fun getBodyAsByteArray(): ByteArray? {
            if (bodyByteArray != null) {
                return bodyByteArray
            }
            var result: ByteArray? = null
            if (body != null) {
                try {
                    result = body!!.bytes()
                } catch (e: IOException) {
                    println("api request出錯, 訊息 = ${e.message}")
                }
                body = null
            }
            return result
        }

        //此方法只能呼叫一次, 之後都回傳null
        //拿一次之後如果不當下操作完畢必須外部自行尋找位置存放
        fun getBodyAsInputStream(): InputStream? {
            var inputStream: InputStream? = null
            if (body != null) {
                try {
                    inputStream = body!!.byteStream()
                } catch (e: IOException) {
                    println("api request出錯, 訊息 = ${e.message}")
                }
                body = null
            }
            return inputStream
        }
    }

    /**
     * 證書驗證
     */
    private inner class AccountHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            var result = false
            try {
                val certs = session.peerCertificates as Array<X509Certificate>
                if (certs.isNotEmpty()) {
                    for (i in certs.indices) { //取出所有證書進行驗證
                        result = OkHostnameVerifier.INSTANCE.verify(hostname, certs[i])
                        if (result) break
                    }
                } else {
                    result = true
                }
            } catch (e: SSLPeerUnverifiedException) {
                e.printStackTrace()
            }
            return result
        }
    }
}