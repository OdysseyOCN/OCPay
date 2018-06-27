//
//  WalletTableView.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/25.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "WalletTableView.h"
#import "WalletManager.h"

@interface WalletTableView ()<UITableViewDelegate,UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UIView *myTopView;
@end

@implementation WalletTableView

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (void)awakeFromNib{
    [super awakeFromNib];
    self.delegate = self;
    self.dataSource = self;
    self.estimatedRowHeight = 56;
    self.rowHeight = 56;
    [self.myTopView setGradientColor:@[UIColorHex(0x405D68),UIColorHex(0x1A3D4E)] gradientType:GradientTypeUpleftToLowright];
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    return 1;
}

- (NSInteger)tableView:(nonnull UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return WalletManager.share.wallets.count;
}

- (nonnull UITableViewCell *)tableView:(nonnull UITableView *)tableView cellForRowAtIndexPath:(nonnull NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"myCell"];
    WalletModel *wallet = WalletManager.share.wallets[indexPath.row];
    cell.textLabel.text = wallet.name;
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    WalletModel *wallet = WalletManager.share.wallets[indexPath.row];
    if (self.selectedCallback) {
        self.selectedCallback(wallet);
    }
}

- (void)show{
    UIView *superView = [UIApplication sharedApplication].keyWindow;
    UIButton *maskView = [[UIButton alloc]initWithFrame:superView.bounds];
    maskView.backgroundColor = [UIColor colorWithWhite:0 alpha:0.f];
    [maskView addTarget:self action:@selector(closeWalletViewNoAnimate:) forControlEvents:UIControlEventTouchUpInside];
    [superView addSubview:maskView];
    [maskView addSubview:self];
    self.frame = CGRectMake(DEVICE_SCREEN_WIDTH, 0, 210, DEVICE_SCREEN_HEIGHT);
    [self reloadData];
    [UIView animateWithDuration:.3 animations:^{
        maskView.backgroundColor = [UIColor colorWithWhite:0 alpha:0.5];
        self.frame = CGRectMake(DEVICE_SCREEN_WIDTH-210, 0, 210, DEVICE_SCREEN_HEIGHT);
    }];
}

- (void)closeWalletViewNoAnimate:(BOOL)noAnimate{
    if (noAnimate) {
        [self.superview removeFromSuperview];
        return;
    }
    [UIView animateWithDuration:.3 animations:^{
        self.superview.backgroundColor = [UIColor colorWithWhite:0 alpha:0.f];
        self.frame = CGRectMake(DEVICE_SCREEN_WIDTH, 0, 210, DEVICE_SCREEN_HEIGHT);
    }completion:^(BOOL finished) {
        [self.superview removeFromSuperview];
    }];
}
@end
