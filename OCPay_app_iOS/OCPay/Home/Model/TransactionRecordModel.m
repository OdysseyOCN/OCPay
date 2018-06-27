//
//  TransactionRecordModel.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/8.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "TransactionRecordModel.h"



@implementation TransactionRecordDateModel
- (NSMutableArray *)transactions{
    if (!_transactions) {
        _transactions = [NSMutableArray array];
    }
    return _transactions;
}
@end


@implementation TransactionRecordModel

+ (void)getRecordDataWithAddress:(WalletModel*)wallet success:(Success)success{
    
    
    __block NSArray *sourceDatas = nil;
    __block NSMutableArray  *showDatas = [NSMutableArray array];

    ArrayPromise *arr = [wallet.api getTransactions:[Address addressWithString:wallet.address] startBlockTag:BLOCK_TAG_LATEST];
    [arr onCompletion:^(ArrayPromise *promise) {
        sourceDatas = promise.value;
        sourceDatas = [[sourceDatas reverseObjectEnumerator] allObjects];
        
        NSMutableArray *muArr = [NSMutableArray array];
        if (sourceDatas) {
            [muArr addObjectsFromArray:sourceDatas];
        }
        NSMutableArray *deleteArr = [NSMutableArray array];
        NSLog(@"缓存记录数量：%ld",wallet.transactionCache.count);
        [WalletManager.share.defaultWallet.transactionCache enumerateObjectsUsingBlock:^(TransactionInfo * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            BOOL haveSame = NO;
            for (TransactionInfo *info in sourceDatas) {
                if ([info.transactionHash isEqualToHash:obj.transactionHash]) {
                    [deleteArr addObject:obj];
                    haveSame = YES;
                    break;
                }
            }
            if (!haveSame) {
                [muArr insertObject:obj atIndex:0];
            }
        }];
        for (TransactionInfo *info in deleteArr) {
            [wallet.transactionCache removeObject:info];
        }
        [WalletManager synchronize];
        sourceDatas = muArr;
        
        
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
        [dateFormatter setLocale:[NSLocale systemLocale]];
        [dateFormatter setDateFormat:@"YYYY-MM"];
        
        NSMutableArray *dateArr = [NSMutableArray array];
        
        [sourceDatas enumerateObjectsUsingBlock:^(TransactionInfo * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSDate *date = [NSDate dateWithTimeIntervalSince1970:obj.timestamp];
            NSString *dateStr = [dateFormatter stringFromDate:date];
            [dateArr addObject:dateStr];
        }];
        
        NSSet *set = [NSSet setWithArray:dateArr];
        NSArray *newDateArr = [set allObjects];
        
        NSMutableArray *newArr = [NSMutableArray arrayWithArray:newDateArr];
        [newArr sortUsingComparator:^NSComparisonResult(NSString *obj1,NSString *obj2) {
            int a = [obj1 stringByReplacingOccurrencesOfString:@"-" withString:@""].intValue;
            int b = [obj2 stringByReplacingOccurrencesOfString:@"-" withString:@""].intValue;
            return [[NSNumber numberWithInt:b] compare:[NSNumber numberWithInt:a]];
        }];
        
        [newArr enumerateObjectsUsingBlock:^(NSString *date, NSUInteger idx, BOOL * _Nonnull stop) {
            TransactionRecordDateModel *dateModel = [TransactionRecordDateModel new];
            dateModel.date = date;
            [showDatas addObject:dateModel];
        }];
        
        [sourceDatas enumerateObjectsUsingBlock:^(TransactionInfo * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSDate *date = [NSDate dateWithTimeIntervalSince1970:obj.timestamp];
            NSString *dateStr = [dateFormatter stringFromDate:date];
            
            [showDatas enumerateObjectsUsingBlock:^(TransactionRecordDateModel *obj1, NSUInteger idx1, BOOL * _Nonnull stop1) {
                if ([obj1.date isEqualToString:dateStr]) {
                    [obj1.transactions addObject:obj];
                    *stop1 = true;
                }
            }];
        }];
        if (success) {
            success(showDatas);
        }
    }];

}


+ (void)getIncomeDataWithAddress:(WalletModel*)wallet tokenType:(TokenType)tokenType success:(Success)success{
    
    ArrayPromise *arrPromise = [[[EtherscanProvider alloc]initWithChainId:ChainType] getTransactions:[Address addressWithString:wallet.address] startBlockTag:BLOCK_TAG_LATEST];
    [arrPromise onCompletion:^(ArrayPromise *promise) {
        
        //逆序反转
        NSArray <TransactionInfo*>*sourceDatas = promise.value;
        sourceDatas = [[sourceDatas reverseObjectEnumerator] allObjects];
        
        NSMutableArray *muArr = [NSMutableArray array];
        if (sourceDatas) {
            [muArr addObjectsFromArray:sourceDatas];
        }
        NSMutableArray *deleteArr = [NSMutableArray array];
        NSLog(@"缓存记录数量：%ld",wallet.transactionCache.count);
        [WalletManager.share.defaultWallet.transactionCache enumerateObjectsUsingBlock:^(TransactionInfo * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            BOOL haveSame = NO;
            for (TransactionInfo *info in sourceDatas) {
                if ([info.transactionHash isEqualToHash:obj.transactionHash]) {
                    [deleteArr addObject:obj];
                    haveSame = YES;
                    break;
                }
            }
            if (!haveSame) {
                [muArr insertObject:obj atIndex:0];
            }
        }];
        for (TransactionInfo *info in deleteArr) {
            [wallet.transactionCache removeObject:info];
        }
        [WalletManager synchronize];
        sourceDatas = muArr;

        
        NSMutableArray *newArr = [NSMutableArray array];
        for (TransactionInfo *data in sourceDatas) {
            NSString *str = [SecureData dataToHexString:data.data];
            if (tokenType == TokenType_OCN) {
                if (str.length > 64) {
                    [newArr addObject:data];
                }
            }else{
                if (str.length < 64) {
                    [newArr addObject:data];
                }
            }
        }
        sourceDatas = newArr;

        
        //日期格式化
        NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
        [dateFormatter setLocale:[NSLocale systemLocale]];
        [dateFormatter setDateFormat:@"MM/dd"];
        
        NSMutableArray *recentydates = [NSMutableArray array];
        NSDate *lastDate =  [NSDate dateWithTimeIntervalSince1970:sourceDatas.firstObject.timestamp];
        for (int i = 0; i < 7; i++) {
            
            NSDate *date1 = [NSDate dateWithTimeInterval:-i*24*60*60 sinceDate:[NSDate date]];
            NSString *dateStr1 = [dateFormatter stringFromDate:date1];
            NSString *finalAmount = wallet.tokens.firstObject.tokenAmount;

            for (TransactionInfo * _Nonnull obj in sourceDatas) {
                
                NSDate *date2 = [NSDate dateWithTimeIntervalSince1970:obj.timestamp];
                NSString *dateStr2 = [dateFormatter stringFromDate:date2];

                //交易金额
                BOOL isContractTransaction = NO;
                BOOL isSend = [obj.fromAddress.checksumAddress isEqualToString:wallet.address];
                NSString *value = nil;
                NSString *str = [SecureData dataToHexString:obj.data];
                if (str.length > 64) {
                    str = [str substringFromIndex:str.length - 64];
                    BigNumber *bg = [BigNumber bigNumberWithHexString:[NSString stringWithFormat:@"0x%@",str]];
                    value = bg.decimalString;
                    isContractTransaction = YES;
                }
                NSString *amount = [NSString stringWithFormat:@"%@",isContractTransaction ? [value decimalNumberByDividing:DecimalNumberTenPower18] : [obj.value.decimalString decimalNumberByDividing:DecimalNumberTenPower18]];

                if ([lastDate earlierDate:date1] == lastDate) {
                    TransactionRecordDateModel *data = [TransactionRecordDateModel new];
                    data.date = dateStr1;
                    data.amount = finalAmount;
                    [recentydates addObject:data];
                    break;
                }
                
                if (isSend) {
                    finalAmount = [finalAmount decimalNumberByAdding:amount];
                }else{
                    finalAmount = [finalAmount decimalNumberBySubtracting:amount];
                }

                if ([dateStr1 isEqualToString:dateStr2] || ([date1 earlierDate:date2] == date2)) {
                    TransactionRecordDateModel *data = [TransactionRecordDateModel new];
                    data.date = dateStr1;
                    data.amount = finalAmount;
                    [recentydates addObject:data];
                    break;
                }
            }
        }
        if (success) {
            success(@[sourceDatas,recentydates]);
        }
    }];
    
}

@end
