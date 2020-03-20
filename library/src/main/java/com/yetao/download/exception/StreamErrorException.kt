package com.yetao.download.exception

import java.lang.Exception

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
class StreamErrorException(message: String = "stream is error") : Exception(message)