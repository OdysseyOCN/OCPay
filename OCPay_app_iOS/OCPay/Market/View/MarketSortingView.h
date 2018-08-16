//
//  MarketSrotingView.h
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/7.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MarketModel.h"

typedef void(^MarketSortingCallback)(MarketSortingType sortingType);

@interface MarketSortingView : UIView

@property (nonatomic, copy) MarketSortingCallback sortingCallback;

- (void)setIndex:(NSInteger)index sortType:(MarketSortingType)type;

@end
