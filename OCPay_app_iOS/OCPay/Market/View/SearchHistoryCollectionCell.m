//
//  SearchHistoryCollectionCell.m
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/7.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import "SearchHistoryCollectionCell.h"

@implementation SearchHistoryCollectionCell

- (void)awakeFromNib{
    [super awakeFromNib];
    self.textLabel.layer.cornerRadius = 15;
    self.textLabel.layer.masksToBounds = true;
}

@end
