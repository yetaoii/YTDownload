package com.yetao.download.dispatcher

import com.yetao.download.callback.DownloadCall
import com.yetao.download.model.store.SqlManager
import com.yetao.download.model.store.TaskBody
import java.io.File

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
class RangeDispatcher : Dispatcher() {
    companion object {
        fun with() = RangeDispatcher()
    }

    override fun dispatch(call: DownloadCall) {
        //装饰range子弹
        checkRangeFromDb(call.task.getUrl())?.apply {
            call.downloadInfo.currentBytes = progress
            call.downloadInfo.totalBytes = total
            call.task.addRequestHeader("Range", "bytes=$progress-$total")
            call.range = true
            call.rangeStart = progress
        }
        super.dispatch(call)
    }

    /**
     * 从数据库中取出当前任务，判断是否满足range
     */
    private fun checkRangeFromDb(url: String?): TaskBody? {
        url?.let {
            val taskBody = SqlManager.instance.find(url)
            taskBody?.apply {
                val file = File(savePath)
                if (isRange() && file.exists() && file.length() == progress) {
                    return this
                }
            }
        }
        return null
    }
}