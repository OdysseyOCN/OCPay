//
//  TransactionRecordTableCell.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/6.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TransactionRecordTableCell : UITableViewCell
@property (nonatomic, strong) TransactionInfo *info;
@property (nonatomic, strong) WalletModel *wallet;
@property (weak, nonatomic) IBOutlet UILabel *dateLabel;

@end
