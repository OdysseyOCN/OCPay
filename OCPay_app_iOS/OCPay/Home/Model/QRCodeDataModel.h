//
//  QRCodeDataModel.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/13.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, QRCodeType) {
    QRCodeType_Unknown,
    QRCodeType_Receive,
    QRCodeType_Observer,
    QRCodeType_Transaction
};


@interface TransactionDataModel : NSObject
@property (nonatomic, copy) NSString *amount;
@property (nonatomic, copy) NSString *contractAddress;
@property (nonatomic, copy) NSString *data;
@property (nonatomic, copy) NSString *gasLimit;
@property (nonatomic, copy) NSString *gasPrice;
@property (nonatomic, copy) NSString *nonce;
@property (nonatomic, copy) NSString *tokenName;
@property (nonatomic, copy) NSString *transactionFrom;
@property (nonatomic, copy) NSString *transactionTo;
@property (nonatomic, copy) NSString *txAction;
@end

@interface QRCodeDataModel : NSObject

@property (nonatomic) QRCodeType type;
@property (nonatomic, copy) NSString *ethereum;
@property (nonatomic, copy) NSString *mode;
@property (nonatomic, copy) NSString *data;
@property (nonatomic, strong) TransactionDataModel *transaction;

@end
