//
//  MarketTableHeaderFooterView.m
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/6.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import "MarketTableHeaderFooterView.h"
#import "MarketService.h"

@interface MarketTableHeaderFooterView ()

@property (weak, nonatomic) IBOutlet UILabel *numberLabel;
@property (weak, nonatomic) IBOutlet UILabel *tokenLabel;
@property (weak, nonatomic) IBOutlet UILabel *tokenAmountLabel;
@property (weak, nonatomic) IBOutlet UIButton *favoriteButton;
@property (weak, nonatomic) IBOutlet UILabel *lastPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *volumeLabel;
@property (weak, nonatomic) IBOutlet UIButton *showMoreButton;
@property (weak, nonatomic) IBOutlet UILabel *exchangeLabel;
@property (weak, nonatomic) IBOutlet UIImageView *bestImageView;
@property (weak, nonatomic) IBOutlet UIView *myContentView;

@end


@implementation MarketTableHeaderFooterView

- (void)awakeFromNib{
    [super awakeFromNib];
    self.exchangeLabel.layer.cornerRadius = 3;
    self.exchangeLabel.layer.masksToBounds = YES;
}

- (IBAction)showAction:(id)sender {
    self.showMoreButton.selected = !self.showMoreButton.selected;
    self.headerData.show = self.showMoreButton.selected;
    if (self.callback) {
        self.callback(MarketTableHeaderCallbackType_show, self.headerData);
    }
}

- (IBAction)favoriteAction:(UIButton *)sender {
    if (!WalletManager.share.account.registered) {
        [self.viewController pushViewControllerWithIdentifier:@"SignInViewController_SignIn" inStoryboard:@"Main"];
        return;
    }
    [self.viewController dispalyLoading:nil];
    [MarketService marketFavoriteToken:self.headerData.token exchange:!_bestImageView ? nil : _bestImageView.image ? nil : self.headerData.exchange_name success:^(__kindof NSObject *data) {
        [self.viewController hideLoading:YES];
        self.headerData.collect_status = !self.headerData.collect_status;
        self.headerData = self.headerData;
        if (self.callback) {
            self.callback(MarketTableHeaderCallbackType_favorite, self.headerData);
        }
    } failure:^(NSError *error) {
        [self.viewController dispalyText:error.domain];
    }];
}

- (void)setHeaderData:(MarketModel *)headerData{
    _headerData = headerData;
    _showMoreButton.selected = headerData.show;
    _myContentView.backgroundColor = headerData.show ? UIColorHex(0xf4f4f4) : UIColor.whiteColor;
    _favoriteButton.selected = headerData.favorite;
    _numberLabel.text = headerData.index;
    _favoriteButton.selected = headerData.collect_status;
    _tokenLabel.text = headerData.token;
    _tokenAmountLabel.text = [NSString stringWithFormat:@"$%@",headerData.value];
    _volumeLabel.text = [NSString stringWithFormat:@"$%@",headerData.vol_format];
    _bestImageView.image = [headerData.type isEqualToString:@"1"] ? [UIImage imageNamed:@"icon-best price"] : nil;
    if ([headerData.degree hasPrefix:@"-"]) {
        _exchangeLabel.text = [NSString stringWithFormat:@"%@%%",headerData.degree];
        _exchangeLabel.backgroundColor = UIColorHex(0xf96768);
    }else{
        _exchangeLabel.text = [NSString stringWithFormat:@"+%@%%",headerData.degree];
        _exchangeLabel.backgroundColor = UIColorHex(0x3cc3a6);
    }
    NSString *text = [NSString stringWithFormat:@"$%@(%@)",headerData.close,headerData.exchange_name];
    NSMutableAttributedString *titleAttributedStr = [[NSMutableAttributedString alloc] initWithString:text];
    [titleAttributedStr addAttributes:@{NSForegroundColorAttributeName:TintColor,NSFontAttributeName:[UIFont systemFontOfSize:16]} range:NSMakeRange(0,titleAttributedStr.length)];
    [titleAttributedStr addAttributes:@{NSForegroundColorAttributeName:UIColorHex(0xa7a7a7),NSFontAttributeName:[UIFont systemFontOfSize:12]} range:NSMakeRange(headerData.close.length+1,titleAttributedStr.length-headerData.close.length-1)];
    _lastPriceLabel.attributedText = titleAttributedStr;
}

@end
