//
//  HomeViewModel.h
//  OCPay
//
//  Created by 何自梁 on 2018/5/28.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HomeDataModel.h"


/**
 模块类型

 - HomeCollectionViewCellStyle_header: 首页头
 - HomeCollectionViewCellStyle_module: 小程序
 - HomeCollectionViewCellStyle_page: 轮播
 - HomeCollectionViewCellStyle_advert: 广告
 - HomeCollectionViewCellStyle_account: 代币
 */
typedef NS_ENUM(NSUInteger, HomeCollectionViewCellStyle) {
    
    HomeCollectionViewCellStyle_header,
    HomeCollectionViewCellStyle_module,
    HomeCollectionViewCellStyle_page,
    HomeCollectionViewCellStyle_advert,
    HomeCollectionViewCellStyle_token,

};




@interface HomeCellViewModel : NSObject
@property (nonatomic) HomeCollectionViewCellStyle style;
@property (nonatomic) CGSize size;
@property (nonatomic, strong) HomeAdvertDataModel *advertData;
@property (nonatomic, strong) TokenModel *tokenData;
@property (nonatomic, strong) HomeModuleDataModel *sectionData;

- (instancetype)initWithStyle:(HomeCollectionViewCellStyle)style;
@end





@interface HomeSectionViewModel : NSObject
@property (nonatomic) BOOL hidden;
@property (nonatomic) CGFloat minimumLineSpacing;
@property (nonatomic) CGFloat minimumInteritemSpacing;
@property (nonatomic) UIEdgeInsets edgeInsets;
@property (nonatomic) HomeCollectionViewCellStyle style;
@property (nonatomic, strong) NSMutableArray <HomeCellViewModel*>*cellDatas;
@property (nonatomic, strong) HomeModuleDataModel *sectionData;

- (instancetype)initWithStyle:(HomeCollectionViewCellStyle)style sectionData:(HomeModuleDataModel*)sectionData;
@end





@interface HomeViewModel : NSObject

@property (nonatomic, strong) NSMutableArray <HomeSectionViewModel*>*sectionDatas;
@property (nonatomic, strong) HomeDataModel *sourceData;
@property (nonatomic, weak) UICollectionView *collectionView;
@property (nonatomic) BOOL hiddenAccountSection;

- (void)updateData;

@end
