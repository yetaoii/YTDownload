package com.yetao.download.exception

import java.lang.Exception

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
class UrlErrorException(message: String = "url is error") : Exception(message)