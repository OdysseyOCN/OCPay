//
//  ContactsModel.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/23.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ContactsModel : NSObject

@property (nonatomic, copy) NSString *firstName;
@property (nonatomic, copy) NSString *familyName;
@property (nonatomic, copy) NSString *walletAddress;
@property (nonatomic, copy) NSString *phoneNumber;
@property (nonatomic, copy) NSString *email;
@property (nonatomic, copy) NSString *note;

@end
