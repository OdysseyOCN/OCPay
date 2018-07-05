//
//  SystemSettingsViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/15.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "SystemSettingsViewController.h"

@interface SystemSettingsViewController ()
@property (weak, nonatomic) IBOutlet UISwitch *mySwitchButton;

@end

@implementation SystemSettingsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.navigationItem.leftBarButtonItem setCustomStyle:UIBarButtonItemCustomStyle_Close];
    BOOL use = [UD boolForKey:UseTouchIDKey];
    self.mySwitchButton.on = use;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return 10;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    return 0.01;
}

- (IBAction)switchAction:(UISwitch *)sender {
    sender.on = !sender.on;
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    [ud setBool:sender.on forKey:UseTouchIDKey];
    [ud synchronize];
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
