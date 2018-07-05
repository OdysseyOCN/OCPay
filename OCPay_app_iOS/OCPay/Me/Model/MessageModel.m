//
//  MessageModel.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/29.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "MessageModel.h"
#import "NSString+TCMExtension.h"
@implementation MessageModel

CoderCopyHash

+ (NSDictionary *)modelCustomPropertyMapper {
    return @{@"messageType"  : @"notification_type"};
}

- (instancetype)initWithTransaction:(TransactionInfo*)info wallet:(WalletModel*)wallet{
    if (self = [super init]) {
        BOOL isSend = NO, isSuccess = NO, isContractTransaction = NO;
        NSString *value = nil;
        NSString *str = [SecureData dataToHexString:info.data];
        if (str.length > 64) {
            str = [str substringFromIndex:str.length - 64];
            BigNumber *bg = [BigNumber bigNumberWithHexString:[NSString stringWithFormat:@"0x%@",str]];
            value = bg.decimalString;
            isContractTransaction = YES;
        }
        if ([[Address addressWithString:info.fromAddress.checksumAddress] isEqualToAddress:[Address addressWithString:wallet.address]]) {
            isSend = YES;
        }
        if (info.txreceiptStatus == 1) {
            isSuccess = YES;
        }
        self.notification_title = [NSString stringWithFormat:@"Notify:%@ %@ %@ successfully",
                                   isContractTransaction ? [value decimalNumberByDividing:DecimalNumberTenPower18] : [info.value.decimalString decimalNumberByDividing:DecimalNumberTenPower18],
                                   isContractTransaction ? @"OCN" : @"ETH",
                                   isSend ? @"sent" : @"received"
                                   ];
        self.notification_describle = [NSString stringWithFormat:@"%@:%@",
                                      isSend ? @"Sender" : @"Receiver",
                                      wallet.address
                                      ];
        self.messageType = isSend ? (isSuccess ? MessageType_SendSuccess : MessageType_SendFail) : MessageType_Received;
        self.notification_timestamp = [NSString stringWithFormat:@"%f",info.timestamp];
        self.transactionInfo = [info dictionaryRepresentation];
    }
    return self;
}

- (void)sendLocalNotification{
    UILocalNotification *localNotification = [[UILocalNotification alloc] init];
    localNotification.alertTitle = self.notification_title;
    localNotification.alertBody = self.notification_describle;
    localNotification.soundName = UILocalNotificationDefaultSoundName;
    localNotification.userInfo = @{LocalNotificationKey:self.transactionInfo};
    localNotification.applicationIconBadgeNumber = WalletManager.share.unreadMessageCount+1;
    [[UIApplication sharedApplication] presentLocalNotificationNow:localNotification];
}
@end
