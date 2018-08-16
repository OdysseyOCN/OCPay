//
//  MarketViewController.h
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/4.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import "BasicViewController.h"

@interface MarketViewController : BasicViewController
@property (nonatomic, strong) NSArray *titles;
@property (nonatomic, copy) NSString *searchText;
@property (nonatomic) BOOL isSearch; //搜索页面
@end
