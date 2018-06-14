//
//  BasicViewController.h
//  OCPay
//
//  Created by 何自梁 on 2018/5/19.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BasicViewController : UIViewController <YYTextKeyboardObserver>
@property (nonatomic, strong) YYTextKeyboardManager *manager;
@property (nonatomic, strong) UIToolbar *textFieldAccessoryView;
@property (nonatomic) BOOL isFromFirstCreate;
@property (nonatomic) NSString *QRCodeResultString;
- (void)keyboardFinishAction;
- (void)setScrollViewContentInsetAdjustmentNever:(UIScrollView*)scrollView;
@end
