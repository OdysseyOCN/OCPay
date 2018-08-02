//
//  TransactionRecordViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/1.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "TransactionRecordViewController.h"
#import "TransactionDetailViewController.h"
#import "TransactionRecordTableCell.h"
#import "TransactionRecordModel.h"



@interface TransactionRecordViewController ()<UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate,UISearchControllerDelegate>
@property (weak, nonatomic) IBOutlet UITableView *myTableView;
//@property (weak, nonatomic) IBOutlet UISearchBar *mySearchBar;
@property (strong, nonatomic) NSMutableArray <TransactionRecordDateModel*>*showDatas;

@end

@implementation TransactionRecordViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.myTableView.rowHeight = UITableViewAutomaticDimension;
    self.myTableView.estimatedRowHeight = 56;
    [self dispalyLoading:nil];
    [self loadData];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self loadData];
}

- (void)loadData{
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
    [TransactionRecordModel getRecordDataWithAddress:self.wallet success:^(__kindof NSObject *data) {
        [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
        [self hideLoading:NO];
        self.showDatas = data;
        [self.myTableView reloadData];
    }];
}

- (NSMutableArray< TransactionRecordDateModel*> *)showDatas{
    if (!_showDatas) {
        _showDatas = [NSMutableArray array];
    }
    return _showDatas;
}

//- (void)updateSearchResultsForSearchController:(UISearchController *)searchController{
//    searchController.searchResultsController.view.hidden = NO;
//}
//
//- (BOOL)searchBarShouldBeginEditing:(UISearchBar *)searchBar{
//    UISearchController *search = [[UISearchController alloc] initWithSearchResultsController:nil];
//    search.delegate = self;
//    [self presentViewController:search animated:YES completion:^{
//
//    }];
//    return true;
//}

#pragma mark - UITableViewDataSource,UITableViewDelegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return self.showDatas.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.showDatas[section].transactions.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    TransactionRecordTableCell *cell = [tableView dequeueReusableCellWithIdentifier:@"myCell"];
    TransactionInfo *info = self.showDatas[indexPath.section].transactions[indexPath.row];
    cell.wallet = self.wallet;
    cell.info = info;
    return cell;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    TransactionRecordTableCell *cell =  [tableView dequeueReusableCellWithIdentifier:@"header"];
    cell.dateLabel.text = self.showDatas[section].date;
    return cell;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    return [tableView dequeueReusableCellWithIdentifier:@"footer"];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    TransactionInfo *info = self.showDatas[indexPath.section].transactions[indexPath.row];
    TransactionDetailViewController *vc = [self pushViewControllerWithIdentifier:@"TransactionDetailViewController" inStoryboard:@"Main"];
    vc.info = info;
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
