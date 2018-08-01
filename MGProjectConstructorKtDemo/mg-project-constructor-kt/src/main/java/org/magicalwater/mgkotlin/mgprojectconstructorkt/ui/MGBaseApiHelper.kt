package org.magicalwater.mgkotlin.mgprojectconstructorkt.ui

import android.os.CountDownTimer
import org.magicalwater.mgkotlin.mgprojectconstructorkt.connect.MGUrlRequest
import org.magicalwater.mgkotlin.mgprojectconstructorkt.helper.MGRequestSender
import org.magicalwater.mgkotlin.mgprojectconstructorkt.helper.MGRequestSenderDelegate
import org.magicalwater.mgkotlin.mgutilskt.util.MGTimerUtils

/**
 * Created by 志朋 on 2017/12/11.
 * 輔助 api 呼叫的 超類
 */

class MGBaseApiHelper(delegate: MGApiHelperDelegate) : MGRequestSenderDelegate {

    private var delegate: MGApiHelperDelegate = delegate

    enum class TimerAction {
        START,  //開始倒數計時
        CANCEL, //結束倒數
        REPEAT  //先結束再開始
    }

    //發送api需求用的物件
    private var requestSender: MGRequestSender = MGRequestSender(this)

    //執行倒數計時的物件, 不直接呼叫使用, 而是使用 timerAction 執行
    private var timer: CountDownTimer? = null

    //每次倒數計時的時間
    var timerTime: Long = 3000 * 10

    /**
     * 呼叫 api 回傳的 request
     * */
    override fun response(request: MGUrlRequest, success: Boolean, requestCode: Int) {
        delegate.response(request, success, requestCode)
    }

    //讓子類呼叫
    fun sendRequest(request: MGUrlRequest, requestCode: Int = MGRequestSender.REQUEST_DEFAUT) {
        requestSender.send(request, requestCode)
    }

    fun timerAction(action: TimerAction, time: Long = timerTime) {
        when (action) {
            TimerAction.START -> {
                timer = MGTimerUtils.countDown(time, ::timesUp)
                timer?.start()
            }
            TimerAction.CANCEL -> timer?.cancel()
            TimerAction.REPEAT -> {
                timerAction(TimerAction.CANCEL)
                timerAction(TimerAction.START)
            }
        }
    }

    //倒數計時到了
    private fun timesUp() = delegate.timesUp()
}

interface MGApiHelperDelegate {
    fun response(request: MGUrlRequest, success: Boolean, requestCode: Int)
    fun timesUp()
}