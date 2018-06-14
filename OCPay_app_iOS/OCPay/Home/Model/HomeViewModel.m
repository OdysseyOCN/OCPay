//
//  HomeViewModel.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/28.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "HomeViewModel.h"

@implementation HomeViewModel

- (instancetype)init{
    if (self = [super init]){
        [self initData];
        [self setHiddenAccountSection:YES];
        [self reloadCollectionView];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadCollectionView) name:updateWalletInfo object:nil];
    }
    return self;
}

- (void)setSourceData:(HomeDataModel *)sourceData{
    _sourceData = sourceData;
    [self updateData];
}


- (void)updateData{
    [self initData];
    [self setHiddenAccountSection:_hiddenAccountSection];
    [self reloadCollectionView];
}

- (void)setHiddenAccountSection:(BOOL)hiddenAccountSection{
    _hiddenAccountSection = hiddenAccountSection;
    [self.sectionDatas enumerateObjectsUsingBlock:^(HomeSectionViewModel * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if (obj.style == HomeCollectionViewCellStyle_token){
            [self.sectionDatas enumerateObjectsUsingBlock:^(HomeSectionViewModel * _Nonnull obj1, NSUInteger idx1, BOOL * _Nonnull stop1) {
                if (obj1.style != HomeCollectionViewCellStyle_header){
                    obj1.hidden = (obj == obj1) ? hiddenAccountSection : !hiddenAccountSection;
                }
            }];
            *stop = true;
        }
    }];
    [self reloadCollectionView];
}

- (void)reloadCollectionView{
    [self.collectionView reloadData];
}

- (void)initData{
    [self.sectionDatas removeAllObjects];
    [self initSectionDatasWithStyle:HomeCollectionViewCellStyle_header sourceData:nil];
    for (HomeModuleDataModel *data in self.sourceData.homePageVos) {
        [self initSectionDatasWithStyle:data.type.integerValue sourceData:data];
    }
    [self initSectionDatasWithStyle:HomeCollectionViewCellStyle_token sourceData:nil];
}


- (void)initSectionDatasWithStyle:(HomeCollectionViewCellStyle)style sourceData:(HomeModuleDataModel*)sourceData{
    HomeSectionViewModel *data = [[HomeSectionViewModel alloc]initWithStyle:style sectionData:sourceData];
    [self.sectionDatas addObject:data];
}

- (NSMutableArray<HomeSectionViewModel *> *)sectionDatas{
    if(!_sectionDatas){
        _sectionDatas = [NSMutableArray array];
    }
    return _sectionDatas;
}

@end





@implementation HomeSectionViewModel
- (NSMutableArray<HomeCellViewModel *> *)cellDatas{
    if (!_cellDatas){
        _cellDatas = [NSMutableArray array];
    }
    return _cellDatas;
}

- (instancetype)initWithStyle:(HomeCollectionViewCellStyle)style sectionData:(HomeModuleDataModel*)sectionData{
    if (self = [super init]){
        self.style = style;
        self.sectionData = sectionData;
        switch (self.style) {
                case HomeCollectionViewCellStyle_header:
            {
                HomeCellViewModel *data = [[HomeCellViewModel alloc] initWithStyle:self.style];
                [self.cellDatas addObject:data];
            }
                break;
                case HomeCollectionViewCellStyle_token:
            {
                for (int i = 0; i < WalletManager.share.defaultWallet.tokens.count; i++) {
                    HomeCellViewModel *data = [[HomeCellViewModel alloc] initWithStyle:self.style];
                    data.tokenData = WalletManager.share.defaultWallet.tokens[i];
                    [self.cellDatas addObject:data];
                }
            }
                break;
//////////////////////////////////////////////////////////////////////////////////////////////////////////
                case HomeCollectionViewCellStyle_module:
            {
                self.minimumLineSpacing = 1;
                for (int i = 0; i < self.sectionData.advertisments.count; i++) {
                    HomeCellViewModel *data = [[HomeCellViewModel alloc] initWithStyle:self.style];
                    data.advertData = self.sectionData.advertisments[i];
                    [self.cellDatas addObject:data];
                }
            }
                break;
                case HomeCollectionViewCellStyle_page:
            {
                HomeCellViewModel *data = [[HomeCellViewModel alloc] initWithStyle:self.style];
                data.sectionData = self.sectionData;
                [self.cellDatas addObject:data];
            }
                break;
                case HomeCollectionViewCellStyle_advert:
            {
                for (int i = 0; i < self.sectionData.advertisments.count; i++) {
                    HomeCellViewModel *data = [[HomeCellViewModel alloc] initWithStyle:self.style];
                    data.advertData = self.sectionData.advertisments[i];
                    [self.cellDatas addObject:data];
                }
            }
        }
    }
    return self;
}
@end





@implementation HomeCellViewModel
- (instancetype)initWithStyle:(HomeCollectionViewCellStyle)style{
    if (self = [super init]){
        self.style = style;
        switch (self.style) {
                case HomeCollectionViewCellStyle_header:
            {
                self.size = CGSizeMake(IPHONE_SCREEN_WIDTH, 300);
            }
                break;
                case HomeCollectionViewCellStyle_module:
            {
                self.size = CGSizeMake((IPHONE_SCREEN_WIDTH-2)/3, (IPHONE_SCREEN_WIDTH-2)/3);
            }
                break;
                case HomeCollectionViewCellStyle_token:
            {
                self.size = CGSizeMake(IPHONE_SCREEN_WIDTH - 22 * 2, 64);
            }
                break;
                case HomeCollectionViewCellStyle_page:
            {
                self.size = CGSizeMake(IPHONE_SCREEN_WIDTH, 130);
            }
                break;
                case HomeCollectionViewCellStyle_advert:
            {
                self.size = CGSizeMake((IPHONE_SCREEN_WIDTH)*.5, 130);
            }
                break;
        }
    }
    return self;
}
@end
