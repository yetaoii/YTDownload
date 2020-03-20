package com.yetao.download.model.api

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

import java.io.IOException


/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
class ProgressResponseBody(
    responseBody: ResponseBody
) :
    ResponseBody() {


    private val responseBody: ResponseBody = responseBody
    var progressListener: ProgressListener? = null
    private var bufferedSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource? {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource
    }

    private fun source(source: Source): Source? {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L
            @Throws(IOException::class)
            override fun read(sink: Buffer?, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0 //不断统计当前下载好的数据
                //接口回调
                progressListener?.update(
                    totalBytesRead,
                    responseBody.contentLength(),
                    bytesRead == -1L
                )
                return bytesRead
            }
        }
    }
}

//回调接口
interface ProgressListener {
    /**
     * @param bytesRead 已经读取的字节数
     * @param contentLength 响应总长度
     * @param done 是否读取完毕
     */
    fun update(
        bytesRead: Long,
        contentLength: Long,
        done: Boolean
    )
}