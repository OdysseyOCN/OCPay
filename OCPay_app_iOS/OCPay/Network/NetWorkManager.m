//
//  NetWorkManager.m
//  OCPay
//
//  Created by 何自梁 on 2018/5/18.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "NetWorkManager.h"

@implementation NetWorkManager

static NetWorkManager *manager;

+ (void)load{
    [self sharedInstance];
}

+ (instancetype)sharedInstance{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [AFNetworkActivityIndicatorManager sharedManager].enabled = YES;
        NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
        manager = [[self alloc] initWithSessionConfiguration:configuration];;
        manager.requestSerializer = [AFHTTPRequestSerializer serializer];
        manager.requestSerializer.timeoutInterval = 30.0f;
        manager.securityPolicy.allowInvalidCertificates = YES;
        manager.securityPolicy.validatesDomainName = NO;//不验证证书的域名
        manager.responseSerializer = [AFJSONResponseSerializer serializer];
        manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json", @"text/html", @"text/json",@"text/plain", @"text/javascript", @"text/xml", @"image/*", nil];
    });
    return manager;
}


+ (void)sendRequestWithHTTPMethod:(NSString*)method
                        URLString:(NSString*)URLString
                       parameters:(nullable id)parameters
                   uploadProgress:(nullable void (^)(NSProgress *uploadProgress)) uploadProgressBlock
                 downloadProgress:(nullable void (^)(NSProgress *downloadProgress)) downloadProgressBlock
                completionHandler:(nullable void (^)(id _Nullable responseObject,  NSError * _Nullable error))completionHandler {
    NSLog(@"%@请求：%@ 参数：\n%@",method,URLString,parameters);
    NSError *error = nil;
    NSMutableDictionary *par = [NSMutableDictionary dictionaryWithDictionary:parameters];
    NSMutableURLRequest *request = [[AFHTTPRequestSerializer serializer] requestWithMethod:method URLString:URLString parameters:par error:&error];
    NSURLSessionDataTask *dataTask = [manager dataTaskWithRequest:request uploadProgress:^(NSProgress * _Nonnull uploadProgress) {
        if (uploadProgressBlock) {
            uploadProgressBlock(uploadProgress);
        }
    } downloadProgress:^(NSProgress * _Nonnull downloadProgress) {
        if (downloadProgressBlock) downloadProgressBlock(downloadProgress);
    } completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        id caches = [self getCachesWithKey:[self getCacheskeyWithInterface:URLString parameters:parameters]];
        if (caches)  responseObject = caches;
        NSLog(@"接口：%@ 返回数据：\n%@\n(%@) 数据类型:%@",URLString,responseObject, caches ? @"缓存数据":@"网路数据",NSStringFromClass([responseObject class]));
        completionHandler(responseObject,error);
        
    }];
    [dataTask resume];
}

#pragma mark - HTTP GET
+ (void)GetWithURL:(NSString *)URLString
        parameters:(NSDictionary *)parameters
           success:(Success)success
           failure:(Failure)failure{
    [self sendRequestWithHTTPMethod:@"GET" URLString:URLString parameters:parameters uploadProgress:nil downloadProgress:nil completionHandler:^(id  _Nullable responseObject, NSError * _Nullable error) {
        if (responseObject) {
            success(responseObject);
        }else{
            failure(error);
        }
    }];
}

#pragma mark - HTTP POST
+ (void)PostWithURL:(NSString *)URLString
         parameters:(id)parameters
            success:(Success)success
            failure:(Failure)failure{
    [self sendRequestWithHTTPMethod:@"POST" URLString:URLString parameters:parameters uploadProgress:nil downloadProgress:nil completionHandler:^(id  _Nullable responseObject, NSError * _Nullable error) {
        if (responseObject) {
            success(responseObject);
        }else{
            failure(error);
        }
    }];
}


#pragma mark - 本地数据缓存
+ (void)saveCaches:(id)caches WithKey:(NSString *)key{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *path = [self getCachesPathWithKey:key];
        [NSKeyedArchiver archiveRootObject:caches toFile:path];
    });
}

+ (id)getCachesWithKey:(NSString *)key{
    if ([AFNetworkReachabilityManager sharedManager].reachable) {
        return nil;
    }else{
        return [NSKeyedUnarchiver unarchiveObjectWithFile:[self getCachesPathWithKey:key]];
    }
}

+ (NSString *)getCachesPathWithKey:(NSString *)key{
    NSArray  *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *cachesDirectory = [paths objectAtIndex:0];
    NSString *path = [cachesDirectory stringByAppendingPathComponent:@"TCMCaches"];
    NSString *str = [path stringByAppendingPathComponent:key];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    if (![fileManager fileExistsAtPath:path]) {
        [fileManager createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:0];
    }
    return str;
}

+ (NSString *)getCacheskeyWithInterface:(NSString *)interface parameters:(NSDictionary *)parameters{
    interface = [interface stringByReplacingOccurrencesOfString:@"/" withString:@""];
    NSMutableString *str = [NSMutableString string];
    [parameters enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL *stop) {
        [str appendFormat:@"%@%@", key, obj];
    }];
    return [NSString stringWithFormat:@"%@%@", interface,str];
}

@end
