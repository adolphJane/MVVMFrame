package com.magicalrice.project.library_widget.base

import android.content.Context
import android.text.*
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.magicalrice.project.library_widget.R


/**
 * Created by ZhaoYun on 2017/9/29.
 * Description 手机号码，银行卡号，身份证号码分割
 */

class ContentWithSpaceEditText : AppCompatEditText {
    private var maxLength = 100
    private var contentType: Int = 0
    private var start: Int = 0
    private var count: Int = 0
    private var before: Int = 0
    private var digits: String? = null

    private val watcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            this@ContentWithSpaceEditText.start = start
            this@ContentWithSpaceEditText.before = before
            this@ContentWithSpaceEditText.count = count
        }

        override fun afterTextChanged(s: Editable?) {
            if (s == null) {
                return
            }
            //判断是否是在中间输入，需要重新计算
            val isMiddle = start + count < s.length
            //在末尾输入时，是否需要加入空格
            var isNeedSpace = false
            if (!isMiddle && isSpace(s.length)) {
                isNeedSpace = true
            }
            if (isMiddle || isNeedSpace || count > 1) {
                var newStr = s.toString()
                newStr = newStr.replace(" ", "")
                val sb = StringBuilder()
                var spaceCount = 0
                for (i in 0 until newStr.length) {
                    sb.append(newStr.substring(i, i + 1))
                    //如果当前输入的字符下一位为空格(i+1+1+spaceCount)，因为i是从0开始计算的，所以一开始的时候需要先加1
                    if (isSpace(i + 2 + spaceCount)) {
                        sb.append(" ")
                        spaceCount += 1
                    }
                }
                removeTextChangedListener(this)
                s.replace(0, s.length, sb)
                //如果是在末尾的话,或者加入的字符个数大于零的话（输入或者粘贴）
                if (!isMiddle || count > 1) {
                    setSelection(if (s.length <= maxLength) s.length else maxLength)
                } else if (isMiddle) {
                    //如果是删除
                    if (count == 0) {
                        //如果删除时，光标停留在空格的前面，光标则要往前移一位
                        if (isSpace(start - before + 1)) {
                            setSelection(if (start - before > 0) start - before else 0)
                        } else {
                            setSelection(if (start - before + 1 > s.length) s.length else start - before + 1)
                        }
                    } else {
                        if (isSpace(start - before + count)) {
                            setSelection(if (start + count - before + 1 < s.length) start + count - before + 1 else s.length)
                        } else {
                            setSelection(start + count - before)
                        }
                    }//如果是增加
                }
                addTextChangedListener(this)
            }
        }
    }

    val textWithoutSpace: String
        get() = super.getText()!!.toString().replace(" ", "")

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        parseAttributeSet(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        parseAttributeSet(context, attrs)
    }

    private fun parseAttributeSet(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ContentWithSpaceEditText, 0, 0)
        contentType = ta.getInt(R.styleable.ContentWithSpaceEditText_typeEdit, 0)
        ta.recycle()
        initType()
        setSingleLine()
        addTextChangedListener(watcher)
    }

    private fun initType() {
        if (contentType == TYPE_PHONE) {
            maxLength = 13
            digits = "0123456789 "
            inputType = InputType.TYPE_CLASS_NUMBER
        } else if (contentType == TYPE_CARD) {
            maxLength = 23
            digits = "0123456789 "
            inputType = InputType.TYPE_CLASS_NUMBER
        } else if (contentType == TYPE_IDCARD) {
            maxLength = 21
            digits = "0123456789xX "
            inputType = InputType.TYPE_CLASS_TEXT
        }
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    }

    override fun setInputType(type: Int) {
        super.setInputType(type)
        // setKeyListener要在setInputType后面调用，否则无效
        if (!TextUtils.isEmpty(digits)) {
            keyListener = DigitsKeyListener.getInstance(digits!!)
        }
    }

    private fun isSpace(length: Int): Boolean {
        if (contentType == TYPE_PHONE) {
            return isSpacePhone(length)
        } else if (contentType == TYPE_CARD) {
            return isSpaceCard(length)
        } else if (contentType == TYPE_IDCARD) {
            return isSpaceIDCard(length)
        }
        return false
    }

    private fun isSpacePhone(length: Int): Boolean {
        return length >= 4 && (length == 4 || (length + 1) % 5 == 0)
    }

    private fun isSpaceCard(length: Int): Boolean {
        return length % 5 == 0
    }

    private fun isSpaceIDCard(length: Int): Boolean {
        return length > 6 && (length == 7 || (length - 2) % 5 == 0)
    }

    fun setContentType(contentType: Int) {
        this.contentType = contentType
        initType()
    }

    companion object {
        const val TYPE_PHONE = 0
        const val TYPE_CARD = 1
        const val TYPE_IDCARD = 2
    }
}
