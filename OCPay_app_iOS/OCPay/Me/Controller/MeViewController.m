//
//  MeViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/24.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "MeViewController.h"
#import "CreateWalletViewController.h"
#import "ImportWalletViewController.h"
#import "SystemSettingsViewController.h"

@interface MeTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *badgeLabel;
@end
@implementation MeTableViewCell
@end

@interface MeViewController ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic, strong) NSArray *icons;
@property (nonatomic, strong) NSArray *titles;
@property (weak, nonatomic) IBOutlet UITableView *myTableView;
@end

@implementation MeViewController


- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Me";
    self.icons = @[@"消息中心",@"联系人",@"系统设置",@"cantact us",@"关于我们"];
    self.titles = @[@"Message Center",@"Contacts",@"System Settings",@"Contact Us",@"About Us"];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self.myTableView reloadData];
}

- (IBAction)manageWalletAction:(UIControl *)sender {
    [self pushViewControllerWithIdentifier:@"ManageWalletViewController" inStoryboard:@"Wallet"];
}

- (IBAction)importWalletAction:(UIControl *)sender {
    [self pushViewControllerWithIdentifier:@"ImportWalletViewController" inStoryboard:@"Wallet"];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (section) {
        return 2;
    }
    return 3;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    MeTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"myCell" forIndexPath:indexPath];
    cell.imageView.image = [UIImage imageNamed:self.icons[indexPath.section ? 3 + indexPath.row : indexPath.row]];
    cell.textLabel.text = self.titles[indexPath.section ? 3 + indexPath.row : indexPath.row];
    cell.badgeLabel.hidden = !(indexPath.row == 0 && indexPath.section == 0 && WalletManager.share.unreadMessageCount > 0);
    cell.badgeLabel.text = [NSString stringWithFormat:@"%ld",WalletManager.share.unreadMessageCount];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return 10;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    return 0.01;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if (indexPath.section == 0 && indexPath.row == 0) {
        [self pushViewControllerWithIdentifier:@"MessageCenterViewController" inStoryboard:@"Main"];
    }else if (indexPath.section == 0 && indexPath.row == 1) {
        [self pushViewControllerWithIdentifier:@"ContactsListViewController" inStoryboard:@"Main"];
    }else if (indexPath.section == 0 && indexPath.row == 2) {
        [self pushViewControllerWithIdentifier:@"SystemSettingsViewController" inStoryboard:@"Main"];
    }else if (indexPath.section == 1 && indexPath.row == 0){
        [self pushViewControllerWithIdentifier:@"ContactUsViewController" inStoryboard:@"Main"];
    }else if (indexPath.section == 1 && indexPath.row == 1){
        [self pushViewControllerWithIdentifier:@"AboutUsViewController" inStoryboard:@"Main"];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
