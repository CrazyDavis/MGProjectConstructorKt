package org.magicalwater.mgkotlin.mgprojectconstructorkt.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import org.magicalwater.mgkotlin.mgutilskt.util.MGAnimationAttr
import org.magicalwater.mgkotlin.mgutilskt.util.MGAnimationUtils

/**
 * Created by 志朋 on 2017/11/11.
 * 註冊需要動態移動的view
 */
class MGUIMover {

    //儲存可移動view的相關
    var movableItem: MutableMap<View, Attr> = mutableMapOf()


    //得到某個view的attr
    fun getAttr(view: View): Attr? {
        return if (movableItem.containsKey(view)) movableItem[view] else null
    }

    fun addItem(view: View, attr: Attr) {
        movableItem.put(view, attr)
    }

//    fun getView(byAttr: Attr): View? {
//        for ((key, value) in movableItem) {
//            if byAttr.
//            return key
//        }
//        return nil
//    }


    //將所有view都往外移, 除了參數的view之外
    fun outAllView(vararg exView: View = emptyArray()) {

        for ((view, _) in movableItem)  {
            if (exView.contains(view)) startMoveOut(view)
        }
    }

    //移動某個特定的view
    fun moveView(view: View, isOut: Boolean) {

        if (movableItem.containsKey(view)) {
            if (isOut) startMoveOut(view) else startMoveIn(view)
        }

    }


    //開始將view移入
    private fun startMoveIn(view: View): Boolean {
        val attr = movableItem[view]!!
        when (attr.state) {
            State.idle_in, State.moving_in -> return false
        }

        attr.state = State.moving_in

        var animAttrs = mutableListOf<MGAnimationAttr>()

        when (attr.direct) {
            Direction.DOWN ->
                animAttrs.add( getTranslateYAttr(
                        view.height.toFloat() + attr.outOffset, 0F, 0F)
                )
            Direction.UP ->
                animAttrs.add( getTranslateYAttr(
                        -view.height.toFloat() + attr.outOffset, 0F, 0F)
                )
            Direction.LEFT ->
                animAttrs.add( getTranslateXAttr(
                        -view.width.toFloat() + attr.outOffset, 0F, 0F)
                )
            Direction.RIGHT ->
                animAttrs.add( getTranslateXAttr(
                        view.width.toFloat() + attr.outOffset, 0F, 0F)
                )
        }

        animAttrs.add( getScaleXAttr(attr.scale, 1f) )
        animAttrs.add( getScaleYAttr(attr.scale, 1f) )

        MGAnimationUtils.animator(view, animAttrs, attr.duration, endListener)

        return true
    }


    //開始將view移出
    private fun startMoveOut(view: View): Boolean {
        val attr = movableItem[view]!!
        when (attr.state) {
            State.idle_out, State.moving_out -> return false
        }

        attr.state = State.moving_out

        var animAttrs = mutableListOf<MGAnimationAttr>()

        when (attr.direct) {
            Direction.DOWN ->
                animAttrs.add( getTranslateYAttr(0f, view.height.toFloat(), attr.outOffset) )
            Direction.UP ->
                animAttrs.add( getTranslateYAttr(0f, -view.height.toFloat(), attr.outOffset) )

            Direction.LEFT ->
                animAttrs.add( getTranslateXAttr(0f, -view.width.toFloat(), attr.outOffset) )
            Direction.RIGHT ->
                animAttrs.add( getTranslateXAttr(0f, view.width.toFloat(), attr.outOffset) )
        }

        animAttrs.add( getScaleXAttr(1f, attr.scale) )
        animAttrs.add( getScaleYAttr(1f, attr.scale) )

        MGAnimationUtils.animator(view, animAttrs, attr.duration, endListener)

        return true
    }

    val endListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
        }
    }

    private fun getTranslateXAttr(start: Float, end: Float, offset: Float): MGAnimationAttr {
        return MGAnimationAttr(MGAnimationUtils.NAME_TRANSLATE_X, start, end + offset)
    }

    private fun getTranslateYAttr(start: Float, end: Float, offset: Float): MGAnimationAttr {
        return MGAnimationAttr(MGAnimationUtils.NAME_TRANSLATE_Y, start, end + offset)
    }

    private fun getScaleXAttr(start: Float, end: Float): MGAnimationAttr {
        return MGAnimationAttr(MGAnimationUtils.NAME_SCALE_X, start, end)
    }

    private fun getScaleYAttr(start: Float, end: Float): MGAnimationAttr {
        return MGAnimationAttr(MGAnimationUtils.NAME_SCALE_Y, start, end)
    }

    /**
     * 當動畫執行結束後設定 attr
     * */
    data class Attr(val direct: Direction, val dur: Int, val s: State) {
        var direction: Direction = direct
        var duration: Int = dur
        var state: State = s
        var outAlpha: Boolean = false //移出時是否要加入透明
        var outOffset: Float = 0f //移出時多增加的位移
        var scale: Float = 1f //移出時的縮放倍數
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    enum class State {
        moving_in, moving_out, idle_in, idle_out
    }
}
