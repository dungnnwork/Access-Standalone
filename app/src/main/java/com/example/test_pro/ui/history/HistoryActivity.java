package com.example.test_pro.ui.history;

import android.graphics.Typeface;
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
import com.example.test_pro.model.database.EventAuthModel;
import com.example.test_pro.ui.base.BaseActivity;
import com.example.test_pro.ui.component.Appbar;
import com.example.test_pro.ui.history.adapter.HistoryAdapter;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.DatetimeUtil;
import com.example.test_pro.ultis.SizeUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {
    private int currentPage = 1;
    private final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private List<EventAuthModel> eventAuthModelList = new ArrayList<>();
    private HistoryAdapter historyAdapter;
    private long totalEventAuth = 0;
    private DatabaseLocal db;
    private ProgressBar progressBar;
//    private final String TAG = "HISTORY_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseLocal.getInstance(this);
        loadTotalEventAuth();
        layoutScreen();
    }

    private void layoutScreen() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(ColorsUtil.WHITE_BG);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Appbar appbar = new Appbar(this, true, false, null, 0, v -> finish());
        linearLayout.addView(appbar);
        linearLayout.addView(SizeUtils.createSpaceHeight(50, this));
        linearLayout.addView(getListViewHistory());
        setContentView(linearLayout);
    }
    @NonNull
    private LinearLayout getListViewHistory() {
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
                    if (eventAuthModelList.size() < totalEventAuth) {
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


        eventAuthModelList = db.getEventAuthPagination(currentPage, PAGE_SIZE);
        sortEventAuthModelList(eventAuthModelList);

        historyAdapter = new HistoryAdapter(this, eventAuthModelList);
        listView.setAdapter(historyAdapter);
        if (!eventAuthModelList.isEmpty()) {
            linearLayout.addView(listView);
        } else {
            TextView textView = new TextView(this);
            linearLayout.setGravity(Gravity.CENTER);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextSize(32);
            textView.setText(getText(R.string.no_events));
            linearLayout.addView(textView);
        }
        return linearLayout;
    }
    private void sortEventAuthModelList(List<EventAuthModel> useEventModelListVal) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatetimeUtil.YYYY_MM_DD_HH_MM_SS);
        try {
            useEventModelListVal.sort((a, b) ->
                    LocalDateTime.parse(b.getCreatedAt(), formatter)
                            .compareTo(LocalDateTime.parse(a.getCreatedAt(), formatter))
            );
        } catch (Exception e) {
            Log.i("exception", "exception" + " " + e);
        }
    }
    private void loadTotalEventAuth() {
        totalEventAuth = db.getTotalRecordsEventAuth();
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
    private void loadMoreData(int page) {
        new Handler().postDelayed(() -> {
            List<EventAuthModel> newData = db.getEventAuthPagination(page, PAGE_SIZE);
            if (!newData.isEmpty()) {
                eventAuthModelList.addAll(newData);
                historyAdapter.notifyDataSetChanged();
            }
            isLoading = false;
            hideLoading();
        }, 300);
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

}
