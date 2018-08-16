//
//  SearchHistoryCollectionReusableView.m
//  BlockAsia
//
//  Created by 何自梁 on 2018/8/7.
//  Copyright © 2018年 MengGen. All rights reserved.
//

#import "SearchHistoryCollectionReusableView.h"

@implementation SearchHistoryCollectionReusableView

- (IBAction)deleteAction:(id)sender {
    if (self.deleteCallback) {
        self.deleteCallback();
    }
}

@end
