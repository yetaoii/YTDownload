package com.yetao.download.model.store

/**
 *  Created by yetao on 2020/3/19
 *  description
 **/
data class TaskBody(
    var url: String,
    var savePath: String,
    var progress: Long = 0,
    var total: Long = 0,
    var updateTime: Long = 0,
    var createTime: Long = 0
) {
    fun isFinish() = progress == total && total > 0L
    fun isRange() = progress != total && progress > 0 && total > 0
}