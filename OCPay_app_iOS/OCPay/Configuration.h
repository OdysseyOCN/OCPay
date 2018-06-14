//
//  TCM_Configuration.h
//  TCMDemo
//
//  Created by JL on 2016/10/14.
//  Copyright © 2016年 Taocaimall Inc. All rights reserved.
//

#ifndef TCM_Configuration_h
#define TCM_Configuration_h


/**
 *  机型定义
 */
/**标准屏宽度*/
#define IPHONE_DESIGN_WIDTH      375.f
#define IPHONE_DESIGN_HEIGHT     667.f

#define IPHONE_SCREEN_WIDTH   ([UIScreen mainScreen].bounds.size.width)
#define IPHONE_SCREEN_HEIGHT  ([UIScreen mainScreen].bounds.size.height)

/**iPhone4、4S、5、5S*/
#define IPHONE          (IPHONE_SCREEN_WIDTH < IPHONE_DESIGN_WIDTH)
/**6、6S*/
#define IPHONE_DESIGN   (IPHONE_SCREEN_WIDTH == IPHONE_DESIGN_WIDTH)
/**Plus*/
#define IPHONE_PLUS     (IPHONE_SCREEN_WIDTH > IPHONE_DESIGN_WIDTH)
/**iPhone X*/
#define IPHONE_X        (IPHONE_SCREEN_WIDTH == IPHONE_DESIGN_WIDTH && IPHONE_SCREEN_HEIGHT > IPHONE_DESIGN_HEIGHT)

/**DesignScaling*/
#define IPHONE_DESIGN_SCALING           (IPHONE_SCREEN_WIDTH/IPHONE_DESIGN_WIDTH)

/**StatusBar*/
#define IPHONE_STATUS_BAR_HEIGHT        (IPHONE_X ? 44 : 20)

/**NavigationBar*/
#define IPHONE_NAVIGATION_BAR_HEIGHT    44

/**TabBar 不再使用*/
#define TCM_TAB_BAR_HEIGHT_DEPRECATED   0

/**Home Indicator*/
#define IPHONE_HOME_INDICATOR_HEIGHT    (IPHONE_X ? 34 : 0)

/**可用空间高度[无Home Indicator]*/
#define IPHONE_AVAILABLE_SCREEN_HEIGHT              (IPHONE_SCREEN_HEIGHT-IPHONE_STATUS_BAR_HEIGHT-IPHONE_NAVIGATION_BAR_HEIGHT)

/**可用空间高度[Home Indicator]*/
#define IPHONE_INDICATOR_AVAILABLE_SCREEN_HEIGHT    (IPHONE_AVAILABLE_SCREEN_HEIGHT - IPHONE_HOME_INDICATOR_HEIGHT)

/**Scaling320不再使用*/
#define TCM_SCREEN_SCALING_320                      (IPHONE_SCREEN_WIDTH/320.0f)

/**BuyNow*/
#define TCM_BUYNOW_COLLECTION_CELL_WIDTH            (IPHONE_SCREEN_WIDTH*0.5f - 3.0f)



#define WeakObj(o) __weak typeof(o) o##Weak = o;
#define StrongObj(o) __strong typeof(o) o = o##Weak;

NS_INLINE
CGFloat
_iPhoneLayoutScaling_(){
    if (IPHONE_SCREEN_WIDTH < IPHONE_DESIGN_WIDTH) {
        return IPHONE_DESIGN_SCALING;
    } else {
        return 1.0f;
    }
}
#define iPhoneLayoutScaling (_iPhoneLayoutScaling_())
NS_INLINE
CGFloat
_tcm_layout_(CGFloat layout, CGFloat scale){
    return layout*scale;
}

/**【实际 IPHONE_DESIGN_SCALING】使用设备同标准屏幕的实际[宽]比*/
#define TCM_StdLayout(layout)                   (_tcm_layout_((layout),IPHONE_DESIGN_SCALING))
#define TCM_StdLayoutConstraint(layout)         layout.constant = TCM_StdLayout(layout.constant);
#define TCM_StdLayoutConstraintArray(array)     \
for (NSLayoutConstraint *layout in array) {\
    TCM_StdLayoutConstraint(layout);\
}

/**【MAX:1.0 iPhoneLayoutScaling】屏幕宽度大于标准屏幕时，Scale为1.0，否则Scale == IPHONE_DESIGN_SCALING */
#define TCM_Layout(layout)                      (_tcm_layout_((layout),iPhoneLayoutScaling))
#define TCM_LayoutConstraint(layout)            layout.constant = TCM_Layout(layout.constant);
#define TCM_LayoutConstraintArray(array)        \
for (NSLayoutConstraint *layout in array) {\
    TCM_LayoutConstraint(layout);\
}




/**
 *  字体相关[]
 */
#define TCM_LayoutFont(label) label.font = TCM_Font(label.font.pointSize);

#define TCM_Font(fontSize)                          [UIFont tcmFontOfSize:fontSize]
#define TCM_FontEx(fontSize1,fontSize2,fontSize3)   \
[UIFont tcmFontOfiPhoneSize:fontSize1   \
              iPhoneStdSize:fontSize2   \
             iPhonePlusSize:fontSize3]


#define TCM_BFont(fontSize)                         [UIFont tcmBFontOfSize:fontSize]
#define TCM_BFontEx(fontSize1,fontSize2,fontSize3)  \
[UIFont tcmBFontOfiPhoneSize:fontSize1  \
               iPhoneStdSize:fontSize2  \
              iPhonePlusSize:fontSize3]


#define TCM_LayoutLabelFontArr(labelArr)       \
for (UILabel *label in labelArr) {\
    TCM_LayoutFont(label)\
}

#define TCM_SetFontForLabelArr(labelArr,Font)       \
for (UILabel *label in labelArr) {\
    label.font = Font;\
}

#define TCM_SetColorForLabelArr(labelArr,Color)     \
for (UILabel *label in labelArr) {\
    label.textColor = Color;\
}


#define CoderCopyHash   \
- (void)encodeWithCoder:(NSCoder *)aCoder { [self modelEncodeWithCoder:aCoder]; }\
- (id)initWithCoder:(NSCoder *)aDecoder { self = [super init]; return [self modelInitWithCoder:aDecoder]; }\
- (id)copyWithZone:(NSZone *)zone { return [self modelCopy]; }\
- (NSUInteger)hash { return [self modelHash]; }\
- (BOOL)isEqual:(id)object { return [self modelIsEqual:object]; }



#endif /* TCM_Configuration_h */
