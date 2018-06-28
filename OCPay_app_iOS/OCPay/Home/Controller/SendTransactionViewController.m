//
//  SendTransactionViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/1.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "SendTransactionViewController.h"
#import "SignDetailViewController.h"
#import "ContactsListViewController.h"
#import "BasicWebViewController.h"
#import "SignVerifyProcessView.h"
#import "QRCodeDataModel.h"
#import "TransactionInfo+Extension.h"

@interface SendTransactionViewController ()
@property (weak, nonatomic) IBOutlet UIButton *helpButton;
@property (weak, nonatomic) IBOutlet UISlider *mySlider;
@property (weak, nonatomic) IBOutlet UIView *easyModelView;
@property (weak, nonatomic) IBOutlet UILabel *costLabel;
@property (weak, nonatomic) IBOutlet UITextField *addressTextField;
@property (weak, nonatomic) IBOutlet UITextField *amountTextField;
@property (weak, nonatomic) IBOutlet UITextField *noteTextField;
@property (weak, nonatomic) IBOutlet UITextField *GASPriceTextField;
@property (weak, nonatomic) IBOutlet UITextField *GASTextField;
@property (weak, nonatomic) IBOutlet UITextField *hexDataTextField;
@property (weak, nonatomic) IBOutlet UIView *myContentView;
@property (weak, nonatomic) IBOutlet UIScrollView *contentScrollView;
//paymentDetailsView相关控件
@property (weak, nonatomic) IBOutlet UIView *paymentDetailsView;
@property (weak, nonatomic) IBOutlet UILabel *paymentFromAddressLabel;
@property (weak, nonatomic) IBOutlet UILabel *paymentToAddressLabel;
@property (weak, nonatomic) IBOutlet UILabel *paymentCostLabel;
@property (weak, nonatomic) IBOutlet UILabel *paymentAmountLabel;
@property (weak, nonatomic) IBOutlet UIButton *nextButton;
@property (weak, nonatomic) IBOutlet UILabel *observerHintLabel;

@property (strong, nonatomic) SignVerifyProcessView *signVerifyProcessView;

@property (strong, nonatomic) Transaction *trans;
@property (strong, nonatomic) QRCodeDataModel *qrdata;
@property (nonatomic, strong) BigNumber *gasPrice;

@end


@implementation SendTransactionViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
    [self.keyboardmanager addObserver:self];
    [self getTranscationPrice:^{
        [self updateCost];
    }];
}

- (void)initUI{
    self.title = self.tokenData.tokenTypeString;
    self.paymentDetailsView.closeCustomViewAnimations = ^{
        self.paymentDetailsView.top = DEVICE_SCREEN_HEIGHT;
    };
    self.paymentDetailsView.showCustomViewAnimations = ^{
        self.paymentDetailsView.top = DEVICE_SCREEN_HEIGHT - self.paymentDetailsView.height;
    };
    
    if (self.wallet.isObserver) {
        self.nextButton.backgroundColor = UIColorHex(#43ADDC);
        self.observerHintLabel.hidden = NO;
    }else{
        self.nextButton.backgroundColor = UIColorHex(#1A3D4E);
        self.observerHintLabel.hidden = YES;
    }
    
    if (self.QRCodedata) {
        self.amountTextField.text = self.QRCodedata.transaction.amount;
        self.addressTextField.text = self.QRCodedata.transaction.transactionTo;
    }
}

- (void)getTranscationNonceAndGasPrice:(dispatch_block_t)finish{
    
    BigNumberPromise *priceCallback = [self.wallet.api getGasPrice];
    IntegerPromise *nonceCallback = [self.wallet.api getTransactionCount:[Address addressWithString:self.wallet.address]];
    
    if (_easyModelView.hidden) {
        finish();
    }else{
        [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
        dispatch_group_t group = dispatch_group_create();
        dispatch_group_enter(group);
        [nonceCallback onCompletion:^(IntegerPromise *itp) {
            self.trans.nonce = itp.value;
            dispatch_group_leave(group);
        }];
        dispatch_group_enter(group);
        [priceCallback onCompletion:^(BigNumberPromise *p) {
            self.gasPrice = p.value;
            dispatch_group_leave(group);
        }];
        dispatch_group_notify(group, dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
            if (finish) finish();
        });
    }
}

- (void)getTranscationPrice:(dispatch_block_t)finish{
    if (self.gasPrice == nil) {
        BigNumberPromise *priceCallback = [self.wallet.api getGasPrice];
        [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
        [priceCallback onCompletion:^(BigNumberPromise *p) {
            [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
            self.gasPrice = p.value;
            if (finish) finish();
        }];
    }else{
        if (finish) finish();
    }
}

- (void)getTranscationNonce:(dispatch_block_t)finish{
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
    IntegerPromise *nonceCallback = [self.wallet.api getTransactionCount:[Address addressWithString:self.wallet.address]];
    [nonceCallback onCompletion:^(IntegerPromise *itp) {
        [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
        self.trans.nonce = itp.value;
        if (finish) finish();
    }];
}


- (void)showPaymentDetailsView{
    self.paymentFromAddressLabel.text = self.wallet.address;
    self.paymentToAddressLabel.text = self.addressTextField.text;
    self.paymentCostLabel.text = [NSString stringWithFormat:@"%@ETH\nGas(%@)*Gas Price(%@wei)",self.costLabel.text,self.trans.gasLimit.decimalString,self.trans.gasPrice.decimalString];
    self.paymentAmountLabel.text = self.amountTextField.text;
    self.paymentDetailsView.height = IPHONE_HOME_INDICATOR_HEIGHT + 284;
    self.paymentDetailsView.width = DEVICE_SCREEN_WIDTH;
    self.paymentDetailsView.top = DEVICE_SCREEN_HEIGHT;
    [self dispalyCustomView:self.paymentDetailsView];
}

- (void)updateCost{
    if (_easyModelView.hidden) {//高级模式
        self.trans.gasPrice = [BigNumber bigNumberWithDecimalString:self.GASPriceTextField.text];
        self.trans.gasLimit = [BigNumber bigNumberWithDecimalString:self.GASTextField.text];
    }else{//简易模式
        self.trans.gasPrice = self.gasPrice;
        self.trans.gasLimit = [BigNumber bigNumberWithDecimalString:[GasLimit decimalNumberByMultiplying:[NSString stringWithFormat:@"%.1f",self.mySlider.value]]];
    }
    _costLabel.text = [[self.trans.gasLimit mul:self.trans.gasPrice].decimalString decimalNumberByDividing:DecimalNumberTenPower18];
    NSLog(@"Limit:%@",self.trans.gasLimit.decimalString);
}

- (void)updateTranscationConfiguration{
    if (self.tokenData.tokenType == TokenType_OCN) {
        self.trans.toAddress = [Address addressWithString:OCNAddress];
        self.trans.value = [BigNumber bigNumberWithDecimalString:@"0"];
        NSString *value = [[NSString alloc] initWithFormat:@"%064llx",[self.amountTextField.text decimalNumberByMultiplying:DecimalNumberTenPower18].longLongValue];
        NSString *dataStr = [NSString stringWithFormat:@"%@%@%@",ContractTransferFunctionPrefix,[self.addressTextField.text stringByReplacingOccurrencesOfString:@"0x" withString:@""],value];
        if(self.hexDataTextField.text.length == 0){
            self.trans.data = [SecureData hexStringToData:dataStr];
        }else{
            self.trans.data = [SecureData hexStringToData:self.hexDataTextField.text];
        }
    }else{
        self.trans.toAddress = [Address addressWithString:self.addressTextField.text];
        self.trans.value = [BigNumber bigNumberWithDecimalString:[self.amountTextField.text decimalNumberByMultiplying:DecimalNumberTenPower18]];
    }
}

- (void)showSignVerifyProcessView{
    QRCodeDataModel *QRCodedata = [[QRCodeDataModel alloc]init];
    QRCodedata.ethereum = self.wallet.address;
    QRCodedata.mode = @"mode_show_eth_tx_sign";
    QRCodedata.transaction.amount = self.amountTextField.text;
    QRCodedata.transaction.nonce = [NSString stringWithFormat:@"%lu",(unsigned long)self.trans.nonce];
    QRCodedata.transaction.data = self.hexDataTextField.text;
    QRCodedata.transaction.gasLimit = self.trans.gasLimit.decimalString;
    QRCodedata.transaction.gasPrice = self.trans.gasPrice.decimalString;
    QRCodedata.transaction.transactionFrom = self.wallet.address;
    QRCodedata.transaction.transactionTo = self.trans.toAddress.checksumAddress;
    QRCodedata.transaction.tokenName = self.tokenData.tokenTypeString;
    QRCodedata.transaction.contractAddress = (self.tokenData.tokenType == TokenType_OCN) ? OCNAddress : nil;
    NSString *value = [QRCodedata modelToJSONString];
    self.signVerifyProcessView = [SignVerifyProcessView showWithType:WalletType_Transaction_Hot value:value];
    @weakify(self);
    self.signVerifyProcessView.callback = ^(SignVerifyProcessViewCallbackType type) {
        @strongify(self)
        switch (type) {
            case SignVerifyProcessViewCallbackType_ScanQRCode:{
                [self scanQRCodeAction:nil];
                break;
            }
            case SignVerifyProcessViewCallbackType_Finish:{
                NSData *data = [SecureData hexStringToData:self.qrdata.data];
                [self send:data];
                break;
            }
        }
    };
}

- (BOOL)checkPass{
    if (self.addressTextField.text.length == 0) {
        [self dispalyText:@"Please enter your address"];
        return false;
    }else if (self.amountTextField.text.length == 0){
        [self dispalyText:@"Please enter your amount"];
        return false;
    }else if (self.easyModelView.hidden){
        if (self.GASPriceTextField.text.length == 0) {
            [self dispalyText:@"Please enter your GAS price"];
            return false;
        }else if (self.GASTextField.text.length == 0){
            [self dispalyText:@"Please enter your GAS"];
            return false;
        }
    }
    return true;
}

- (void)send:(NSData *)transData{
    HashPromise *sendCallback = [self.wallet.api sendTransaction:transData];
    [self dispalyLoading:@"Transfer..."];
    [sendCallback onCompletion:^(HashPromise *hash) {
        NSLog(@"transctionResult:%@",hash.value.hexString);
        [self hideLoading:NO];
        [self closePaymentDetailsView];
        if (hash.value.hexString.length > 0) {
            [self.navigationController dispalyText:@"Transfer success!"];
            NSMutableDictionary *dic = [NSMutableDictionary dictionary];
            [dic setValue:[NSString stringWithFormat:@"%f",[NSDate date].timeIntervalSince1970] forKey:@"timestamp"];
            [dic setValue:hash.value.hexString forKey:@"hash"];
            [dic setValue:self.trans.fromAddress.checksumAddress forKey:@"from"];
            [dic setValue:self.trans.toAddress.checksumAddress forKey:@"to"];
            [dic setValue:self.trans.gasLimit.decimalString forKey:@"gasLimit"];
            [dic setValue:self.trans.gasPrice forKey:@"gasPrice"];
            [dic setValue:[NSString stringWithFormat:@"%ld",self.trans.nonce] forKey:@"nonce"];
            [dic setValue:(self.tokenData.tokenType == TokenType_OCN) ? OCNAddress : nil forKey:@"contractAddress"];
            [dic setValue:[SecureData dataToHexString:self.trans.data] forKey:@"data"];
            [dic setValue:self.trans.value.decimalString forKey:@"value"];
            [dic setValue:[self.costLabel.text decimalNumberByMultiplying:DecimalNumberTenPower18] forKey:@"gasUsed"];
            TransactionInfo *info = [TransactionInfo transactionInfoFromDictionary:dic];
            
#if DEBUG
            if (info.transactionHash == nil) {
                [self dispalyConfirmText:[NSString stringWithFormat:@"交易信息缓存数据生成失败:%@",info]];
            }
#endif
            if (info.transactionHash) {
                info.pending = @"Pendding";
                [self.wallet.transactionCache addObject:info];
                [WalletManager synchronize];
            }
            [self.navigationController popViewControllerAnimated:YES];
        }else{
            [self.navigationController dispalyText:@"Transfer fail!"];
        }
    }];
}


- (IBAction)exchageAction:(UIButton*)sender {
    _easyModelView.hidden = !_easyModelView.hidden;
    _helpButton.hidden = !_helpButton.hidden;
    if (_easyModelView.hidden == NO) {
        [self getTranscationPrice:^{
            [self updateCost];
        }];
    }
}

- (IBAction)sliderAction:(UISlider *)sender {
    NSLog(@"Value:%f",sender.value);
    [self updateCost];
}


- (IBAction)nextStepAction:(id)sender {
    [self keyboardFinishAction];
    if (![self checkPass]) {
        return;
    }
    [self dispalyLoading:@"Preparing..."];
    [self getTranscationNonceAndGasPrice:^{
        [self hideLoading:true];
        [self updateCost];
        [self updateTranscationConfiguration];
        if (self.wallet.isObserver) {
            [self showSignVerifyProcessView];
        }else{
            [self showPaymentDetailsView];
        }
    }];
}

- (IBAction)closePaymentDetailsView{
    [self closeCustomView:self.paymentDetailsView];
}

- (IBAction)pamentDetailViewNextAction:(id)sender {
    [self closePaymentDetailsView];
    [self checkPassWithWallet:self.wallet completion:^(BOOL pass, Account *account) {
        if (pass) {
            [account sign:self.trans];
            [self send:[self.trans serialize]];
        }
    }];
}

- (IBAction)scanQRCodeAction:(id)sender {
    QRCodeViewController *vc = [[QRCodeViewController alloc] init];
    vc.reciveResult = ^(NSString *result) {
        self.qrdata = [QRCodeDataModel modelWithJSON:result];
        if (self.qrdata.type == QRCodeType_Receive) {//获取收款人钱包地址
            self.addressTextField.text = self.qrdata.ethereum;
        }else if (self.qrdata.type == QRCodeType_Transaction) {
            self.signVerifyProcessView.result = self.qrdata.data;
        }
    };
    [self QRCodeScanVC:vc];
}

- (IBAction)contactAction:(id)sender {
    ContactsListViewController *vc = [self pushViewControllerWithIdentifier:@"ContactsListViewController" inStoryboard:@"Main"];
    @weakify(self)
    vc.selectContactsCallback = ^(ContactsModel *contacts) {
        @strongify(self)
        self.addressTextField.text = contacts.walletAddress;
    };
}

- (IBAction)signHelpAction:(id)sender {
    BasicWebViewController *webVC = [[BasicWebViewController alloc]init];
    webVC.URLString = [NSString stringWithFormat:@"%@Howdoesofflinesigningwork.html",H5BaseURLPrefix];;
    [self.navigationController pushViewController:webVC animated:YES];
}

- (IBAction)helpAction:(id)sender {
    BasicWebViewController *webVC = [[BasicWebViewController alloc]init];
    webVC.URLString = [NSString stringWithFormat:@"%@Howtouseadvancetransfermode.html",H5BaseURLPrefix];;
    [self.navigationController pushViewController:webVC animated:YES];
}

- (IBAction)minerCostHelpAction:(id)sender {
    BasicWebViewController *webVC = [[BasicWebViewController alloc]init];
    webVC.URLString = [NSString stringWithFormat:@"%@WhatistheGasinEthereum.html",H5BaseURLPrefix];;
    [self.navigationController pushViewController:webVC animated:YES];
}

- (IBAction)textFieldEditingChanged:(UITextField *)sender {
}

#pragma mark - 键盘处理
- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField{
    [textField setInputAccessoryView:self.keyboardAccessoryView];
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    if (textField == _addressTextField) {
        [_amountTextField becomeFirstResponder];
    }else if (textField == _amountTextField) {
        [_noteTextField becomeFirstResponder];
    }
    return YES;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField{
    if (!self.keyboardmanager.keyboardVisible) {
        return;
    }
    CGRect frame = self.keyboardmanager.keyboardFrame;
    frame = [self.keyboardmanager convertRect:frame toView:self.myContentView];
    [self calculateOffsetForScrollViewWithTextField:textField keyboardFrame:frame];
}

- (void)keyboardFinishAction{
    [self closeKeyboard];
}

- (void)closeKeyboard{
    [self.view endEditing:YES];
    BOOL offestOutOfContenSize = (self.contentScrollView.contentOffset.y > (self.contentScrollView.contentSize.height - self.contentScrollView.bounds.size.height));
    if (offestOutOfContenSize) {
        [self.contentScrollView scrollToBottom];
    }
}

- (void)calculateOffsetForScrollViewWithTextField:(UITextField *)textfield keyboardFrame:(CGRect )keyboardFrame {
    CGFloat offest = CGRectGetMinY(keyboardFrame) - CGRectGetMaxY(textfield.frame);
    if (offest<=0) {
        [self.contentScrollView setContentOffset:CGPointMake(0, self.contentScrollView.contentOffset.y - offest + 60) animated:YES];
    }
}

- (void)keyboardChangedWithTransition:(YYTextKeyboardTransition)transition {
    CGRect toFrame = [self.keyboardmanager convertRect:transition.toFrame toView:self.myContentView];
    UITextField *textfield = [self getFirstResponderForTextField];
    if (!textfield) {
        return;
    }
    [self calculateOffsetForScrollViewWithTextField:textfield keyboardFrame:toFrame];
}

- (UITextField *)getFirstResponderForTextField{
    UITextField *textfield = nil;
    if([self.addressTextField isFirstResponder]){
        textfield = self.addressTextField;
    }else if ([self.amountTextField isFirstResponder]) {
        textfield = self.amountTextField;
    }else if ([self.noteTextField isFirstResponder]) {
        textfield = self.noteTextField;
    }else if ([self.GASPriceTextField isFirstResponder]){
        textfield = self.GASPriceTextField;
    }else if ([self.GASTextField isFirstResponder]){
        textfield = self.GASTextField;
    }else if ([self.hexDataTextField isFirstResponder]){
        textfield = self.hexDataTextField;
    }
    return textfield;
}

- (Transaction *)trans{
    if (!_trans) {
        _trans = [Transaction transactionWithFromAddress:[Address addressWithString:self.wallet.address]];
    }
    return _trans;
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
