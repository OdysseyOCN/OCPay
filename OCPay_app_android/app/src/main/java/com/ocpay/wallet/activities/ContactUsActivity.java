package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.R;
import com.ocpay.wallet.activities.lockpattern.CreateGestureActivity;
import com.ocpay.wallet.adapter.FullyGridLayoutManager;
import com.ocpay.wallet.adapter.SpaceItemDecoration;
import com.ocpay.wallet.bean.FeedbackBean;
import com.ocpay.wallet.databinding.ActivityContactUsBinding;
import com.ocpay.wallet.http.client.OCPayHttpClientIml;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.ocpay.wallet.widget.pictureselect.GridImageAdapter;
import com.snow.commonlibrary.utils.RegularExpressionUtils;
import com.snow.commonlibrary.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.ERROR.RESPONSE_ERROR;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_CONTACT_US_SEND_MSG;

public class ContactUsActivity extends BaseActivity implements View.OnClickListener, GridImageAdapter.onAddPicClickListener {


    private ActivityContactUsBinding binding;
    private int protectMode;
    private List<LocalMedia> selectList;
    private List<String> imageResponse;
    private GridImageAdapter adapter;


    public static void startContactUsActivity(Activity activity) {
        Intent intent = new Intent(activity, ContactUsActivity.class);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ContactUsActivity.this, R.layout.activity_contact_us);
        initActionBar();
        initView();
//        initTest();
        initRxBus();

    }

    private void initTest() {

        binding.etTheme.setText("fs@qq.com");
        binding.etDesc.setText("fs@qq.com");
        binding.etEmail.setText("fs@qq.com");
    }

    private void initListener() {


    }

    private void initRxBus() {
        final Disposable imageDisposable = RxBus.getInstance()
                .toObservable(Constans.RXBUS.ACTION_CONTACT_US_POST_IMAGE, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        if (response == null || RESPONSE_ERROR.equals(response)) {
                            WarmDialog.showTip(ContactUsActivity.this, getString(R.string.tip_contact_us_submit_failed));
                            dismissLoading();
                            return;
                        }
                        if (imageResponse == null) imageResponse = new ArrayList<>();
                        imageResponse.add(response);

                        if (imageResponse.size() == selectList.size()) {
                            postMessage();
                        }

                    }
                });
        addDisposable(imageDisposable);

        Disposable messageDisposable = RxBus.getInstance()
                .toObservable(ACTION_CONTACT_US_SEND_MSG, Boolean.class)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        dismissLoading();
                        if (success) {
                            WarmDialog.showTip(ContactUsActivity.this, getString(R.string.tip_contact_us_submit_success));
                        } else {
                            WarmDialog.showTip(ContactUsActivity.this, getString(R.string.tip_contact_us_submit_failed));

                        }

                    }
                });
        addDisposable(messageDisposable);


    }

    private void postMessage() {
        FeedbackBean feedbackBean = new FeedbackBean();
        feedbackBean.setEmail(binding.etEmail.getText().toString().trim());
        feedbackBean.setDescription(binding.etDesc.getText().toString().trim());
        feedbackBean.setTheme(binding.etTheme.getText().toString().trim());


        if (imageResponse != null && imageResponse.size() > 0) {
            feedbackBean.setImg1(imageResponse.get(0));
        }
        if (imageResponse != null && imageResponse.size() > 1) {
            feedbackBean.setImg2(imageResponse.get(1));

        }
        if (imageResponse != null && imageResponse.size() > 2) {
            feedbackBean.setImg3(imageResponse.get(2));

        }
        if (imageResponse != null && imageResponse.size() > 3) {
            feedbackBean.setImg4(imageResponse.get(3));

        }
        OCPayHttpClientIml.sendFeedback(ACTION_CONTACT_US_SEND_MSG, feedbackBean);

    }


    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(R.string.settings_contact_us);
        binding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_back);
        binding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);
        binding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        binding.includeActionBar.tbTvMenu.setVisibility(View.VISIBLE);
        binding.includeActionBar.tbTvMenu.setText(getString(R.string.btn_bottom_send));
        binding.includeActionBar.tbTvMenu.setOnClickListener(this);
    }


    private void initView() {
        adapter = new GridImageAdapter(ContactUsActivity.this, this);
        adapter.setList(null);
        adapter.setSelectMax(4);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(ContactUsActivity.this, 4, GridLayoutManager.VERTICAL, false);
        binding.recycler.setLayoutManager(manager);
        binding.recycler.addItemDecoration(new SpaceItemDecoration(ContactUsActivity.this, -1));
        binding.recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(ContactUsActivity.this).themeStyle(R.style.picture_Sina_style).openExternalPreview(position, selectList);
                            break;

                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
                        Log.i("图片-----》", media.getPath());
                    }
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    public void verifyGesture() {
        CreateGestureActivity.startCreateGestureActivity(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_menu_icon:
                ContactsCreateActivity.startContactsCreateActivity(ContactUsActivity.this);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tb_tv_menu:
                contactUs();
                break;

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAddPicClick() {
        PictureSelector.create(ContactUsActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_Sina_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(4)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .enableCrop(false)// 是否裁剪
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
//                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .selectionMedia(selectList)// 是否传入已选图片
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }


    private void contactUs() {

        boolean valid = checkInput();
        if (valid) {
            showLoading(false);
            if (selectList != null && selectList.size() > 0) {
                if (imageResponse != null) imageResponse.clear();
                for (int i = 0; i < selectList.size(); i++) {
                    String path = selectList.get(i).getPath();
                    File file = new File(path);
                    if (!file.exists()) {
                        return;
                    }
                    OCPayHttpClientIml.imagePost(Constans.RXBUS.ACTION_CONTACT_US_POST_IMAGE, file);
                }
            } else {
                postMessage();
            }
        }


    }

    private boolean checkInput() {
        String email = binding.etEmail.getText().toString().trim();
        if (StringUtil.isEmpty(email) || !RegularExpressionUtils.valid(email, Constans.REGULAR.REGULAR_EMAIL)) {
            WarmDialog.showTip(this, getString(R.string.tip_contact_us_email_format_error));
            return false;
        }


        String desc = binding.etDesc.getText().toString().trim();
        if (StringUtil.isEmpty(desc)) {
            WarmDialog.showTip(this, getString(R.string.tip_input_not_null));
            return false;
        }

        String theme = binding.etTheme.getText().toString().trim();
        if (StringUtil.isEmpty(theme)) {
            WarmDialog.showTip(this, getString(R.string.tip_input_not_null));
            return false;
        }


        return true;
    }
}
