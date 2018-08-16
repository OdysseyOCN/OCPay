//
//  SearchHistoryCollectionReusableView.h
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/7.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface SearchHistoryCollectionReusableView : UICollectionReusableView

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *deleteButton;
@property (copy, nonatomic) dispatch_block_t deleteCallback;

@end
