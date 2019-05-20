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

import java.nio.charset.Charset

/**
 * Charsets
 */
object Charsets {
    val ISO_8859_1 = Charset.forName("ISO-8859-1")
    val US_ASCII = Charset.forName("US-ASCII")
    val UTF_16 = Charset.forName("UTF-16")
    val UTF_16BE = Charset.forName("UTF-16BE")
    val UTF_16LE = Charset.forName("UTF-16LE")
    val UTF_8 = Charset.forName("UTF-8")
    fun toCharset(charset: Charset?): Charset {
        return charset ?: Charset.defaultCharset()
    }

    fun toCharset(charset: String?): Charset {
        return if (charset == null) Charset.defaultCharset() else Charset.forName(charset)
    }
}
