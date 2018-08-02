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

@interface AccountViewController ()<UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UIImageView *walletImageView;
@property (weak, nonatomic) IBOutlet UILabel *walletAddressLabel;
@property (weak, nonatomic) IBOutlet UITextField *walletAmountField;
@property (weak, nonatomic) IBOutlet UIImageView *walletAddressQRCode;
@property (weak, nonatomic) IBOutlet UIScrollView *myScrollView;
@property (nonatomic, strong) QRCodeDataModel *QRCodedata;
@property (weak, nonatomic) IBOutlet UIButton *myCopyButton;
@property (weak, nonatomic) IBOutlet UILabel *suffixLabel;
@property (nonatomic, copy) NSNumberFormatter *numberFormatter;
@end



@implementation AccountViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.neverAdjustContentInsetScrollView = self.myScrollView;
    [self.navigationItem.leftBarButtonItem setCustomStyle:UIBarButtonItemCustomStyle_Close];
    self.walletAmountField.inputAccessoryView = self.keyboardAccessoryView;
    self.walletAddressLabel.text = self.wallet.address;
    self.walletAddressQRCode.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:[self.QRCodedata modelToJSONString] imageViewWidth:260];
    [self.view setGradientColor:@[UIColorHex(0x405D68),UIColorHex(0x1A3D4E)] gradientType:GradientTypeLeftToRight];
    self.suffixLabel.text = self.tokenData ? self.tokenData.tokenTypeString : @"ETH";
    TCM_LayoutFont(self.myCopyButton.titleLabel);
}

- (IBAction)touchAction:(id)sender {
    [self.walletAmountField becomeFirstResponder];
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

- (NSNumberFormatter *)numberFormatter{
    if (!_numberFormatter) {
        _numberFormatter = [[NSNumberFormatter alloc] init];
        _numberFormatter.numberStyle = NSNumberFormatterDecimalStyle;
        _numberFormatter.maximumFractionDigits = 8;
    }
    return _numberFormatter;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string{
    if ([string isEqualToString:@""]) {
        return YES;
    }
    if ([textField.text containsString:@"."] && [string isEqualToString:@"."]) {
        return NO;
    }
    if ([textField.text containsString:@"."] &&[textField.text componentsSeparatedByString:@"."].lastObject.length >= 8) {
        return NO;
    }
    return YES;
}

- (IBAction)editingTextChangeAction:(UITextField *)sender {
    self.suffixLabel.hidden = sender.text.length == 0 ? YES : NO;
    NSString *text = [sender.text stringByReplacingOccurrencesOfString:@"," withString:@""];
    NSNumber *a = [self.numberFormatter numberFromString:text];
    NSString *b = [self.numberFormatter stringFromNumber:a];
    if (![text hasSuffix:@"."] && ![text hasSuffix:@"0"]) {
        sender.text = b;
    }
    NSLog(@"Text:%@ A:%@ B:%@",text,a,b);
    [self updateQRCodeWithAmoutn:text];
}

- (void)updateQRCodeWithAmoutn:(NSString*)amount{
    self.QRCodedata.transaction.amount = amount;
    self.QRCodedata.data = [self.QRCodedata.transaction modelToJSONString];//兼容安卓
    NSString *value = [self.QRCodedata modelToJSONString];
    self.walletAddressQRCode.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:value imageViewWidth:260];
}

- (IBAction)copyAction:(id)sender {
    [UIPasteboard generalPasteboard].string = self.wallet.address;
    [self dispalyText:@"Has been copied to the paste board!"];
}

- (IBAction)shareAction:(UIBarButtonItem*)sender {
    UIActivityViewController *vc = [[UIActivityViewController alloc] initWithActivityItems:@[self.wallet.address] applicationActivities:nil];
    if (UIDevice.currentDevice.isPad) {
        UIPopoverController *popover = [[UIPopoverController alloc]initWithContentViewController:vc];
        [popover presentPopoverFromBarButtonItem:self.navigationItem.rightBarButtonItem permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
    }else{
        [self presentViewController:vc animated:YES completion:nil];
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
