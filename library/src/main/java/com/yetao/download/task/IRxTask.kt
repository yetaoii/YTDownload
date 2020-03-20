package com.yetao.download.task

import com.yetao.download.task.data.DownloadInfo
import io.reactivex.Observable

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
interface IRxTask {

    fun rxjava(): Observable<DownloadInfo>
    fun pause()
}