//
//  SignDetailViewController.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/12.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "BasicViewController.h"
#import "QRCodeDataModel.h"


@interface SignDetailViewController : BasicViewController
@property (nonatomic, strong) QRCodeDataModel *data;
@end
