//
//  MessageDetailViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/29.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "MessageDetailViewController.h"

@interface MessageDetailViewController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (weak, nonatomic) IBOutlet UILabel *contentLabel;
@property (nonatomic, strong) NSDateFormatter* dateFormatter;

@end

@implementation MessageDetailViewController

- (NSDateFormatter *)dateFormatter{
    if (!_dateFormatter) {
        _dateFormatter = [[NSDateFormatter alloc] init];
        [_dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
        [_dateFormatter setLocale:[NSLocale systemLocale]];
        [_dateFormatter setDateFormat:@"dd/MM/YYYY"];
    }
    return _dateFormatter;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.message.read = YES;
    self.titleLabel.text = self.message.notification_title;
    self.contentLabel.text = self.message.notification_describle;
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:self.message.notification_timestamp.integerValue];
    NSString *dateStr = [self.dateFormatter stringFromDate:date];
    self.timeLabel.text = dateStr;
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
