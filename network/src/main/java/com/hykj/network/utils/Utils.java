package com.hykj.network.utils;

import android.text.TextUtils;

import okhttp3.internal.Util;

public class Utils {
    /**
     * 检查键值对是否符合要求
     *
     * @param name
     * @param value
     */
    public static void checkNameAndValue(String name, String value) {
        if (TextUtils.isEmpty(name))
            throw new NullPointerException("name == null or name is empty");
        for (int i = 0, length = name.length(); i < length; i++) {
            char c = name.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                throw new IllegalArgumentException(Util.format(
                        "Unexpected char %#04x at %d in header name: %s", (int) c, i, name));
            }
        }
        if (value == null) throw new NullPointerException("value == null");
        for (int i = 0, length = value.length(); i < length; i++) {
            char c = value.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                throw new IllegalArgumentException(Util.format(
                        "Unexpected char %#04x at %d in %s value: %s", (int) c, i, name, value));
            }
        }
    }
}
