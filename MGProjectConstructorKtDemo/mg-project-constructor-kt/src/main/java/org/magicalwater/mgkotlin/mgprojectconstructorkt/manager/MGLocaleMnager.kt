package org.magicalwater.mgkotlin.mgprojectconstructorkt.manager

import android.content.Context
import org.magicalwater.mgkotlin.mgprojectconstructorkt.R
import org.magicalwater.mgkotlin.mgprojectconstructorkt.deserializemodel.rawfile.language.RawLanguageSetting
import org.magicalwater.mgkotlin.mgutilskt.util.MGJsonDataParseUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGResourcesFileUtils
import org.magicalwater.mgkotlin.mgutilskt.util.MGSettingUtils
import java.util.*

//app語系管理
class MGLocaleMnager {

    private lateinit var langObject: RawLanguageSetting

    //選擇的語系, 若之前沒選擇過, 則莫認為第一筆
    lateinit var showLanguage: RawLanguageSetting.Language

    //選擇的語言是第幾筆
    var showIndex: Int = 0

    //當前app選擇的語系
    private lateinit var showCountry: String
    private lateinit var showLang: String

    //儲存相關設定的utils
    private val settingUtil: MGSettingUtils = MGSettingUtils.shared

    companion object {
        val shared: MGLocaleMnager = MGLocaleMnager()
    }

    //開始真正的初始化
    fun initStart(context: Context) {
        //也需要初始化 settingUtil
        settingUtil.init(context)

        //先從文件讀取語言設定
        loadOpenLangFromFile(context)

        //在獲取選擇的語系
        getSelectedLangauge()
    }

    //選擇第幾筆的語系, 設定之後需要重新開啟app
    //重啟app的實作交給外部
    fun selectLocale(index: Int) {
        saveSetting()
    }

    //得到可供選擇的語言清單
    fun getSelectable(): List<RawLanguageSetting.Language> {
        return langObject.language
    }

    //從檔案中讀取要顯示給使用者看, 可以選擇的相關語系
    private fun loadOpenLangFromFile(context: Context) {

        //讀取不到文件是不允許的, 真的沒讀到的話直接讓app崩潰
        val json = MGResourcesFileUtils.loadRawText(context, R.raw.mglang)!!

        //將json字串反序列化成object
        langObject = MGJsonDataParseUtils.deserialize(json, RawLanguageSetting::class)!!
    }


    /**
     * 得到選擇的語系
     * 如果有設定過, 則比對下儲存的語系是否包含在文件當中
     *  有 -> 直接讀取
     *  無 -> 使用默認選項
     * 如果沒有設定過, 直接使用默認選項
     *
     * 默認選項為第一筆
     */
    private fun getSelectedLangauge() {

        //從本地獲取設定
        loadSetting()

        //所選擇的語系 index, 先使用默認給0
        showIndex = 0

        //比對語系是否在 mglang obj 有出現
        langObject.language.forEachIndexed { index, language ->
            if (showCountry == language.country && showLang == language.lang) {
                showIndex = index
                return@forEachIndexed
            }
        }

        showLanguage = langObject.language[showIndex]

        //最後將過濾完成的語系設定存入本地
        saveSetting()
    }

    //取出 showLanguage 對應的 locale, 此選項是為了設定app的語系
    fun getSelectLocale(): Locale {
        //從手機與係找出對應的語言
        val localeList = Locale.getAvailableLocales()

        var selectIndex = 0

        localeList.forEachIndexed { index, locale ->
            if (locale.language == showLanguage.lang) {
                //如果有country, 則需要再進一次比對
                val localeCountry = locale.country ?: ""
                val showCountry = showLanguage.country ?: ""

                if (localeCountry == showCountry) {
                    selectIndex = index
                    return@forEachIndexed
                }
            }
        }

        return localeList[selectIndex]
    }

    //將選擇的伺服器語系名稱存入本地
    fun saveSetting() {
        settingUtil.put(MGSettingTag.NAME_LOCALE_COUNTRY, showCountry)
        settingUtil.put(MGSettingTag.NAME_LOCALE_LANG, showLang)
    }

    //從本地讀取預設值
    private fun loadSetting() {
        showCountry = settingUtil.get(MGSettingTag.NAME_LOCALE_COUNTRY, "")
        showLang = settingUtil.get(MGSettingTag.NAME_LOCALE_LANG, "")
    }
}