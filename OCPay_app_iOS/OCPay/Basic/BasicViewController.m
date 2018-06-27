//
//  BasicViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/19.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "BasicViewController.h"

@interface BasicViewController ()
@property (nonatomic, strong) UIScrollView *myScrollView;
@end

@implementation BasicViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.automaticallyAdjustsScrollViewInsets = NO;
}

- (void)setScrollViewContentInsetAdjustmentNever:(UIScrollView*)scrollView{
    self.myScrollView = scrollView;
    if (@available(iOS 11.0, *)) {
        [scrollView setContentInsetAdjustmentBehavior:UIScrollViewContentInsetAdjustmentNever];
    }
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    if (self.myScrollView) {
        [(BasicNavigationController*)self.navigationController setNavigationBarTransparent];
    }
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    if (self.myScrollView) {
        [(BasicNavigationController*)self.navigationController reverseNavigationBar];
    }
}

- (YYTextKeyboardManager *)manager{
    if (!_manager) {
        _manager = [YYTextKeyboardManager defaultManager];
    }
    return _manager;
}

- (UIToolbar *)textFieldAccessoryView{
    if (!_textFieldAccessoryView) {
        _textFieldAccessoryView = [[UIToolbar alloc]init];
        [_textFieldAccessoryView sizeToFit];
        [_textFieldAccessoryView setBarStyle:UIBarStyleDefault];
        UIBarButtonItem *btnSpace =[[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:self action:nil];
        UIBarButtonItem * completeButton =[[UIBarButtonItem alloc] initWithTitle:@"完成" style:UIBarButtonItemStyleDone target:self action:@selector(keyboardFinishAction)];
        NSArray *buttonsArray = [NSArray arrayWithObjects:btnSpace,completeButton,nil];
        [_textFieldAccessoryView setItems:buttonsArray];
    }
    return _textFieldAccessoryView;
}

- (void)keyboardFinishAction{
    [self.view endEditing:YES];
}

- (void)dealloc{
    [_manager removeObserver:self];
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
