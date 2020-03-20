package com.yetao.download.util

import okhttp3.Headers

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
object HeaderUtil {
    fun convertHeaders(headers: Headers) = hashMapOf<String, String>().apply {
        for (i in 0 until headers.size()) {
            this[headers.name(i).toLowerCase()] = headers.value(i)
        }
    }
}