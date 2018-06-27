//
//  HomeCollectionViewCell.h
//  OCPay
//
//  Created by 何自梁 on 2018/5/28.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HomeViewModel.h"

typedef NS_ENUM(NSUInteger, HomeCollectionCellCallbackType) {
    HeadCellCallbackType_ScanQRCode,
    HeadCellCallbackType_showTokens,
    HeadCellCallbackType_ShowAccount,
    HeadCellCallbackType_Record,
    HeadCellCallbackType_Send,
    HomeCollectionCellCallbackType_chooseTokens,
    HomeCollectionCellCallbackType_chooseAdvert,
    HomeCollectionCellCallbackType_chooseModule,
    HomeCollectionCellCallbackType_choosePage
};

typedef void(^HeadCellCallback)(HomeCollectionCellCallbackType type, HomeCellViewModel *rowData);


@interface HomeCollectionViewCell : UICollectionViewCell
@property (nonatomic, copy) HeadCellCallback cellCallback;
@property (nonatomic, strong) HomeCellViewModel *rowData;
@property (nonatomic, strong) HomeSectionViewModel *sectionData;
@end
