//
//  MessageTableViewCell.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/29.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "MessageTableViewCell.h"

@interface MessageTableViewCell()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (weak, nonatomic) IBOutlet UILabel *contentLabel;
@property (weak, nonatomic) IBOutlet UILabel *redPointLabel;
@property (nonatomic, strong) NSDateFormatter* dateFormatter;
@end

@implementation MessageTableViewCell

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
        [_dateFormatter setDateFormat:@"YYYY/MM/dd"];
    }
    return _dateFormatter;
}

- (void)setMessage:(MessageModel *)message{
    _message = message;
    self.titleLabel.text = message.notification_title;
    self.contentLabel.text = message.notification_describle;
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:self.message.notification_timestamp.integerValue];
    NSString *dateStr = [self.dateFormatter stringFromDate:date];
    self.timeLabel.text = dateStr;

    self.redPointLabel.hidden = message.read;
}

@end
