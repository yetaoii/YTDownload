package com.yetao.download.executor

import com.yetao.download.callback.DownloadCall
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
interface Executor {
    fun cancel()
    fun execute(call: DownloadCall)
}