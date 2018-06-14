//
//  WalletTableView.h
//  OCPay
//
//  Created by 何自梁 on 2018/5/25.
//  Copyright © 2018年 menggen. All rights reserved.
//

typedef void(^WalletTableViewDisSelectCallback)(WalletModel *wallet);

#import <UIKit/UIKit.h>

@interface WalletTableView : UITableView

@property (nonatomic, copy) WalletTableViewDisSelectCallback selectedCallback;


- (void)show;

- (void)closeWalletViewNoAnimate:(BOOL)noAnimate;

@end
