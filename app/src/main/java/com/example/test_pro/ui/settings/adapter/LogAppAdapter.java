package com.example.test_pro.ui.settings.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import com.example.test_pro.model.database.LogAppModel;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.DatetimeUtil;
import java.util.List;

public class LogAppAdapter extends ArrayAdapter<LogAppModel> {
    private final Context context;
    private final List<LogAppModel> logAppModelList;

    public LogAppAdapter(Context context, List<LogAppModel> logAppModelList) {
        super(context, 0, logAppModelList);
        this.context = context;
        this.logAppModelList = logAppModelList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LogAppModel logAppModel = logAppModelList.get(position);

        if (!(convertView instanceof CardView)) {
            convertView = new CardView(context);
            ((CardView) convertView).setRadius(12);
            ((CardView) convertView).setCardElevation(4);
            ((CardView) convertView).setUseCompatPadding(true);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            ((CardView) convertView).setContentPadding(24, 16, 24, 16);
        } else {
            ((CardView) convertView).removeAllViews();
        }

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Function
        TextView functionText = new TextView(context);
        String text = "• " + logAppModel.getFunction();
        functionText.setText(text);
        functionText.setTypeface(Typeface.DEFAULT_BOLD);
        functionText.setTextSize(16);
        functionText.setTextColor(ColorsUtil.TEXT_DARK);

        // Time
        TextView timeText = new TextView(context);
        timeText.setText(DatetimeUtil.convertDatetimeFormat(logAppModel.getCreatedAt()));
        timeText.setTextSize(12);
        timeText.setTextColor(ColorsUtil.TEXT_GRAY);

        TextView contentText = getTextView(logAppModel);

        layout.addView(functionText);
        layout.addView(timeText);
        layout.addView(contentText);

        ((CardView) convertView).addView(layout);

        return convertView;
    }

    @NonNull
    private TextView getTextView(@NonNull LogAppModel logAppModel) {
        TextView contentText = new TextView(context);
        contentText.setText(logAppModel.getLogContent());
        contentText.setTextSize(14);
        contentText.setTextColor(ColorsUtil.TEXT_MEDIUM);
        contentText.setPadding(0, 8, 0, 0);
        contentText.setMaxLines(Integer.MAX_VALUE);
        contentText.setEllipsize(null);
        contentText.setLineSpacing(0, 1.2f);
        contentText.setMovementMethod(new ScrollingMovementMethod());
        contentText.setVerticalScrollBarEnabled(true);
        return contentText;
    }

}
