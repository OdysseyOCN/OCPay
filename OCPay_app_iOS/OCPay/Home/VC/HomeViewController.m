
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
#import "WalletTableView.h"
#import "HomeCollectionView.h"
#import "HomeViewModel.h"
#import "HomeDataModel.h"
#import "MyPrompt.h"
#import "BackupWalletViewController.h"
#import "SignVerifyProcessView.h"

@interface HomeViewController ()
@property (weak, nonatomic) IBOutlet WalletTableView *walletTableView;
@property (weak, nonatomic) IBOutlet HomeCollectionView *homeCollectionView;
@property (strong, nonatomic) IBOutlet UIView *myBackupAlertView;
@property (nonatomic, strong) HomeViewModel *viewData;
@end

@implementation HomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initWalletTableView];
    [self initHomeColletionView];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
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
    [NetWorkManager PostWithURL:@"http://ocpay-admin-dev.stormfives.com/api/ocpay/v1/token/get-Advertisment" parameters:nil success:^(__kindof NSDictionary *data) {
        HomeDataModel *sourceData = [HomeDataModel modelWithJSON:data[@"data"]];
        self.viewData.sourceData = sourceData;
    } failure:^(NSError *error) {
    }];
}

- (void)loadAcountData{
    [WalletManager.share.defaultWallet getWalletAllTokenInfo:^() {
        [self.viewData updateData];
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
        [self loadAcountData];
    };
}

- (void)initHomeColletionView{
    self.homeCollectionView.data = self.viewData;
    [self setScrollViewContentInsetAdjustmentNever:self.homeCollectionView];
    @weakify(self)
    self.homeCollectionView.callback = ^(HomeCellViewModel *data, HomeCollectionCellCallbackType type) {
        @strongify(self)
        switch (type) {
                case HeadCellCallbackType_ScanQRCode:{
                QRCodeViewController *vc = [[QRCodeViewController alloc] init];
                vc.reciveResult = ^(NSString *result) {
                    NSDictionary *dic = [NSString dictionaryWithJsonString:result];
                    NSString *model = dic[@"mode"];
                    if ([model isEqualToString:@"mode_data_sign"]) {
                        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
                        [dic setValue:@"1" forKey:@"ethereum"];
                        [dic setValue:@"mode_data_sign" forKey:@"mode"];
                        NSString *value = [dic jsonStringEncoded];
                        [SignVerifyProcessView showWithType:WalletType_Cold value:value];
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
                vc.isContractsTransaction = YES;
                [self.navigationController pushViewController:vc animated:YES];
                break;
            }
            case HomeCollectionCellCallbackType_choosePage:{
                break;
            }
            case HomeCollectionCellCallbackType_chooseModule:{
                break;
            }
            case HomeCollectionCellCallbackType_chooseAdvert:{
                NSData *data = [@"hello kitty" dataUsingEncoding:NSUTF8StringEncoding];
                Account *a = [Account accountWithPrivateKey:[SecureData hexStringToData:WalletManager.share.defaultWallet.privateKey]];
                Signature *b = [a signMessage:data];
                
                Address *c = [Account verifyMessage:data signature:b];
                NSString *d = c.checksumAddress;
                NSString *e = d;
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
        [self.navigationController.navigationBar setBackgroundImage:[UIImage imageWithColor:[UIColor colorWithRGB:0xffffff alpha:ratio]] forBarMetrics:UIBarMetricsDefault];
        if (ratio == 1) {
            [(BasicNavigationController*)self.navigationController reverseNavigationBar];
        }else{
            [(BasicNavigationController*)self.navigationController setNavigationBarTransparent];
        }
    };
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
