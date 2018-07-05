//
//  ContactsViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/21.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "ContactsListViewController.h"
#import "ContactsTableViewCell.h"

@interface ContactsListViewController ()<UITableViewDataSource,UITableViewDelegate>
@property (weak, nonatomic) IBOutlet UITableView *myTableView;
@property (nonatomic, strong) NSMutableArray *sourceData;
@end

@implementation ContactsListViewController

- (NSMutableArray *)sourceData{
    if (!_sourceData) {
        _sourceData = [NSMutableArray array];
    }
    [_sourceData removeAllObjects];
    for (ContactsModel *contact in WalletManager.share.contacts) {
        [_sourceData addObject:contact];
    }
    for (WalletModel *wallet in WalletManager.share.wallets) {
        if (![wallet.address isEqualToString:WalletManager.share.defaultWallet.address]) {
            ContactsModel *contact = [[ContactsModel alloc]init];
            contact.walletAddress = wallet.address;
            contact.firstName = wallet.name;
            [_sourceData addObject:contact];
        }
    }
    return _sourceData;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.myTableView.rowHeight = UITableViewAutomaticDimension;
    self.myTableView.estimatedRowHeight = 60;
    self.myTableView.tableFooterView = [UIView new];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self.myTableView reloadData];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.sourceData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    ContactsTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"myCell"];
    cell.contacts = self.sourceData[indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if (self.selectContactsCallback) {
        self.selectContactsCallback(self.sourceData[indexPath.row]);
        [self.navigationController popViewControllerAnimated:YES];
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
