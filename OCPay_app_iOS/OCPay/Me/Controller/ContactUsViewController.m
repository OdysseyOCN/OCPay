//
//  ContactUsViewController.m
//  OCPay
//
//  Created by 何自梁 on 2018/6/23.
//  Copyright © 2018年 menggen. All rights reserved.
//

#import "ContactUsViewController.h"
#import <TZImagePickerController/TZImagePickerController.h>
#import "ContactUsCollectionViewCell.h"

@interface ContactUsViewController ()<UICollectionViewDelegateFlowLayout,UICollectionViewDataSource,TZImagePickerControllerDelegate>
@property (weak, nonatomic) IBOutlet UITextField *emailAddressTextField;
@property (weak, nonatomic) IBOutlet UITextField *themeTextField;
@property (weak, nonatomic) IBOutlet UITextView *myTextView;
@property (weak, nonatomic) IBOutlet UICollectionViewFlowLayout *myFlowLayout;
@property (nonatomic, strong) NSMutableArray *selectedImages;
@property (weak, nonatomic) IBOutlet UICollectionView *myCollectionView;
@property (nonatomic, strong) TZImagePickerController *imagePickerVc;
@end

@implementation ContactUsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    _myTextView.inputAccessoryView = self.keyboardAccessoryView;
    _myTextView.zw_placeHolder = @"Enter a description of the problem. If necessary, please cooperate with the picture";
    _myTextView.zw_placeHolderColor = UIColorHex(#B5BDC8);
}

- (TZImagePickerController *)imagePickerVc{
    if (!_imagePickerVc) {
        _imagePickerVc = [[TZImagePickerController alloc] initWithMaxImagesCount:4 delegate:self];
    }
    return _imagePickerVc;
}

- (IBAction)sendAction:(id)sender {
    [self.view endEditing:YES];
    if (self.emailAddressTextField.text.length == 0) {
        [self dispalyText:@"Email is empty!"];
        return;
    }else if (_themeTextField.text.length == 0){
        [self dispalyText:@"Theme is empty!"];
        return;
    }else if (_myTextView.text.length == 0){
        [self dispalyText:@"Description is empty!"];
        return;
    }
    [self feedback];
}

- (void)feedback{
    [self dispalyLoading:@"Uploading..."];
    [self uploadImagesFinish:^(NSArray *imageURLArr) {
        NSMutableDictionary *par = [NSMutableDictionary dictionary];
        [imageURLArr enumerateObjectsUsingBlock:^(NSString *obj, NSUInteger idx, BOOL * _Nonnull stop) {
            [par setValue:obj forKey:[NSString stringWithFormat:@"img%ld",idx+1]];
        }];
        [par setValue:self.emailAddressTextField.text forKey:@"email"];
        [par setValue:self.themeTextField.text forKey:@"theme"];
        [par setValue:self.myTextView.text forKey:@"description"];
        [NetWorkManager PostWithURL:@"api/ocpay/v1/token/add-feedback" parameters:par success:^(__kindof NSObject *data) {
            [self hideLoading:YES];
            [self.navigationController dispalyText:@"Feedback success!"];
            [self.navigationController popViewControllerAnimated:YES];
        } failure:^(NSError *error) {
            [self hideLoading:YES];
        }];
    }];
}

- (void)uploadImagesFinish:(void(^)(NSArray *imageURLArr))finish{
    NSMutableArray *imageURLArr = [NSMutableArray array];
    dispatch_group_t group = dispatch_group_create();
    for (UIImage *image in self.selectedImages) {
        NSData *imageData = UIImageJPEGRepresentation(image,0.3);
        dispatch_group_enter(group);
        [NetWorkManager UploadWithURL:@"api/ocpay/upload/token/v1/file" fileData:imageData fileName:@"111.jpg" mimeType:@"image/jpeg" success:^(__kindof NSDictionary *data) {
            NSDictionary *dataImageURL = data[@"data"];
            NSString *imageURL = dataImageURL[@"message"];
            if (imageURL) {
                [imageURLArr addObject:imageURL];
            }
            dispatch_group_leave(group);
        } failure:^(NSError *error) {
            dispatch_group_leave(group);
        }];
    }
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        NSLog(@"图片链接:%@",imageURLArr);
        if (finish) {
            finish(imageURLArr);
        }
    });
}

- (void)imagePickerController:(TZImagePickerController *)picker didFinishPickingPhotos:(NSArray<UIImage *> *)photos sourceAssets:(NSArray *)assets isSelectOriginalPhoto:(BOOL)isSelectOriginalPhoto{
    [self.selectedImages removeAllObjects];
    [photos enumerateObjectsUsingBlock:^(UIImage * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        [self.selectedImages addObject:obj];
    }];
    [self.myCollectionView reloadData];
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return self.selectedImages.count+1;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath{
    return CGSizeMake(40, 40);
}

- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath{
    ContactUsCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"myItem" forIndexPath:indexPath];
    cell.index = indexPath.row;
    @weakify(self)
    cell.deleteCallback = ^(NSInteger index) {
        @strongify(self)
       [self.selectedImages removeObjectAtIndex:index];
        [self.myCollectionView reloadData];
    };
    if (indexPath.row == self.selectedImages.count) {
        [cell.myImageView setImage:[UIImage imageNamed:@"添加图片 copy"]];
        [cell.myDeleteButton setHidden:true];
        cell.myImageView.closePreview = YES;
    }else{
        [cell.myImageView setImage:self.selectedImages[indexPath.row]];
        [cell.myDeleteButton setHidden:false];
        cell.myImageView.closePreview = NO;
    }
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath{
    if (indexPath.row == self.selectedImages.count) {
        [self presentViewController:self.imagePickerVc animated:YES completion:nil];
    }
}

- (NSMutableArray *)selectedImages{
    if (!_selectedImages) {
        _selectedImages = [NSMutableArray array];
    }
    return _selectedImages;
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
