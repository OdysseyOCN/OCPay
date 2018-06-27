//
//  HomeCollectionViewCell.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/28.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "HomeCollectionViewCell.h"
#import "UIView+RectCorner.h"
#import "MyPageView.h"

@interface HomeCollectionViewCell ()<MyPageViewProtocol>

///header
@property (weak, nonatomic) IBOutlet UIView *myCardView;
@property (weak, nonatomic) IBOutlet UIView *topShadow;
@property (weak, nonatomic) IBOutlet UIView *bottomShadow;
@property (weak, nonatomic) IBOutlet UILabel *walletNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *walletAddressLabel;
@property (weak, nonatomic) IBOutlet UILabel *totalAssetsLabel;
@property (weak, nonatomic) IBOutlet UILabel *toatalLegalCurrencyAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *backupTagLabel;


//小程序模块
@property (weak, nonatomic) IBOutlet UILabel *myModuleNameLabel;
@property (weak, nonatomic) IBOutlet UIImageView *myModuleImageView;

//轮播
@property (weak, nonatomic) IBOutlet MyPageView *myPageView;

//广告
@property (weak, nonatomic) IBOutlet UIImageView *myAdvertImageView;


//账户
@property (weak, nonatomic) IBOutlet UIImageView *accountImageView;
@property (weak, nonatomic) IBOutlet UILabel *accountNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *accountAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *accountDollarLabel;
@end

@implementation HomeCollectionViewCell

- (IBAction)showTokensAction:(id)sender {
    if (self.cellCallback) {
        self.cellCallback(HeadCellCallbackType_showTokens,nil);
    }
}

- (IBAction)scanQRCodeAction:(id)sender {
    if (self.cellCallback) {
        self.cellCallback(HeadCellCallbackType_ScanQRCode,nil);
    }
}

- (IBAction)showAccount:(id)sender{
    if (self.cellCallback) {
        self.cellCallback(HeadCellCallbackType_ShowAccount,nil);
    }
}

- (IBAction)showTransactionRecord:(id)sender{
    if (self.cellCallback) {
        self.cellCallback(HeadCellCallbackType_Record,nil);
    }
}

- (IBAction)sendTransaction:(id)sender{
    if (self.cellCallback) {
        self.cellCallback(HeadCellCallbackType_Send,nil);
    }
}

- (void)awakeFromNib{
    [super awakeFromNib];
    self.myPageView.delegate = self;
    self.myPageView.openTimer = YES;
    self.backupTagLabel.layer.borderWidth = 1;
    self.backupTagLabel.hidden = YES;
}

- (void)layoutSubviews{
    [super layoutSubviews];
    _myCardView.layer.cornerRadius = 6;
    [_topShadow setCornerRadius:6 rectCorner:UIRectCornerTopLeft | UIRectCornerTopRight];
    [_bottomShadow setCornerRadius:6 rectCorner:UIRectCornerBottomLeft | UIRectCornerBottomRight];
    [_myCardView setLayerShadow:[UIColor colorWithRGB:0x040000 alpha:0.25f] offset:CGSizeMake(0, 7.5) radius:6];
    _backupTagLabel.layer.cornerRadius = _backupTagLabel.height*.5;
}

- (void)setSectionData:(HomeSectionViewModel *)sectionData{
    _sectionData = sectionData;
    if (sectionData.style != HomeCollectionViewCellStyle_page) return;
    NSMutableArray *arr = [NSMutableArray array];
    [sectionData.sectionData.advertisments enumerateObjectsUsingBlock:^(HomeAdvertDataModel * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        [arr addObject:obj.showImg];
    }];
    _myPageView.imageURLStrings = arr;
}

- (void)setRowData:(HomeCellViewModel *)rowData{
    _rowData = rowData;
    switch (rowData.style) {
            case HomeCollectionViewCellStyle_header:
        {
            self.walletNameLabel.text = WalletManager.share.defaultWallet.name;
            self.walletAddressLabel.text = WalletManager.share.defaultWallet.address;
            self.totalAssetsLabel.text = [NSString stringWithFormat:@"%.4f", WalletManager.share.defaultWallet.totalBaseTokenAmount.floatValue];
            self.toatalLegalCurrencyAmountLabel.text = [NSString stringWithFormat:@"≈ %.2f%@", WalletManager.share.defaultWallet.toatalLegalCurrencyAmount.doubleValue,WalletManager.legalCurrencyTypeSymbol];
            if (WalletManager.share.defaultWallet.isObserver) {
                self.backupTagLabel.hidden = NO;
                self.backupTagLabel.textColor = UIColorHex(#ffffff);
                self.backupTagLabel.layer.borderColor = UIColorHex(#ffffff).CGColor;
                self.backupTagLabel.text = @"  Watching  ";
            }else if(WalletManager.share.defaultWallet.noBackup){
                self.backupTagLabel.hidden = NO;
                self.backupTagLabel.textColor = UIColorHex(#43ADDC);
                self.backupTagLabel.layer.borderColor = UIColorHex(#43ADDC).CGColor;
                self.backupTagLabel.text = @"  Please Backup  ";
            }else{
                self.backupTagLabel.hidden = YES;
            }
        }
            break;
            case HomeCollectionViewCellStyle_module:
        {
            [self.myModuleImageView setImageURL:[NSURL URLWithString:rowData.advertData.showImg]];
            [self.myModuleNameLabel setText:rowData.advertData.title];
        }
            break;
            case HomeCollectionViewCellStyle_token:
        {
            self.accountNameLabel.text = self.rowData.tokenData.tokenTypeString;
            self.accountAmountLabel.text = [NSString stringWithFormat:@"%@",self.rowData.tokenData.tokenAmount];
            self.accountDollarLabel.text = [NSString stringWithFormat:@"≈ %.2f%@",self.rowData.tokenData.baseLegalCurrencyAmount.doubleValue,WalletManager.legalCurrencyTypeSymbol];
            self.accountImageView.image = [UIImage imageNamed:self.rowData.tokenData.tokenType == TokenType_ETH ? @"ETH logo" : @"OCN logo Copy"];
        }
            break;
            case HomeCollectionViewCellStyle_advert:
        {
            [self.myAdvertImageView setImageURL:[NSURL URLWithString:rowData.advertData.showImg]];
        }
            break;

        default:
            break;
    }
}

-(void)myPageView:(MyPageView*)myPageView clickedOnIndex:(NSInteger)index image:(UIImage*)image{
    if (self.cellCallback) {
        self.rowData.advertData =  self.sectionData.sectionData.advertisments[index];
        self.cellCallback(HomeCollectionCellCallbackType_choosePage,self.rowData);
    }
}
@end
