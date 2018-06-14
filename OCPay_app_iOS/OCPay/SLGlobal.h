//
//  SLGlobal.h
//  LLDai
//
//  Created by y_liang on 8/12/14.
//  Copyright (c) 2014 y_liang. All rights reserved.
//

#ifndef LLDai_SLGlobal_h
#define LLDai_SLGlobal_h



//RGB

#define RGB(r,g,b)  \
[UIColor colorWithRed:r/255.0f green:g/255.0f blue:b/255.0f alpha:1.0f]

#define UIColorFromRGB(rgbValue)        \
[UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0  \
                green:((float)((rgbValue & 0xFF00) >> 8))/255.0 \
                 blue:((float)(rgbValue & 0xFF))/255.0 \
                alpha:1.0]

#define UIColorFromRGBAlpha(rgbValue,alphaValue)    \
[UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0  \
                green:((float)((rgbValue & 0xFF00) >> 8))/255.0 \
                 blue:((float)(rgbValue & 0xFF))/255.0  \
                alpha:alphaValue]


#pragma mark - 全部颜色

/*标号:颜色:说明
 ******NO.1:0xFFFFFF：背景色、按钮文字、图标
 ******NO.2:0xFAFAFA：底部tab颜色
 ******NO.3:0xf5f5f9：商品列表页面背景色
 ******NO.4:0xD9D9D9：底部tab栏描边颜色
 ******NO.5:0xB3B3B3：失效图标颜色
 ******NO.6:0x666666：失效、辅助文字颜色、图标默认颜色
 ******NO.7:0x333333：默认状态文字颜色
 ******NO.8:0x000000：店铺名称一级标题等颜色
 ******NO.9:0xFF0033：金额、重要醒目、点击状态等文字颜色、点击图标颜色
 ******NO.10:0xFFA800：订单完成到待评价状态颜色，如待评价
 ******NO.11:0xFF4242：订单未结束状态显示文字等颜色，如待付款
 ******NO.12:0x35CB25：订单配送过程对应状态颜色，如配送中
 */

#define Color_NO_1  UIColorFromRGB(0xFFFFFF)
#define Color_NO_2  UIColorFromRGB(0xFAFAFA)
#define Color_NO_3  UIColorFromRGB(0xf5f5f9)
#define Color_NO_4  UIColorFromRGB(0xD9D9D9)
#define Color_NO_5  UIColorFromRGB(0xB3B3B3)
#define Color_NO_6  UIColorFromRGB(0x666666)
#define Color_NO_7  UIColorFromRGB(0x333333)
#define Color_NO_8  UIColorFromRGB(0x000000)
#define Color_NO_9  UIColorFromRGB(0xFF0033)
#define Color_NO_10 UIColorFromRGB(0xFFA800)
#define Color_NO_11 UIColorFromRGB(0xFF4242)
#define Color_NO_12 UIColorFromRGB(0x35CB25)
#define Color_NO_13 UIColorFromRGB(0x2FB320)
#define Color_NO_14 UIColorFromRGB(0xfafafa)
#define Color_NO_15 UIColorFromRGB(0x999999)
#define Color_NO_16 UIColorFromRGB(0xe6e6e6)
#define Color_NO_17 UIColorFromRGB(0xfdb7c5)
#define Color_NO_18 UIColorFromRGB(0xfef3f5)
#define Color_NO_19 UIColorFromRGB(0xffa300)
#define Color_NO_20 UIColorFromRGB(0x00cf00)
#define Color_NO_21  UIColorFromRGB(0xf9f9f9)
#define Color_NO_22  UIColorFromRGB(0xe4e4e4)
#define Color_NO_23  UIColorFromRGB(0xe5e4e5)
#define Color_NO_24  UIColorFromRGB(0xeeefef)

/*背景色
 */
#define Color_Background_NO_1   UIColorFromRGB(0xFFFFFF)
#define Color_Background_NO_2   UIColorFromRGB(0xFAFAFA)
#define Color_Background_NO_3   UIColorFromRGB(0xf5f5f9)
#define Color_Background_NO_4   UIColorFromRGB(0xD9D9D9)
#define Color_Background_NO_5   UIColorFromRGB(0xB3B3B3)
#define Color_Background_NO_6   UIColorFromRGB(0x666666)
#define Color_Background_NO_7   UIColorFromRGB(0x333333)
#define Color_Background_NO_8   UIColorFromRGB(0x000000)
#define Color_Background_NO_9   UIColorFromRGB(0xFF0033)
#define Color_Background_NO_10  UIColorFromRGB(0xFFA800)
#define Color_Background_NO_11  UIColorFromRGB(0xFF4242)
#define Color_Background_NO_12  UIColorFromRGB(0x35CB25)
#define Color_Background_NO_13  UIColorFromRGB(0x2FB320)
#define Color_Background_NO_14  UIColorFromRGB(0xFFD801)
#define COLOR_BACKGROUND_NO_15  UIColorFromRGB(0xF78088)
#define COLOR_BACKGROUND_NO_16  UIColorFromRGB(0xfef7ed)

/*文字
 */
#define Color_TEXT_NO_1     UIColorFromRGB(0xFFFFFF)
#define Color_TEXT_NO_2     UIColorFromRGB(0xFAFAFA)
#define Color_TEXT_NO_3     UIColorFromRGB(0xf5f5f9)
#define Color_TEXT_NO_4     UIColorFromRGB(0xD9D9D9)
#define Color_TEXT_NO_5     UIColorFromRGB(0xB3B3B3)
#define Color_TEXT_NO_6     UIColorFromRGB(0x666666)
#define Color_TEXT_NO_7     UIColorFromRGB(0x333333)
#define Color_TEXT_NO_8     UIColorFromRGB(0x000000)
#define Color_TEXT_NO_9     UIColorFromRGB(0xFF0033)
#define Color_TEXT_NO_10    UIColorFromRGB(0xFFA800)
#define Color_TEXT_NO_11    UIColorFromRGB(0xFF4242)
#define Color_TEXT_NO_12    UIColorFromRGB(0x35CB25)
#define Color_TEXT_NO_13    UIColorFromRGB(0xFDB9C6)
#define Color_TEXT_NO_14    UIColorFromRGB(0xfafafa)
#define Color_TEXT_NO_15    UIColorFromRGB(0x999999)
#define Color_TEXT_NO_16    UIColorFromRGB(0x723411)
#define Color_TEXT_NO_17    UIColorFromRGB(0x773E07)
#define Color_TEXT_NO_18    UIColorFromRGB(0xf9670d)
#define Color_TEXT_NO_19    UIColorFromRGB(0xF76D57)
#define Color_TEXT_NO_20    UIColorFromRGB(0xDC6AA2)
#define Color_TEXT_NO_21    UIColorFromRGB(0xC41975)
#define Color_TEXT_NO_22    UIColorFromRGB(0x653A08)
#define Color_TEXT_NO_23    UIColorFromRGB(0x1ABB40)
#define Color_TEXT_NO_24    UIColorFromRGB(0x101010)

/*按钮
 */
#define Color_Button_NO_1   UIColorFromRGB(0xFFFFFF)
#define Color_Button_NO_2   UIColorFromRGB(0xFAFAFA)
#define Color_Button_NO_3   UIColorFromRGB(0xf5f5f9)
#define Color_Button_NO_4   UIColorFromRGB(0xD9D9D9)
#define Color_Button_NO_5   UIColorFromRGB(0xB3B3B3)
#define Color_Button_NO_6   UIColorFromRGB(0x666666)
#define Color_Button_NO_7   UIColorFromRGB(0x333333)
#define Color_Button_NO_8   UIColorFromRGB(0x000000)
#define Color_Button_NO_9   UIColorFromRGB(0xFF0033)
#define Color_Button_NO_10  UIColorFromRGB(0xFFA800)
#define Color_Button_NO_11  UIColorFromRGB(0xFF4242)
#define Color_Button_NO_12  UIColorFromRGB(0x35CB25)
#define Color_Button_NO_13  UIColorFromRGB(0x0099ff)
#define Color_Button_NO_14  UIColorFromRGB(0xFFD801)
#define Color_Button_NO_15  UIColorFromRGB(0xCCCCCC)

/*分割线
 */
#define Color_Line_NO_1     UIColorFromRGB(0xFFFFFF)
#define Color_Line_NO_2     UIColorFromRGB(0xFAFAFA)
#define Color_Line_NO_3     UIColorFromRGB(0xF0F0F0)
#define Color_Line_NO_4     UIColorFromRGB(0xF0F0F0)
#define Color_Line_NO_5     UIColorFromRGB(0xB3B3B3)
#define Color_Line_NO_6     UIColorFromRGB(0x666666)
#define Color_Line_NO_7     UIColorFromRGB(0x333333)
#define Color_Line_NO_8     UIColorFromRGB(0x000000)
#define Color_Line_NO_9     UIColorFromRGB(0xFF0033)
#define Color_Line_NO_10    UIColorFromRGB(0xFFA800)
#define Color_Line_NO_11    UIColorFromRGB(0xFF4242)
#define Color_Line_NO_12    UIColorFromRGB(0x35CB25)

/*图标
 */
#define Color_Icon_NO_1     UIColorFromRGB(0xFFFFFF)
#define Color_Icon_NO_2     UIColorFromRGB(0xFAFAFA)
#define Color_Icon_NO_3     UIColorFromRGB(0xf5f5f9)
#define Color_Icon_NO_4     UIColorFromRGB(0xD9D9D9)
#define Color_Icon_NO_5     UIColorFromRGB(0xB3B3B3)
#define Color_Icon_NO_6     UIColorFromRGB(0x666666)
#define Color_Icon_NO_7     UIColorFromRGB(0x333333)
#define Color_Icon_NO_8     UIColorFromRGB(0x000000)
#define Color_Icon_NO_9     UIColorFromRGB(0xFF0033)
#define Color_Icon_NO_10    UIColorFromRGB(0xFFA800)
#define Color_Icon_NO_11    UIColorFromRGB(0xFF4242)
#define Color_Icon_NO_12    UIColorFromRGB(0x35CB25)

#pragma mark - 字号

/*名称：字号：说明
 *NO.1:13:顶部导航栏名称、文字按钮、金额（现价、总价）
 *NO.2:12:标题、商品名称
 *NO.3:11:提示信息、注释文字、打折信息
 *NO.4:10:底部菜单文字
 */
#define Font_NO_1XXL    [UIFont systemFontOfSize:18.0]
#define Font_NO_2L      [UIFont systemFontOfSize:16.0]
#define Font_NO_1XL     [UIFont systemFontOfSize:15.0]
#define Font_NO_1L      [UIFont systemFontOfSize:14.0]
#define Font_NO_1       [UIFont systemFontOfSize:13.0]
#define Font_NO_2       [UIFont systemFontOfSize:12.0]
#define Font_NO_3       [UIFont systemFontOfSize:11.0]
#define Font_NO_4       [UIFont systemFontOfSize:10.0]
#define Font_NO_5       [UIFont systemFontOfSize:9.0]
#define Font_NO_6       [UIFont systemFontOfSize:8.0]

/*金额
 */
#define Font_Cash_NO_1  [UIFont systemFontOfSize:13.0]
#define Font_Cash_NO_2  [UIFont systemFontOfSize:11.0]

#pragma mark - 用户相关信息
//用户信息
#define UserInfo            [GDUserInfo sharedGDUserInfo]
#define KUserPhoneNumber    UserInfo.phoneNumber
#define KUserId             UserInfo.userIdStr
#define KUserLogin          [UserInfo isLogin]

//当前日期
#define KNowDate        [GDUserInfo nowDate]

//上次登录日期
#define KLastLoginDate  [[NSUserDefaults standardUserDefaults] objectForKey:@"KLastLoginDate"]

#pragma mark - 通讯录
//通讯录最新时间
#define kAddressBookLastDateFlag    @"kAddressBookLastDateFlag"

//保存通讯录读取的页数
#define kAddressBookLastCurrentPage @"kAddressBookLastCurrentPage"


#pragma mark - 默认图片
#define kDefaultImage   [UIImage imageNamed:@"defaultImage"]

#pragma mark - BundleId
#define EnterpriseBundleIdentifier  @"com.gd.taocaimall"
#define PersonalBundleIdentifier    @"com.Taocaimall.WeexDemo"


#define kAppBundleId        [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleIdentifier"]

#pragma mark - 记录版本类型：抢先版和appstore版，记录版本号：用于升级机制
//EnterpriseBundleIdentifier企业版，PersonalBundleIdentifier个人版
#define VERSION_TYPE        [[[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleIdentifier"] isEqualToString:EnterpriseBundleIdentifier]?EnterpriseBundleIdentifier:PersonalBundleIdentifier
#define VERSION_TYPE_FLAG   @"VersionTypeFlag"

//1表示重大版本，0表示非重大版本
#define IMPORTANT_VERSION_TYPE  @"0"

//版本号
#define Version     @"version"

/**
 *  apstore下载地址
 *
 *  @return return value description
 */
#define APPStoreUrl     [NSURL URLWithString:[NSString stringWithFormat:@"http://itunes.apple.com/cn/app/tao-cai-mao/id1012946513"]]

#pragma mark - 当前公告页书和总页数
#define CurrentPageNum  @"CurrentPageNum"
#define TotalPageNum    @"TotalPageNum"

#define ADDRESS_FLAG    @"SaveAddressKey"


#pragma mark - 字符串转浮点型计算

#define DN_STRING(str)      [NSDecimalNumber decimalNumberWithString:str.length > 0 ? str : @"0"]

#define DN_ADD(a,b)         [a decimalNumberByAdding:b]

#define DN_SUBTRACT(a,b)    [a decimalNumberBySubtracting:b]

#define DN_MULTIPLY(a,b)    [a decimalNumberByMultiplyingBy:b]

#define DN_DIVID(a,b)       [a decimalNumberByDividingBy:b]




#pragma mark -- 通知
#define NOTICE_RefreshNewPersonActivity     @"NOTICE_RefreshNewPersonActivity" // 新人活动
#define NOTICE_RequestAnnouncement          @"NOTICE_RequestAnnouncement"  // 请求公告

//MARK:KEY
#define TCM_AESKEY      @"taocaimall201609" // AES加密key
#define TCM_APPID       @"93d8dd933973ee892b505f076d5896e5"

#endif



