package com.yetao.download.task.inf

import io.reactivex.Observable

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
interface IRxTask<T> {

    fun rxjava(): Observable<T>

    fun pause()
}