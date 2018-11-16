//
//  AADataLabels.h
//  AAChartKit
//
//  Created by An An on 17/1/6.
//  Copyright © 2017年 An An. All rights reserved.
//*************** ...... SOURCE CODE ...... ***************
//***...................................................***
//*** https://github.com/AAChartModel/AAChartKit        ***
//*** https://github.com/AAChartModel/AAChartKit-Swift  ***
//***...................................................***
//*************** ...... SOURCE CODE ...... ***************

/*
 
 * -------------------------------------------------------------------------------
 *
 * 🌕 🌖 🌗 🌘  ❀❀❀   WARM TIPS!!!   ❀❀❀ 🌑 🌒 🌓 🌔
 *
 * Please contact me on GitHub,if there are any problems encountered in use.
 * GitHub Issues : https://github.com/AAChartModel/AAChartKit/issues
 * -------------------------------------------------------------------------------
 * And if you want to contribute for this project, please contact me as well
 * GitHub        : https://github.com/AAChartModel
 * StackOverflow : https://stackoverflow.com/users/7842508/codeforu
 * JianShu       : http://www.jianshu.com/u/f1e6753d4254
 * SegmentFault  : https://segmentfault.com/u/huanghunbieguan
 *
 * -------------------------------------------------------------------------------
 
 */

#import <Foundation/Foundation.h>

@class AAStyle;

@interface AADataLabels : NSObject

//https://api.hcharts.cn/highcharts#plotOptions.area.dataLabels.align
//align: 水平对齐
//allowOverlap: 允许重叠
//backgroundColor: 背景颜色
//borderColor: 边框颜色
//borderRadius: 边框圆角
//borderWidth: 边框宽度
//className: 类名
//color: 颜色
//crop: 裁剪
//defer: 延迟显示
//enabled: 开关
//format: 格式化字符串
//formatter: 格式化函数
//inside: 显示在内部
//overflow: 溢出处理
//padding: 内边距
//rotation: 旋转角度
//shadow: 阴影
//shape: 箭头形状
//style: 样式
//useHTML: HTML 渲染
//verticalAlign: 竖直对齐方式
//x: 水平偏移
//y: 竖直偏移
//zIndex

AAPropStatementAndFuncStatement(assign, AADataLabels, BOOL      , enabled);
AAPropStatementAndFuncStatement(strong, AADataLabels, AAStyle  *, style);
AAPropStatementAndFuncStatement(copy,   AADataLabels, NSString *, format);
AAPropStatementAndFuncStatement(copy,   AADataLabels, NSNumber *, rotation);
AAPropStatementAndFuncStatement(assign, AADataLabels, BOOL      , allowOverlap);

@end
