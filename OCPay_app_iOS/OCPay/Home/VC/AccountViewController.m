//
//  AccountViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/1.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "AccountViewController.h"
#import "BackupWalletViewController.h"

@interface AccountViewController ()
@property (weak, nonatomic) IBOutlet UIImageView *walletImageView;
@property (weak, nonatomic) IBOutlet UILabel *walletAddressLabel;
@property (weak, nonatomic) IBOutlet UILabel *walletAmountLabel;
@property (weak, nonatomic) IBOutlet UIImageView *walletAddressQRCode;
@property (weak, nonatomic) IBOutlet UIScrollView *myScrollView;

@end

@implementation AccountViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setScrollViewContentInsetAdjustmentNever:self.myScrollView];
    // Do any additional setup after loading the view.
    UIBarButtonItem *a = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemPlay target:self action:@selector(popAction)];
    self.navigationItem.leftBarButtonItem = a;
    
    self.walletAddressLabel.text = self.wallet.address;
    self.walletAmountLabel.text = [NSString stringWithFormat:@"%.4f", self.wallet.totalAssets.floatValue];;
    
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    [dic setValue:self.wallet.address forKey:@"ethereum"];
    [dic setValue:@"mode_tx_receive" forKey:@"mode"];
    NSString *value = [dic jsonStringEncoded];
    self.walletAddressQRCode.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:value imageViewWidth:260];
}

- (void)popAction{
    [self.navigationController popViewControllerAnimated:YES];
}

- (IBAction)copyAction:(id)sender {
    [UIPasteboard generalPasteboard].string = self.walletAddressLabel.text;
    [self dispalyText:@"地址已复制"];
}



- (IBAction)shareAction:(id)sender {
    if (self.walletAddressLabel.text == nil) {
        return;
    }
    UIActivityViewController *vc = [[UIActivityViewController alloc] initWithActivityItems:@[self.walletAddressLabel.text] applicationActivities:nil];
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
