package com.yetao.download.task

import com.yetao.download.task.data.DownloadInfo
import io.reactivex.Observable
import java.util.HashMap

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
interface IDownloadTask {

    /**
     * 设置优先级，默认为10
     */
    fun setPriority(priority: Int): IDownloadTask

    /**
     * 获取优先级
     */
    fun getPriority(): Int

    /**
     * 添加请求头
     */
    fun addRequestHeader(key: String, value: String): IDownloadTask

    /**
     * 获取请求头
     */
    fun getRequestHeader(key: String): String?

    /**
     * 获取所有请求头
     */
    fun getAllRequestHeaders(): Map<String, String>

    /**
     * 添加所有请求头
     */
    fun addAllRequestHeaders(map: Map<String, String>): Any

    /**
     * 添加响应头
     */
    fun addResponseHeader(key: String, value: String): IDownloadTask

    /**
     * 获取响应头
     */
    fun getResponseHeader(key: String): String?

    /**
     * 获取所有响应头
     */
    fun getAllResponseHeaders(): Map<String, String>

    /**
     * 添加所有响应头
     */
    fun addAllResponseHeaders(header: Map<String, String>): IDownloadTask

    /**
     * 添加url和文件名
     */
    fun addUrl(url: String?,fileName:String? = null): IDownloadTask

    /**
     * 获取url
     */
    fun getUrl(): String?

    /**
     * 获取任务所有url
     */
    fun getUrls(): ArrayList<String>

    /**
     * 获取保存路径
     */
    fun getSavePath(): String?

    /**
     * 设置保存路径
     */
    fun setSavePath(path: String?): IDownloadTask

    /**
     * 获取文件名
     */
    fun getSaveFileName(): String?

    /**
     * 获取所有文件名
     */
    fun getSaveFileNames(): HashMap<String, String?>

    /**
     * 设置回调间隔时间
     */
    fun setIntervalTime(millSecond: Long): IDownloadTask

    /**
     * 获取回调间隔时间
     */
    fun getIntervalTime(): Long

    /**
     * 暂停取消任务
     */
    fun pause()

    /**
     * 转rxjava链式
     */
    fun rxjava(): Observable<DownloadInfo>

    /**
     * 单任务
     */
    fun single(): IRxTask
    fun single(url: String?): IRxTask


    /**
     * 转串行批量下载
     */
    fun serial(): IRxTask

    /**
     * 转并行批量下载
     */
    fun parallel(): IRxTask
}