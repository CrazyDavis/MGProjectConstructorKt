package org.magicalwater.mgkotlin.mgprojectconstructorkt.util

import android.view.ViewGroup
import cn.bingoogolapple.qrcode.core.QRCodeView
import org.jetbrains.anko.layoutInflater
import org.magicalwater.mgkotlin.mgprojectconstructorkt.R
import org.magicalwater.mgkotlin.mgprojectconstructorkt.ui.MGZbarView
import org.magicalwater.mgkotlin.mgutilskt.util.MGLogUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGToastUtils

class MGCodeScanUtils(parent: ViewGroup) {

    //目前ZBarView不支持直接code初始化的方式, 所有東西需要在第一部layou直接做好
    private var view: MGZbarView = parent.context.layoutInflater.inflate(R.layout.widget_zbarview, parent, false) as MGZbarView
    var delegate: MGCodeScanDelegate? = null

    init {
        parent.addView(view)

        view.setDelegate(object : QRCodeView.Delegate {
            //掃描到結果
            override fun onScanQRCodeSuccess(result: String?) {
                MGLogUtils.d("掃描成功 - 內容: $result")
                if (delegate?.scanSuccess(result ?: "") != false) {
                    stopScan()
                }
            }
            //掃描發生錯誤
            override fun onScanQRCodeOpenCameraError() {
                MGToastUtils.show(view.context, "掃描發生錯誤")
                delegate?.scanErr()
            }
        })
    }

    fun setting(scanStyle: ScanStyle) {
        view.setStyle(scanStyle)
    }

    //一進來此頁面即刻啟動相機預覽, 並且開啟qrcode掃描
    fun startScan() {
        view.startCamera()
        view.startSpotAndShowRect()
    }

    //離開頁面時要呼叫此方法關閉qrcode掃描, 也關閉相機預覽
    fun stopScan() {
        view.stopSpotAndHiddenRect()
        view.stopCamera()
    }

    //掃描的類型, 是條碼還是二維碼
    enum class Type {
        BAR,
        QR
    }

    /**
     * 邊框的設置
     * @param borderSize - 寬度
     * @param bordColor - 顏色
     * */
    data class Border(var borderSize: Int, var bordColor: Int)

    /**
     * 角邊框的設置
     * @param cornerWidth - 寬度
     * @param cornerLength - 長度
     * @param cornerColor - 顏色
     * */
    data class Corner(var cornerWidth: Int, var cornerLength: Int, var cornerColor: Int)

    /**
     * 中央掃描線的顏色
     * @param color - 掃描線顏色
     * @param color - 自訂掃描線的樣式, 當設定此值後 color 失效
     * */
    data class ScanLine(var color: Int, var customDrawableResource: Int? = null)

    /**
     * @param type - 掃描類型
     * @param tipText - 上方提示文字
     * @param maskColor - 除中央掃描框外的顏色, 通常設定有透明值
     * @param border - 邊框樣式
     * @param corner - 角邊框樣式
     * @param scanLine - 掃描線樣式
     * */
    data class ScanStyle(var type: Type,
                         val tipText: String,
                         val maskColor: Int,
                         var border: Border,
                         var corner: Corner,
                         var scanLine: ScanLine)
}

interface MGCodeScanDelegate {
    //掃描之後相機會停止, 回傳是否關閉相機
    fun scanSuccess(result: String): Boolean
    fun scanErr()
}