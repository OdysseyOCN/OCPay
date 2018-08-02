//
//  TokenIncomeViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/7.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "TokenIncomeViewController.h"
#import "TransactionRecordModel.h"
#import "TransactionRecordTableCell.h"
#import "TransactionDetailViewController.h"
#import "SendTransactionViewController.h"
#import "AccountViewController.h"


@interface TokenIncomeViewController ()

@property (weak, nonatomic) IBOutlet UITableView *myTableView;
@property (weak, nonatomic) IBOutlet UIView *myGraph;
@property (weak, nonatomic) IBOutlet UIView *myCardView;
@property (weak, nonatomic) IBOutlet UIView *topShadow;

@property (strong, nonatomic) NSMutableArray <TransactionInfo*>*showDatas;
@property (strong, nonatomic) AAChartView *aaChartView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topConstraint;
@property (weak, nonatomic) IBOutlet UILabel *amountLabel;
@property (weak, nonatomic) IBOutlet UILabel *legalCurrencyAmountLabel;

@end


@implementation TokenIncomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
    [self dispalyLoading:nil];
    [self loadData:YES];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self loadData:NO];
}

- (void)initUI{
    self.title = self.tokenData.tokenTypeString;
    self.neverAdjustContentInsetScrollView = self.myTableView;
    self.amountLabel.text = [NSString stringWithFormat:@"%.4f", self.tokenData.tokenAmount.floatValue];
    self.legalCurrencyAmountLabel.text = [NSString stringWithFormat:@"≈ %.2f%@", self.tokenData.baseLegalCurrencyAmount.doubleValue,WalletManager.legalCurrencyTypeSymbol];
    self.myTableView.sectionHeaderHeight = UITableViewAutomaticDimension;
    self.myTableView.estimatedSectionHeaderHeight = 30;
    self.topConstraint.constant = IPHONE_NAVIGATION_BAR_HEIGHT+IPHONE_STATUS_BAR_HEIGHT;
}

- (void)viewDidLayoutSubviews{
    [super viewDidLayoutSubviews];
    self.aaChartView.frame = self.myGraph.bounds;
    self.myCardView.layer.cornerRadius = 6;
    [self.topShadow setCornerRadius:6 rectCorner:UIRectCornerTopLeft | UIRectCornerTopRight];
    [self.aaChartView setCornerRadius:6 rectCorner:UIRectCornerBottomLeft | UIRectCornerBottomRight];
    [self.myCardView setLayerShadow:[UIColor colorWithRGB:0x040000 alpha:0.25f] offset:CGSizeMake(0, 7.5) radius:6];
}

- (void)loadData:(BOOL)updateChart{
    [TransactionRecordModel getIncomeDataWithAddress:self.wallet tokenType:self.tokenData.tokenType success:^(__kindof NSObject *data) {
        NSArray *sourceData = data;
        [self hideLoading:YES];
        self.showDatas = sourceData.firstObject;
        [self.myTableView reloadData];

        if (!updateChart) {
            return ;
        }
        NSMutableArray *dates = [NSMutableArray array];
        NSMutableArray *values = [NSMutableArray array];
        for (TransactionRecordDateModel *recordData in sourceData.lastObject) {
            [values addObject:[NSNumber numberWithString:recordData.amount]];
            [dates addObject:recordData.date];
        }
        NSNumber *max = [values valueForKeyPath:@"@max.floatValue"];
        [self drawChartWithDates:dates values:values max:max];
    }];
}

- (AAChartView *)aaChartView{
    if (!_aaChartView) {
        _aaChartView= [[AAChartView alloc]init];
        _aaChartView.scrollEnabled = YES;
        [self.myGraph addSubview:_aaChartView];
    }
    return _aaChartView;
}

- (void)drawChartWithDates:(NSArray*)dates values:(NSArray*)values max:(NSNumber*)max{
    AAChartModel *aaChartModel = AAObject(AAChartModel)
    .chartTypeSet(AAChartTypeArea)
    .zoomTypeSet(AAChartZoomTypeXY)
    .titleSet(@"")
    .subtitleSet(@"")
    .yAxisTitleSet(@"")
    .colorsThemeSet(@[@"#2870C3"])
    .gradientColorEnabledSet(true)
    .yAxisGridLineWidthSet(@0)
    .xAxisGridLineWidthSet(@1)
    .yAxisLineWidthSet(@1)
    .markerRadiusSet(@0)//设置折线连接点宽度为0,即是隐藏连接点
    .animationTypeSet(AAChartAnimationEaseOutQuart)//图形的渲染动画为 EaseOutQuart 动画
    .xAxisCrosshairWidthSet(@0.9)//Zero width to disable crosshair by default
    .xAxisCrosshairColorSet(@"#38525F")//准星线
    .xAxisCrosshairDashStyleTypeSet(AALineDashSyleTypeSolid)
    .yAxisMaxSet(max)
    .yAxisMinSet(@0)
//    .yAxisTickPositionsSet(@[@(0),@(25),@(50),@(75),@(100)])//指定y轴坐标;
    .categoriesSet(dates)
    .seriesSet(@[AAObject(AASeriesElement)
                 .nameSet(@"Assets")
                 .dataSet(values)
                 ])
    ;
    [_aaChartView aa_drawChartWithChartModel:aaChartModel];
}

- (IBAction)sendAction:(id)sender {
    [FIRAnalytics logEventWithName:self.tokenData.tokenType == TokenType_ETH ? @"ETH_button_Send" : @"OCN_button_Send" parameters:nil];
    SendTransactionViewController *vc = [SendTransactionViewController instantiateViewControllerWithIdentifier:@"SendTransactionViewController" inStoryboard:@"Main"];
    vc.wallet = self.wallet;
    vc.tokenData = self.tokenData;
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)receiveAction:(id)sender {
    [FIRAnalytics logEventWithName:self.tokenData.tokenType == TokenType_ETH ? @"ETH_button_Receive" : @"OCN_button_Receive" parameters:nil];
    AccountViewController *accountVC = [UIViewController instantiateViewControllerWithIdentifier:@"AccountViewController" inStoryboard:@"Main"];
    accountVC.wallet = self.wallet;
    accountVC.tokenData = self.tokenData;
    [self.navigationController pushViewController:accountVC animated:YES];
}

#pragma mark - UITableViewDataSource,UITableViewDelegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.showDatas.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    TransactionRecordTableCell *cell = [tableView dequeueReusableCellWithIdentifier:@"myCell"];
    TransactionInfo *info = self.showDatas[indexPath.row];
    cell.wallet = self.wallet;
    cell.info = info;
    return cell;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    TransactionRecordTableCell *cell =  [tableView dequeueReusableCellWithIdentifier:@"header"];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    TransactionInfo *info = self.showDatas[indexPath.row];
    TransactionDetailViewController *vc = [self pushViewControllerWithIdentifier:@"TransactionDetailViewController" inStoryboard:@"Main"];
    vc.info = info;
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    if (scrollView.contentOffset.y >= IPHONE_NAVIGATION_BAR_HEIGHT+IPHONE_STATUS_BAR_HEIGHT) {
        [(BasicNavigationController*)self.navigationController reverseNavigationBar];
    }else{
        [(BasicNavigationController*)self.navigationController setNavigationBarTransparent];
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
