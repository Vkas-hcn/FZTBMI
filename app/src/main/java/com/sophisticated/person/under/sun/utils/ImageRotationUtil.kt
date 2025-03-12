package com.sophisticated.person.under.sun.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.ImageView
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import kotlin.math.abs

/**
 * 图片无缝旋转工具类
 */
class ImageRotationUtil {

    /**
     * 让ImageView中的图片无缝旋转
     * @param imageView 需要旋转的ImageView
     * @param duration 完成一次360度旋转所需的时间(毫秒)
     * @param clockwise 是否顺时针旋转，默认为true
     * @return ValueAnimator 动画对象，可用于控制动画(暂停、恢复等)
     */
    fun rotateImageSmoothly(
        imageView: ImageView,
        duration: Long = 3000,
        clockwise: Boolean = true
    ): ValueAnimator {
        // 创建值动画
        val rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            // 设置动画时长
            this.duration = duration
            // 设置线性插值器使旋转速度恒定
            interpolator = LinearInterpolator()
            // 设置动画无限重复
            repeatCount = ValueAnimator.INFINITE
            // 设置重复模式为重新开始
            repeatMode = ValueAnimator.RESTART

            // 更新动画值时旋转ImageView
            addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                // 根据旋转方向设置正负值
                val rotationValue = if (clockwise) value else -value
                imageView.rotation = rotationValue
            }
        }

        // 开始动画
        rotationAnimator.start()
        return rotationAnimator
    }

    /**
     * 旋转Bitmap图片
     * @param source 原始Bitmap图片
     * @param degrees 旋转角度
     * @return 旋转后的Bitmap图片
     */
    fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(
            source,
            0,
            0,
            source.width,
            source.height,
            matrix,
            true
        )
    }

    /**
     * 创建一个可定制的无缝旋转动画
     * @param onRotation 每次角度变化时的回调函数
     * @param duration 完成一次360度旋转所需的时间(毫秒)
     * @param clockwise 是否顺时针旋转，默认为true
     * @return ValueAnimator 动画对象
     */
    fun createCustomRotationAnimator(
        onRotation: (Float) -> Unit,
        duration: Long = 3000,
        clockwise: Boolean = true
    ): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 360f).apply {
            this.duration = duration
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART

            addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                val rotationValue = if (clockwise) value else -value
                onRotation(rotationValue)
            }

            start()
        }
    }
}