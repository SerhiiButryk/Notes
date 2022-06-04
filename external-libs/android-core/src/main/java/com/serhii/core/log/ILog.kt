/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.log

internal interface ILog {
    fun info(tag: String, message: String)
    fun detail(tag: String, message: String)
    fun error(tag: String, message: String)
    var tag: String
}