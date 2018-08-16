//
//  MarketModel.m
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/6.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import "MarketModel.h"

@implementation MarketModel

+ (NSDictionary *)modelContainerPropertyGenericClass {
    return @{@"child" : MarketModel.class,
             };
}

@end


@implementation ExchangeModel

@end


@implementation HistoryModel

@end
