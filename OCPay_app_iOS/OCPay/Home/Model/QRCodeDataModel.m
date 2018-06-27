//
//  QRCodeDataModel.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/13.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "QRCodeDataModel.h"

@implementation QRCodeDataModel


- (instancetype)init{
    if (self = [super init]) {
        self.transaction = [[TransactionDataModel alloc]init];
    }
    return self;
}


- (QRCodeType)type{
    if ([self.mode isEqualToString:@"mode_tx_receive"]) {
        _type = QRCodeType_Receive;
    }else if ([self.mode isEqualToString:@"mode_data_sign"]) {
        _type = QRCodeType_Observer;
    }else if ([self.mode isEqualToString:@"mode_show_eth_tx_sign"]){
        _type = QRCodeType_Transaction;
    }else{
        _type = QRCodeType_Unknown;
    }
    return _type;
}


@end


@implementation TransactionDataModel


@end
