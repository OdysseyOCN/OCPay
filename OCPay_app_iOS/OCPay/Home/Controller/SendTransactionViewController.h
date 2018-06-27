//
//  SendTransactionViewController.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/1.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "BasicViewController.h"
#import "QRCodeDataModel.h"

@interface SendTransactionViewController : BasicViewController
@property (nonatomic, strong) WalletModel *wallet;
@property (nonatomic, strong) TokenModel *tokenData;
@property (nonatomic, strong) QRCodeDataModel *QRCodedata;
@end
