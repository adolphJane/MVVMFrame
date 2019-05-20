/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.magicalrice.project.library_base.base

import java.io.Serializable
import java.io.Writer

/**
 * [Writer] implementation that outputs to a [StringBuilder].
 *
 *
 * **NOTE:** This implementation, as an alternative to
 * `java.io.StringWriter`, provides an *un-synchronized*
 * (i.e. for use in a single thread) implementation for better performance.
 * For safe usage with multiple [Thread]s then
 * `java.io.StringWriter` should be used.
 *
 * @version $Id: StringBuilderWriter.java 1304052 2012-03-22 20:55:29Z ggregory $
 * @since 2.0
 */
class StringBuilderWriter : Writer, Serializable {

    /**
     * Return the underlying builder.
     *
     * @return The underlying builder
     */
    val builder: StringBuilder

    /**
     * Construct a new [StringBuilder] instance with default capacity.
     */
    constructor() {
        this.builder = StringBuilder()
    }

    /**
     * Construct a new [StringBuilder] instance with the specified capacity.
     *
     * @param capacity The initial capacity of the underlying [StringBuilder]
     */
    constructor(capacity: Int) {
        this.builder = StringBuilder(capacity)
    }

    /**
     * Construct a new instance with the specified [StringBuilder].
     *
     * @param builder The String builder
     */
    constructor(builder: StringBuilder?) {
        this.builder = builder ?: StringBuilder()
    }

    /**
     * Append a single character to this Writer.
     *
     * @param value The character to append
     * @return This writer instance
     */
    override fun append(value: Char): Writer {
        builder.append(value)
        return this
    }

    /**
     * Append a character sequence to this Writer.
     *
     * @param value The character to append
     * @return This writer instance
     */
    override fun append(value: CharSequence?): Writer {
        builder.append(value)
        return this
    }

    /**
     * Append a portion of a character sequence to the [StringBuilder].
     *
     * @param value The character to append
     * @param start The index of the first character
     * @param end The index of the last character + 1
     * @return This writer instance
     */
    override fun append(value: CharSequence?, start: Int, end: Int): Writer {
        builder.append(value, start, end)
        return this
    }

    /**
     * Closing this writer has no effect.
     */
    override fun close() {}

    /**
     * Flushing this writer has no effect.
     */
    override fun flush() {}


    /**
     * Write a String to the [StringBuilder].
     *
     * @param value The value to write
     */
    override fun write(value: String?) {
        if (value != null) {
            builder.append(value)
        }
    }

    /**
     * Write a portion of a character array to the [StringBuilder].
     *
     * @param value The value to write
     * @param offset The index of the first character
     * @param length The number of characters to write
     */
    override fun write(value: CharArray?, offset: Int, length: Int) {
        if (value != null) {
            builder.append(value, offset, length)
        }
    }

    /**
     * Returns [StringBuilder.toString].
     *
     * @return The contents of the String builder.
     */
    override fun toString(): String {
        return builder.toString()
    }
}
