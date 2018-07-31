package org.magicalwater.mgkotlin.mgprojectconstructorkt.deserializemodel.rawfile.language

/**
 * Created by magicalwater on 2018/2/3.
 */
data class RawLanguageSetting(var version: Int,
                              var language: List<Language>) {
    data class Language(var country: String?,
                        var lang: String,
                        var name: String)
}