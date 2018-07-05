//
//  ContactUsCollectionViewCell.h
//  OCPay
//
//  Created by 何自梁 on 2018/6/23.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AmplifyImageView.h"

typedef void(^DeleteCallback)(NSInteger index);

@interface ContactUsCollectionViewCell : UICollectionViewCell
@property (weak, nonatomic) IBOutlet AmplifyImageView *myImageView;
@property (weak, nonatomic) IBOutlet UIButton *myDeleteButton;
@property (nonatomic) NSInteger index;
@property (nonatomic, copy) DeleteCallback deleteCallback;
@end
