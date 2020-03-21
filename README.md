# YTDownloader

最近对网络下载这一块比较感兴趣，所以专门整理和写了一个简易的下载库--YTDownload

## 特性

* 基于Retrofit和Okhttp3来请求网络
* 基于RxJava，支持链式调用
* 支持断点续传，默认开启
* 支持串行、和并行批量下载
* 支持进度回调
* 自动校验服务器文件是否更改(前提，url对应文件还未下好)

## 集成

在app的build.gradle中添加依赖

```java
    //仓库mavenCentral()
    implementation "com.yetaoii:yt-download:1.0.1"
```


在AndroidManifest.xml添加权限

```java
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.INTERNET"/>
```

在Application的onCreate()方法初始化

```java

    override fun onCreate() {
        super.onCreate()
        initYTDownload()
    }

    private fun initYTDownload() {
        YTDownloadManager.instance.init(this)//初始化
        YTDownloadManager.instance.debug = true//调试模式日志开关
        //    YTDownloadManager.instance.baseUrl=//配置retrofit的baseUrl
        //    YTDownloadManager.instance.defaultSavePath = //默认保存路径
        //    YTDownloadManager.instance.okHttpClient = //retrofit的okHttpClient
        //    YTDownloadManager.instance.callAdapterFactory = //retrofit的callAdapterFactory
        //    YTDownloadManager.instance.converterFactory = //retrofit的converterFactory
    }
```

## 基本使用

### 单任务

```java
    private fun singeDownload() {
        var disposable: Disposable? = null
        val task = DownloadTask()
            .addUrl("http://mxd.clientdown.sdo.com/169/Data169.zip","filename.zip")//下载链接，使用addUrl是方便后续转换批量下载支持,文件名参数可省略
            .setPriority(10)//等待队列中优先级，默认为10
            .setSavePath(YTDownloadManager.instance.defaultSavePath)//保存路径，默认值为Download目录
            .setIntervalTime(500)//回调间隔，默认1000ms
            .single()//转换单任务

        task.rxjava()//转rxjava回调，使用dispose()取消下载，可继续使用链式
            .subscribe(object : Observer<DownloadInfo> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(t: DownloadInfo) {
                }

                override fun onError(e: Throwable) {

                }
            })

        //取消下载
        disposable?.dispose()
        //或者
        task.pause()
    }
```

### 串行批量下载

```java

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
            .subscribe(object : Observer<DownloadInfo> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(t: DownloadInfo) {
                }

                override fun onError(e: Throwable) {

                }
            })
        //取消下载
        disposable?.dispose()
        //或者
        task.pause()
    }

```


### 并行批量下载

```java
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
            .subscribe(object : Observer<DownloadInfo> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(t: DownloadInfo) {
                }

                override fun onError(e: Throwable) {

                }
            })
        //取消下载
        disposable?.dispose()
        //或者
        task.pause()
    }
```

### 使用YTDownloadManager控制

```java

    YTDownloadManager.instance.cancel(url)//取消单个下载任务
    YTDownloadManager.instance.cancelAll()//取消下载
    YTDownloadManager.instance.parallel(array或List)//数组列表转换并行下载
    YTDownloadManager.instance.serial(array或List)//数组列表转换串行下载
```

