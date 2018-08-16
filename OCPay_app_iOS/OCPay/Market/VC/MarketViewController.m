//
//  MarketViewController.m
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/4.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import "MarketViewController.h"
#import "IndicatorView.h"
#import "MarketService.h"
#import "MarketTableHeaderFooterView.h"
#import "MarketTableViewCell.h"
#import "MarketSortingView.h"
#import "MarketSearchViewController.h"
#import "UIScrollView+Refresh.h"

@interface MarketViewController ()<UITableViewDataSource,UITableViewDelegate,IndicatorViewProtocol,UISearchControllerDelegate>
@property (weak, nonatomic) IBOutlet IndicatorView *indicatorView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet UITableView *favoriteTableView;
@property (weak, nonatomic) IBOutlet UITableView *tokenTableView;
@property (weak, nonatomic) IBOutlet UITableView *exchangeTableView;
@property (weak, nonatomic) IBOutlet MarketSortingView *sortingView;
@property (strong, nonatomic) NSMutableArray <NSNumber*>*sortArray;
@property (strong, nonatomic) NSArray <MarketModel*>*favorietList;
@property (strong, nonatomic) NSArray <MarketModel*>*tokenList;
@property (strong, nonatomic) NSArray <ExchangeModel*>*exchangeList;
@end

@implementation MarketViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
    if (!self.isSearch) {
        [self.favoriteTableView headerBeginRefreshing];
        [self.tokenTableView headerBeginRefreshing];
        [self.exchangeTableView headerBeginRefreshing];
    }
    @weakify(self)
    [[NSNotificationCenter defaultCenter] addObserverForName:UserStateChangeNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification * _Nonnull note) {
        @strongify(self)
        [self loadFavoriteList];
    }];
}

#pragma mark - 初始化界面
- (void)initUI{
    [self initIndicatorView];
    [self initSortingView];
    [self initTableView];
}

- (void)initIndicatorView{
    self.indicatorView.delegate = self;
    self.indicatorView.moveLineWidthRatio = 0.8;
    self.indicatorView.selectColor = TintColor;
    self.indicatorView.unselectColor = UIColorHex(0xa4afb4);
    self.indicatorView.titles = self.titles;
    self.indicatorView.relateScrollview = self.scrollView;
    self.indicatorView.index = self.isSearch ? 0 : 1;
}

- (void)initTableView{
    [self addRefreshForScrollView:self.favoriteTableView];
    [self addRefreshForScrollView:self.tokenTableView];
    [self addRefreshForScrollView:self.exchangeTableView];
    [self.favoriteTableView registerNib:[UINib nibWithNibName:@"MarketFavoriteHeader" bundle:nil] forHeaderFooterViewReuseIdentifier:@"header"];
    [self.favoriteTableView registerNib:[UINib nibWithNibName:@"MarketTokenViewCell" bundle:nil] forCellReuseIdentifier:@"cell"];
    [self.tokenTableView registerNib:[UINib nibWithNibName:self.isSearch ? @"MarketFavoriteHeader" : @"MarketTokenHeader" bundle:nil] forHeaderFooterViewReuseIdentifier:@"header"];
    [self.tokenTableView registerNib:[UINib nibWithNibName:@"MarketTokenViewCell" bundle:nil] forCellReuseIdentifier:@"cell"];
    [self.exchangeTableView registerNib:[UINib nibWithNibName:@"MarketExchangeCell" bundle:nil] forCellReuseIdentifier:@"cell"];
}

- (void)initSortingView{
    @weakify(self)
    self.sortingView.sortingCallback = ^(MarketSortingType sortingType) {
        @strongify(self)
        self.sortArray[(self.isSearch) ? self.indicatorView.index+1 : self.indicatorView.index] = [NSNumber numberWithInteger:sortingType];
        [self loadData];
    };
}

#pragma mark 加载数据 及 刷新
- (void)refreshWithScrollView:(UIScrollView *)scrollView{
    if (scrollView == _exchangeTableView) {
        [self loadExchangeList];
    }else if (scrollView == _tokenTableView){
        [self loadTokenList];
    }else{
        [self loadFavoriteList];
    }
}

- (void)loadData{
    if (!self.isSearch) {//搜索页面
        if (self.indicatorView.index == 0) {
            [self loadFavoriteList];
        }else if (self.indicatorView.index == 1){
            [self loadTokenList];
        }else{
            [self loadExchangeList];
        }
    }else{
        if (self.indicatorView.index == 0) {
            [self loadTokenList];
        }else{
            [self loadExchangeList];
        }
    }
}

- (void)loadFavoriteList{
    [MarketService marketFavoriteListWithType:self.sortArray[0].integerValue success:^(__kindof NSObject *data) {
        self.favorietList = data;
        [self.favoriteTableView endRefreshing];
        [self.favoriteTableView reloadData];
    } failure:^(NSError *error) {
        self.favorietList = nil;
        [self.favoriteTableView endRefreshing];
        [self.favoriteTableView reloadData];
    }];
}

- (void)loadTokenList{
    [MarketService marketTokenListWithType:self.sortArray[1].integerValue searchText:self.searchText success:^(__kindof NSObject *data) {
        self.tokenList = data;
        [self.tokenTableView endRefreshing];
        [self.tokenTableView reloadData];
    } failure:^(NSError *error) {
        self.tokenList = nil;
        [self.tokenTableView endRefreshing];
        [self.tokenTableView reloadData];
    }];
}

- (void)loadExchangeList{
    [MarketService marketExchangeListWithType:self.sortArray[2].integerValue searchText:self.searchText success:^(__kindof NSObject *data) {
        self.exchangeList = data;
        [self.exchangeTableView endRefreshing];
        [self.exchangeTableView reloadData];
    } failure:^(NSError *error) {
        self.exchangeList = nil;
        [self.exchangeTableView endRefreshing];
        [self.exchangeTableView reloadData];
    }];
}

#pragma mark - UITableViewDataSource,UITableViewDelegate
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    if (tableView == _exchangeTableView) {
        return 0;
    }
    return 62;
}

- (CGFloat)tableView:(UITableView *)tableView estimatedHeightForHeaderInSection:(NSInteger)section{
    if (tableView == _exchangeTableView) {
        return 0;
    }
    return 62;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    if (tableView == _exchangeTableView) {
        return 68;
    }else if (tableView == _tokenTableView){
        return 57;
    }else{
        return 0;
    }
}

- (CGFloat)tableView:(UITableView *)tableView estimatedHeightForRowAtIndexPath:(NSIndexPath *)indexPath{
    if (tableView == _exchangeTableView) {
        return 68;
    }else if (tableView == _tokenTableView){
        return 57;
    }else{
        return 0;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    if (tableView == _tokenTableView) {
        return self.tokenList.count;
    }else if (tableView == _favoriteTableView) {
        return self.favorietList.count;
    }
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (tableView == _favoriteTableView) {
        return self.favorietList.count;
    }else if (tableView == _tokenTableView){
        MarketModel *data = self.tokenList[section];
        return data.show ? self.tokenList[section].child.count : 0;
    }else{
        return self.exchangeList.count;
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    MarketTableHeaderFooterView *header = [tableView dequeueReusableHeaderFooterViewWithIdentifier:@"header"];
    MarketModel *market = (tableView == _favoriteTableView) ? self.favorietList[section] : self.tokenList[section];
    market.index = [NSString stringWithFormat:@"%02ld",section+1];
    header.headerData = market;
    @weakify(self)
    header.callback = ^(MarketTableHeaderCallbackType callbackType, MarketModel *headerData) {
        @strongify(self)
        switch (callbackType) {
            case MarketTableHeaderCallbackType_show:
                [self.tokenTableView reloadData];
                break;
            case MarketTableHeaderCallbackType_favorite:
                if (self.favoriteTableView == tableView) {
                    [self loadTokenList];
                }else{
                    [self loadFavoriteList];
                }
                break;
        }
    };
    return header;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    MarketTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
    if (tableView == _exchangeTableView) {
        cell.exchangeData = self.exchangeList[indexPath.row];
    }else if (tableView == _tokenTableView){
        cell.tokenData = self.tokenList[indexPath.section].child[indexPath.row];
    }else{
        cell.tokenData = self.favorietList[indexPath.section].child[indexPath.row];
    }
    @weakify(self)
    cell.collectionBlock = ^(MarketModel *tokenData) {
        @strongify(self)
        [self loadFavoriteList];
    };
    return cell;
}

#pragma mark - IndicatorViewProtocol
- (void)indicatorView:(IndicatorView*)indicatorView index:(NSInteger)index{
    [self.sortingView setIndex:self.indicatorView.index sortType:self.sortArray[index].integerValue];
    if (self.isSearch) {
        [self loadData];
    }
}

#pragma mark - 搜索相关
- (IBAction)searchAction:(id)sender {
    MarketSearchViewController *vc = [UIViewController instantiateViewControllerWithIdentifier:@"MarketSearchViewController" inStoryboard:@"Main"];
    UISearchController * searchVC = [[UISearchController alloc] initWithSearchResultsController:vc];
    searchVC.delegate = self;
    searchVC.dimsBackgroundDuringPresentation = NO;
    searchVC.searchBar.delegate = vc;
    searchVC.searchResultsUpdater = vc;
    searchVC.searchBar.barTintColor = UIColor.whiteColor;
    searchVC.searchBar.tintColor = UIColorHex(0xA4AFB4);
    UITextField *searchField = [searchVC.searchBar valueForKey:@"searchField"];
    if (searchField){
        searchField.backgroundColor = UIColorHex(0xf3f3f3);
        searchField.tintColor = TintColor;
        if (@available(iOS 11.0, *)) searchField.layer.cornerRadius = 20;
        else searchField.layer.cornerRadius = 12;
        searchField.layer.masksToBounds = YES;
        searchField.textColor = TintColor;
    }
    self.definesPresentationContext = YES;
    [self.tabBarController.tabBar setHidden:YES];
    [self presentViewController:searchVC animated:YES completion:nil];
}

- (void)willDismissSearchController:(UISearchController *)searchController{
    [self.tabBarController.tabBar setHidden:NO];
    [self loadData];
}

#pragma mark - 懒加载数据
- (NSMutableArray<NSNumber *> *)sortArray{
    if (!_sortArray) {
        _sortArray = [NSMutableArray arrayWithObjects:@(MarketSortingType_A),@(MarketSortingType_A),@(MarketSortingType_A), nil];
    }
    return _sortArray;
}

- (NSArray *)titles{
    if (!_titles) _titles = @[@"Favorite",@"Token",@"Exchange"];
    return _titles;
}

- (void)setSearchText:(NSString *)searchText{
    _searchText = searchText;
    [self loadData];
}

#pragma mark - 无数据处理

- (UIColor *)xy_noDataViewMessageColor{
    return UIColorHex(0xa4afb4);
}

- (UIButton *)xy_noDataViewButton{
    if (self.favoriteTableView && self.indicatorView.index == 0) {
        UIButton *button = [UIButton buttonWithType:UIButtonTypeSystem];
        [button setTitle:@"Login" forState:UIControlStateNormal];
        [button addTarget:self action:@selector(gotologin) forControlEvents:UIControlEventTouchUpInside];
        return WalletManager.share.account.registered ? nil : button;
    }
    return nil;
}

- (void)gotologin{
    [self pushViewControllerWithIdentifier:@"SignInViewController_SignIn" inStoryboard:@"Main"];
}

- (UIImage *)xy_noDataViewImage{
    if (self.favoriteTableView && self.indicatorView.index == 0) {
        return [UIImage imageNamed:@"No record"];
    }else if (self.isSearch) {
        return [UIImage imageNamed:@"pic-none"];
    }else{
        return [UIImage imageNamed:@"No record"];
    }
}

- (NSString *)xy_noDataViewMessage{
    if (self.isSearch) {
        return @"Not find related results";
    }else{
        if (self.favoriteTableView && self.indicatorView.index == 0) {
            return WalletManager.share.account.registered ? @"No Data" : @"The favorite will occur after login";
        }else{
            return @"No Data";
        }
    }
}

- (BOOL)xy_noDataViewCalculateContainSection:(__kindof UIScrollView *)scrollView{
    if (scrollView != _exchangeTableView ) {
        return YES;
    }
    return NO;
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
