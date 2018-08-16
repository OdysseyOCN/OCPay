//
//  MarketSearchViewController.m
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/7.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import "MarketSearchViewController.h"
#import "SearchHistoryCollectionCell.h"
#import "SearchHistoryCollectionReusableView.h"
#import "MarketService.h"

@interface MarketSearchViewController ()<UICollectionViewDelegate,UICollectionViewDataSource>
@property (nonatomic, strong) NSMutableArray *historyArr;
@property (nonatomic, strong) NSMutableArray *hotHistoryArr;
@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topConstraint;
@property (weak, nonatomic) UISearchController *searchController;
@property (weak, nonatomic) IBOutlet UIView *searchView;
@end

@implementation MarketSearchViewController

- (NSMutableArray *)historyArr{
    if (!_historyArr) {
        NSArray *arr = [UD objectForKey:HistoryKey];
        if (!arr) {
            _historyArr = [NSMutableArray array];
        }else{
            _historyArr = [NSMutableArray arrayWithArray:arr];
        }
    }
    return _historyArr;
}

- (NSMutableArray *)hotHistoryArr{
    if (!_hotHistoryArr) {
        _hotHistoryArr = [NSMutableArray array];
    }
    return _hotHistoryArr;
}

- (NSArray *)titles{
    return @[@"Token",@"Exchange"];
}

- (BOOL)isSearch{
    return YES;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.topConstraint.constant = IS_IPHONE_X ? 54 : 44;
    [self loadHotHistoryData];
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [UD setObject:self.historyArr forKey:HistoryKey];
    [UD synchronize];
}

- (void)updateSearchResultsForSearchController:(UISearchController *)searchController{
    self.view.hidden = NO;
    self.searchController = searchController;
    if (searchController.searchBar.text.length > 0) {
        self.searchView.hidden = NO;
        if (![self.searchText isEqualToString:searchController.searchBar.text]) {
            self.searchText = searchController.searchBar.text;
        }
    }else{
        self.searchView.hidden = YES;
        [self.collectionView reloadData];
    }
}

- (void)loadHotHistoryData{
    [MarketService marketHotSearchSuccess:^(__kindof NSObject *data) {
        NSArray *arr = data;
        for (HistoryModel *history in arr) {
            [self.hotHistoryArr addObject:history.token];
        }
        [self.collectionView reloadData];
    } failure:^(NSError *error) {
    }];
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    return self.historyArr.count > 0 ? 2 : 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    if (self.historyArr.count > 0) {
        if (section == 0) {
            return self.historyArr.count;
        }else{
            return self.hotHistoryArr.count;
        }
    }else{
        return self.hotHistoryArr.count;
    }
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath{
    SearchHistoryCollectionReusableView *header = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:@"header" forIndexPath:indexPath];
    if (self.historyArr.count > 0) {
        if (indexPath.section == 0) {
            header.titleLabel.text = @"Search History";
            header.deleteButton.hidden = NO;
        }else {
            header.titleLabel.text = @"Hot History";
            header.deleteButton.hidden = YES;
        }
    }else{
        header.titleLabel.text = @"Hot History";
        header.deleteButton.hidden = YES;
    }
    @weakify(self)
    header.deleteCallback = ^{
        @strongify(self)
        [self.historyArr removeAllObjects];
        [self.collectionView reloadData];
    };
    return header;
}

- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath{
    SearchHistoryCollectionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"cell" forIndexPath:indexPath];
    NSString * text = [self cellText:indexPath];
    cell.textLabel.text = text;
    return cell;
}

- (NSString *)cellText:(NSIndexPath *)indexPath {
    NSString *text = nil;
    if (_historyArr.count > 0) {
        if (indexPath.section == 0) {
            text = self.historyArr[indexPath.row];
        }else{
            text = self.hotHistoryArr[indexPath.row];
        }
    }else{
        text = self.hotHistoryArr[indexPath.row];
    }
    return text;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath{
    NSString * text = [self cellText:indexPath];
    CGSize size = [text sizeForFont:[UIFont systemFontOfSize:14] size:CGSizeMake(collectionView.width-60, collectionView.height) mode:NSLineBreakByWordWrapping];
    return CGSizeMake(size.width+30, 30);
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath{
    NSString * text = [self cellText:indexPath];
    [self dealHistory:text];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    [self dealHistory:searchBar.text];
}

- (void)dealHistory:(NSString*)text{
    if (text.length == 0) {
        return;
    }
    if ([self.historyArr containsObject:text]) {
        [self.historyArr removeObject:text];
    }
    [self.historyArr insertObject:text atIndex:0];
    [self.collectionView reloadData];
    self.searchController.searchBar.text = text;
    [self.searchController.searchBar resignFirstResponder];
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
