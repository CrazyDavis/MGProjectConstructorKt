package org.magicalwater.mgkotlin.mgprojectconstructorkt.manager

import android.graphics.Color
import android.view.ViewGroup
import org.magicalwater.mgkotlin.mgextensionkt.px
import org.magicalwater.mgkotlin.mgprojectconstructorkt.util.MGCodeScanDelegate
import org.magicalwater.mgkotlin.mgprojectconstructorkt.util.MGCodeScanUtils

/**
 * 條碼掃描管理, 目前使用 bga 的 lib
 * 其包含了 QRCode 與 BarCode
 * */
class MGCodeScanManager(layout: ViewGroup) {

    //傳入ViewGroup, 表示預覽相機的frame
    private var mParentLayout: ViewGroup = layout
    var delegate: MGCodeScanDelegate?
        get() = mCodeScanUtils.delegate
        set(value) { mCodeScanUtils.delegate = value }

    private val mCodeScanUtils: MGCodeScanUtils by lazy {
        MGCodeScanUtils(mParentLayout)
    }

    private val mDefaultStyle: MGCodeScanUtils.ScanStyle by lazy {
        val maskColor = Color.parseColor("#33FFFFFF")
        val borderColor = Color.parseColor("#FFA185")
        val cornerColor = Color.parseColor("#85CCFF")
        val scanLine = Color.parseColor("#FFA133")
        MGCodeScanUtils.ScanStyle(
                MGCodeScanUtils.Type.QR, "請將 QRCode 對準框內",
                maskColor,
                MGCodeScanUtils.Border(5.px, borderColor),
                MGCodeScanUtils.Corner(10.px, 20.px, cornerColor),
                MGCodeScanUtils.ScanLine(scanLine, null)
        )
    }

    fun setStyle(style: MGCodeScanUtils.ScanStyle = mDefaultStyle) {
        mCodeScanUtils.setting(style)
    }

    fun startScan() {
        mCodeScanUtils.startScan()
    }
    fun stopScan() = mCodeScanUtils.stopScan()
}

