//
//  HomeDataModel.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/29.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "HomeDataModel.h"

@implementation HomeDataModel
+ (NSDictionary *)modelContainerPropertyGenericClass {
    return @{@"homePageVos" : [HomeModuleDataModel class]};
}
@end


@implementation HomeModuleDataModel
+ (NSDictionary *)modelCustomPropertyMapper {
    return @{@"ID" : @"id"};
}

+ (NSDictionary *)modelContainerPropertyGenericClass {
    return @{@"advertisments" : [HomeAdvertDataModel class]};
}

@end


@implementation HomeAdvertDataModel
+ (NSDictionary *)modelCustomPropertyMapper {
    return @{@"ID" : @"id"};
}

@end
