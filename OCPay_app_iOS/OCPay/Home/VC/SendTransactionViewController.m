//
//  SendTransactionViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/1.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "SendTransactionViewController.h"

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

@property (strong, nonatomic) EtherscanProvider *provieder;
@property (strong, nonatomic) Transaction *trans;
@end


@implementation SendTransactionViewController


- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
    [self.manager addObserver:self];
    [self getTranscationNonceAndGasPrice:^{
        [self updateCost];
    }];
}

- (void)initUI{
    self.paymentDetailsView.closeCustomViewAnimations = ^{
        self.paymentDetailsView.top = IPHONE_SCREEN_HEIGHT;
    };
    self.paymentDetailsView.showCustomViewAnimations = ^{
        self.paymentDetailsView.top = IPHONE_SCREEN_HEIGHT - self.paymentDetailsView.height;
    };
}

- (IBAction)scanQRCodeAction:(id)sender {
    QRCodeViewController *vc = [[QRCodeViewController alloc] init];
    vc.reciveResult = ^(NSString *result) {
    };
    [self QRCodeScanVC:vc];
}

- (IBAction)exchageAction:(UIButton*)sender {
    _easyModelView.hidden = !_easyModelView.hidden;
    _helpButton.hidden = !_helpButton.hidden;
    [self getTranscationNonceAndGasPrice:^{
        [self updateCost];
    }];
}

- (IBAction)sliderAction:(UISlider *)sender {
    NSLog(@"Value:%f",sender.value);
    [self updateCost];
}

- (void)updateCost{
    if (_easyModelView.hidden) {
        self.trans.gasPrice = [BigNumber bigNumberWithDecimalString:self.GASPriceTextField.text];
        self.trans.gasLimit = [BigNumber bigNumberWithDecimalString:self.GASTextField.text];
        _costLabel.text = [[self.GASTextField.text decimalNumberByMultiplying:self.GASPriceTextField.text] decimalNumberByDividing:DecimalNumberTenPower18];
    }else{
        self.trans.gasLimit = [BigNumber bigNumberWithDecimalString:[@"252000" decimalNumberByMultiplying:[NSString stringWithFormat:@"%.1f",self.mySlider.value]]];
        _costLabel.text = [[self.trans.gasLimit mul:self.trans.gasPrice].decimalString decimalNumberByDividing:DecimalNumberTenPower18];
    }
    NSLog(@"Limit:%@",self.trans.gasLimit.decimalString);
}

- (void)updateTranscationConfiguration{
    if (self.isContractsTransaction) {
        self.trans.toAddress = [Address addressWithString:OCNAddress];
        self.trans.value = [BigNumber bigNumberWithDecimalString:@"0"];
        NSString *value = [[NSString alloc] initWithFormat:@"%064llx",[self.amountTextField.text decimalNumberByMultiplying:DecimalNumberTenPower18].longLongValue];
        NSString *dataStr = [NSString stringWithFormat:@"%@%@%@",ContractTransferFunctionPrefix,[self.addressTextField.text stringByReplacingOccurrencesOfString:@"0x" withString:@""],value];
        self.trans.data = [SecureData hexStringToData:dataStr];
    }else{
        self.trans.toAddress = [Address addressWithString:self.addressTextField.text];
        self.trans.value = [BigNumber bigNumberWithDecimalString:[self.amountTextField.text decimalNumberByMultiplying:DecimalNumberTenPower18]];
    }
}

- (IBAction)nextStepAction:(id)sender {
    [self keyboardFinishAction];
    if (![self checkPass]) {
        return;
    }
    [self dispalyLoading:@"准备中..."];
    [self getTranscationNonceAndGasPrice:^{
        [self hideLoading:true];
        [self updateCost];
        [self updateTranscationConfiguration];
        [self showPaymentDetailsView];
    }];
}

- (BOOL)checkPass{
    if (self.addressTextField.text.length == 0) {
        [self dispalyText:@"请输入地址"];
        return false;
    }else if (self.amountTextField.text.length == 0){
        [self dispalyText:@"请输入转账金额"];
        return false;
    }else if (self.easyModelView.hidden){
        if (self.GASPriceTextField.text.length == 0) {
            [self dispalyText:@"请输入GAS价格"];
            return false;
        }else if (self.GASTextField.text.length == 0){
            [self dispalyText:@"请输入GAS数量"];
            return false;
        }
    }
    return true;
}


- (void)getTranscationNonceAndGasPrice:(dispatch_block_t)finish{
    
    BigNumberPromise *priceCallback = [self.provieder getGasPrice];
    IntegerPromise *nonceCallback = [self.provieder getTransactionCount:[Address addressWithString:self.wallet.address]];
    
    if (_easyModelView.hidden) {
        finish();
    }else{
        
        dispatch_group_t group = dispatch_group_create();
        dispatch_group_enter(group);
        [nonceCallback onCompletion:^(IntegerPromise *itp) {
            self.trans.nonce = itp.value;
            dispatch_group_leave(group);
        }];
        dispatch_group_enter(group);
        [priceCallback onCompletion:^(BigNumberPromise *p) {
            self.trans.gasPrice = p.value;
            dispatch_group_leave(group);
        }];
        dispatch_group_notify(group, dispatch_get_main_queue(), ^{
            if (finish) finish();
        });
    }
}

- (void)showPaymentDetailsView{
    self.paymentFromAddressLabel.text = self.wallet.address;
    self.paymentToAddressLabel.text = self.addressTextField.text;
    self.paymentCostLabel.text = [NSString stringWithFormat:@"%@ETH\nGas(%@)*Gas Price(%@wei)",self.costLabel.text,self.trans.gasLimit.decimalString,self.trans.gasPrice.decimalString];
    self.paymentAmountLabel.text = self.amountTextField.text;
    self.paymentDetailsView.height = IPHONE_HOME_INDICATOR_HEIGHT + 284;
    self.paymentDetailsView.width = IPHONE_SCREEN_WIDTH;
    self.paymentDetailsView.top = IPHONE_SCREEN_HEIGHT;
    [self dispalyCustomView:self.paymentDetailsView];
}

- (IBAction)pamentDetailViewNextAction:(id)sender {
    [self closePaymentDetailsView];
    [self checkPassWithPassword:self.wallet.password completion:^(BOOL pass) {
        if (pass) {
            [self send:self.trans];
        }else{
            [self dispalyConfirmText:@"Wrong password"];
        }
    }];
}

- (void)send:(Transaction *)trans{
    Account *a = [Account accountWithPrivateKey:[SecureData hexStringToData:self.wallet.privateKey]];
    [a sign:trans];
    HashPromise *sendCallback = [self.provieder sendTransaction:[trans serialize]];
    [self dispalyLoading:@"转帐中..."];
    [sendCallback onCompletion:^(HashPromise *hash) {
        [self hideLoading:NO];
        if (hash.value.hexString.length > 0) {
            [self dispalyText:@"已转帐"];
            [self.wallet.transactionCache addObject:hash.value.hexString];
        }else{
            [self dispalyText:@"转帐失败"];
        }
        NSLog(@"transctionResult:%@",hash.value.hexString);
        [self closePaymentDetailsView];
    }];
}

- (IBAction)contactAction:(id)sender {
    
}

- (IBAction)closePaymentDetailsView{
    [self closeCustomView:self.paymentDetailsView];
}

- (IBAction)helpAction:(id)sender {
    //    NSString *b = [[NSString alloc] initWithFormat:@"%064x",131415];
    
//        Transaction *trans = [Transaction transaction];
//        trans.toAddress = [Address addressWithString:OCNAddress];
//        trans.gasLimit = [BigNumber bigNumberWithDecimalString:@"252000"];
//        trans.value = [BigNumber bigNumberWithDecimalString:@"0"];
//        NSString *dataStr = [NSString stringWithFormat:@"%@%@%@",ContractTransferFunctionPrefix,@"F7e39822C2faFA4DB665b02A542318410e72C511",[[NSString alloc] initWithFormat:@"%064lx",249000000000000000]];
//        trans.data = [SecureData hexStringToData:dataStr];
//
//        EtherscanProvider *provieder = [[EtherscanProvider alloc]initWithChainId:ChainType];
//        BigNumberPromise *price = [provieder getGasPrice];
//        IntegerPromise *iter = [provieder getTransactionCount:[Address addressWithString:self.wallet.address]];
//        [iter onCompletion:^(IntegerPromise *itp) {
//            trans.nonce = itp.value;
//            [price onCompletion:^(BigNumberPromise *p) {
//                trans.gasPrice = p.value;
//                Account *a = [Account accountWithPrivateKey:[SecureData hexStringToData:self.wallet.privateKey]];
//                [a sign:trans];
//                HashPromise *hp = [provieder sendTransaction:[trans serialize]];
//                [hp onCompletion:^(HashPromise *hhh) {
//                    NSLog(@"%@",hhh.value.hexString);
//                }];
//            }];
//        }];
    
    
    
    //    Transaction *trans = [Transaction transaction];
    //    trans.toAddress = [Address addressWithString:@"0xF7e39822C2faFA4DB665b02A542318410e72C511"];
    //    trans.gasLimit = [BigNumber bigNumberWithDecimalString:@"252000"];
    //    trans.value = [BigNumber bigNumberWithDecimalString:@"1"];
    //    EtherscanProvider *provieder = [[EtherscanProvider alloc]initWithChainId:ChainType];
    //    BigNumberPromise *price = [provieder getGasPrice];
    //    IntegerPromise *iter = [provieder getTransactionCount:[Address addressWithString:self.wallet.address]];
    //    [iter onCompletion:^(IntegerPromise *itp) {
    //        trans.nonce = itp.value;
    //        [price onCompletion:^(BigNumberPromise *p) {
    //            trans.gasPrice = p.value;
    //            Account *a = [Account accountWithPrivateKey:[SecureData hexStringToData:self.wallet.privateKey]];
    //            [a sign:trans];
    //            HashPromise *hp = [provieder sendTransaction:[trans serialize]];
    //            [hp onCompletion:^(HashPromise *hhh) {
    //                NSLog(@"%@",hhh.value.hexString);
    //            }];
    //        }];
    //    }];
}

- (IBAction)textFieldEditingChanged:(UITextField *)sender {
    
}

#pragma mark - 键盘处理
- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField{
    [textField setInputAccessoryView:self.textFieldAccessoryView];
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
    if (!self.manager.keyboardVisible) {
        return;
    }
    CGRect frame = self.manager.keyboardFrame;
    frame = [self.manager convertRect:frame toView:self.myContentView];
    [self calculateOffsetForScrollViewWithTextField:textField keyboardFrame:frame];
}

- (void)keyboardFinishAction{
    [self closeKeyboard];
}

- (void)closeKeyboard{
    [self.view endEditing:YES];
//    NSLog(@"%f\n%f",self.contentScrollView.contentSize.height,self.contentScrollView.bounds.size.height);
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
    CGRect toFrame =  [self.manager convertRect:transition.toFrame toView:self.myContentView];
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
        _trans = [Transaction transaction];
    }
    return _trans;
}

- (EtherscanProvider *)provieder{
    if (!_provieder) {
        _provieder = [[EtherscanProvider alloc]initWithChainId:ChainType];
    }
    return _provieder;
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
