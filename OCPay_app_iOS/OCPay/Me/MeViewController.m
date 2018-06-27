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

@interface MeViewController ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic, strong) NSArray *icons;
@property (nonatomic, strong) NSArray *titles;
@property (weak, nonatomic) IBOutlet UITableView *myTableView;
@end

@implementation MeViewController

//- (void)viewWillAppear:(BOOL)animated{
//    [super viewWillAppear:animated];
//    [self.navigationController setNavigationBarHidden:YES animated:YES];
//}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Me";
    self.icons = @[@"消息中心",@"联系人",@"系统设置",@"个人中心",@"帮助中心",@"关于我们"];
    self.titles = @[@"Message Center",@"Contacts",@"System Settings",@"Personal Center",@"Help Center",@"About Us"];
}

- (IBAction)manageWalletAction:(UIControl *)sender {
    [self pushViewControllerWithIdentifier:@"ManageWalletViewController" inStoryboard:@"Wallet"];
}

- (IBAction)importWalletAction:(UIControl *)sender {
//    CreateWalletViewController *vc = [self pushViewControllerWithIdentifier:@"CreateWalletViewController" inStoryboard:@"Wallet"];
    [self pushViewControllerWithIdentifier:@"ImportWalletViewController" inStoryboard:@"Wallet"];
}

- (IBAction)shareAction:(id)sender {
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 3;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"myCell" forIndexPath:indexPath];
    cell.imageView.image = [UIImage imageNamed:self.icons[indexPath.section ? 3 + indexPath.row : indexPath.row]];
    cell.textLabel.text = self.titles[indexPath.section ? 3 + indexPath.row : indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
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
