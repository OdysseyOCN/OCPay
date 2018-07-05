//
//  AboutUsViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/20.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "AboutUsViewController.h"
#import "BasicWebViewController.h"

@interface AboutUsViewController ()
@property (weak, nonatomic) IBOutlet UILabel *versionLabel;

@end

@implementation AboutUsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.tableView.rowHeight = UITableViewAutomaticDimension;
    self.tableView.estimatedRowHeight = 120;
    self.tableView.tableFooterView = [UIView new];
    self.versionLabel.text = [NSString stringWithFormat:@"V%@",UIApplication.sharedApplication.appVersion];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSString *url = nil;
    if (indexPath.row == 1) {
        url = [NSString stringWithFormat:@"%@OCPayTermsofService.html",H5BaseURLPrefix];
    }else if (indexPath.row == 2){
        url = [NSString stringWithFormat:@"%@OCPayPrivacyPolicy.html",H5BaseURLPrefix];
    }else if (indexPath.row == 3){
        [self checkVersion];
    }
    if (url.length > 0) {
        BasicWebViewController *webVC = [[BasicWebViewController alloc]init];
        webVC.URLString = url;
        [self.navigationController pushViewController:webVC animated:YES];
    }
}

- (void)checkVersion{
    
    [NetWorkManager PostWithURL:@"/api/ocpay/token/v1/get-last-version" parameters:nil success:^(__kindof NSDictionary *data) {
        NSString *lastVersion = data[@"data"];
        NSComparisonResult result = [AppVersion compare:lastVersion options:NSNumericSearch];
        if (result == NSOrderedDescending || result == NSOrderedSame){
            [self dispalyConfirmText:@"No update available"];
        }else{
            [self dispalyConfirmText:@"have update available"];
        }
        
    } failure:^(NSError *error) {
    }];
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
