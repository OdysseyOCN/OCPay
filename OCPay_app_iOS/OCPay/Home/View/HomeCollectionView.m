//
//  HomeCollectionView.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/28.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "HomeCollectionView.h"
#import "JHCollectionViewFlowLayout.h"

@interface HomeCollectionView ()<JHCollectionViewDelegateFlowLayout,UICollectionViewDataSource>
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end


@implementation HomeCollectionView

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (void)awakeFromNib{
    [super awakeFromNib];
    self.delegate = self;
    self.dataSource = self;
}

#pragma mark - UICollectionViewDataSource,UICollectionViewDelegateFlowLayout
- (UIColor *)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout backgroundColorForSection:(NSInteger)section{
    
    return section == 0 ? [UIColor clearColor] : UIColorHex(#ffffff);
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    return self.data.sectionDatas.count;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    HomeSectionViewModel *sectionData = self.data.sectionDatas[section];
    return sectionData.hidden ? 0 : sectionData.cellDatas.count;
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section{
    return self.data.sectionDatas[section].minimumLineSpacing;
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section{
    return self.data.sectionDatas[section].minimumInteritemSpacing;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath{
    return self.data.sectionDatas[indexPath.section].cellDatas[indexPath.row].size;
}

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout insetForSectionAtIndex:(NSInteger)section{
    return self.data.sectionDatas[section].edgeInsets;
}

//- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout referenceSizeForHeaderInSection:(NSInteger)section{
//    return CGSizeZero;
//}

//- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath{
//    return [self dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:@"spaceReusable" forIndexPath:indexPath];
//}

- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath{
    HomeCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:[NSString stringWithFormat:@"cell%ld",self.data.sectionDatas[indexPath.section].cellDatas[indexPath.row].style] forIndexPath:indexPath];
    cell.rowData = self.data.sectionDatas[indexPath.section].cellDatas[indexPath.row];
    cell.sectionData = self.data.sectionDatas[indexPath.section];
    @weakify(self)
    cell.cellCallback = ^(HomeCollectionCellCallbackType type,HomeCellViewModel *rowData){
        @strongify(self)
        if (self.callback){
           self.callback(rowData, type);
        }
        if (type == HeadCellCallbackType_showTokens) {
            [self showAndClose];
        }
    };
    return cell;
}

- (void)showAndClose{
    self.data.hiddenAccountSection = !self.data.hiddenAccountSection;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath{
    HomeCellViewModel *data = self.data.sectionDatas[indexPath.section].cellDatas[indexPath.row];
    if (!self.callback){
        return;
    }
    switch (data.style) {
        case HomeCollectionViewCellStyle_token:
            self.callback(data, HomeCollectionCellCallbackType_chooseTokens);
            break;
        case HomeCollectionViewCellStyle_advert:
            self.callback(data, HomeCollectionCellCallbackType_chooseAdvert);
            break;
        case HomeCollectionViewCellStyle_module:
            self.callback(data, HomeCollectionCellCallbackType_chooseModule);
            break;
        default:
            break;
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    CGFloat y = (scrollView.contentOffset.y >= IPHONE_NAVIGATION_BAR_HEIGHT+IPHONE_STATUS_BAR_HEIGHT) ? IPHONE_NAVIGATION_BAR_HEIGHT+IPHONE_STATUS_BAR_HEIGHT : ((scrollView.contentOffset.y <= 0) ? 0 : scrollView.contentOffset.y);
    CGFloat a = y / (IPHONE_NAVIGATION_BAR_HEIGHT+IPHONE_STATUS_BAR_HEIGHT);
    self.titleLabel.textColor = (a == 1) ? UIColorHex(0x38525F) : UIColorHex(0xFFFFFF);
    if (self.scrollRatioBlock){
        self.scrollRatioBlock(a);
    }
}
@end
