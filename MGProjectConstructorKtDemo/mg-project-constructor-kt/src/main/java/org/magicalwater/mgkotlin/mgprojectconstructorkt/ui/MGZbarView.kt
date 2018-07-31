package org.magicalwater.mgkotlin.mgprojectconstructorkt.ui

import android.content.Context
import android.util.AttributeSet
import cn.bingoogolapple.qrcode.zbar.ZBarView
import org.magicalwater.mgkotlin.mgprojectconstructorkt.util.MGCodeScanUtils

//自訂擴展zbarView
class MGZbarView: ZBarView {
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(context, attributeSet, defStyleAttr)

    fun setStyle(scanStyle: MGCodeScanUtils.ScanStyle) {
        mScanBoxView.borderColor = scanStyle.border.bordColor
        mScanBoxView.borderSize = scanStyle.border.borderSize
        mScanBoxView.cornerColor = scanStyle.corner.cornerColor
        mScanBoxView.cornerLength = scanStyle.corner.cornerLength
        mScanBoxView.cornerSize = scanStyle.corner.cornerWidth

        mScanBoxView.scanLineColor = scanStyle.scanLine.color
        mScanBoxView.maskColor = scanStyle.maskColor
        val customDrawableRes = scanStyle.scanLine.customDrawableResource
        if (customDrawableRes != null) {
            mScanBoxView.customScanLineDrawable = resources.getDrawable(customDrawableRes)
        } else {
            mScanBoxView.customScanLineDrawable = null

            //需要用反射將此值設定成null, 否則 scaleLine 不起作用
            val fieldName = "mOriginQRCodeScanLineBitmap"
            val mOriginQRCodeScanLineBitmapField = mScanBoxView.javaClass.getDeclaredField(fieldName)
            mOriginQRCodeScanLineBitmapField.isAccessible = true
            mOriginQRCodeScanLineBitmapField.set(mScanBoxView, null)
        }

        when (scanStyle.type) {
            MGCodeScanUtils.Type.BAR -> {
                mScanBoxView.barCodeTipText = scanStyle.tipText
                mScanBoxView.isBarcode = true
            }
            MGCodeScanUtils.Type.QR -> {
                mScanBoxView.qrCodeTipText = scanStyle.tipText
                mScanBoxView.isBarcode = false
            }
        }

        /**
         * 因為 scan line 圖片的設置只在 "私有" 方法 afterInitCustomAttrs 裡面
         * 所以必須使用反射調用, 此方法同時會同步所有屬性, 所以不用再調用其餘的刷新方法
         *
         * 但是scan line仍然不起作用, 不知道是什麼原因
         * */
        val afterInitCustomAttrsMethod = mScanBoxView.javaClass.getDeclaredMethod("afterInitCustomAttrs")
        afterInitCustomAttrsMethod.isAccessible = true
        afterInitCustomAttrsMethod.invoke(mScanBoxView)
    }
}
