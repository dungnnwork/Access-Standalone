
package com.example.test_pro.ui.history.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.common.enum_common.AuthMethod;
import com.example.test_pro.model.database.EventAuthModel;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.ConvertByteToImage;
import com.example.test_pro.ultis.DatetimeUtil;
import com.example.test_pro.ultis.SizeUtils;
import com.example.test_pro.R;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<EventAuthModel> {
    private final Context context;
    private final List<EventAuthModel> eventAuthModelList;

    public HistoryAdapter(Context context, List<EventAuthModel> eventAuthModelList) {
        super(context, 0, eventAuthModelList);
        this.context = context;
        this.eventAuthModelList = eventAuthModelList;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        EventAuthModel eventAuthModel = this.eventAuthModelList.get(position);

        if (!(convertView instanceof CardView)) {
            convertView = getCardView(parent);
        } else {
            ((LinearLayout) ((CardView) convertView).getChildAt(0)).removeAllViews();
        }

        CardView cardViewImage = new CardView(context);
        cardViewImage.setRadius(8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 100
        );
        params.gravity = Gravity.CENTER;
        cardViewImage.setCardElevation(2);
        cardViewImage.setLayoutParams(params);

        // ImageView
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                100, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        String filePath = eventAuthModel.getFilePath();
        Bitmap bitmap = ConvertByteToImage.imgDataGet(filePath, context);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.photo);
        }

        cardViewImage.addView(imageView);
        LinearLayout linearLayout = (LinearLayout) ((CardView) convertView).getChildAt(0);
        linearLayout.addView(cardViewImage);
        linearLayout.addView(SizeUtils.createSpaceWidth(30, context));
        View infoEventAuth = infoEventAuth(eventAuthModel);
        LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                5
        );
        infoEventAuth.setLayoutParams(paramsInfo);
        linearLayout.addView(infoEventAuth);
        ImageView useCaseView = statusEventAuth(eventAuthModel.getUseCase());
        LinearLayout.LayoutParams useCaseParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                2
        );
        useCaseParams.gravity = Gravity.CENTER_VERTICAL;
        useCaseParams.setMargins(0, 16, 0, 16);
        useCaseView.setLayoutParams(useCaseParams);
        linearLayout.addView(useCaseView);
        return convertView;
    }

    @NonNull
    private CardView getCardView(@NonNull ViewGroup parent) {
        Context contextParent = parent.getContext();

        CardView cardView = new CardView(contextParent);
        cardView.setRadius(12);
        cardView.setCardElevation(3);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(16, 16, 16, 16);

        // LinearLayout
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardView.setLayoutParams(params);

        cardView.addView(linearLayout);
        return cardView;
    }

    @NonNull
    private View infoEventAuth(@NonNull EventAuthModel eventAuthModel) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        // Row 1

        TextView txtName = new TextView(context);
        txtName.setTypeface(Typeface.DEFAULT_BOLD);
        txtName.setTextSize(16);
        String nameStr = eventAuthModel.getUserName() != null ? eventAuthModel.getUserName() : context.getString(R.string.unknown);
        txtName.setText(nameStr);
        View spaceFirst = new View(context);
        spaceFirst.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        ));
        View spaceSecond = new View(context);
        spaceSecond.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        ));
        TextView txtTime = new TextView(context);
        txtTime.setTypeface(Typeface.DEFAULT_BOLD);
        txtTime.setTextSize(16);
        txtTime.setText(DatetimeUtil.convertDatetimeFormat(eventAuthModel.getCreatedAt()));
        //
        TextView txtMethod = new TextView(context);
        txtMethod.setTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);
        txtMethod.setTextSize(16);
        String methodStr;
        if(eventAuthModel.getAuthMethod() == AuthMethod.CARD) {
            methodStr = context.getString(R.string.card_method);
        } else {
            methodStr = context.getString(R.string.face_method);
        }
        txtMethod.setTextColor(ColorsUtil.BLUE);
        txtMethod.setText(methodStr);
        //
        linearLayout.addView(txtName);
        linearLayout.addView(spaceFirst);
        linearLayout.addView(txtTime);
        linearLayout.addView(spaceSecond);
        linearLayout.addView(txtMethod);
        return linearLayout;
    }
    @NonNull
    private ImageView statusEventAuth(int useCase) {
        ImageView imgStatus = new ImageView(context);
        imgStatus.setAdjustViewBounds(true);
        imgStatus.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        if (useCase == NumericConstants.RESULT_SUCCESS) {
            imgStatus.setImageResource(R.drawable.success);
        } else {
            imgStatus.setImageResource(R.drawable.failed);
        }

        return imgStatus;
    }


}
