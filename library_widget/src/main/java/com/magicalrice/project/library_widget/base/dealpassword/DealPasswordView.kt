package com.magicalrice.project.library_widget.base.dealpassword

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.magicalrice.project.library_base.base.KeyboardUtils
import com.magicalrice.project.library_base.base.ScreenUtils
import com.magicalrice.project.library_widget.R

/**
 * @package com.magicalrice.project.library_widget.base.dealpassword
 * @author Adolph
 * @date 2019-04-20 Sat
 * @description 验证码输入框
 */

class DealPasswordView : AppCompatEditText {
    private var outDiameter: Float = 0f
    private var innerDiameter: Float = 0f
    private var spaceBetween: Float = 0f
    private var mPaint: Paint
    private var completeListener: DealPasswordCompleteListener? = null
    var passwordSize: Int = 0
    var mPassword: StringBuilder? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleValue: Int) : super(
        context,
        attrs,
        defStyleValue
    ) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.DealPasswordView, 0, 0
        )
        passwordSize = a.getInteger(R.styleable.DealPasswordView_passwordSize, 0)
        a.recycle()

        outDiameter = ScreenUtils.dp2pxFloat(context, 45)
        innerDiameter = ScreenUtils.dp2pxFloat(context, 10)
        spaceBetween = ScreenUtils.dp2pxFloat(context, 6)

        mPaint = Paint()
        mPaint.isAntiAlias = true
        isFocusableInTouchMode = true
        inputType = InputType.TYPE_CLASS_NUMBER
        filters = arrayOf(InputFilter.LengthFilter(passwordSize))
        mPassword = StringBuilder()
        isCursorVisible = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = outDiameter
        val width =
            outDiameter * passwordSize + spaceBetween * (if (passwordSize > 0) passwordSize - 1 else 0)
        setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        mPaint.color = ContextCompat.getColor(context, R.color.white1)
        canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), mPaint)
        mPaint.color = ContextCompat.getColor(context, R.color.gray9)
        for (i in 1..passwordSize) {
            canvas?.drawCircle(
                outDiameter / 2 + (i - 1) * (spaceBetween + outDiameter),
                outDiameter / 2,
                outDiameter / 2,
                mPaint
            )
        }

        mPaint.color = ContextCompat.getColor(context, R.color.black2)
        for (i in 1..(mPassword?.length ?: 0)) {
            canvas?.drawCircle(
                outDiameter / 2 + (i - 1) * (spaceBetween + outDiameter),
                outDiameter / 2,
                innerDiameter / 2,
                mPaint
            )
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!hasWindowFocus) {
            KeyboardUtils.hideSoftInput(this)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                requestFocus()
                KeyboardUtils.showSoftInput(this)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setComListener(listener: DealPasswordCompleteListener) {
        this.completeListener = listener
    }

    fun getComListener(): DealPasswordCompleteListener? {
        return completeListener
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (text?.isNotEmpty() == true) {
            val newNum = text[text.length - 1]
            if (newNum in '0'..'9') {
                if (lengthAfter > 0) {
                    mPassword?.append(newNum)
                }
                if (lengthBefore > 0) {
                    mPassword?.deleteCharAt((mPassword?.length ?: 1) - 1)
                }
                invalidate()
            }
            if (mPassword?.length ?: 0 >= passwordSize) {
                completeListener?.passwordComplete(mPassword.toString(), true)
            } else {
                completeListener?.passwordComplete(mPassword.toString(), false)
            }
        } else {
            if (mPassword?.isNotEmpty() == true) {
                mPassword?.clear()
                invalidate()
            }
        }
    }
}