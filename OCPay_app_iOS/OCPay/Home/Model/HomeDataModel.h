//
//  HomeDataModel.h
//  OCPay
//
//  Created by 何自梁 on 2018/5/29.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface HomeAdvertDataModel : NSObject
@property (nonatomic, copy) NSString *ID;
@property (nonatomic, copy) NSString *content;
@property (nonatomic, copy) NSString *createBy;
@property (nonatomic, copy) NSString *createTime;
@property (nonatomic, copy) NSString *updateTime;
@property (nonatomic, copy) NSString *updateBy;
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *directUrl;
@property (nonatomic, copy) NSString *showSort;
@property (nonatomic, copy) NSString *directType;
@property (nonatomic, copy) NSString *homePageId;
@property (nonatomic, copy) NSString *showImg;
@end

@interface HomeModuleDataModel : NSObject
@property (nonatomic, copy) NSString *sort;
@property (nonatomic, copy) NSString *content;
@property (nonatomic, copy) NSString *ID;
@property (nonatomic, copy) NSString *createBy;
@property (nonatomic, copy) NSString *createTime;
@property (nonatomic, copy) NSString *updateTime;
@property (nonatomic, copy) NSString *updateBy;
@property (nonatomic, copy) NSString *type;
@property (nonatomic, strong) NSArray <HomeAdvertDataModel*>*advertisments;
@end

@interface HomeDataModel : NSObject
@property (nonatomic, strong) NSArray <HomeModuleDataModel *>*homePageVos;

@end




