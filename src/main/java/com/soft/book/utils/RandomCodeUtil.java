package com.soft.book.utils;

import java.util.UUID;

public class RandomCodeUtil {

    public static String generateUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
