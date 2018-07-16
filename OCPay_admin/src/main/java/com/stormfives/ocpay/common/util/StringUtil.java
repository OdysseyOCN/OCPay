package com.stormfives.ocpay.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public   class StringUtil {
 
    /**
     * 判断字符串是否为空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        return value == null || "".equals(value.trim());
    }
 
    /**
     * 判断字符串是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmail(String email) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }
 
    /**
     * 判断是否是数字
     *
     * @param sourceString
     * @return
     */
    public static boolean isNumber(String sourceString) {
        if (isEmpty(sourceString))
            return false;
        char[] sourceChar = sourceString.toCharArray();
        for (int i = 0; i < sourceChar.length; i++)
            if ((sourceChar[i] < '0') || (sourceChar[i] > '9'))
                return false;
        return true;
    }
}