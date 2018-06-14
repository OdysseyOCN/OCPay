//
//  SendTransactionViewController.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/1.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "BasicViewController.h"

@interface SendTransactionViewController : BasicViewController
@property (nonatomic, strong) WalletModel *wallet;
@property (nonatomic) BOOL isContractsTransaction;
@end
