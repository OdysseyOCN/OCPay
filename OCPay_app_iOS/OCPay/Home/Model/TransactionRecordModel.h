//
//  TransactionRecordModel.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/8.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface TransactionRecordDateModel : NSObject
@property (nonatomic, copy) NSString *date;
@property (nonatomic, strong) NSMutableArray *transactions;
@property (nonatomic, copy) NSString *amount;
@end



@interface TransactionRecordModel : NSObject

+ (void)getRecordDataWithAddress:(WalletModel*)wallet success:(Success)success;

+ (void)getIncomeDataWithAddress:(WalletModel*)wallet tokenType:(TokenType)tokenType success:(Success)success;

@end
