//
//  TokenIncomeViewController.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/7.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "BasicViewController.h"

@interface TokenIncomeViewController : BasicViewController
@property (nonatomic, strong) WalletModel *wallet;
@property (nonatomic, strong) TokenModel *tokenData;
@end
