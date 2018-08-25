//
//  QRCodeViewController.h
//  OCPay
//
//  Created by 何自梁 on 2018/5/28.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^ReciveResultCallback)(NSString *result);


@interface QRCodeViewController : UIViewController

@property (nonatomic, copy) ReciveResultCallback reciveResult;

@end
