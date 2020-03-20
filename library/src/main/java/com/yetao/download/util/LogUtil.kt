package com.yetao.download.util

import android.util.Log
import com.yetao.download.manager.YTDownloadManager

/**
 *  Created by yetao on 2020/3/19
 *  description
 **/
fun String.log(tag:String = "YTDownload")  {
    if(YTDownloadManager.instance.debug) {
        Log.e(tag, this)
    }
}