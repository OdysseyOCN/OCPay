//
//  AccountViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/1.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "AccountViewController.h"
#import "BackupWalletViewController.h"
#import "QRCodeDataModel.h"

@interface AccountViewController ()
@property (weak, nonatomic) IBOutlet UIImageView *walletImageView;
@property (weak, nonatomic) IBOutlet UILabel *walletAddressLabel;
@property (weak, nonatomic) IBOutlet UITextField *walletAmountField;
@property (weak, nonatomic) IBOutlet UIImageView *walletAddressQRCode;
@property (weak, nonatomic) IBOutlet UIScrollView *myScrollView;
@property (nonatomic, strong) QRCodeDataModel *QRCodedata;
@end

@implementation AccountViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.neverAdjustContentInserScrollView = self.myScrollView;
    [self.navigationItem.leftBarButtonItem setCustomStyle:UIBarButtonItemCustomStyle_Close];
    self.walletAmountField.inputAccessoryView = self.keyboardAccessoryView;
    self.walletAddressLabel.text = self.wallet.address;
    self.walletAddressQRCode.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:[self.QRCodedata modelToJSONString] imageViewWidth:260];
    [self.view setGradientColor:@[UIColorHex(0x405D68),UIColorHex(0x1A3D4E)] gradientType:GradientTypeLeftToRight];
}

- (QRCodeDataModel *)QRCodedata{
    if (!_QRCodedata) {
        _QRCodedata = [[QRCodeDataModel alloc]init];
        _QRCodedata.ethereum = self.wallet.address;
        _QRCodedata.mode = @"mode_tx_receive";
        _QRCodedata.transaction.transactionTo = self.wallet.address;
        _QRCodedata.transaction.tokenName = self.tokenData ? self.tokenData.tokenTypeString : @"ETH";
    }
    return _QRCodedata;
}

- (IBAction)editingTextChangeAction:(UITextField *)sender {
    self.QRCodedata.transaction.amount = sender.text;
    NSString *value = [self.QRCodedata modelToJSONString];
    self.walletAddressQRCode.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:value imageViewWidth:260];
}

- (IBAction)copyAction:(id)sender {
    [UIPasteboard generalPasteboard].string = self.wallet.address;
    [self dispalyText:@"The address has been copied to the paste board"];
}

- (IBAction)shareAction:(id)sender {
    if (UIDevice.currentDevice.isPad) {
        return;
    }
    UIActivityViewController *vc = [[UIActivityViewController alloc] initWithActivityItems:@[self.wallet.address] applicationActivities:nil];
    [self presentViewController:vc animated:YES completion:nil];
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
