//
//  EditContactsViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/21.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "EditContactsViewController.h"
#import "ContactsModel.h"
#import "QRCodeDataModel.h"

@interface EditContactsViewController ()

@property (weak, nonatomic) IBOutlet UITextField *firstTextField;
@property (weak, nonatomic) IBOutlet UITextField *familyTextField;
@property (weak, nonatomic) IBOutlet UITextField *paymentTextField;
@property (weak, nonatomic) IBOutlet UITextField *phoneNumberTextField;
@property (weak, nonatomic) IBOutlet UITextField *emailTextField;
@property (weak, nonatomic) IBOutlet UITextField *noteTextField;

@end

@implementation EditContactsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.tableView.tableFooterView = [UIView new];
}

- (IBAction)scanQRCodeAction:(id)sender {
    QRCodeViewController *vc = [[QRCodeViewController alloc] init];
    @weakify(self)
    vc.reciveResult = ^(NSString *result) {
        @strongify(self)
        QRCodeDataModel *qrdata = [QRCodeDataModel modelWithJSON:result];
        if (qrdata.type == QRCodeType_Receive) {//获取钱包地址
            self.paymentTextField.text = qrdata.ethereum;
        }else{
            self.paymentTextField.text = result;
        }
    };
    [self QRCodeScanVC:vc];
}

- (IBAction)doneAction:(id)sender {
    if (![self check]) {
        return;
    }
    [self addContacts];
}

- (void)addContacts{
    ContactsModel *contact = [[ContactsModel alloc]init];
    contact.firstName = _firstTextField.text;
    contact.familyName = _familyTextField.text;
    contact.walletAddress = _paymentTextField.text;
    contact.phoneNumber = _phoneNumberTextField.text;
    contact.email = _emailTextField.text;
    contact.note = _noteTextField.text;
    [WalletManager.share addContacts:contact];
    [self.navigationController popViewControllerAnimated:YES];
    [self.navigationController dispalyText:@"add contacts success!"];
}

- (BOOL)check{
    if (_firstTextField.text.length == 0) {
        [self dispalyText:@"first name is empty!"];
        return false;
    }else if (_paymentTextField.text.length == 0){
        [self dispalyText:@"payee payment platform address is empty!"];
        return false;
    }
    return true;
}

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField{
    [textField setInputAccessoryView:self.keyboardAccessoryView];
    return YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
