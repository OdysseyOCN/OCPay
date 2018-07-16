package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.adapter.ContactsListAdapter;
import com.ocpay.wallet.databinding.ActivityContactsBinding;
import com.ocpay.wallet.greendao.Contact;
import com.ocpay.wallet.greendao.manager.ContactDaoUtils;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.utils.OCPPrefUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_SELECT_CONTACT;

public class ContactsActivity extends BaseActivity implements View.OnClickListener {


    private ActivityContactsBinding binding;
    private List<Contact> contactList;
    private ContactsListAdapter contactsListAdapter;
    public static final int MODE_DEFULT = 0;
    public static final int MODE_Select = 1;
    public static final String ACTIVITY_MODE = "contacts_activity_mode";
    private int currentMode;

    public static void startContactsActivity(Activity activity) {
        startContactsActivity(activity, MODE_DEFULT);
    }

    public static void startContactsActivity(Activity activity, int mode) {
        Intent intent = new Intent(activity, ContactsActivity.class);
        intent.putExtra(ACTIVITY_MODE, mode);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ContactsActivity.this, R.layout.activity_contacts);
        currentMode = getIntent().getIntExtra(ACTIVITY_MODE, 0);
        initActionBar();
        initData();
        initListView();
        initRxBus();

    }

    private void initRxBus() {
        Disposable disposable = RxBus.getInstance()
                .toObservable(Constans.RXBUS.ACTION_CONTACTS_UPDATE, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        List<Contact> contacts = ContactDaoUtils.sqlAll(MyApp.getContext());
                        contactList.clear();
                        contactList.addAll(contacts);
                        showListView();
                        contactsListAdapter.notifyDataSetChanged();
                    }
                });

        addDisposable(disposable);
    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(R.string.activity_title_contacts);
        binding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_close_black);
        binding.includeActionBar.toolbarMenuIcon.setImageResource(R.mipmap.ic_add);
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.toolbarMenuIcon.setOnClickListener(this);
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);
    }

    private void initData() {
        contactList = ContactDaoUtils.sqlAll(MyApp.getContext());
        if (contactList == null) {
            contactList = new ArrayList<>();
        }
    }


    private void initListView() {
        showListView();
        contactsListAdapter = new ContactsListAdapter(ContactsActivity.this, contactList);
        binding.slvContacts.setAdapter(contactsListAdapter);
        contactsListAdapter.setOnItemClickListener(new ContactsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact contact, int position) {
                if (currentMode != MODE_Select) return;
                RxBus.getInstance().post(ACTION_SELECT_CONTACT, contact.getWalletAddress());
                finish();
            }
        });

    }

    private void showListView() {
        if (contactList == null || contactList.size() <= 0) {
            binding.includeNoRecord.rlNullRecord.setVisibility(View.VISIBLE);
            binding.slvContacts.setVisibility(View.GONE);
        } else {
            binding.includeNoRecord.rlNullRecord.setVisibility(View.GONE);
            binding.slvContacts.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_menu_icon:
                ContactsCreateActivity.startContactsCreateActivity(ContactsActivity.this);
                break;
            case R.id.iv_back:
                finish();
                break;

        }

    }


}
