//
//  AAChartView.m
//  AAChartKit
//
//  Created by An An on 17/1/16.
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
 * ------------------------------------------------------------------------------
 * And if you want to contribute for this project, please contact me as well
 * GitHub        : https://github.com/AAChartModel
 * StackOverflow : https://stackoverflow.com/users/7842508/codeforu
 * JianShu       : http://www.jianshu.com/u/f1e6753d4254
 * SegmentFault  : https://segmentfault.com/u/huanghunbieguan
 *
 * -------------------------------------------------------------------------------
 
 */

#import "AAChartView.h"
#import <WebKit/WebKit.h>

/**
 *  Get the system version number
 */
#define AASYSTEM_VERSION [[[UIDevice currentDevice] systemVersion] floatValue]
/**
 *  The console output log
 */
#ifdef DEBUG // Debug status, open the LOG function
#define AADetailLog(fmt, ...) NSLog((@"-------> %@ [Line %d] \n"fmt "\n\n"), [[NSString stringWithFormat:@"%s",__FILE__] lastPathComponent], __LINE__, ##__VA_ARGS__);
#else // Release status, turn off the LOG function
#define AADetailLog(...)
#endif

#define kDevice_Is_iPhoneX ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(1125, 2436), [[UIScreen mainScreen] currentMode].size) : NO)

@interface AAChartView()<WKNavigationDelegate,UIWebViewDelegate> {
    UIWebView *_uiWebView;
    WKWebView *_wkWebView;
    NSString  *_optionJson;
    NSDictionary *_dictAdditionalOptions;
}

@end

@implementation AAChartView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setUpBasicWebView];
    }
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self setUpBasicWebView];
    }
    return self;
}

- (void)setUpBasicWebView {
    
    if (AASYSTEM_VERSION >= 9.0) {
        _wkWebView = [[WKWebView alloc] init];
        _wkWebView.navigationDelegate = self;
        _wkWebView.backgroundColor = [UIColor whiteColor];
        [self addSubview:_wkWebView];
        _wkWebView.translatesAutoresizingMaskIntoConstraints = NO;
        [self addConstraints:[self configureTheConstraintArrayWithItem:_wkWebView toItem:self]];
    } else {
        _uiWebView = [[UIWebView alloc] init];
        _uiWebView.delegate = self;
        _uiWebView.backgroundColor = [UIColor whiteColor];
        [self addSubview:_uiWebView];
        _uiWebView.translatesAutoresizingMaskIntoConstraints = NO;
        [self addConstraints:[self configureTheConstraintArrayWithItem:_uiWebView toItem:self]];
    }
}

- (NSArray *)configureTheConstraintArrayWithItem:(UIView *)childView toItem:(UIView *)fatherView{
    return  @[[NSLayoutConstraint constraintWithItem:childView
                                           attribute:NSLayoutAttributeLeft
                                           relatedBy:NSLayoutRelationEqual
                                              toItem:fatherView
                                           attribute:NSLayoutAttributeLeft
                                          multiplier:1.0
                                            constant:0],
              [NSLayoutConstraint constraintWithItem:childView
                                           attribute:NSLayoutAttributeRight
                                           relatedBy:NSLayoutRelationEqual
                                              toItem:fatherView
                                           attribute:NSLayoutAttributeRight
                                          multiplier:1.0
                                            constant:0],
              [NSLayoutConstraint constraintWithItem:childView
                                           attribute:NSLayoutAttributeTop
                                           relatedBy:NSLayoutRelationEqual
                                              toItem:fatherView
                                           attribute:NSLayoutAttributeTop
                                          multiplier:1.0
                                            constant:0],
              [NSLayoutConstraint constraintWithItem:childView
                                           attribute:NSLayoutAttributeBottom
                                           relatedBy:NSLayoutRelationEqual
                                              toItem:fatherView
                                           attribute:NSLayoutAttributeBottom
                                          multiplier:1.0
                                            constant:0],
              
              ];
}

- (NSURLRequest *)getJavaScriptFileURLRequest {
    NSString *webPath = [[NSBundle mainBundle] pathForResource:@"AAChartView" ofType:@"html"];
    NSURL *webURL = [NSURL fileURLWithPath:webPath];
    NSURLRequest *URLRequest = [[NSURLRequest alloc] initWithURL:webURL];
    return URLRequest;
}

- (void)configureTheOptionsJsonStringWithAAOptions:(AAOptions *)options {
    if (self.isClearBackgroundColor == YES) {
        options.chart.backgroundColor = @"rgba(0,0,0,0)";
    }
    _optionJson = [AAJsonConverter getPureOptionsString:options];
 
     //Check if _dictAdditionalOptions (which is equal to chartModel.additionalOptions) is not nil
    if (_dictAdditionalOptions) {
        
        //Convert _optionJson to NSDictionary
        NSData *data = [_optionJson dataUsingEncoding:NSUTF8StringEncoding];
        NSError *error;
        NSMutableDictionary *jsonDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
        
        //Create a temporay copy of the dictionary which we can modify while enumerating the original
        NSMutableDictionary *jsonDictTemp = [jsonDict mutableCopy];
        
        //Enumerate the dictionary
        for (id key in _dictAdditionalOptions) {
            if (![[jsonDict allKeys] containsObject:key])  {// If key does not already exist in options dictionary, copy it from the _dictAdditionalOptions dictionary
                [jsonDictTemp setObject:[_dictAdditionalOptions objectForKey:key] forKey:key];
            } else {// If key does already exist in options dictionary, delete it and set the new one from the _dictAdditionalOptions dictionary
                [jsonDictTemp removeObjectForKey:key];
                [jsonDictTemp setObject:[_dictAdditionalOptions objectForKey:key] forKey:key];
            }
        }
        
        //Now, convert the dictionary back to a JSON string
        NSError * err;
        NSData * jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictTemp options:0 error:&err];
        NSString * myString = [[[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding] substringFromIndex:1];
        
        //Done: add a leading '{' to the new _optionJson
        _optionJson=[NSString stringWithFormat:@"%@%@",@"{", myString];
        
    }
}

- (NSString *)configTheJavaScriptString {
    CGFloat chartViewContentWidth = self.contentWidth;
    CGFloat contentHeight = self.frame.size.height;
    if (kDevice_Is_iPhoneX == YES) {
        contentHeight = contentHeight - 20;
    }
    CGFloat chartViewContentHeight = self.contentHeight == 0 ? contentHeight : self.contentHeight;
    NSString *javaScriptStr = [NSString stringWithFormat:@"loadTheHighChartView('%@','%@','%@')",_optionJson,[NSNumber numberWithFloat:chartViewContentWidth],[NSNumber numberWithFloat:chartViewContentHeight-1]];
    return javaScriptStr;
}

//***********************CONFIGURE THE CHART VIEW CONTENT WITH `AACHARTMODEL`***********************//
//
- (void)aa_drawChartWithChartModel:(AAChartModel *)chartModel {
    AAOptions *options = [AAOptionsConstructor configureChartOptionsWithAAChartModel:chartModel];
    _dictAdditionalOptions = chartModel.additionalOptions;
    [self aa_drawChartWithOptions:options];
}

- (void)aa_refreshChartWithChartModel:(AAChartModel *)chartModel {
    AAOptions *options = [AAOptionsConstructor configureChartOptionsWithAAChartModel:chartModel];
    [self aa_refreshChartWithOptions:options];
}

- (void)aa_onlyRefreshTheChartDataWithChartModelSeries:(NSArray<NSDictionary *> *)series {
    [self aa_onlyRefreshTheChartDataWithOptionsSeries:series];
}
//
//***********************CONFIGURE THE CHART VIEW CONTENT WITH `AACHARTMODEL`***********************//



//=======================CONFIGURE THE CHART VIEW CONTENT WITH `AAOPTIONS`=======================//
//
- (void)aa_drawChartWithOptions:(AAOptions *)options {
    if (!_optionJson) {
        [self configureTheOptionsJsonStringWithAAOptions:options];
        NSURLRequest *URLRequest = [self getJavaScriptFileURLRequest];
        if (AASYSTEM_VERSION >= 9.0) {
            [_wkWebView loadRequest:URLRequest];
        } else {
            [_uiWebView loadRequest:URLRequest];
        }
    } else {
        [self aa_refreshChartWithOptions:options];
    }
}

- (void)aa_refreshChartWithOptions:(AAOptions *)options {
    [self configureTheOptionsJsonStringWithAAOptions:options];
    [self drawChart];
}

- (void)aa_onlyRefreshTheChartDataWithOptionsSeries:(NSArray<NSDictionary *> *)series {
    NSString *seriesJsonStr = [AAJsonConverter getPureSeriesString:series];
    NSString *javaScriptStr = [NSString stringWithFormat:@"onlyRefreshTheChartDataWithSeries('%@')",seriesJsonStr];
    [self evaluateJavaScriptWithFunctionNameString:javaScriptStr];
}
//
//=======================CONFIGURE THE CHART VIEW CONTENT WITH `AAOPTIONS`=======================//

- (void)drawChart {
    NSString *javaScriptStr = [self configTheJavaScriptString];
    [self evaluateJavaScriptWithFunctionNameString:javaScriptStr];
}

///WKWebView did finish load
- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    [self drawChart];
    [self.delegate AAChartViewDidFinishLoad];
}

//UIWebView did finish load
- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [self drawChart];
    [self.delegate AAChartViewDidFinishLoad];
}

- (void)aa_showTheSeriesElementContentWithSeriesElementIndex:(NSInteger)elementIndex {
    NSString *javaScriptStr = [NSString stringWithFormat:@"showTheSeriesElementContentWithIndex(%ld)",(long)elementIndex];
    [self evaluateJavaScriptWithFunctionNameString:javaScriptStr];
}

- (void)aa_hideTheSeriesElementContentWithSeriesElementIndex:(NSInteger)elementIndex {
    NSString *javaScriptStr = [NSString stringWithFormat:@"hideTheSeriesElementContentWithIndex(%ld)",(long)elementIndex];
    [self evaluateJavaScriptWithFunctionNameString:javaScriptStr];
}

- (void)evaluateJavaScriptWithFunctionNameString:(NSString *)functionNameStr {
    if (AASYSTEM_VERSION >= 9.0) {
        [_wkWebView  evaluateJavaScript:functionNameStr completionHandler:^(id item, NSError * _Nullable error) {
            if (error) {
                AADetailLog(@"☠️☠️💀☠️☠️WARNING!!!!! THERE ARE SOME ERROR INFOMATION_______%@",error);
            }
        }];
    } else {
        [_uiWebView  stringByEvaluatingJavaScriptFromString:functionNameStr];
    }
}

#pragma mark -- setter method

- (void)setScrollEnabled:(BOOL)scrollEnabled {
    _scrollEnabled = scrollEnabled;
    if (AASYSTEM_VERSION >= 9.0) {
        _wkWebView.scrollView.scrollEnabled = _scrollEnabled;
    } else {
        _uiWebView.scrollView.scrollEnabled = _scrollEnabled;
    }
}

- (void)setContentWidth:(CGFloat)contentWidth {
    _contentWidth = contentWidth;
    NSString *javaScriptStr = [NSString stringWithFormat:@"setTheChartViewContentWidth(%f)",_contentWidth];
    [self evaluateJavaScriptWithSetterMethodNameString:javaScriptStr];
}

- (void)setContentHeight:(CGFloat)contentHeight {
    _contentHeight = contentHeight;
    NSString *javaScriptStr = [NSString stringWithFormat:@"setTheChartViewContentHeight(%f)",_contentHeight];
    [self evaluateJavaScriptWithSetterMethodNameString:javaScriptStr];
}

- (void)setChartSeriesHidden:(BOOL)chartSeriesHidden {
    _chartSeriesHidden = chartSeriesHidden;
    NSString *jsStr = [NSString stringWithFormat:@"setChartSeriesHidden(%d)",_chartSeriesHidden];
    [self evaluateJavaScriptWithSetterMethodNameString:jsStr];
}

- (void)evaluateJavaScriptWithSetterMethodNameString:(NSString *)JSFunctionStr {
    if (_optionJson) {
          [self evaluateJavaScriptWithFunctionNameString:JSFunctionStr];
    }
}

- (void)setIsClearBackgroundColor:(BOOL)isClearBackgroundColor {
    _isClearBackgroundColor = isClearBackgroundColor;
    if (_isClearBackgroundColor == YES) {
        self.backgroundColor = [UIColor clearColor];
        if (AASYSTEM_VERSION >= 9.0) {
            [_wkWebView setBackgroundColor:[UIColor clearColor]];
            [_wkWebView setOpaque:NO];
        } else {
            [_uiWebView setBackgroundColor:[UIColor clearColor]];
            [_uiWebView setOpaque:NO];
        }
    }
}

- (void)setBlurEffectEnabled:(BOOL)blurEffectEnabled {
    _blurEffectEnabled = blurEffectEnabled;
    if (_blurEffectEnabled) {
        UIBlurEffect *effect = [UIBlurEffect effectWithStyle:UIBlurEffectStyleLight];
        UIVisualEffectView *effectView = [[UIVisualEffectView alloc] initWithEffect:effect];
        [self addSubview:effectView];
        [self sendSubviewToBack:effectView];
        
        effectView.translatesAutoresizingMaskIntoConstraints = NO;
        [self addConstraints:[self configureTheConstraintArrayWithItem:effectView toItem:self]];
    }
}

@end



#import <objc/runtime.h>

@implementation AAJsonConverter

+ (NSDictionary*)getObjectData:(id)obj {
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    unsigned int propsCount;
    Class class = [obj class];
    do {
        objc_property_t *props = class_copyPropertyList(class, &propsCount);
        for (int i = 0;i < propsCount; i++) {
            objc_property_t prop = props[i];
            
            NSString *propName = [NSString stringWithUTF8String:property_getName(prop)];
            id value = [obj valueForKey:propName];
            if (value == nil) {
                value = [NSNull null];
                continue;
            } else {
                value = [self getObjectInternal:value];
            }
            [dic setObject:value forKey:propName];
        }
        free(props);
        class = [class superclass];
    } while (class != [NSObject class]);
    
    return dic;
}

+ (NSData*)getJSON:(id)obj options:(NSJSONWritingOptions)options error:(NSError**)error {
    return [NSJSONSerialization dataWithJSONObject:[self getObjectData:obj] options:options error:error];
}

+ (id)getObjectInternal:(id)obj {
    if (   [obj isKindOfClass:[NSString class]]
        || [obj isKindOfClass:[NSNumber class]]
        || [obj isKindOfClass:[NSNull   class]] ) {
        return obj;
    }
    
    if ([obj isKindOfClass:[NSArray class]]) {
        NSArray *objarr = obj;
        NSMutableArray *arr = [NSMutableArray arrayWithCapacity:objarr.count];
        for (int i = 0;i < objarr.count; i++) {
            [arr setObject:[self getObjectInternal:[objarr objectAtIndex:i]] atIndexedSubscript:i];
        }
        return arr;
    }
    
    if ([obj isKindOfClass:[NSDictionary class]]) {
        NSDictionary *objdic = obj;
        NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:[objdic count]];
        for (NSString *key in objdic.allKeys) {
            [dic setObject:[self getObjectInternal:[objdic objectForKey:key]] forKey:key];
        }
        return dic;
    }
    return [self getObjectData:obj];
}

+ (NSString*)convertDictionaryIntoJson:(NSDictionary *)dictionary {
    NSError *parseError = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:NSJSONWritingPrettyPrinted error:&parseError];
    NSString *string =[[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    return string;
}

+ (NSString*)wipeOffTheLineBreakAndBlankCharacter:(NSString *)originalString {
    NSString *str =[originalString stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    str = [str stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    return str;
}

+ (NSString *)getPureOptionsString:(id)optionsObject {
    NSDictionary *dic = [self getObjectData:optionsObject];
    NSString *str = [self convertDictionaryIntoJson:dic];
    return [self wipeOffTheLineBreakAndBlankCharacter:str];
}

+ (NSString *)getPureSeriesString:(NSArray<NSDictionary*> *)series {
    NSError *parseError = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:series options:NSJSONWritingPrettyPrinted error:&parseError];
    NSString *seriesStr = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    return [self wipeOffTheLineBreakAndBlankCharacter:seriesStr];
}

@end

