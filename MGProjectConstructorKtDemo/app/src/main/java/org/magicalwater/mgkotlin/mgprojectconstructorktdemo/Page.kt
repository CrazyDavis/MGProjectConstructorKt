package org.magicalwater.mgkotlin.mgprojectconstructorktdemo

class Page {
    companion object {
        val LOGIN = PageTag("LOGIN")
        val DEPOSIT = PageTag("DEPOSIT", "點數儲值")
        val TRANSLATE = PageTag("TRANSLATE", "點數轉移")
        val CUSTOMER = PageTag("CUSTOMER")
        val MENU = PageTag("MENU")
        val SIGNUP = PageTag("SIGNUP")

        val HOME = PageTag("HOME", "首頁")
        val CASINO = PageTag("CASINO", "真人娛樂")
        val SPORT = PageTag("SPORT", "體育運彩")
        val SLOT = PageTag("SLOT", "電子遊戲")
        val LOTTERY = PageTag("LOTTERY", "彩票彩球")
        val ESPORT = PageTag("ESPORT", "電競遊戲")
        val PROMOTION = PageTag("PROMOTION", "優惠活動")

        val OTHER_HELP = PageTag("OTHER_HELP", "其他幫助")
        val ABOUT = PageTag("ABOUT", "關於威京")
        val DUTY = PageTag("DUTY", "責任博彩")
        val RULE = PageTag("RULE", "規則條款")
        val PRIVACY = PageTag("PRIVACY", "隱私保護")
        val DEPOSIT_HELP = PageTag("DEPOSIT_HELP", "儲值教學")
        val GAME_HELP = PageTag("GAME_HELP", "遊戲幫助")
        val MEMBER_CENTER = PageTag("MEMBER_CENTER", "會員中心")

        val MEMBER_DATA = PageTag("MEMBER_DATA", "會員資料")
        val DEPOSIT_HISTORY = PageTag("DEPOSIT_HISTORY", "儲值紀錄")
        val TRANSLATE_HISTORY = PageTag("TRANSLATE_HISTORY", "轉移紀錄")
        val TRANSLATE_OUT = PageTag("TRANSLATE_OUT", "點數轉出")
        val TRANSLATE_OUT_HISTORY = PageTag("TRANSLATE_OUT_HISTORY", "轉出紀錄")
    }
}