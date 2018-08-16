//
//  MarketTableViewCell.m
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/6.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import "MarketTableViewCell.h"
#import "MarketService.h"

@interface MarketTableViewCell ()
///////////////////////代币/////////////////////
@property (weak, nonatomic) IBOutlet UILabel *exchangeLabel;
@property (weak, nonatomic) IBOutlet UILabel *changeLabel;
@property (weak, nonatomic) IBOutlet UILabel *lastPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *volumeLabel;
@property (weak, nonatomic) IBOutlet UIButton *favoriteButton;

////////////////////////交易所//////////////////
@property (weak, nonatomic) IBOutlet UIImageView *logoImageView;
@property (weak, nonatomic) IBOutlet UILabel *exchangeNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *tokenPairLabel;
@property (weak, nonatomic) IBOutlet UILabel *exchangeVolumeLabel;
@end


@implementation MarketTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    self.logoImageView.layer.cornerRadius =  self.logoImageView.height * .5;
    self.logoImageView.layer.masksToBounds = YES;
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    // Configure the view for the selected state
}

- (IBAction)favoriteAction:(UIButton *)sender {
    if (!WalletManager.share.account.registered) {
        [self.viewController pushViewControllerWithIdentifier:@"SignInViewController_SignIn" inStoryboard:@"Main"];
        return;
    }
    [self.viewController dispalyLoading:nil];
    [MarketService marketFavoriteToken:self.tokenData.token exchange:self.tokenData.exchange_name success:^(__kindof NSObject *data) {
        [self.viewController hideLoading:YES];
        if (self.collectionBlock) {
            self.collectionBlock(self.tokenData);
        }
        self.tokenData.collect_status = !self.tokenData.collect_status;
        self.tokenData = self.tokenData;
    } failure:^(NSError *error) {
        [self.viewController dispalyText:error.domain];
    }];
}

- (void)setTokenData:(MarketModel *)tokenData{
    _tokenData = tokenData;
    _exchangeLabel.text = tokenData.exchange_name;
    _lastPriceLabel.text = [NSString stringWithFormat:@"$%@",tokenData.close];
    _volumeLabel.text = [NSString stringWithFormat:@"$%@",tokenData.vol_format];
    if ([tokenData.degree hasPrefix:@"-"]) {
        _changeLabel.text = [NSString stringWithFormat:@"%@%%",tokenData.degree];
    }else{
        _changeLabel.text = [NSString stringWithFormat:@"+%@%%",tokenData.degree];
    }
    _favoriteButton.selected = tokenData.collect_status;
}

- (void)setExchangeData:(ExchangeModel *)exchangeData{
    _exchangeData = exchangeData;
    [_logoImageView setImageWithURL:[NSURL URLWithString:exchangeData.icon] placeholder:_logoImageView.image];
    [_exchangeNameLabel setText:exchangeData.exchange_name];
    [_tokenPairLabel setText:exchangeData.pair];
    [_exchangeVolumeLabel setText:exchangeData.vol_format];
}

@end
