//
//  BasicTabBarController.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/19.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "BasicTabBarController.h"

@interface BasicTabBarController ()

@end

@implementation BasicTabBarController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.childViewControllers enumerateObjectsUsingBlock:^(__kindof UIViewController * _Nonnull childController, NSUInteger idx, BOOL * _Nonnull stop) {
        [childController.tabBarItem setImage:[childController.tabBarItem.image imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
        [childController.tabBarItem setSelectedImage:[childController.tabBarItem.image imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
        [childController.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:UIColorHex(0x282828)} forState:UIControlStateNormal];
        [childController.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:UIColorHex(0x282828)} forState:UIControlStateSelected];
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
