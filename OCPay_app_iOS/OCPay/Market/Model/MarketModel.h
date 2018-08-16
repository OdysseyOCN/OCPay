//
//  MarketModel.h
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/6.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import <Foundation/Foundation.h>


/**
 市场排序规则
 
 - MarketSortingType_A:  最新价降序
 - MarketSortingType_B: 最新价升序
 - MarketSortingType_C: 量降序
 - MarketSortingType_D: 量升序
 - MarketSortingType_E: 涨跌降序
 - MarketSortingType_F: 涨跌升序
 */
typedef NS_ENUM(NSUInteger, MarketSortingType) {
    MarketSortingType_A   = 1,
    MarketSortingType_B,
    MarketSortingType_C,
    MarketSortingType_D,
    MarketSortingType_E,
    MarketSortingType_F
};


@interface MarketModel : NSObject
@property (nonatomic, copy) NSString *ID;
@property (nonatomic, copy) NSString *exchange_name;
@property (nonatomic, copy) NSString *token;
@property (nonatomic, copy) NSString *value;
@property (nonatomic, copy) NSString *currency;
@property (nonatomic, copy) NSString *close;
@property (nonatomic, copy) NSString *degree;
@property (nonatomic, copy) NSString *vol;
@property (nonatomic, copy) NSString *vol_format;
@property (nonatomic, copy) NSString *type;
@property (nonatomic) BOOL collect_status;
@property (nonatomic, strong) NSArray <MarketModel*>*child;
@property (nonatomic) BOOL show;
@property (nonatomic) BOOL favorite;
@property (nonatomic, copy) NSString *index;
@end


@interface ExchangeModel : NSObject
@property (nonatomic, copy) NSString *exchange_name;
@property (nonatomic, copy) NSString *pair;
@property (nonatomic, copy) NSString *vol_format;
@property (nonatomic, copy) NSString *icon;
@end


@interface HistoryModel : NSObject
@property (nonatomic, copy) NSString *token;
@end

