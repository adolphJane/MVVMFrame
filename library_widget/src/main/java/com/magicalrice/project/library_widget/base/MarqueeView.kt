package com.magicalrice.project.library_widget.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import com.magicalrice.project.library_base.base.ScreenUtils
import com.magicalrice.project.library_base.base.log.LogUtils
import com.magicalrice.project.library_widget.R

class MarqueeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var string: String? = null//最终绘制的文本
    private var speed = 1f//移动速度
    private var textColor = Color.BLACK//文字颜色,默认黑色
    private var textSize = 12f//文字颜色,默认黑色
    private var startLocationDistance = 0.5f//开始的位置选取，百分比来的，距离左边，0~1，0代表不间距，1的话代表，从右面，1/2代表中间。
    private val itemDistance = 4 //默认item间隔4个空格

    private var hasShadow = false
    private var shadowDy = 0f
    private var shadowDx = 0f
    private var shadowRadius = 0f
    private var shadowColor = Color.BLACK

    private var isResetLocation = true//默认为true
    private var xLocation = 0f//文本的x坐标
    private var contentWidth: Int = 0//内容的宽度

    @Volatile
    private var isRoll = false//是否继续滚动

    private var iconAreaWidth = 0    //开头图标所占宽度
    private var iconDrawable: Drawable? = null //开头图标

    private var paint: TextPaint? = null//画笔
    private var rect: Rect? = null

    private var bgPaint: Paint? = null//背景画笔

    private var resetInit = true

    private var content = ""

    private var textHeight: Float = 0.toFloat()

    private val mHandler = Handler {
        if (it.what == 1) {
            if (isRoll && !TextUtils.isEmpty(content)) {
                xLocation -= speed
                postInvalidate()//每隔10毫秒重绘视图
                it.target.sendEmptyMessageDelayed(1, 30)
            }
        }
        false
    }

    /**
     * http://blog.csdn.net/u014702653/article/details/51985821
     * 详细解说了
     *
     * @param
     * @return
     */
    private val contentHeight: Float
        get() {
            val fontMetrics = paint!!.fontMetrics
            return Math.abs(fontMetrics.bottom - fontMetrics.top) / 2
        }


    /**
     * 计算出一个空格的宽度
     */
    private val blankWidth: Float
        get() {
            val text1 = "en en"
            val text2 = "enen"
            return getContentWidth(text1) - getContentWidth(text2)

        }

    init {
        initAttrs(attrs)
        initView()
        initPaint()
    }

    @SuppressLint("RestrictedApi")
    private fun initAttrs(attrs: AttributeSet?) {
        val tta = TintTypedArray.obtainStyledAttributes(
            context, attrs,
            R.styleable.MarqueeView
        )

        textColor = tta.getColor(R.styleable.MarqueeView_text_color, textColor)
        isResetLocation = tta.getBoolean(R.styleable.MarqueeView_is_resetLocation, isResetLocation)
        speed = tta.getFloat(R.styleable.MarqueeView_text_speed, speed)
        textSize = tta.getFloat(R.styleable.MarqueeView_text_size, textSize)
        //        startLocationDistance = tta.getFloat(R.styleable.MarqueeView_marqueeview_text_startlocationdistance, startLocationDistance);
        //        itemDistance = tta.getInt(R.styleable.MarqueeView_marqueeview_text_distance, itemDistance);
        hasShadow = tta.getBoolean(R.styleable.MarqueeView_text_shadow, false)
        shadowDx = tta.getFloat(R.styleable.MarqueeView_text_shadow_dx, 0f)
        shadowDy = tta.getFloat(R.styleable.MarqueeView_text_shadow_dy, 0f)
        shadowRadius = tta.getFloat(R.styleable.MarqueeView_text_shadow_radius, 0f)
        shadowColor = tta.getColor(R.styleable.MarqueeView_text_shadow_color, shadowColor)
        iconDrawable = tta.getDrawable(R.styleable.MarqueeView_icon)
        tta.recycle()
    }

    private fun initView() {}

    /**
     * 刻字机修改
     */
    private fun initPaint() {
        rect = Rect()
        paint = TextPaint(Paint.ANTI_ALIAS_FLAG)//初始化文本画笔
        paint!!.style = Paint.Style.FILL
        paint!!.color = textColor//文字颜色值,可以不设定
        paint!!.textSize = ScreenUtils.sp2pxFloat(context, textSize)//文字大小
        if (hasShadow) {
            paint!!.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
        }
        iconAreaWidth = ScreenUtils.dp2pxInt(context, 50)

        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint!!.color = ContextCompat.getColor(context, R.color.white1)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(
                0f,
                0f,
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                (measuredHeight / 2).toFloat(),
                (measuredHeight / 2).toFloat(),
                bgPaint!!
            )
        } else {
            canvas.drawRect(
                (measuredHeight / 2).toFloat(),
                0f,
                (measuredWidth - measuredHeight / 2).toFloat(),
                measuredHeight.toFloat(),
                bgPaint!!
            )
            canvas.drawCircle(
                (measuredHeight / 2).toFloat(),
                (measuredHeight / 2).toFloat(),
                (measuredHeight / 2).toFloat(),
                bgPaint!!
            )
            canvas.drawCircle(
                (measuredWidth - measuredHeight / 2).toFloat(),
                (measuredHeight / 2).toFloat(),
                (measuredHeight / 2).toFloat(),
                bgPaint!!
            )
        }
        if (resetInit) {
            if (startLocationDistance < 0) {
                startLocationDistance = 0f
            } else if (startLocationDistance > 1) {
                startLocationDistance = 1f
            }
            xLocation = startLocationDistance
            resetInit = false
        }
        if (isRoll) {
            if (xLocation <= -(contentWidth + blankWidth * itemDistance)) {
                xLocation = xLocation + contentWidth.toFloat() + blankWidth * itemDistance
                string = string!! + content
                LogUtils.e("Marquee", xLocation)
                //调用pattern.quote ,防止replaceFirst中含有特殊字符比如完整的(),导致的crash
                //                string = string.replaceFirst(pattern.quote(""+content), "");   //修改内存问题,防止文字长度不断增加导致内存溢出的情况,
            }
        }

        //把文字画出来
        if (string != null) {
            if (iconDrawable == null) {
                canvas.drawText(string!!, xLocation, height / 2 + textHeight / 2, paint!!)
            } else {
                canvas.save()
                canvas.clipRect(
                    iconAreaWidth,
                    0,
                    measuredWidth - measuredHeight / 2,
                    measuredHeight
                )
                canvas.drawText(string!!, xLocation, height / 2 + textHeight / 2, paint!!)
                canvas.restore()
            }
        }

        if (iconDrawable != null) {
            val left = ScreenUtils.dp2pxInt(context, 15)
            val top = (measuredHeight - iconDrawable!!.intrinsicHeight) / 2
            iconDrawable!!.setBounds(
                left,
                top,
                left + iconDrawable!!.intrinsicWidth,
                top + iconDrawable!!.intrinsicHeight
            )
            iconDrawable!!.draw(canvas)
        }
    }

    /**
     * 继续滚动
     */
    fun continueRoll() {
        postInvalidate()
        mHandler.removeMessages(1)
        isRoll = true
        mHandler.sendEmptyMessage(1)
    }

    /**
     * 停止滚动
     */
    fun stopRoll() {
        isRoll = false
        resetInit = true
        mHandler.removeMessages(1)
        postInvalidate()
    }

    fun setTextColor(color: Int) {
        textColor = color
        paint!!.color = color
        postInvalidate()
    }

    private fun getContentWidth(black: String): Float {
        if (TextUtils.isEmpty(black)) {
            return 0f
        }

        if (rect == null) {
            rect = Rect()
        }
        paint!!.getTextBounds(black, 0, black.length, rect)
        textHeight = contentHeight

        return rect!!.width().toFloat()
    }

    /**
     * 添加文字
     *
     * @param content  显示文字的内容
     * @param needRoll 是否需要滚动,如果不需要,就把文字定位到startLocationDistance的位置,而且不重复
     */
    fun setContent(content: String, needRoll: Boolean) {
        var content = content
        if (TextUtils.isEmpty(content)) {
            return
        }

        isResetLocation = true
        resetInit = true
        if (needRoll) {
            for (i in 0 until itemDistance) {
                content = "$content "
            }
            this.content = content
            contentWidth = getContentWidth(content).toInt()//可以理解为一个单元内容的长度
            val contentCount = width / contentWidth + 2
            this.string = ""
            for (i in 0..contentCount + 2) {
                this.string = String.format("%s%s", this.string, this.content)//根据重复次数去叠加。
            }
            postInvalidate()
        } else {
            stopRoll()
            resetInit = true
            getContentWidth(content)
            this.content = content  //如果不需要滚动,就直接走这里了
            this.string = content
            postInvalidate()
        }

    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == View.VISIBLE) {
            continueRoll()
        } else {
            stopRoll()
        }
    }
}