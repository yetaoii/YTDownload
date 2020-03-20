package com.yetao.download.executor

import com.yetao.download.model.api.DownloadApiService
import com.yetao.download.model.api.DownloadRetrofit
import com.yetao.download.model.api.ProgressListener
import com.yetao.download.model.api.ProgressResponseBody
import com.yetao.download.callback.DownloadCall
import com.yetao.download.exception.StreamErrorException
import com.yetao.download.exception.UrlErrorException
import com.yetao.download.manager.YTDownloadManager
import com.yetao.download.model.store.SqlManager
import com.yetao.download.model.store.TaskBody
import com.yetao.download.util.FileUtil
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.lang.Exception
import java.util.*

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
class RetrofitExecutor : Executor {

    companion object {
        private val downloadApiService by lazy { DownloadRetrofit.instance.create<DownloadApiService>() }
    }

    var call: DownloadCall? = null

    var disposable: Disposable? = null

    /**
     * 开始执行
     */
    override fun execute(call: DownloadCall) {
        checkUrlEmpty(call.task.getUrl())
        this.call = call
        downloadApiService.downloadProgress(
            call.task.getUrl()!!,
            call.task.getAllRequestHeaders()
        )
            .subscribeOn(Schedulers.io())
            .flatMap { t ->
                return@flatMap Observable.create<DownloadCall> { emitter ->
                    checkBodyNull(t.body())?.let {
                        emitter.onError(it)
                        return@create
                    }
                    val body = ProgressResponseBody(t.body()!!)
                    readBody(body, call, emitter)
                    writeFile(body,call, emitter)
                }

            }.subscribe(object : Observer<DownloadCall> {
                override fun onComplete() {
                    call.callback.onCompleted()
                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                    call.callback.onStart()
                }

                override fun onNext(t: DownloadCall) {
                    call.callback.onProgress(t.downloadInfo)
                }

                override fun onError(e: Throwable) {
                    call.callback.onError(e)
                }

            })
    }

    /**
     * 写到文件
     */
    private fun writeFile(
        body: ProgressResponseBody,
        call: DownloadCall,
        emitter: ObservableEmitter<DownloadCall>
    ) {
        //取保存路径-默认为YTDownloadManager.instance.defaultSavePath
        val saveDir =
            call.task.getSavePath() ?: YTDownloadManager.instance.defaultSavePath
        //取保存文件名-默认为uuid+文件后缀
        val saveFileName = call.task.getSaveFileName()
            ?: ("${UUID.randomUUID()}${call.getFileSuffix()}")
        var filePath = "$saveDir/$saveFileName"
        filePath = saveStartToDb(call, filePath, body)
        //写到文件
        FileUtil.writeFile(
            body.byteStream(),
            filePath,
            call.range
        ) {
            !emitter.isDisposed//停止读的条件
        }
    }

    /**
     * 更新数据库开始信息
     */
    private fun saveStartToDb(
        call: DownloadCall,
        filePath: String,
        body: ProgressResponseBody
    ): String {
        var filePath1 = filePath
        SqlManager.instance.find(call.task.getUrl()!!)?.apply {
            this.progress = call.rangeStart
            if (call.range) {
                filePath1 = this.savePath
            } else {
                this.savePath = filePath1
            }
            SqlManager.instance.update(this)
        } ?: SqlManager.instance.insert(
            TaskBody(
                call.task.getUrl()!!,
                filePath1,
                total = body.contentLength() + call.rangeStart
            )
        )
        return filePath1
    }

    /**
     * 从流中读取并回调
     */
    private fun readBody(
        body: ProgressResponseBody,
        call: DownloadCall,
        emitter: ObservableEmitter<DownloadCall>
    ) {
        var time = 0L
        body.progressListener = object : ProgressListener {
            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                call.downloadInfo.currentBytes = bytesRead + call.rangeStart
                call.downloadInfo.totalBytes = contentLength + call.rangeStart

                if (emitter.isDisposed || done) {
                    updateProgressToDb(call)
                }
                //限制回到时间
                if ((System.currentTimeMillis() - time >= call.task.getIntervalTime() && bytesRead != contentLength) || done) {
                    emitter.onNext(call)
                    time = System.currentTimeMillis()
                }
                //如果已完成触发发射器
                if (done) {
                    emitter.onComplete()
                    body.progressListener = null
                }
            }
        }
    }

    /**
     * 检查url空
     */
    private fun checkUrlEmpty(url:String?) {
        if (url.isNullOrBlank()) {
            throw UrlErrorException("Url is null or blank.Please check url")
        }
    }

    /**
     * 检查流内容为空
     */
    private fun checkBodyNull(responseBody: ResponseBody?):Exception? {
        if (responseBody?.byteStream() == null) {
            return StreamErrorException("stream body is null")
        }
        return null
    }

    /**
     * 取消时更新进度
     */
    private fun updateProgressToDb(call: DownloadCall) {
        SqlManager.instance.find(call.task.getUrl()!!)?.apply {
            progress = call.downloadInfo.currentBytes
            total = call.downloadInfo.totalBytes
            SqlManager.instance.update(this)
        }
    }

    override fun cancel() {
        disposable?.dispose()
        call?.let {
            updateProgressToDb(it)
        }
    }

}
 