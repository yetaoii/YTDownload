package com.yetao.download.util

/**
 *  Created by yetao on 2019-09-30
 *  description
 **/


inline fun <reified T> Any?.as2(): T {
    return this as T
}

inline fun <reified T> Any?.isT(): Boolean {
    this?.let {
        return it is T
    }
    return false
}
