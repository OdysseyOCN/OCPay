//
//  MarketSrotingView.m
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/7.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import "MarketSortingView.h"

@interface MarketSortingView ()
@property (weak, nonatomic) IBOutlet UIButton *leftButton;
@property (weak, nonatomic) IBOutlet UIImageView *leftImageView;

@property (weak, nonatomic) IBOutlet UIButton *mediumButton;
@property (weak, nonatomic) IBOutlet UIImageView *mediumImageView;

@property (weak, nonatomic) IBOutlet UIButton *rightButton;
@property (weak, nonatomic) IBOutlet UIImageView *rightImageView;

@property (nonatomic) MarketSortingType sortingType;
@property (nonatomic) NSInteger index;
@end

@implementation MarketSortingView

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/
- (void)awakeFromNib{
    [super awakeFromNib];
    self.sortingType = MarketSortingType_A;
}

- (IBAction)sortAction:(UIButton *)sender {
    if (sender == _leftButton) {//交易所下name不排序
        if ((self.tag == 1 && self.index == 1) || self.index == 2) {
            return;
        }
    }
    switch (sender.tag) {
        case 0:
            if (self.sortingType == MarketSortingType_A) {
                self.sortingType = MarketSortingType_B;
            }else if (self.sortingType == MarketSortingType_B) {
                self.sortingType = MarketSortingType_A;
            }else{
                self.sortingType = MarketSortingType_A;
            }
            break;
        case 1:
            if (self.sortingType == MarketSortingType_C) {
                self.sortingType = MarketSortingType_D;
            }else if (self.sortingType == MarketSortingType_D) {
                self.sortingType = MarketSortingType_C;
            }else{
                self.sortingType = MarketSortingType_C;
            }
            break;
        case 2:
            if (self.sortingType == MarketSortingType_E) {
                self.sortingType = MarketSortingType_F;
            }else if (self.sortingType == MarketSortingType_F) {
                self.sortingType = MarketSortingType_E;
            }else{
                self.sortingType = MarketSortingType_E;
            }
            break;
    }
    if (self.sortingCallback) {
        self.sortingCallback(self.sortingType);
    }
}

- (void)setSortingType:(MarketSortingType)sortingType{
    _sortingType = sortingType;
    switch (sortingType) {
        case MarketSortingType_A:
            if ((self.tag == 1 && self.index == 1) || self.index == 2) {
                _leftImageView.image = nil;
            }else{
                _leftImageView.image = [UIImage imageNamed:@"icon-shunxu 1"];
            }
            _mediumImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            _rightImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            break;
        case MarketSortingType_B:
            if ((self.tag == 1 && self.index == 1) || self.index == 2) {
                _leftImageView.image = nil;
            }else{
                _leftImageView.image = [UIImage imageNamed:@"icon-shunxu 2"];
            }
            _mediumImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            _rightImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            break;
        case MarketSortingType_C:
            if ((self.tag == 1 && self.index == 1) || self.index == 2) {
                _leftImageView.image = nil;
            }else{
                _leftImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            }
            _mediumImageView.image = [UIImage imageNamed:@"icon-shunxu 1"];
            _rightImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            break;
        case MarketSortingType_D:
            if ((self.tag == 1 && self.index == 1) || self.index == 2) {
                _leftImageView.image = nil;
            }else{
                _leftImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            }
            _mediumImageView.image = [UIImage imageNamed:@"icon-shunxu 2"];
            _rightImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            break;
        case MarketSortingType_E:
            if ((self.tag == 1 && self.index == 1) || self.index == 2) {
                _leftImageView.image = nil;
            }else{
                _leftImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            }
            _mediumImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            _rightImageView.image = [UIImage imageNamed:@"icon-shunxu 1"];
            break;
        case MarketSortingType_F:
            if ((self.tag == 1 && self.index == 1) || self.index == 2) {
                _leftImageView.image = nil;
            }else{
                _leftImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            }
            _mediumImageView.image = [UIImage imageNamed:@"icon-shunxu 3"];
            _rightImageView.image = [UIImage imageNamed:@"icon-shunxu 2"];
            break;
    }
}

- (void)setIndex:(NSInteger)index sortType:(MarketSortingType)type{
    self.index = index;
    self.sortingType = type;
    switch (index) {
        case 1:
            if (self.tag == 1) {//tag为1代表搜索视图
                _leftImageView.image = nil;
                [_leftButton setTitle:@"Name" forState:UIControlStateNormal];
                [_mediumButton setTitle:@"Token Pair" forState:UIControlStateNormal];
                [_rightButton setTitle:@"Volume" forState:UIControlStateNormal];
            }else{
                [_leftButton setTitle:@"Token/Value" forState:UIControlStateNormal];
                [_mediumButton setTitle:@"Last Price/Volume" forState:UIControlStateNormal];
                [_rightButton setTitle:@"Change.24H" forState:UIControlStateNormal];
            }
            break;
        case 2:
            _leftImageView.image = nil;
            [_leftButton setTitle:@"Name" forState:UIControlStateNormal];
            [_mediumButton setTitle:@"Token Pair" forState:UIControlStateNormal];
            [_rightButton setTitle:@"Volume" forState:UIControlStateNormal];
            break;
        default:
            [_leftButton setTitle:@"Token/Value" forState:UIControlStateNormal];
            [_mediumButton setTitle:@"Last Price/Volume" forState:UIControlStateNormal];
            [_rightButton setTitle:@"Change.24H" forState:UIControlStateNormal];
            break;
    }
}
@end
