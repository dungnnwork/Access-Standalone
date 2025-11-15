package com.example.test_pro.ui.company.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test_pro.R;
import com.example.test_pro.model.DurationDetectModel;

import java.io.File;
import java.util.List;

public class DurationDetectAdapter extends ArrayAdapter<DurationDetectModel> {
    private final Context context;

    public DurationDetectAdapter(Context context, List<DurationDetectModel> data) {
        super(context, 0, data);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DurationDetectModel item = getItem(position);
        if(item == null) {
            TextView textView = new TextView(context);
            String text = "No content";
            textView.setText(text);
            return textView;
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_duration_detect, parent, false);
        }

        ImageView imgAvatar = convertView.findViewById(R.id.imgAvatar);
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtCaseDetect = convertView.findViewById(R.id.txtCaseDetect);
        TextView txtDuration = convertView.findViewById(R.id.txtDuration);
        TextView txtSimilarity = convertView.findViewById(R.id.txtSimilarity);

        // Set text info
        File file = new File(item.getName());
        String rawName = file.getName();

        int dotIndex = rawName.lastIndexOf('.');
        String nameWithoutExt = (dotIndex > 0) ? rawName.substring(0, dotIndex) : rawName;

        String name = "Name: " + " " + nameWithoutExt;
        txtName.setText(name);

        // Case
        String caseDetect = "Case: " + " " + item.getCaseDetect();
        txtCaseDetect.setText(caseDetect);
        // Time
        String time = "Time: " + " " + item.getDuration() + " " + "ms";
        txtDuration.setText(time);
        // Similarity
        String similarity = "Similarity: " + " " + item.getSimilarity();
        txtSimilarity.setText(similarity);

        File imgFile = new File(item.getName());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgAvatar.setImageBitmap(bitmap);
        } else {
            imgAvatar.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        return convertView;
    }
}
