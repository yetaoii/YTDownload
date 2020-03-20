package com.yetao.ytdownload

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.tbruyelle.rxpermissions2.RxPermissions
import com.yetao.download.manager.YTDownloadManager
import com.yetao.download.task.DownloadTask
import com.yetao.download.task.data.DownloadInfo
import com.yetao.download.util.log
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        YTDownloadManager.instance.cancel(url)//取消单个下载任务
//        YTDownloadManager.instance.cancelAll()//取消下载
//        YTDownloadManager.instance.parallel(array或List)//数组列表转换并行下载
//        YTDownloadManager.instance.serial(array或List)//数组列表转换串行下载

        fab2.setOnClickListener {
           YTDownloadManager.instance.cancelAll()
        }
        fab.setOnClickListener { view ->
            val rxPermissions = RxPermissions(this)
            rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).subscribe {
                if (it) {
                    singeDownload()
                }
            }

        }
    }

    private fun singeDownload() {
        var disposable: Disposable? = null
        val task = DownloadTask()
            .addUrl("http://mxd.clientdown.sdo.com/169/Data169.zip","filename.zip")//下载链接，使用addUrl是方便后续转换批量下载支持,文件名参数可省略
            .setPriority(10)//等待队列中优先级，默认为10
            .setSavePath(YTDownloadManager.instance.defaultSavePath)//保存路径，默认值为Download目录
            .setIntervalTime(500)//回调间隔，默认1000ms
            .single()//转换单任务

        task.rxjava()//转rxjava回调，使用dispose()取消下载，可继续使用链式
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<DownloadInfo> {
                override fun onComplete() {
                    "onComplete".log()
                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                    "onSubscribe".log()
                }

                override fun onNext(t: DownloadInfo) {
                    "onNext  info:${t}".log()
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

    private fun serialDownload(){
        var disposable: Disposable? = null

        val task = DownloadTask()
            .addUrl("http://mxd.clientdown.sdo.com/169/Data169.zip","filename.zip")//下载链接，使用addUrl是方便后续转换批量下载支持,文件名参数可省略
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
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<DownloadInfo> {
                override fun onComplete() {
                    "onComplete".log()
                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                    "onSubscribe".log()
                }

                override fun onNext(t: DownloadInfo) {
                    "onNext  info:${t}".log()
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

    private fun parallelDownload(){
        var disposable: Disposable? = null

        val task = DownloadTask()
            .addUrl("http://mxd.clientdown.sdo.com/169/Data169.zip","filename.zip")//下载链接，使用addUrl是方便后续转换批量下载支持,文件名参数可省略
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
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<DownloadInfo> {
                override fun onComplete() {
                    "onComplete".log()
                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                    "onSubscribe".log()
                }

                override fun onNext(t: DownloadInfo) {
                    "onNext  info:${t}".log()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
