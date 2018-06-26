//
//  RootViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/15.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "RootViewController.h"
#import "LaunchWalletViewController.h"
#import "BasicNavigationController.h"

@interface RootViewController ()

@end

@implementation RootViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    if (WalletManager.share.wallets.count > 0) {
        [UIApplication sharedApplication].delegate.window.rootViewController = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateInitialViewController];
        
    }else{
        LaunchWalletViewController *vc = [UIViewController instantiateViewControllerWithIdentifier:@"LaunchWalletViewController" inStoryboard:@"Wallet"];
        BasicNavigationController *nvc = [[BasicNavigationController alloc]initWithRootViewController:vc];
        nvc.navigationBar.hidden = YES;
        [UIApplication sharedApplication].delegate.window.rootViewController = nvc;
    }
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
