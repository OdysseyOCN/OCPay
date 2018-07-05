//
//  ContactsViewController.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/21.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "BasicViewController.h"

typedef void(^SelectContactsCallback)(ContactsModel *contacts);

@interface ContactsListViewController : BasicViewController
@property (nonatomic, copy) SelectContactsCallback selectContactsCallback;
@end
