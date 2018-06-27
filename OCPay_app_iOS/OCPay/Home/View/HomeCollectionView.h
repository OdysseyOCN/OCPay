//
//  HomeCollectionView.h
//  OCPay
//
//  Created by 何自梁 on 2018/5/28.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HomeViewModel.h"
#import "HomeCollectionViewCell.h"

typedef void(^ScrollRatioBlock)(CGFloat ratio);


typedef void(^HomeCollectionViewCallback)(HomeCellViewModel *data, HomeCollectionCellCallbackType type);

@interface HomeCollectionView : UICollectionView

@property (nonatomic, copy) HomeCollectionViewCallback callback;

@property (nonatomic, copy) ScrollRatioBlock scrollRatioBlock;

@property (nonatomic, strong) HomeViewModel *data;

@end
