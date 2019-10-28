package com.guillaumetousignant.payingcamel.ui.helpers

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.AnimatorInflater
import android.content.Context
import android.view.View
import com.guillaumetousignant.payingcamel.R


object FlipAnimator {
    private val TAG = FlipAnimator::class.java.simpleName
    private var leftIn: Animator? = null
    private var rightOut: Animator? = null
    private var leftOut: Animator? = null
    private var rightIn: Animator? = null

    /**
     * Performs flip animation on two views
     */
    fun flipView(context: Context, back: View, front: View, showFront: Boolean) {
        leftIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in)
        rightOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out)
        leftOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out)
        rightIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in)

        val showFrontAnim = AnimatorSet()
        val showBackAnim = AnimatorSet()

        leftIn?.setTarget(back)
        rightOut?.setTarget(front)
        showFrontAnim.playTogether(leftIn, rightOut)

        leftOut?.setTarget(back)
        rightIn?.setTarget(front)
        showBackAnim.playTogether(rightIn, leftOut)

        if (showFront) {
            showFrontAnim.start()
        } else {
            showBackAnim.start()
        }
    }
}