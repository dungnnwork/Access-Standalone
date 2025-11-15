package com.example.test_pro.ui.company;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.test_pro.R;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.model.DurationDetectModel;
import com.example.test_pro.ui.base.BaseActivity;
import com.example.test_pro.ui.company.adapter.DurationDetectAdapter;

import java.util.List;

public class CompanyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_activity);
        setupToolbar();
        layoutToolbar();

        ListView listViewDuration = findViewById(R.id.listViewDuration);
        List<DurationDetectModel> data = DatabaseLocal.getInstance(this).getAllDurationDetect();
        DurationDetectAdapter adapter = new DurationDetectAdapter(this, data);
        listViewDuration.setAdapter(adapter);
    }

    private void layoutToolbar() {
        TextView textViewToolbar = findViewById(R.id.toolbar_title);
        textViewToolbar.setText(getText(R.string.history));
        ImageView imageViewToolbar = findViewById(R.id.toolbar_action_icon);
        imageViewToolbar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.add_user));
    }
}
