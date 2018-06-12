//
//  BasicNavigationController.h
//  OCPay
//
//  Created by 何自梁 on 2018/5/19.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <UIKit/UIKit.h>
/**
 此返回协议方法,用于处理自定义返回方式（如返回动画，或 返回到某一指定页面）
 */
@protocol BasicNavigationControllerDelegate <NSObject>

@optional

- (void)popAction;

@end


@interface BasicNavigationController : UINavigationController

- (void)setNavigationBarTransparent;

- (void)reverseNavigationBar;

@end
