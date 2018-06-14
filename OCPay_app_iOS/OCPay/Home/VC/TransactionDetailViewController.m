//
//  TransactionDetailViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/6.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "TransactionDetailViewController.h"
#import "BasicWebViewController.h"
#import "UIView+RectCorner.h"

@interface TransactionDetailViewController ()
@property (weak, nonatomic) IBOutlet UIScrollView *myScrollView;
@property (weak, nonatomic) IBOutlet UIView *myTopView;
@property (weak, nonatomic) IBOutlet UIView *myCardView;

@property (weak, nonatomic) IBOutlet UILabel *amountLabel;
@property (weak, nonatomic) IBOutlet UILabel *sendHashValueLabel;
@property (weak, nonatomic) IBOutlet UILabel *receiveHashValueLabel;
@property (weak, nonatomic) IBOutlet UILabel *costLabel;
@property (weak, nonatomic) IBOutlet UIButton *transacationHashValue;
@property (weak, nonatomic) IBOutlet UILabel *blockLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (weak, nonatomic) IBOutlet UIImageView *QRCodeImageView;

@property (nonatomic, strong) NSDateFormatter* dateFormatter;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topConstraint;

@end

@implementation TransactionDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
    [self updateUI];
}

- (void)initUI{
    [self setScrollViewContentInsetAdjustmentNever:self.myScrollView];
    self.topConstraint.constant = IPHONE_NAVIGATION_BAR_HEIGHT+IPHONE_STATUS_BAR_HEIGHT;
    _myCardView.layer.cornerRadius = 6;
    [_myTopView setCornerRadius:6 rectCorner:UIRectCornerTopLeft | UIRectCornerTopRight];
    [_myCardView setLayerShadow:[UIColor colorWithRGB:0x040000 alpha:0.05f] offset:CGSizeMake(0, 7.5) radius:6];
}

- (void)updateUI{
    NSString *value = nil;
    NSString *str = [SecureData dataToHexString:self.info.data];
    if (str.length > 64) {
        str = [str substringFromIndex:str.length - 64];
        BigNumber *bg = [BigNumber bigNumberWithHexString:[NSString stringWithFormat:@"0x%@",str]];
        value = bg.decimalString;
    }
    if (value.length > 0) {
        _amountLabel.text = [NSString stringWithFormat:@"%@ OCN",[value decimalNumberByDividing:DecimalNumberTenPower18]];
    }else{
        _amountLabel.text = [NSString stringWithFormat:@"%@ ETH",[self.info.value.decimalString decimalNumberByDividing:DecimalNumberTenPower18]];
    }
    
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:self.info.timestamp];
    NSString *dateStr = [self.dateFormatter stringFromDate:date];
    
    self.sendHashValueLabel.text = self.info.fromAddress.checksumAddress;
    self.receiveHashValueLabel.text = self.info.toAddress.checksumAddress;
    self.costLabel.text = self.info.gasUsed.decimalString;
    [self.transacationHashValue setTitle:self.info.transactionHash.hexString forState:UIControlStateNormal];
    self.blockLabel.text = [NSString stringWithFormat:@"%ld",self.info.blockNumber];
    self.timeLabel.text =  dateStr;
    self.QRCodeImageView.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:self.info.transactionHash.hexString imageViewWidth:_QRCodeImageView.bounds.size.width];

}

- (IBAction)readTransactionCodeAction:(UIButton *)sender {
    BasicWebViewController *webVC = [[BasicWebViewController alloc]init];
    webVC.URLString = [NSString stringWithFormat:@"https://ropsten.etherscan.io/tx/%@",sender.titleLabel.text];
    [self.navigationController pushViewController:webVC animated:YES];
}

- (IBAction)copyURLAction:(id)sender {
    [UIPasteboard generalPasteboard].string = self.info.transactionHash.hexString;
    [self dispalyText:@"已复制"];
}

- (NSDateFormatter *)dateFormatter{
    if (!_dateFormatter) {
        _dateFormatter = [[NSDateFormatter alloc] init];
        [_dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
        [_dateFormatter setLocale:[NSLocale systemLocale]];
        [_dateFormatter setDateFormat:@"dd/MM/YYYY HH:mm:ss"];
    }
    return _dateFormatter;
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
