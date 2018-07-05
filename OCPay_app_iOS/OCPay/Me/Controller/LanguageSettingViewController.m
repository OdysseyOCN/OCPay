//
//  LanguageSettingViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/20.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "LanguageSettingViewController.h"

@interface LanguageSettingTableCell : UITableViewCell

@end

@implementation LanguageSettingTableCell
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    self.accessoryType = selected ? UITableViewCellAccessoryCheckmark : UITableViewCellAccessoryNone;
}
@end


@interface LanguageSettingViewController ()
@end

@implementation LanguageSettingViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    [self.navigationItem.leftBarButtonItem setCustomStyle:UIBarButtonItemCustomStyle_Close];
}

- (void)viewDidLayoutSubviews{
    [super viewDidLayoutSubviews];
    NSNumber *type = [[NSUserDefaults standardUserDefaults] objectForKey:BaseLegalCurrencyType];
    LegalCurrencyType legalCurrencyType = type.integerValue;
    [self.tableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:legalCurrencyType inSection:0] animated:NO scrollPosition:UITableViewScrollPositionTop];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return 10;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    return 0.01;
}

- (IBAction)doneAction:(id)sender {
    WalletManager.baseLegalCurrencyType = self.tableView.indexPathForSelectedRow.row;
    [self.navigationController popViewControllerAnimated:YES];
}
@end
