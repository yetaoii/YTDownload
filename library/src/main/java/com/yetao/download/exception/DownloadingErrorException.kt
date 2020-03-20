package com.yetao.download.exception

import java.lang.Exception

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
class DownloadingErrorException(message: String = "the url is downloading.") : Exception(message)