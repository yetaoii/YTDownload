package com.yetao.download.task.data

import com.yetao.download.task.inf.IDownloadTask

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
data class DownloadInfo(val task: IDownloadTask, var currentBytes: Long = -1, var totalBytes: Long = -1)