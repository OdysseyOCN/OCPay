//
//  BasicViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/19.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "BasicViewController.h"

@interface BasicViewController() <YYTextKeyboardObserver>
@end

@implementation BasicViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.automaticallyAdjustsScrollViewInsets = NO;
}

- (void)setNeverAdjustContentInserScrollView:(UIScrollView *)neverAdjustContentInserScrollView{
    _neverAdjustContentInserScrollView = neverAdjustContentInserScrollView;
    if (@available(iOS 11.0, *)) {
        [neverAdjustContentInserScrollView setContentInsetAdjustmentBehavior:UIScrollViewContentInsetAdjustmentNever];
    }
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    if (self.neverAdjustContentInserScrollView) {
        [(BasicNavigationController*)self.navigationController setNavigationBarTransparent];
    }
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    if (self.neverAdjustContentInserScrollView) {
        [(BasicNavigationController*)self.navigationController reverseNavigationBar];
    }
}

- (YYTextKeyboardManager *)keyboardmanager{
    if (!_keyboardmanager) {
        _keyboardmanager = [YYTextKeyboardManager defaultManager];
    }
    return _keyboardmanager;
}

- (void)dealloc{
    [_keyboardmanager removeObserver:self];
    NSLog(@"%@ 已释放",NSStringFromClass(self.class));
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
