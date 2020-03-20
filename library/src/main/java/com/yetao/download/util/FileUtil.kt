package com.yetao.download.util

import com.yetao.download.manager.YTDownloadManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
object FileUtil {
    fun writeFile(
        inputStream: InputStream,
        filePath:String?,
        range: Boolean = false,
        running:(()->Boolean)? = null
    ) {
        val file = File(filePath)
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                file.mkdirs()
            }
            file.createNewFile()
        }
        var fos = FileOutputStream(file, range)
        val buffer = ByteArray(2048)
        // 3.开始读文件
        // 3.开始读文件
        var len = -1
        try {
            while (running?.invoke() != false && inputStream.read(buffer).also {
                    len = it
                } != -1) { // 将Buffer中的数据写到outputStream对象中
                fos.write(buffer, 0, len)
            }

        } catch (e: IOException) {
//            e.printStackTrace()
        } finally {
            fos.close()
            inputStream.close()
        }
    }

    fun getSavePath(path: String?, filename: String?, suffix: String?) {
        val savePath =
            path ?: YTDownloadManager.instance.defaultSavePath
        val saveFileName = filename
            ?: ("/${UUID.randomUUID()}${suffix ?: ""}")
    }
}