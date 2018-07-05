//
//  ContactsTableViewCell.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/23.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "ContactsTableViewCell.h"

@interface ContactsTableViewCell ()
@property (weak, nonatomic) IBOutlet UIImageView *myImageView;
@property (weak, nonatomic) IBOutlet UILabel *walletNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;

@end

@implementation ContactsTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setContacts:(ContactsModel *)contacts{
    _contacts = contacts;
    _walletNameLabel.text = [NSString stringWithFormat:@"%@ %@",contacts.firstName.length ? contacts.firstName : @"",contacts.familyName.length ? contacts.familyName : @""];
    _addressLabel.text = contacts.walletAddress;
}

@end
