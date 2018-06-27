
//
//  HomeViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/24.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "HomeViewController.h"
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

@interface HomeViewController ()
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *myBackgroundViewHeight;
@property (weak, nonatomic) IBOutlet UIView *myBackgroundView;
@property (weak, nonatomic) IBOutlet WalletTableView *walletTableView;
@property (weak, nonatomic) IBOutlet HomeCollectionView *homeCollectionView;
@property (strong, nonatomic) IBOutlet UIView *myBackupAlertView;
@property (nonatomic, strong) HomeViewModel *viewData;
@property (nonatomic, strong) UIRefreshControl *refreshControl;
@property (nonatomic) CGFloat ratio;
@end

@implementation HomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initWalletTableView];
    [self initHomeColletionView];
    [self initRefreshControl];
    [self loadModuleData];
}

- (void)viewDidLayoutSubviews{
    [super viewDidLayoutSubviews];
    [self.myBackgroundView setGradientColor:@[UIColorHex(0x405D68),UIColorHex(0x1A3D4E)] gradientType:GradientTypeUpleftToLowright];
//    [self.myBackgroundView setGradientColor:@[UIColor.blueColor,UIColor.redColor] gradientType:GradientTypeUpleftToLowright];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self setRatio:_ratio];
    [self.homeCollectionView reloadData];
    [self loadAcountData];
}

- (void)initRefreshControl{
    _refreshControl = [[UIRefreshControl  alloc] init];
    _refreshControl.tintColor = UIColorHex(0xffffff);
    [_refreshControl addTarget:self action:@selector(updateData) forControlEvents:UIControlEventValueChanged];
    [_homeCollectionView addSubview:_refreshControl];
}

- (void)updateData{
    if (self.refreshControl.refreshing) {
        [self loadModuleData];
        [self loadAcountData];
    }
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

- (IBAction)leftBarButtonItemAction:(UIBarButtonItem *)sender {
    [self.walletTableView show];
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
}

- (void)initWalletTableView{
    @weakify(self)
    self.walletTableView.selectedCallback = ^(WalletModel *wallet) {
        @strongify(self)
        [self.walletTableView closeWalletViewNoAnimate:NO];
        WalletManager.share.defaultWallet = wallet;
        [self.homeCollectionView reloadData];
        [self loadAcountData];
    };
}

- (void)initHomeColletionView{
    self.homeCollectionView.data = self.viewData;
    self.neverAdjustContentInserScrollView = self.homeCollectionView;
    @weakify(self)
    self.homeCollectionView.callback = ^(HomeCellViewModel *data, HomeCollectionCellCallbackType type) {
        @strongify(self)
        switch (type) {
                case HeadCellCallbackType_ScanQRCode:{
                QRCodeViewController *vc = [[QRCodeViewController alloc] init];
                vc.reciveResult = ^(NSString *result) {
                    QRCodeDataModel *qrdata = [QRCodeDataModel modelWithJSON:result];
                    if (qrdata.type == QRCodeType_Observer || qrdata.type == QRCodeType_Transaction) {
                        SignDetailViewController *vc = [self pushViewControllerWithIdentifier:@"SignDetailViewController" inStoryboard:@"Main"];
                        vc.data = qrdata;
                    }else if (qrdata.type == QRCodeType_Receive){
                        SendTransactionViewController *vc = [SendTransactionViewController instantiateViewControllerWithIdentifier:@"SendTransactionViewController" inStoryboard:@"Main"];
                        vc.wallet = WalletManager.share.defaultWallet;
                        vc.QRCodedata = qrdata;
                        for (TokenModel *token in vc.wallet.tokens) {
                            if ([token.tokenTypeString isEqualToString:qrdata.transaction.tokenName]) {
                                vc.tokenData = token;
                                break;
                            }
                        }
                        [self.navigationController pushViewController:vc animated:YES];

                    }
                };
                [self QRCodeScanVC:vc];
                break;
            }
            case HeadCellCallbackType_ShowAccount:{
                
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
                TransactionRecordViewController *vc = [TransactionRecordViewController instantiateViewControllerWithIdentifier:@"TransactionRecordViewController" inStoryboard:@"Main"];
                vc.wallet = WalletManager.share.defaultWallet;
                [self.navigationController pushViewController:vc animated:YES];
                break;
            }
            case HeadCellCallbackType_Send:{
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

- (void)setRatio:(CGFloat)ratio{
    _ratio = ratio;
    self.myBackgroundViewHeight.constant = 260 - self.homeCollectionView.contentOffset.y;
    [self.navigationController.navigationBar setBackgroundImage:[UIImage imageWithColor:[UIColor colorWithWhite:1 alpha:ratio]] forBarMetrics:UIBarMetricsDefault];
    if (ratio == 1) {
        [self.navigationController.navigationBar setShadowImage:nil];
    }else{
        [self.navigationController.navigationBar setShadowImage:[UIImage imageNamed:@"TransparentPixel"]];
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
