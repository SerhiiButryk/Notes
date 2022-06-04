/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.utils

/**
 * Collection of helper functions
 */
object CollectionUtils {

    fun <T> getLastElement(collection: Collection<T>): T? {
        if (collection.isEmpty()) {
            return null
        }
        val iter = collection.iterator()
        var elem = iter.next()
        while (iter.hasNext()) {
            elem = iter.next()
        }
        return elem
    }

    fun <T1, T2> getMapKeyByValue(collection: Map<T1, T2>, value: T2): T1? {
        for ((key, value1) in collection) {
            if (value1 == value) {
                return key
            }
        }
        return null
    }

}