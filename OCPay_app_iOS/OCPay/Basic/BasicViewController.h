//
//  BasicViewController.h
//  OCPay
//
//  Created by 何自梁 on 2018/5/19.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BasicViewController : UIViewController 
@property (nonatomic, strong) YYTextKeyboardManager *keyboardmanager;
@property (nonatomic) BOOL isFromFirstCreate;
@property (nonatomic) NSString *QRCodeResultString;
@property (nonatomic, strong) UIScrollView *neverAdjustContentInserScrollView;
@end
