package org.magicalwater.mgkotlin.mgprojectconstructorkt.helper

import android.app.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.app.Fragment
import org.magicalwater.mgkotlin.mgutilskt.util.MGImgLoadUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGPhotoPickerUtils

/**
 * Created by magicalwater on 2018/2/6.
 * 封裝圖片選擇(照相)的類別, 需傳入activity或者fgt
 */
class MGPhotoPickerHelper {

    private var activity: Activity? = null
    private var fragment: Fragment? = null

    //選擇圖片的返回code
    private val REQUEST_CODE_PHOTO_PICKER: Int = 100
    private val REQUEST_CODE_PHOTO_CROP: Int = 101

    private var photoAttr: PickerAttr? = null

    var delegate: PhotoPickerDelegate? = null

    /**
     * 選擇圖片相關屬性(是否裁減, 若需要裁減, 那麼寬高分別是多少)
     * 是否裁減
     *      - 是: 寬高分別是多少(同時width跟height必須有值)
     *      - 否: 縮放圖片到怎樣的寬高
     * */
    data class PickerAttr(val needCrop: Boolean, val width: Int?, val height: Int?)

    //開始選擇圖片, 無論是相機或者圖庫
    fun startSelectPhoto(activity: Activity, attr: PickerAttr) {
        this.fragment = null
        this.activity = activity
        this.photoAttr = attr

        val it = MGPhotoPickerUtils.getPhotoSelectIntent(null)
        activity.startActivityForResult(it, REQUEST_CODE_PHOTO_PICKER)
    }

    fun startSelectPhoto(fragment: Fragment, attr: PickerAttr) {
        this.activity = null
        this.fragment = fragment
        this.photoAttr = attr

        val it = MGPhotoPickerUtils.getPhotoSelectIntent(null)
        fragment.startActivityForResult(it, REQUEST_CODE_PHOTO_PICKER)
    }


    //將頁面回傳設定到此, 參數完全依照 onActivityResult 的參數設定
    fun sendActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val aty = activity
        val fgt = fragment
        val attr = photoAttr
        if(resultCode != -1 || attr == null || (aty == null && fgt == null)) return

        when (requestCode) {
            REQUEST_CODE_PHOTO_PICKER -> {
                // 處理圖片選擇結果
                if (data != null){
                    //返回圖片進行處理, 檢查是否需要剪裁, 當需要剪裁時, width跟height必定有值
                    if (attr.needCrop) {
                        val it = MGPhotoPickerUtils.getImageCropIntent(
                                data.data, null,
                                attr.width!!, attr.height!!
                        )
                        if (aty != null) aty.startActivityForResult(it, REQUEST_CODE_PHOTO_CROP)
                        else fgt?.startActivityForResult(it, REQUEST_CODE_PHOTO_CROP)
                    } else {
                        //不需剪裁, 得到圖片uri, 藉由uri獲得圖片, 並且清除activity
                        activity = null
                        fragment = null

                        MGImgLoadUtils.loadBitmap(aty ?: fgt!!.context!!, data.data, MGImgLoadUtils.ImageAttr(attr.width, attr.height)) { bmp ->
                            delegate?.pickerBmp(bmp)
                        }
                    }
                } else {
//                    val saveUri = Uri.fromFile(userInfo.login.headerIconPath)
//                    MGPhotoPickerUtils.getImageCropIntent(
//                            saveUri, null,
//                            SizeManager.headerIcon, SizeManager.headerIcon
//                    )
                }
            }
            REQUEST_CODE_PHOTO_CROP -> {
                // 處理圖片裁減結果, 無論結果如何, 清除aty
                activity = null
                fragment = null
                if (data == null) {
                    return
                } else {
                    val extras = data.extras
                    if (extras != null) {
                        //得到裁剪後的圖像
                        val bm = extras.getParcelable<Bitmap>("data")
                        delegate?.pickerBmp(bm)
                    }
                }
            }
        }
    }


    interface PhotoPickerDelegate {
        fun pickerBmp(bmp: Bitmap)
    }
}