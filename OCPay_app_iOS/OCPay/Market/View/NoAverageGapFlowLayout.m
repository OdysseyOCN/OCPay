//
//  NoAverageGapFlowLayout.m
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/13.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import "NoAverageGapFlowLayout.h"

@interface NoAverageGapFlowLayout ()

/**
 游标x
 */
@property (nonatomic) CGFloat global_x;

/**
 游标y
 */
@property (nonatomic) CGFloat global_y;

/**
 布局属性数组
 */
@property (nonatomic,strong) NSMutableArray *attrsArray;

@end

@implementation NoAverageGapFlowLayout

- (NSMutableArray *)attrsArray{
    if (!_attrsArray) {
        _attrsArray = [NSMutableArray array];
    }
    return _attrsArray;
}

- (BOOL)shouldInvalidateLayoutForBoundsChange:(CGRect)newBounds{
    CGRect oldBounds = self.collectionView.bounds;
    if (CGRectGetWidth(newBounds) != CGRectGetWidth(oldBounds)) {
        return YES;
    }
    return NO;
}

-(NSArray *)layoutAttributesForElementsInRect:(CGRect)rect {
    NSMutableArray *rArray = [NSMutableArray array];
    for (UICollectionViewLayoutAttributes *cacheAttr in _attrsArray) {
        if (CGRectIntersectsRect(cacheAttr.frame, rect)) {
            [rArray addObject:cacheAttr];
        }
    }
    return rArray;
}

- (void)prepareLayout{
    [super prepareLayout];
    [self.attrsArray removeAllObjects];
    self.global_x = 0; self.global_y = 0;
    for (NSInteger section = 0; section < [self.collectionView numberOfSections]; section++){
        UICollectionViewLayoutAttributes *headerAttr = [super layoutAttributesForSupplementaryViewOfKind:UICollectionElementKindSectionHeader atIndexPath:[NSIndexPath indexPathForRow:0 inSection:section]];
        self.global_y = CGRectGetMaxY(headerAttr.frame);
        if (headerAttr) [self.attrsArray addObject:headerAttr];
        
        for (NSInteger row = 0; row < [self.collectionView numberOfItemsInSection:section]; row++){
            [self.attrsArray addObject:[self layoutAttributesForItemAtIndexPath:[NSIndexPath indexPathForRow:row inSection:section]]];
        }
        UICollectionViewLayoutAttributes *footerAttr = [super layoutAttributesForSupplementaryViewOfKind:UICollectionElementKindSectionFooter atIndexPath:[NSIndexPath indexPathForRow:0 inSection:section]];
        self.global_y = CGRectGetMaxY(footerAttr.frame);
        if (footerAttr) [self.attrsArray addObject:footerAttr];
    }
}

- (UICollectionViewLayoutAttributes *)layoutAttributesForItemAtIndexPath:(NSIndexPath *)indexPath{
    UICollectionViewLayoutAttributes *attr = [super layoutAttributesForItemAtIndexPath:indexPath];
    if (CGRectGetMinX(attr.frame) - self.global_x > self.minimumInteritemSpacing) {
        attr.frame = CGRectMake(self.global_x+self.minimumInteritemSpacing, attr.frame.origin.y, attr.frame.size.width, attr.frame.size.height);
    }
    self.global_x = CGRectGetMaxX(attr.frame);
    return attr;
}

@end
