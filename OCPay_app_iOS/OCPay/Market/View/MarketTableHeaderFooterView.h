//
//  MarketTableHeaderFooterView.h
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/6.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MarketModel.h"

typedef NS_ENUM(NSUInteger, MarketTableHeaderCallbackType) {
    MarketTableHeaderCallbackType_show,
    MarketTableHeaderCallbackType_favorite,
};

typedef void(^MarketTableHeaderCallback)(MarketTableHeaderCallbackType callbackType, MarketModel *headerData);

@interface MarketTableHeaderFooterView : UITableViewHeaderFooterView
@property (nonatomic, strong) MarketModel *headerData;
@property (nonatomic, copy) MarketTableHeaderCallback callback;

@end
