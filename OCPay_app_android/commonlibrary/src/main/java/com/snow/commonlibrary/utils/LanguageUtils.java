package com.snow.commonlibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Locale;


public class LanguageUtils {

    public static Locale compareLocale(Context context, String item) {

//        String item = AppSettings.getLanguage(context);

        Locale locale = Locale.getDefault();
        if (TextUtils.isEmpty(item) /*|| context.getResources().getString(R.string.language_default).equals(item)*/) { // TODO
            return locale;
        }
        if ("English".equals(item)) {
            locale = new Locale("en", "");
        } else if ("български".equals(item)) {
            locale = new Locale("bg", "");
        } else if ("Čeština".equals(item)) {
            locale = new Locale("cs", "");
        } else if ("Dansk".equals(item)) {
            locale = new Locale("da", "");
        } else if ("Deutsch".equals(item)) {
            locale = new Locale("de", "");
        } else if ("Eλληνικά".equals(item)) {
            locale = new Locale("el", "");
        } else if ("Español".equals(item)) {
            locale = new Locale("es", "");
        } else if ("Español(Latinoamérica)".equals(item)) {
            locale = new Locale("es", "LA");
        } else if ("Français".equals(item)) {
            locale = new Locale("fr", "");
        } else if ("हिन्दी".equals(item)) {
            locale = new Locale("hi", "");
        } else if ("Hrvatski".equals(item)) {
            locale = new Locale("hr", "");
        } else if ("Magyar".equals(item)) {
            locale = new Locale("hu", "");
        } else if ("Indonesia".equals(item)) {
            locale = new Locale("id", "");
        } else if ("Italiano".equals(item)) {
            locale = new Locale("it", "");
        } else if ("日本語".equals(item)) {
            locale = new Locale("ja", "");
        } else if ("한국의".equals(item)) {
            locale = new Locale("ko", "");
        } else if ("Melayu".equals(item)) {
            locale = new Locale("ms", "");
        } else if ("Nederlands".equals(item)) {
            locale = new Locale("nl", "");
        } else if ("Norsk bokmâl".equals(item)) {
            locale = new Locale("nb", "");
        } else if ("Polski".equals(item)) {
            locale = new Locale("pl", "");
        } else if ("Português(Portugal)".equals(item)) {
            locale = new Locale("pt", "");
        } else if ("Português(Brasil)".equals(item)) {
            locale = new Locale("pt", "BR");
        } else if ("Română".equals(item)) {
            locale = new Locale("ro", "");
        } else if ("Pусский".equals(item)) {
            locale = new Locale("ru", "");
        } else if ("Slovenský".equals(item)) {
            locale = new Locale("sk", "");
        } else if ("Slovenščina".equals(item)) {
            locale = new Locale("sl", "BR");
        } else if ("Српски".equals(item)) {
            locale = new Locale("sr", "");
        } else if ("Svenska".equals(item)) {
            locale = new Locale("sv", "");
        } else if ("ไทย".equals(item)) {
            locale = new Locale("th", "");
        } else if ("Türkçe".equals(item)) {
            locale = new Locale("tr", "");
        } else if ("Tiếng Việt".equals(item)) {
            locale = new Locale("vi", "");
        } else if ("简体中文".equals(item)) {
            locale = Locale.SIMPLIFIED_CHINESE;
        } else if ("繁體中文(香港)".equals(item)) {
            locale = new Locale("zh", "HK");
        } else if ("繁體中文(台灣)".equals(item)) {
            locale = new Locale("zh", "TW");
        } else if ("বাঙালি".equals(item)) {
            locale = new Locale("bn", "");
        } else if ("Український".equals(item)) {
            locale = new Locale("uk", "");
        } else if ("عربي".equals(item)) {
            locale = new Locale("ar", "");
        }
        String abc = locale.toString();
        return locale;
    }


    public static void setLanguage(Context context, String item) {
        Locale locale = compareLocale(context, item);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = locale;
        resources.updateConfiguration(config, dm);
    }

    public static void restartApp(Context context, Class c) {
        Intent intent = new Intent(context, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String getLanguageRealTime(Context context, String item) {
        Locale locale = compareLocale(context, item);
        String language = locale.getLanguage();
        return language;
    }

//    public static boolean isRtlLanguage(Context context){
//        boolean defaultLanguageAr =(Locale.getDefault().getLanguage().equals(new Locale("ar", "").getLanguage()));
//        boolean appLanguageAr =AppSettings.getLanguage(context).equals("عربي");
//        boolean appLanguageAuto = AppSettings.getLanguage(context).equals("AUTO");
//        YbLog.i("BaseActivity","判断语言 defaultLanguageAr : "+defaultLanguageAr+" appLanguageAr "+appLanguageAr+" appLanguageAuto "+appLanguageAuto);
//        if ((defaultLanguageAr && appLanguageAuto ) || appLanguageAr){
//            return true;
//        }
//        Configuration configuration = context.getResources().getConfiguration();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            return configuration.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
//        }
//        return false;
//    }
//
//    public static boolean isRtlLanguageForApp(Context context) {
//        boolean defaultLanguageAr = (Locale.getDefault().getLanguage().equals(new Locale("ar", "").getLanguage()));
//        boolean appLanguageAr = AppSettings.getLanguage(context).equals("عربي");
//        boolean appLanguageAuto = AppSettings.getLanguage(context).equals("AUTO");
//        YbLog.i("BaseActivity", "判断语言 defaultLanguageAr : " + defaultLanguageAr + " appLanguageAr " + appLanguageAr + " appLanguageAuto " + appLanguageAuto);
//        if ((defaultLanguageAr && appLanguageAuto) || appLanguageAr) {
//            return true;
//        }
////        return false;
////        Configuration configuration = context.getResources().getConfiguration();
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
////            return configuration.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
////        }
//        return false;
//    }
//
//    public static boolean isArLanguage(Context context) {
//        boolean defaultLanguageAr = (Locale.getDefault().getLanguage().equals(new Locale("ar", "").getLanguage()));
//        boolean appLanguageAr = AppSettings.getLanguage(context).equals("عربي");
//        boolean appLanguageAuto = AppSettings.getLanguage(context).equals("AUTO");
//        YbLog.i("BaseActivity", "判断语言 defaultLanguageAr : " + defaultLanguageAr + " appLanguageAr " + appLanguageAr + " appLanguageAuto " + appLanguageAuto);
//        return (defaultLanguageAr && appLanguageAuto) || appLanguageAr;
//    }
//
//
//    public static boolean isEnLanguage(Context context) {
//        boolean defaultLanguageAr = (Locale.getDefault().getLanguage().equals(new Locale("en", "").getLanguage()));
//        boolean appLanguageAr = AppSettings.getLanguage(context).equals("English");
//        boolean appLanguageAuto = AppSettings.getLanguage(context).equals("AUTO");
//        YbLog.i("BaseActivity", "判断语言 defaultLanguageAr : " + defaultLanguageAr + " appLanguageAr " + appLanguageAr + " appLanguageAuto " + appLanguageAuto);
//        return (defaultLanguageAr && appLanguageAuto) || appLanguageAr;
//    }
}
