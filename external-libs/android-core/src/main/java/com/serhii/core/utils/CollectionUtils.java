package com.serhii.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *  Library java collection helper functions
 */

public class CollectionUtils {

    public static <T> T getLastElement(Collection<T> collection) {

        if ( collection.isEmpty()) {
            return null;
        }

        final Iterator<T> iter = collection.iterator();
        T elem = iter.next();

        while (iter.hasNext()) {
            elem = iter.next();
        }

        return elem;
    }

    public static <T1, T2> T1 getMapKeyByValue(Map<T1, T2> collection, T2 value) {

        for (Map.Entry<T1, T2> entry : collection.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static String[] getConvertedArray(ArrayList<String> collection) {

        String[] array = new String[collection.size()];

        for (int i = 0; i < array.length; i++) {
            array[i] = collection.get(i);
        }

        return (array.length == 0) ? null : array;
    }
}
