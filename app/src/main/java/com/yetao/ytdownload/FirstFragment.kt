package com.yetao.ytdownload

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.tbruyelle.rxpermissions2.RxPermissions
import com.yetao.download.manager.YTDownloadManager
import com.yetao.download.task.DownloadTask
import com.yetao.download.task.data.DownloadInfo
import com.yetao.download.util.io2main
import com.yetao.download.util.log
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    var disposable1: Disposable? = null
    var disposable2: Disposable? = null
    var disposable3: Disposable? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        initListener()
    }

    private fun initListener() {
        switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    val rxPermissions = RxPermissions(this)
                    rxPermissions.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).subscribe {
                        if (it) {
                            singeDownload()
                        }
                    }
                }
                false -> disposable1?.dispose()
            }

        }
        switch2.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    val rxPermissions = RxPermissions(this)
                    rxPermissions.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).subscribe {
                        if (it) {
                            serialDownload()
                        }
                    }
                }
                false -> disposable2?.dispose()
            }

        }
        switch3.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    val rxPermissions = RxPermissions(this)
                    rxPermissions.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).subscribe {
                        if (it) {
                            parallelDownload()
                        }
                    }
                }
                false -> disposable3?.dispose()
            }

        }
    }

    private fun singeDownload() {
        val task = DownloadTask()
            .addUrl(
                "https://ruanshi1.8686c.com/prod/4.6.17944.0223/Zoom.pkg",
                "filename.pkg"
            )//下载链接，使用addUrl是方便后续转换批量下载支持,文件名参数可省略
            .setPriority(10)//等待队列中优先级，默认为10
            .setSavePath(YTDownloadManager.instance.defaultSavePath)//保存路径，默认值为Download目录
            .setIntervalTime(500)//回调间隔，默认1000ms
            .single()//转换单任务

        task.rxjava()//转rxjava回调，使用dispose()取消下载，可继续使用链式
            .io2main()
            .subscribe(object : Observer<DownloadInfo> {
                override fun onComplete() {
                    "onComplete".log()
                    switch1.isChecked = false
                }

                override fun onSubscribe(d: Disposable) {
                    disposable1 = d
                    "onSubscribe".log()
                }

                override fun onNext(t: DownloadInfo) {
                    val percent = t.currentBytes * 100 / t.totalBytes
                    progressBar1.progress = percent.toInt()
                    switch1.text = "${percent}%"
                    "onNext  info:${t}".log()
                }

                override fun onError(e: Throwable) {
                    "onError  message:${e.message}".log()
                    switch1.isChecked = false
                }
            })

//        //取消下载
//        disposable?.dispose()
//        //或者
//        task.pause()
    }

    private fun serialDownload() {

        val task = DownloadTask()
            .addUrl(
                "http://mxd.clientdown.sdo.com/169/Data169.zip",
                "filename.zip"
            )//下载链接，使用addUrl是方便后续转换批量下载支持,文件名参数可省略
            .addUrl("http://mxd.clientdown.sdo.com/169/Data16901.cab")
            .addUrl("http://mxd.clientdown.sdo.com/169/Data16902.cab")
            .addUrl("http://mxd.clientdown.sdo.com/169/Data16903.cab")
            .addUrl("http://jxsj-client.dl.kingsoft.com/jxsjlv/setup/jxsjls_mini_setup_20190416.exe")
            .addUrl("https://ruanshi1.8686c.com/prod/4.6.17944.0223/Zoom.pkg")
            .setPriority(10)//等待队列中优先级，默认为10
            .setSavePath(YTDownloadManager.instance.defaultSavePath)//保存路径，默认值为Download目录
            .setIntervalTime(500)//回调间隔，默认1000ms
            .serial()//转换串行下载

        task.rxjava()//转rxjava回调，使用dispose()取消下载，可继续使用链式
            .io2main()
            .subscribe(object : Observer<List<DownloadInfo>> {
                override fun onComplete() {
                    "onComplete".log()
                }

                override fun onSubscribe(d: Disposable) {
                    disposable2 = d
                    "onSubscribe".log()
                }

                override fun onNext(t: List<DownloadInfo>) {
                    "onNext  info:${t}".log()
                    val singlePercent = 100 / t.size.toFloat()
                    var percent = 0f
                    for (info in t) {
                        if (info.totalBytes > 0 && info.currentBytes > 0)
                            percent += info.currentBytes * singlePercent / info.totalBytes
                    }
                    if(percent>100) percent = 100f
                    progressBar2.progress = percent.toInt()
                    switch2.text = "${String.format("%.2f", percent)}%"
                }

                override fun onError(e: Throwable) {
                    "onError  message:${e.message}".log()
                }
            })
//        //取消下载
//        disposable?.dispose()
//        //或者
//        task.pause()
    }

    private fun parallelDownload() {

        val task = DownloadTask()
            .addUrl(
                "http://mxd.clientdown.sdo.com/169/Data169.zip",
                "filename.zip"
            )//下载链接，使用addUrl是方便后续转换批量下载支持,文件名参数可省略
            .addUrl("http://mxd.clientdown.sdo.com/169/Data16901.cab")
            .addUrl("http://mxd.clientdown.sdo.com/169/Data16902.cab")
            .addUrl("http://mxd.clientdown.sdo.com/169/Data16903.cab")
            .addUrl("http://jxsj-client.dl.kingsoft.com/jxsjlv/setup/jxsjls_mini_setup_20190416.exe")
            .addUrl("https://ruanshi1.8686c.com/prod/4.6.17944.0223/Zoom.pkg")
            .setPriority(10)//等待队列中优先级，默认为10
            .setSavePath(YTDownloadManager.instance.defaultSavePath)//保存路径，默认值为Download目录
            .setIntervalTime(500)//回调间隔，默认1000ms
            .parallel()//转换串行下载

        task.rxjava()//转rxjava回调，使用dispose()取消下载，可继续使用链式
            .io2main()
            .subscribe(object : Observer<List<DownloadInfo>> {
                override fun onComplete() {
                    "onComplete".log()
                    switch3.isChecked = false

                }

                override fun onSubscribe(d: Disposable) {
                    disposable3 = d
                    "onSubscribe".log()
                }

                override fun onNext(t: List<DownloadInfo>) {
                    "onNext  info:${t}".log()
                    val singlePercent = 100 / t.size.toFloat()
                    var percent = 0f
                    for (info in t) {
                        if (info.totalBytes > 0 && info.currentBytes > 0)
                            percent += info.currentBytes * singlePercent / info.totalBytes
                    }
                    if(percent>100) percent = 100f
                    progressBar3.progress = percent.toInt()
                    switch3.text = "${String.format("%.2f", percent)}%"
                }

                override fun onError(e: Throwable) {
                    "onError  message:${e.message}".log()
                    switch3.isChecked = false

                }
            })
//        //取消下载
//        disposable?.dispose()
//        //或者
//        task.pause()
    }


}
