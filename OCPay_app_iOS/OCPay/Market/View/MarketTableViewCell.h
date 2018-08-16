//
//  MarketTableViewCell.h
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/6.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MarketModel.h"

typedef void(^CollectionBlock)(MarketModel *tokenData);

@interface MarketTableViewCell : UITableViewCell
@property (nonatomic, strong) MarketModel *tokenData;
@property (nonatomic, strong) ExchangeModel *exchangeData;
@property (nonatomic, copy) CollectionBlock collectionBlock;
@end
