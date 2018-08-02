//
//  SignDetailViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/12.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "SignDetailViewController.h"
#import "SignVerifyProcessView.h"

@interface SignDetailViewController ()
@property (weak, nonatomic) IBOutlet UIView *transactionView;
@property (weak, nonatomic) IBOutlet UILabel *signerAddressLabel;
@property (weak, nonatomic) IBOutlet UILabel *dataLabel;
@property (weak, nonatomic) IBOutlet UILabel *toAddressLabel;
@property (weak, nonatomic) IBOutlet UILabel *fromAddressLabel;
@property (weak, nonatomic) IBOutlet UILabel *amountLabel;
@property (weak, nonatomic) IBOutlet UILabel *costLabel;
@property (strong, nonatomic) WalletModel *wallet;
@end

@implementation SignDetailViewController

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    if(self.data.type == QRCodeType_Transaction){
        [(BasicNavigationController*)self.navigationController setNavigationBarTransparent];
    }
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    if(self.data.type == QRCodeType_Transaction){
        [(BasicNavigationController*)self.navigationController reverseNavigationBar];
    }
}


- (void)viewDidLoad {
    [super viewDidLoad];
    
    BOOL haveAddress = NO;
    for (WalletModel *wallet in WalletManager.share.wallets) {
        if ([[Address addressWithString:wallet.address] isEqualToAddress:[Address addressWithString:self.data.ethereum]]) {
            self.wallet = wallet;
            haveAddress = YES;
            break;
        }
    }
    if (haveAddress == NO) {
        [self.navigationController dispalyConfirmText:@"wallet address nonexistence"];
        [self.navigationController popViewControllerAnimated:YES];
        return;
    }

    
    switch (self.data.type) {
        case QRCodeType_Observer:
        {
            self.title = @"Sign detail";
            self.transactionView.hidden = YES;
            _dataLabel.text = self.data.data;
            _signerAddressLabel.text = self.data.ethereum;
            break;
        }
        case QRCodeType_Transaction:
        {
            self.title = @"Preview the transfer";
            self.transactionView.hidden = NO;
            [(BasicNavigationController*)self.navigationController setNavigationBarTransparent];
            self.fromAddressLabel.text = self.data.transaction.transactionFrom;
            self.toAddressLabel.text = self.data.transaction.transactionTo;
            self.costLabel.text = [NSString stringWithFormat:@"%@ETH",[[self.data.transaction.gasLimit decimalNumberByMultiplying:self.data.transaction.gasPrice] decimalNumberByDividing:DecimalNumberTenPower18]];
            self.amountLabel.text = [NSString stringWithFormat:@"%@%@", self.data.transaction.amount ,self.data.transaction.tokenName];
            break;
        }
        default:
            break;
    }
}

- (IBAction)nextAction:(id)sender {
    
    switch (self.data.type) {
        case QRCodeType_Observer:
        {
            [self checkPassWithWallet:self.wallet completion:^(BOOL pass, Account *myAccount, NSString *password) {
                if (pass) {
                    NSString *md5 = [self.data.data md5String];
                    self.data.data = md5;
                    NSString *value = [self.data modelToJSONString];
                    [SignVerifyProcessView showWithType:WalletType_Observer_Cold value:value];
                }
            }];
            break;
        }
        case QRCodeType_Transaction:
        {
            QRCodeDataModel *QRCodedata = [[QRCodeDataModel alloc]init];
            QRCodedata.mode = @"mode_show_eth_tx_sign";
            
            WalletModel *myWallet = nil;
            for (WalletModel *wallet in WalletManager.share.wallets) {
                if ([[Address addressWithString:wallet.address] isEqualToAddress:[Address addressWithString:self.data.transaction.transactionFrom]]) {
                    myWallet = wallet;
                    break;
                }
            }
            [self checkPassWithWallet:myWallet completion:^(BOOL pass, Account *myAccount, NSString *password) {
                if (pass) {
                    Transaction *trans = [Transaction transactionWithFromAddress:[Address addressWithString:self.data.transaction.transactionFrom]];
                    trans.toAddress = [Address addressWithString:self.data.transaction.transactionTo];
                    trans.nonce = self.data.transaction.nonce.integerValue;
                    if (self.data.transaction.data.length > 0) {
                        trans.data = [SecureData hexStringToData:self.data.transaction.data];
                    }else{
                        if(![self.data.transaction.tokenName isEqualToString:@"ETH"]){
                            NSString *value = [[NSString alloc] initWithFormat:@"%064llx",[self.data.transaction.amount decimalNumberByMultiplying:DecimalNumberTenPower18].longLongValue];
                            NSString *dataStr = [NSString stringWithFormat:@"%@%@%@",ContractTransferFunctionPrefix,[self.data.transaction.transactionTo stringByReplacingOccurrencesOfString:@"0x" withString:@""],value];
                            trans.data = [SecureData hexStringToData:dataStr];
                        }
                    }
                    if([self.data.transaction.tokenName isEqualToString:@"ETH"]){
                        trans.value = [BigNumber bigNumberWithDecimalString:[self.data.transaction.amount decimalNumberByMultiplying:DecimalNumberTenPower18]];
                    }else{
                        trans.value = [BigNumber bigNumberWithDecimalString:@"0"];
                    }
                    trans.gasLimit = [BigNumber bigNumberWithDecimalString:self.data.transaction.gasLimit];
                    trans.gasPrice = [BigNumber bigNumberWithDecimalString:self.data.transaction.gasPrice];
                    [myAccount sign:trans];
                    NSString *value = [SecureData dataToHexString:[trans serialize]];
                    QRCodedata.data = value;
                    [SignVerifyProcessView showWithType:WalletType_Observer_Cold value:[QRCodedata modelToJSONString]];
                }
            }];

            break;
        }
        default:
            break;
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
