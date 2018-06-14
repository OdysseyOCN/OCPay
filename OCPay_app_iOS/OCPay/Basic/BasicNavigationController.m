//
//  BasicNavigationController.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/19.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "BasicNavigationController.h"
#import "UIImage+TCMExtension.h"

@interface BasicNavigationController ()<UIGestureRecognizerDelegate>

@end

@implementation BasicNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self reverseNavigationBar];
}

- (void)setNavigationBarTransparent{
    [self.navigationBar setTintColor:[UIColor whiteColor]];
    [self.navigationBar setTitleTextAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:18],NSForegroundColorAttributeName:[UIColor whiteColor]}];
    [self.navigationBar setShadowImage:[UIImage imageNamed:@"TransparentPixel"]];
    [self.navigationBar setBackgroundImage:[UIImage imageNamed:@"TransparentPixel"] forBarMetrics:UIBarMetricsDefault];
}

- (void)reverseNavigationBar{
    [self.navigationBar setTintColor:UIColorHex(0x38525F)];
    [self.navigationBar setTitleTextAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:18],NSForegroundColorAttributeName:UIColorHex(0x38525F)}];
    [self.navigationBar setShadowImage:nil];
    [self.navigationBar setBackgroundImage:[UIImage ImageWithColor:[UIColor whiteColor]] forBarMetrics:UIBarMetricsDefault];
}

-(UIViewController *)childViewControllerForStatusBarStyle{
    return self.visibleViewController;
}

-(void)pushViewController:(UIViewController *)viewController animated:(BOOL)animated{
    if (self.childViewControllers.count > 0) {
        UIBarButtonItem *barButtonItem = [[UIBarButtonItem alloc]initWithImage:[[UIImage imageNamed:@"back_black"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(popViewControllerWhenHaveChildVC)];
        viewController.navigationItem.leftBarButtonItem = barButtonItem;
        viewController.hidesBottomBarWhenPushed = YES;
        self.interactivePopGestureRecognizer.delegate = self;
    }
    [super pushViewController:viewController animated:animated];
}

- (void)popViewControllerWhenHaveChildVC{//栈内有视图控制器时 弹出
    if (self.childViewControllers.count>1) {
        UIViewController *vc = self.topViewController;
        if ([vc respondsToSelector:@selector(popAction)]) {
            [vc popAction];
        }else{
            [self popViewControllerAnimated:YES];
        }
    }
}

-(BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer{//实现代理方法
    if (self.childViewControllers.count>1) {
        UIViewController *vc = self.topViewController;
        if ([vc respondsToSelector:@selector(popAction)]) {
            [vc popAction];
            return false;
        }
    }
    return self.childViewControllers.count > 1;// 判断如果不是根控制器 才需要pop返回手势
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
