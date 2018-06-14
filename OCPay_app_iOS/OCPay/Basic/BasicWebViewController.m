//
//  BasicWebViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/7.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "BasicWebViewController.h"
#import <WebKit/WKWebView.h>
#import <WebKit/WKNavigationDelegate.h>
#import "UIImage+TCMExtension.h"

@interface BasicWebViewController ()<WKNavigationDelegate>
@property (nonatomic, strong) WKWebView *wkWebView;
@property (nonatomic, strong) UIProgressView *progressView;//设置加载进度条
@end

@implementation BasicWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.view addSubview:self.wkWebView];
    [self.view addSubview:self.progressView];
    [self.wkWebView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:self.URLString]]];
    [_wkWebView addObserver:self forKeyPath:@"estimatedProgress" options:NSKeyValueObservingOptionNew context:nil];
    [_wkWebView addObserver:self forKeyPath:@"title" options:NSKeyValueObservingOptionNew context:NULL];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar setShadowImage:[UIImage imageNamed:@"TransparentPixel"]];
    [self.navigationController.navigationBar setBackgroundImage:[UIImage imageWithColor:[UIColor colorWithRGB:0xffffff]] forBarMetrics:UIBarMetricsDefault];
}
- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [(BasicNavigationController*)self.navigationController reverseNavigationBar];
}


- (void)popAction{
    if ([_wkWebView canGoBack]) {
        [_wkWebView goBack];
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
}

-(UIProgressView *)progressView{
    if (!_progressView) {
        _progressView = [[UIProgressView alloc] initWithProgressViewStyle:UIProgressViewStyleDefault];
        _progressView.frame = CGRectMake(0, IPHONE_STATUS_BAR_HEIGHT+IPHONE_NAVIGATION_BAR_HEIGHT, self.view.bounds.size.width, 2);
        _progressView.trackTintColor = [UIColor colorWithRGB:0xf3f3f3];
        _progressView.progressTintColor = [UIColor colorWithRGB:0x38525F];
    }
    return _progressView;
}

- (WKWebView *)wkWebView{
    if (!_wkWebView) {
        _wkWebView = [[WKWebView alloc]initWithFrame:self.view.bounds];
        _wkWebView.allowsBackForwardNavigationGestures = YES;
        _wkWebView.navigationDelegate = self;
    }
    return _wkWebView;
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSString *,id> *)change context:(void *)context {
    if (object == self.wkWebView){
        if ([keyPath isEqualToString:@"estimatedProgress"]) {
            self.progressView.progress = self.wkWebView.estimatedProgress;
            if (self.progressView.progress >= 1) {
                /*
                 *添加一个简单的动画，将progressView的Height变为1.4倍，在开始加载网页的代理中会恢复为1.5倍
                 *动画时长0.25s，延时0.3s后开始动画
                 *动画结束后将progressView隐藏
                 */
                __weak typeof (self)weakSelf = self;
                [UIView animateWithDuration:0.25f delay:0.3f options:UIViewAnimationOptionCurveEaseOut animations:^{
                    weakSelf.progressView.transform = CGAffineTransformMakeScale(1.0f, 1.4f);
                } completion:^(BOOL finished) {
                    weakSelf.progressView.hidden = YES;
                    [self.navigationController.navigationBar setShadowImage:[UIImage imageWithColor:Color_Line_NO_3 size:CGSizeMake(IPHONE_SCREEN_WIDTH, 0.5)]];
                }];
            }
        }else if ([keyPath isEqualToString:@"title"]){
            if (object == self.wkWebView){
                self.title = self.wkWebView.title;
            }else{
                [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
            }
        }else{
            [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
        }

    }else{
        [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
    }
}


//开始加载
- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(WKNavigation *)navigation {
    NSLog(@"开始加载网页");
    [self.navigationController.navigationBar setShadowImage:[UIImage imageNamed:@"TransparentPixel"]];
    //开始加载网页时展示出progressView
    self.progressView.hidden = NO;
    //开始加载网页的时候将progressView的Height恢复为1.5倍
    self.progressView.transform = CGAffineTransformMakeScale(1.0f, 1.5f);
    //防止progressView被网页挡住
    [self.view bringSubviewToFront:self.progressView];
}

//加载完成
- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    NSLog(@"加载完成");
    //加载完成后隐藏progressView
    self.progressView.hidden = YES;
    [self.navigationController.navigationBar setShadowImage:[UIImage imageWithColor:Color_Line_NO_3 size:CGSizeMake(IPHONE_SCREEN_WIDTH, 0.5)]];
}

//加载失败
- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation withError:(NSError *)error {
    NSLog(@"加载失败");
    //加载失败同样需要隐藏progressView
    self.progressView.hidden = YES;
    [self.navigationController.navigationBar setShadowImage:[UIImage imageWithColor:Color_Line_NO_3 size:CGSizeMake(IPHONE_SCREEN_WIDTH, 0.5)]];
}

- (void)dealloc {
    [self.wkWebView removeObserver:self forKeyPath:@"estimatedProgress"];
    [self.wkWebView removeObserver:self forKeyPath:@"title"];
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
