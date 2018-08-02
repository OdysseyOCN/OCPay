
//
//  HomeViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/24.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "HomeViewController.h"
#import "WalletDetailViewController.h"
#import "ManageWalletViewController.h"
#import "AccountViewController.h"
#import "TransactionRecordViewController.h"
#import "BasicNavigationController.h"
#import "CreateWalletViewController.h"
#import "SendTransactionViewController.h"
#import "TokenIncomeViewController.h"
#import "BackupWalletViewController.h"
#import "SignDetailViewController.h"
#import "BasicWebViewController.h"
#import "WalletTableView.h"
#import "HomeCollectionView.h"
#import "HomeViewModel.h"
#import "HomeDataModel.h"
#import "MyPrompt.h"
@import Firebase;

@interface HomeViewController ()
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *myBackgroundViewHeight;
@property (weak, nonatomic) IBOutlet UIView *myBackgroundView;
@property (weak, nonatomic) IBOutlet WalletTableView *walletTableView;
@property (weak, nonatomic) IBOutlet HomeCollectionView *homeCollectionView;
@property (strong, nonatomic) IBOutlet UIView *myBackupAlertView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (nonatomic, strong) HomeViewModel *viewData;
@property (nonatomic, strong) UIRefreshControl *refreshControl;
@property (weak, nonatomic) IBOutlet UIImageView *headImageView;
@property (nonatomic) CGFloat ratio;
@end

@implementation HomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initWalletTableView];
    [self initHomeColletionView];
    [self initRefreshControl];
    [self launchRefresh];
}

- (void)viewDidLayoutSubviews{
    [super viewDidLayoutSubviews];
    [self.myBackgroundView setGradientColor:@[UIColorHex(0x405D68),UIColorHex(0x1A3D4E)] gradientType:GradientTypeUpleftToLowright];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self setRatio:_ratio];
    [self loadAcountData];
    self.titleLabel.text = WalletManager.share.defaultWallet.name;
    self.headImageView.image = [UIImage imageNamed:WalletManager.share.defaultWallet.headImage];
    [self.homeCollectionView reloadData];
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [self.refreshControl endRefreshing];
}

-(void)launchRefresh{
    [self.homeCollectionView setContentOffset:CGPointMake(0, self.homeCollectionView.contentOffset.y - self.refreshControl.frame.size.height) animated:YES];
    [self.refreshControl beginRefreshing];
    [self.refreshControl sendActionsForControlEvents:UIControlEventValueChanged];
}

- (void)initRefreshControl{
    _refreshControl = [[UIRefreshControl  alloc] init];
    [_homeCollectionView addSubview:_refreshControl];
    _refreshControl.center = CGPointMake(DEVICE_SCREEN_WIDTH*.5, 0);
    _refreshControl.tintColor = UIColorHex(0xffffff);
    [_refreshControl addTarget:self action:@selector(updateData) forControlEvents:UIControlEventValueChanged];
}

- (void)updateData{
    [self loadModuleData];
    [self loadAcountData];
}

- (HomeViewModel *)viewData{
    if (!_viewData){
        _viewData = [[HomeViewModel alloc]init];
        _viewData.collectionView = _homeCollectionView;
    }
    return _viewData;
}

- (void)loadModuleData{
    [NetWorkManager PostWithURL:@"api/ocpay/v1/token/get-Advertisment" parameters:nil success:^(__kindof NSDictionary *data) {
        HomeDataModel *sourceData = [HomeDataModel modelWithJSON:data[@"data"]];
        self.viewData.sourceData = sourceData;
    } failure:^(NSError *error) {
        
    }];
}

- (void)loadAcountData{
    [WalletManager.share.defaultWallet getWalletAllTokenInfo:^() {
        [self.viewData updateData];
        [self.refreshControl endRefreshing];
    }];
}

- (IBAction)leftBarButtonItemAction:(id)sender {
    WalletDetailViewController *vc = [self pushViewControllerWithIdentifier:@"WalletDetailViewController" inStoryboard:@"Wallet"];
    vc.wallet = WalletManager.share.defaultWallet;
}

- (IBAction)rightBarButtonItemAction:(id)sender {
    [self.walletTableView show];
    [FIRAnalytics logEventWithName:@"home_button_Walletmanagement" parameters:nil];
}

- (IBAction)createWalletAction:(id)sender {
    [self.walletTableView closeWalletViewNoAnimate:YES];
    [self pushViewControllerWithIdentifier:@"CreateWalletViewController" inStoryboard:@"Wallet"];
}

- (IBAction)importWalletAction:(id)sender {
    [self.walletTableView closeWalletViewNoAnimate:YES];
    [self pushViewControllerWithIdentifier:@"ImportWalletViewController" inStoryboard:@"Wallet"];
}

- (IBAction)backupAction:(id)sender {
    [MyPrompt closePromptView];
    BackupWalletViewController *vc = [self pushViewControllerWithIdentifier:@"BackupWalletViewController" inStoryboard:@"Wallet"];
    vc.wallet = WalletManager.share.defaultWallet;
    [FIRAnalytics logEventWithName:@"Youneedtobackupfirst_button_BackupNow" parameters:nil];
}

- (void)initWalletTableView{
    @weakify(self)
    self.walletTableView.selectedCallback = ^(WalletModel *wallet) {
        @strongify(self)
        [self.walletTableView closeWalletViewNoAnimate:NO];
        WalletManager.share.defaultWallet = wallet;
        self.headImageView.image = [UIImage imageNamed:WalletManager.share.defaultWallet.headImage];
        self.titleLabel.text = WalletManager.share.defaultWallet.name;
        [self.homeCollectionView reloadData];
        [self loadAcountData];
    };
}

- (void)initHomeColletionView{
    self.homeCollectionView.data = self.viewData;
    self.neverAdjustContentInsetScrollView = self.homeCollectionView;
    @weakify(self)
    self.homeCollectionView.callback = ^(HomeCellViewModel *data, HomeCollectionCellCallbackType type) {
        @strongify(self)
        switch (type) {
            case HeadCellCallbackType_ScanQRCode:{
                [FIRAnalytics logEventWithName:@"home_button_QRcode" parameters:nil];
                QRCodeViewController *vc = [[QRCodeViewController alloc] init];
                vc.reciveResult = ^(NSString *result) {
                    [self dealQRCodeResult:result];
                };
                [self QRCodeScanVC:vc];
                break;
            }
            case HeadCellCallbackType_ShowAccount:{
                [FIRAnalytics logEventWithName:@"home_button_Switch" parameters:nil];

                if (WalletManager.share.defaultWallet.mnemonicPhrase.length > 0) {
                    [MyPrompt showPromptView:^(MyPrompt *view) {
                        view.canClose = NO;
                        self.myBackupAlertView.width = view.width-40;
                        self.myBackupAlertView.centerX = view.width*.5;
                        self.myBackupAlertView.centerY = view.height*.5;
                        self.myBackupAlertView.layer.cornerRadius = 7;
                        [view addSubview:self.myBackupAlertView];
                    }];
                    return;
                }
                AccountViewController *accountVC = [UIViewController instantiateViewControllerWithIdentifier:@"AccountViewController" inStoryboard:@"Main"];
                accountVC.wallet = WalletManager.share.defaultWallet;
                [self.navigationController pushViewController:accountVC animated:YES];
                break;
            }
            case HeadCellCallbackType_Record:{
                [FIRAnalytics logEventWithName:@"home_button_Record" parameters:nil];

                TransactionRecordViewController *vc = [TransactionRecordViewController instantiateViewControllerWithIdentifier:@"TransactionRecordViewController" inStoryboard:@"Main"];
                vc.wallet = WalletManager.share.defaultWallet;
                [self.navigationController pushViewController:vc animated:YES];
                break;
            }
            case HeadCellCallbackType_Send:{
                [FIRAnalytics logEventWithName:@"home_button_Send" parameters:nil];

                SendTransactionViewController *vc = [SendTransactionViewController instantiateViewControllerWithIdentifier:@"SendTransactionViewController" inStoryboard:@"Main"];
                vc.wallet = WalletManager.share.defaultWallet;
                TokenModel *myToken = nil;
                for (TokenModel *token in WalletManager.share.defaultWallet.tokens) {
                    if (token.tokenType == TokenType_ETH) {
                        myToken = token;
                    }
                }
                vc.tokenData = myToken;
                [self.navigationController pushViewController:vc animated:YES];
                break;
            }
            case HomeCollectionCellCallbackType_choosePage:
            case HomeCollectionCellCallbackType_chooseModule:
            case HomeCollectionCellCallbackType_chooseAdvert:{
                BasicWebViewController *webVC = [[BasicWebViewController alloc]init];
                webVC.URLString = data.advertData.directUrl;
                [self.navigationController pushViewController:webVC animated:YES];
                break;
            }
            case HomeCollectionCellCallbackType_chooseTokens:{
                TokenIncomeViewController *vc = [TokenIncomeViewController instantiateViewControllerWithIdentifier:@"TokenIncomeViewController" inStoryboard:@"Main"];
                vc.wallet = WalletManager.share.defaultWallet;
                vc.tokenData = data.tokenData;
                [self.navigationController pushViewController:vc animated:YES];
                
                break;
            }
            default:
                break;
        }
    };
    
    self.homeCollectionView.scrollRatioBlock = ^(CGFloat ratio) {
        self.ratio = ratio;
    };
}

- (void)dealQRCodeResult:(NSString*)result{
    QRCodeDataModel *qrdata = [QRCodeDataModel modelWithJSON:result];
    if (qrdata.type == QRCodeType_Observer || qrdata.type == QRCodeType_Transaction) {
        SignDetailViewController *vc = [self pushViewControllerWithIdentifier:@"SignDetailViewController" inStoryboard:@"Main"];
        vc.data = qrdata;
    }else if (qrdata.type == QRCodeType_Receive){
        SendTransactionViewController *vc = [SendTransactionViewController instantiateViewControllerWithIdentifier:@"SendTransactionViewController" inStoryboard:@"Main"];
        vc.wallet = WalletManager.share.defaultWallet;
        if (qrdata.transaction == nil) {//处理安卓数据的特殊情况
            NSDictionary *dic = [NSString dictionaryWithJsonString:result];
            TransactionDataModel *transData = [TransactionDataModel modelWithJSON:dic[@"data"]];
            qrdata.transaction = transData;
        }
        vc.QRCodedata = qrdata;
        for (TokenModel *token in vc.wallet.tokens) {
            if ([token.tokenTypeString isEqualToString:qrdata.transaction.tokenName]) {
                vc.tokenData = token;
                break;
            }
        }
        [self.navigationController pushViewController:vc animated:YES];
    }
}

- (void)setRatio:(CGFloat)ratio{
    _ratio = ratio;
    if (self.navigationController.topViewController != self) {
        return;
    }
    self.myBackgroundViewHeight.constant = 260 - self.homeCollectionView.contentOffset.y;
    [self.navigationController.navigationBar setBackgroundImage:[UIImage imageWithColor:[UIColor colorWithWhite:1 alpha:ratio]] forBarMetrics:UIBarMetricsDefault];
    if (ratio == 1) {
        [self.navigationController.navigationBar setShadowImage:nil];
        [self.navigationController.navigationBar setTintColor:UIColorHex(0x38525F)];
    }else{
        [self.navigationController.navigationBar setShadowImage:[UIImage imageNamed:@"TransparentPixel"]];
        [self.navigationController.navigationBar setTintColor:[UIColor whiteColor]];
    }
}


#pragma mark - Navigation
// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
@end
