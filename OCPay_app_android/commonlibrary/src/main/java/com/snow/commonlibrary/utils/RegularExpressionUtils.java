package com.snow.commonlibrary.utils;

import java.util.regex.Pattern;

/**
 * Created by y on 2018/4/25.
 */

public class RegularExpressionUtils {

    public static boolean valid(String str, String regEx) {
        if (StringUtil.isEmpty(str)) return false;
        if (StringUtil.isEmpty(regEx)) return false;
        boolean matches = Pattern.matches(regEx, str);
        return matches;
    }


}
