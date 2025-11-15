package com.example.test_pro.ui.members;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.test_pro.R;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.model.database.MemberModel;
import com.example.test_pro.ui.base.BaseActivity;
import com.example.test_pro.ui.component.Appbar;
import com.example.test_pro.ui.members.adapter.MembersAdapter;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.DatetimeUtil;
import com.example.test_pro.ultis.DialogUtil;
import com.example.test_pro.ultis.FunctionUtil;
import com.example.test_pro.ultis.SizeUtils;
import com.example.test_pro.ultis.ToastUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MembersActivity extends BaseActivity {
    private MembersAdapter membersAdapter;
    private int currentPage = 1;
    private final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private List<MemberModel> memberModelList = new ArrayList<>();
    private long totalMemberModel = 0;
    private DatabaseLocal db;
    private ProgressBar progressBar;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private final String TAG = "MEMBERS_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseLocal.getInstance(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
        );
        loadTotalMember();
        layoutScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter == null) {
            Log.i(TAG, "NFC not supported");
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.not_supported_nfc), this, false, null);
        } else {
            IntentFilter[] intentFiltersArray = new IntentFilter[]{};
            String[][] techListsArray = new String[][]{
                    new String[]{android.nfc.tech.NfcA.class.getName()},
                    new String[]{android.nfc.tech.NfcB.class.getName()},
                    new String[]{android.nfc.tech.NfcF.class.getName()},
                    new String[]{android.nfc.tech.NfcV.class.getName()},
                    new String[]{android.nfc.tech.IsoDep.class.getName()},
                    new String[]{android.nfc.tech.MifareClassic.class.getName()},
                    new String[]{android.nfc.tech.MifareUltralight.class.getName()},
                    new String[]{android.nfc.tech.Ndef.class.getName()}
            };
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }

    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                Log.i(TAG, "Read nfc" + " " + "NFC");
                byte[] tagId = tag.getId();
                String hexId = FunctionUtil.bytesToHex(tagId);
                if(membersAdapter != null) {
                    membersAdapter.updateCardIdFromNFC(hexId);
                }
                Log.i(TAG, "Hex_ID :" + hexId);
            } else {
                ToastUtil.show(this, getString(R.string.not_read_card_code));
                Log.i(TAG, "Read nfc" + " " + "tag null");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    // TODO [Layout]
    private void layoutScreen() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(ColorsUtil.WHITE_BG);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Appbar appbar = new Appbar(this, true, false, null, 0, v -> finish());
        linearLayout.addView(appbar);
        linearLayout.addView(SizeUtils.createSpaceHeight(50, this));
        linearLayout.addView(getListViewMembers());
        setContentView(linearLayout);
    }

    @NonNull
    private LinearLayout getListViewMembers() {
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setLayoutParams(params);
        ListView listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(10);
        LinearLayout.LayoutParams listViewParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        listView.setLayoutParams(listViewParams);
        progressBar = new ProgressBar(this);
        listView.addFooterView(progressBar);
        progressBar.setVisibility(View.GONE);
        listView.setPadding(32, 0, 32, 20);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && (firstVisibleItem + visibleItemCount >= totalItemCount) && totalItemCount > 0) {
                    if (memberModelList.size() < totalMemberModel) {
                        isLoading = true;
                        showLoading();
                        currentPage++;
                        loadMoreData(currentPage);
                        return;
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        memberModelList = db.getMemberModelPagination(currentPage, PAGE_SIZE);
        sortMemberModelList(memberModelList);

        membersAdapter = new MembersAdapter(memberModelList, this, db);
        listView.setAdapter(membersAdapter);
        if (!memberModelList.isEmpty()) {
            linearLayout.addView(listView);
        } else {
            TextView textView = new TextView(this);
            linearLayout.setGravity(Gravity.CENTER);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextSize(32);
            textView.setText(getText(R.string.empty_list));
            linearLayout.addView(textView);
        }
        return linearLayout;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminateTintList(
                android.content.res.ColorStateList.valueOf(ColorsUtil.BLUE)
        );
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(300);
        progressBar.startAnimation(fadeIn);
    }

    private void hideLoading() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(300);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        progressBar.startAnimation(fadeOut);
        isLoading = false;
    }

    // TODO [data]

    private void sortMemberModelList(List<MemberModel> memberModelList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatetimeUtil.YYYY_MM_DD_HH_MM_SS);
        try {
            memberModelList.sort((a, b) ->
                    LocalDateTime.parse(b.getCreatedAt(), formatter)
                            .compareTo(LocalDateTime.parse(a.getCreatedAt(), formatter))
            );
        } catch (Exception e) {
            Log.i("exception", "exception" + " " + e);
        }
    }

    private void loadMoreData(int page) {
        new Handler().postDelayed(() -> {
            List<MemberModel> newData = db.getMemberModelPagination(page, PAGE_SIZE);
            if (!newData.isEmpty()) {
                memberModelList.addAll(newData);
                membersAdapter.notifyDataSetChanged();
            }
            isLoading = false;
            hideLoading();
        }, 300);
    }
    private void loadTotalMember() {
        totalMemberModel = db.getTotalRecordsMemberModel();
    }

    public void refreshMembersList() {
        showLoading();
        new Handler().postDelayed(() -> {
            memberModelList.clear();
            currentPage = 1;
            loadTotalMember();
            memberModelList.addAll(db.getMemberModelPagination(currentPage, PAGE_SIZE));
            sortMemberModelList(memberModelList);
            membersAdapter.notifyDataSetChanged();
            hideLoading();
        }, 300);
    }


}
