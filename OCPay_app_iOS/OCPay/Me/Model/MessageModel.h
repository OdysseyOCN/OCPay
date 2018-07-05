//
//  MessageModel.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/29.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, MessageType) {
    MessageType_Notice  = 1,
    MessageType_Waring,
    MessageType_Received,
    MessageType_SendSuccess,
    MessageType_SendFail
};

@interface MessageModel : NSObject
@property (nonatomic) MessageType messageType;
@property (nonatomic, copy) NSString *notification_title;
@property (nonatomic, copy) NSString *notification_content;
@property (nonatomic, copy) NSString *notification_describle;
//@property (nonatomic, copy) NSString *notification_type;
@property (nonatomic, copy) NSString *notification_timestamp;
@property (nonatomic, strong) NSDictionary *transactionInfo;
@property (nonatomic) BOOL read;

- (instancetype)initWithTransaction:(TransactionInfo*)info wallet:(WalletModel*)wallet;
- (void)sendLocalNotification;

@end
