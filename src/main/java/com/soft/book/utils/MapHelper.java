package com.soft.book.utils;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author ddmcc
 */
public class MapHelper {



    public static <K, V> Map<K, V> ofLinkedMap(Object...values) {
        if (values.length % 2 != 0) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Map<K, V> map = Maps.newLinkedHashMap();
        for (int i = 0; i < values.length - 1; i+=2) {
            K k = (K) values[i];
            V v = (V) values[i + 1];
            map.put(k, v);
        }

        return map;
    }


}
