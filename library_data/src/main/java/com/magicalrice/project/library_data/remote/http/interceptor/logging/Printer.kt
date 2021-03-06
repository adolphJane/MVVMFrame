package com.magicalrice.project.library_data.remote.http.interceptor.logging

import android.text.TextUtils
import okhttp3.FormBody
import okhttp3.Request
import okio.Buffer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * @package com.magicalrice.project.library_third_internal.storage.remote.http.interceptor.logging
 * @author Adolph
 * @date 2019-04-24 Wed
 * @description TODO
 */

object Printer {
    private val JSON_INDENT = 3

    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: ""
    private val DOUBLE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR

    private val OMITTED_RESPONSE = arrayOf(LINE_SEPARATOR, "Omitted response body")
    private val OMITTED_REQUEST = arrayOf(LINE_SEPARATOR, "Omitted request body")

    private val N = "\n"
    private val T = "\t"
    private val REQUEST_UP_LINE =
        "┌────── Request ────────────────────────────────────────────────────────────────────────"
    private val END_LINE =
        "└───────────────────────────────────────────────────────────────────────────────────────"
    private val RESPONSE_UP_LINE =
        "┌────── Response ───────────────────────────────────────────────────────────────────────"
    private val BODY_TAG = "Body:"
    private val URL_TAG = "URL: "
    private val METHOD_TAG = "Method: @"
    private val HEADERS_TAG = "Headers:"
    private val STATUS_CODE_TAG = "Status Code: "
    private val RECEIVED_TAG = "Received in: "
    private val CORNER_UP = "┌ "
    private val CORNER_BOTTOM = "└ "
    private val CENTER_LINE = "├ "
    private val DEFAULT_LINE = "│ "

    private fun isEmpty(line: String): Boolean {
        return TextUtils.isEmpty(line) || N == line || T == line || TextUtils.isEmpty(line.trim { it <= ' ' })
    }

    fun printJsonRequest(builder: LoggingInterceptor.Builder, request: Request) {
        val requestBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + bodyToString(
            request
        )
        val tag = builder.getTag(true)
        if (builder.getLogger() == null)
            I.log(
                builder.getType(),
                tag,
                REQUEST_UP_LINE
            )
        logLines(
            builder.getType(),
            tag,
            arrayOf(URL_TAG + request.url()),
            builder.getLogger(),
            false
        )
        logLines(
            builder.getType(),
            tag,
            getRequest(
                request,
                builder.getLevel()
            ),
            builder.getLogger(),
            true
        )
        if (request.body() is FormBody) {
            val formBody = StringBuilder()
            val body = request.body() as FormBody?
            if (body != null && body.size() != 0) {
                for (i in 0 until body.size()) {
                    formBody.append(body.encodedName(i) + "=" + body.encodedValue(i) + "&")
                }
                formBody.delete(formBody.length - 1, formBody.length)
                logLines(
                    builder.getType(),
                    tag,
                    arrayOf(formBody.toString()),
                    builder.getLogger(),
                    true
                )
            }
        }
        if (builder.getLevel() === Level.BASIC || builder.getLevel() === Level.BODY) {
            logLines(
                builder.getType(),
                tag,
                requestBody.split(LINE_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray(),
                builder.getLogger(),
                true
            )
        }
        if (builder.getLogger() == null)
            I.log(
                builder.getType(),
                tag,
                END_LINE
            )
    }

    fun printJsonResponse(
        builder: LoggingInterceptor.Builder, chainMs: Long, isSuccessful: Boolean,
        code: Int, headers: String, bodyString: String, segments: List<String>
    ) {
        val responseBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + getJsonString(
            bodyString
        )
        val tag = builder.getTag(false)
        if (builder.getLogger() == null)
            I.log(
                builder.getType(),
                tag,
                RESPONSE_UP_LINE
            )

        logLines(
            builder.getType(),
            tag,
            getResponse(
                headers, chainMs, code, isSuccessful,
                builder.getLevel(), segments
            ),
            builder.getLogger(),
            true
        )
        if (builder.getLevel() === Level.BASIC || builder.getLevel() === Level.BODY) {
            logLines(
                builder.getType(),
                tag,
                responseBody.split(LINE_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray(),
                builder.getLogger(),
                true
            )
        }
        if (builder.getLogger() == null)
            I.log(
                builder.getType(),
                tag,
                END_LINE
            )
    }

    fun printFileRequest(builder: LoggingInterceptor.Builder, request: Request) {
        val tag = builder.getTag(true)
        if (builder.getLogger() == null)
            I.log(
                builder.getType(),
                tag,
                REQUEST_UP_LINE
            )
        logLines(
            builder.getType(),
            tag,
            arrayOf(URL_TAG + request.url()),
            builder.getLogger(),
            false
        )
        logLines(
            builder.getType(),
            tag,
            getRequest(
                request,
                builder.getLevel()
            ),
            builder.getLogger(),
            true
        )
        if (request.body() is FormBody) {
            val formBody = StringBuilder()
            val body = request.body() as FormBody?
            if (body != null && body!!.size() != 0) {
                for (i in 0 until body!!.size()) {
                    formBody.append(body!!.encodedName(i) + "=" + body!!.encodedValue(i) + "&")
                }
                formBody.delete(formBody.length - 1, formBody.length)
                logLines(
                    builder.getType(),
                    tag,
                    arrayOf(formBody.toString()),
                    builder.getLogger(),
                    true
                )
            }
        }
        if (builder.getLevel() === Level.BASIC || builder.getLevel() === Level.BODY) {
            logLines(
                builder.getType(),
                tag,
                OMITTED_REQUEST,
                builder.getLogger(),
                true
            )
        }
        if (builder.getLogger() == null)
            I.log(
                builder.getType(),
                tag,
                END_LINE
            )
    }

    fun printFileResponse(
        builder: LoggingInterceptor.Builder, chainMs: Long, isSuccessful: Boolean,
        code: Int, headers: String, segments: List<String>
    ) {
        val tag = builder.getTag(false)
        if (builder.getLogger() == null)
            I.log(
                builder.getType(),
                tag,
                RESPONSE_UP_LINE
            )

        logLines(
            builder.getType(),
            tag,
            getResponse(
                headers, chainMs, code, isSuccessful,
                builder.getLevel(), segments
            ),
            builder.getLogger(),
            true
        )
        logLines(
            builder.getType(),
            tag,
            OMITTED_RESPONSE,
            builder.getLogger(),
            true
        )
        if (builder.getLogger() == null)
            I.log(
                builder.getType(),
                tag,
                END_LINE
            )
    }

    private fun getRequest(request: Request, level: Level): Array<String> {
        val message: String
        val header = request.headers().toString()
        val loggableHeader = level === Level.HEADERS || level === Level.BASIC
        message = METHOD_TAG + request.method() + DOUBLE_SEPARATOR +
                if (isEmpty(
                        header
                    )
                ) "" else if (loggableHeader) HEADERS_TAG + LINE_SEPARATOR + dotHeaders(
                    header
                ) else ""
        return message.split(LINE_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    private fun getResponse(
        header: String, tookMs: Long, code: Int, isSuccessful: Boolean,
        level: Level, segments: List<String>
    ): Array<String> {
        val message: String
        val loggableHeader = level === Level.HEADERS || level === Level.BASIC
        val segmentString =
            slashSegments(
                segments
            )
        message =
            (((if (!TextUtils.isEmpty(segmentString)) "$segmentString - " else "") + "is success : "
                    + isSuccessful + " - " + RECEIVED_TAG + tookMs + "ms" + DOUBLE_SEPARATOR + STATUS_CODE_TAG +
                    code + DOUBLE_SEPARATOR + (if (isEmpty(
                    header
                )
            )
                ""
            else if (loggableHeader)
                HEADERS_TAG + LINE_SEPARATOR +
                        dotHeaders(
                            header
                        )
            else
                "")))
        return message.split((LINE_SEPARATOR).toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
    }

    private fun slashSegments(segments: List<String>): String {
        val segmentString = StringBuilder()
        for (segment in segments) {
            segmentString.append("/").append(segment)
        }
        return segmentString.toString()
    }

    private fun dotHeaders(header: String): String {
        val headers =
            header.split((LINE_SEPARATOR).toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val builder = StringBuilder()
        var tag = "─ "
        if (headers.size > 1) {
            for (i in headers.indices) {
                if (i == 0) {
                    tag =
                        CORNER_UP
                } else if (i == headers.size - 1) {
                    tag =
                        CORNER_BOTTOM
                } else {
                    tag =
                        CENTER_LINE
                }
                builder.append(tag).append(headers[i]).append("\n")
            }
        } else {
            for (item in headers) {
                builder.append(tag).append(item).append("\n")
            }
        }
        return builder.toString()
    }

    private fun logLines(
        type: Int,
        tag: String,
        lines: Array<String>,
        logger: Logger?,
        withLineSize: Boolean
    ) {
        for (line in lines) {
            val lineLength = line.length
            val MAX_LONG_SIZE = if (withLineSize) 110 else lineLength
            for (i in 0..lineLength / MAX_LONG_SIZE) {
                val start = i * MAX_LONG_SIZE
                var end = (i + 1) * MAX_LONG_SIZE
                end = if (end > line.length) line.length else end
                if (logger == null) {
                    I.log(
                        type,
                        tag,
                        DEFAULT_LINE + line.substring(
                            start,
                            end
                        )
                    )
                } else {
                    logger.log(type, tag, line.substring(start, end))
                }
            }
        }
    }

    private fun bodyToString(request: Request): String {
        try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            if (copy.body() == null)
                return ""
            copy.body()!!.writeTo(buffer)
            return getJsonString(
                buffer.readUtf8()
            )
        } catch (e: IOException) {
            return "{\"err\": \"" + e.message + "\"}"
        }

    }

    fun getJsonString(msg: String): String {
        var message: String = ""
        try {
            if (msg.startsWith("{")) {
                val jsonObject = JSONObject(msg)
                message = jsonObject.toString(JSON_INDENT)
            } else if (msg.startsWith("[")) {
                val jsonArray = JSONArray(msg)
                message = jsonArray.toString(JSON_INDENT)
            } else {
                message = msg
            }
        } catch (e: JSONException) {
            message = msg
        }

        return message
    }
}