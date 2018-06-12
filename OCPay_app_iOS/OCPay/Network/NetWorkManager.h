//
//  NetWorkManager.h
//  OCPay
//
//  Created by 何自梁 on 2018/5/18.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AFNetworking.h"
#import "AFNetworkActivityIndicatorManager.h"

typedef void(^Success)(__kindof NSObject*data);
typedef void(^Failure)(NSError*error);


@interface NetWorkManager : AFHTTPSessionManager

+ (void)GetWithURL:(NSString *)URLString
        parameters:(NSDictionary *)parameters
           success:(Success)success
           failure:(Failure)failure;

+ (void)PostWithURL:(NSString *)URLString
         parameters:(id)parameters
            success:(Success)success
            failure:(Failure)failure;

@end
