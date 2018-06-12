//
//  TransactionRecordTableCell.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/6.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "TransactionRecordTableCell.h"
@interface TransactionRecordTableCell()
@property (nonatomic, strong) NSDateFormatter* dateFormatter;
@property (weak, nonatomic) IBOutlet UIImageView *myIconImageView;
@property (weak, nonatomic) IBOutlet UILabel *myNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *myDateLabel;
@property (weak, nonatomic) IBOutlet UILabel *myAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *myTransactionStateLabel;
@end

@implementation TransactionRecordTableCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    
    // Configure the view for the selected state
}

- (NSDateFormatter *)dateFormatter{
    if (!_dateFormatter) {
        _dateFormatter = [[NSDateFormatter alloc] init];
        [_dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
        [_dateFormatter setLocale:[NSLocale systemLocale]];
        [_dateFormatter setDateFormat:@"dd/MM/YYYY"];
    }
    return _dateFormatter;
}


- (void)setInfo:(TransactionInfo *)info{
    _info = info;
    
    BOOL isContractTransaction = NO;
    NSString *value = nil;
    NSString *str = [SecureData dataToHexString:info.data];
    if (str.length > 64) {
        str = [str substringFromIndex:str.length - 64];
        BigNumber *bg = [BigNumber bigNumberWithHexString:[NSString stringWithFormat:@"0x%@",str]];
        value = bg.decimalString;
        isContractTransaction = YES;
    }
    
    BOOL isSend = NO;
    if ([_info.fromAddress.checksumAddress isEqualToString:self.wallet.address]) {
        isSend = YES;
    }
    
    if ([[NSString stringWithFormat:@"%lx",[BigNumber bigNumberWithHexString:_info.toAddress.checksumAddress].integerValue] isEqualToString:OCNAddress]) {
    }
    
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:info.timestamp];
    NSString *dateStr = [self.dateFormatter stringFromDate:date];
    
    _myDateLabel.text = dateStr;
    _myNameLabel.text = info.transactionHash.hexString;
    _myTransactionStateLabel.hidden = info.txreceiptStatus == 1 ?: NO;
    _myAmountLabel.textColor = isSend ? [UIColor colorWithRGB:0xD6556D] : [UIColor colorWithRGB:0x2EC424];
    _myAmountLabel.text = [NSString stringWithFormat:@"%@%@ %@",
                           isSend ? @"-" : @"+",
                           isContractTransaction ? [value decimalNumberByDividing:DecimalNumberTenPower18] : [info.value.decimalString decimalNumberByDividing:DecimalNumberTenPower18],
                           isContractTransaction ? @"OCN" : @"ETH"
                           ];
}

@end
