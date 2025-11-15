package com.example.test_pro.ui.members.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.test_pro.R;
import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.model.database.EventAuthModel;
import com.example.test_pro.model.database.MemberModel;
import com.example.test_pro.ui.component.RoundedEditText;
import com.example.test_pro.ui.members.MembersActivity;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.ConvertByteToImage;
import com.example.test_pro.ultis.DatetimeUtil;
import com.example.test_pro.ultis.DialogUtil;
import com.example.test_pro.ultis.SizeUtils;
import org.jetbrains.annotations.Contract;
import java.util.List;

public class MembersAdapter extends ArrayAdapter<MemberModel> {
    private final List<MemberModel> memberModelList;
    private final Context context;
    private final DatabaseLocal db;
    private RoundedEditText currentEdtCardId = null;
    private AlertDialog currentDialog = null;
    public MembersAdapter(List<MemberModel> memberModelList, Context context, DatabaseLocal db) {
        super(context, 0, memberModelList);
        this.memberModelList = memberModelList;
        this.context = context;
        this.db = db;
    }


    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        MemberModel memberModel = this.memberModelList.get(position);

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

        String filePath = memberModel.getFilePath();
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
        View infoEventAuth = infoMember(memberModel);
        LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                5
        );
        infoEventAuth.setLayoutParams(paramsInfo);
        linearLayout.addView(infoEventAuth);
        TextView useCaseView = timeLastIn(memberModel.getId());
        LinearLayout.LayoutParams useCaseParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                2
        );
        useCaseParams.gravity = Gravity.END;
        useCaseView.setLayoutParams(useCaseParams);
        linearLayout.addView(useCaseView);
        convertView.setOnClickListener(v -> showInfoMember(memberModel));
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
    private View infoMember(@NonNull MemberModel memberModel) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        // Row 1
        TextView txtName = new TextView(context);
        txtName.setTypeface(Typeface.DEFAULT_BOLD);
        txtName.setTextSize(18);
        String nameStr;
        nameStr = memberModel.getName();
        txtName.setText(nameStr);
        //
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
        //
        TextView txtPhone = new TextView(context);
        txtPhone.setTypeface(Typeface.DEFAULT_BOLD);
        txtPhone.setTextSize(18);
        txtPhone.setText(memberModel.getPhoneNumber());
        //
        TextView txtPosition = new TextView(context);
        txtPosition.setTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);
        txtPosition.setTextColor(ColorsUtil.BLUE);
        txtPosition.setTextSize(18);
        txtPosition.setText(memberModel.getPosition());
         //
        linearLayout.addView(txtName);
///        linearLayout.addView(spaceFirst);
///        linearLayout.addView(txtPhone);
        linearLayout.addView(spaceSecond);
        linearLayout.addView(txtPosition);
        return linearLayout;
    }

    @NonNull
    private TextView timeLastIn(String userID) {
        EventAuthModel eventAuthModel = db.getLastEventAuthTodayByUserID(userID);
        TextView txtTimeIn = new TextView(context);
        txtTimeIn.setGravity(Gravity.CENTER);
        txtTimeIn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
        txtTimeIn.setTypeface(Typeface.DEFAULT_BOLD);
        if(eventAuthModel != null) {
            txtTimeIn.setText(DatetimeUtil.convertDatetimeFormat(eventAuthModel.getCreatedAt()));
            if(eventAuthModel.getUseCase() == NumericConstants.RESULT_SUCCESS) {
                txtTimeIn.setTextColor(ColorsUtil.GREEN);
            } else {
                txtTimeIn.setTextColor(ColorsUtil.VIETNAM_RED);
            }
        } else {
            txtTimeIn.setText(context.getText(R.string.not_verified));
            txtTimeIn.setTextColor(ColorsUtil.VIETNAM_RED);
        }

        return txtTimeIn;
    }

    private void showInfoMember(@NonNull MemberModel memberModel) {
        int SIZE_200 = SizeUtils.dpToPx(context, 200);
        int SIZE_40 = SizeUtils.dpToPx(context, 40);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        titleLayout.setPadding(80, 30, 40, 20);

        TextView title = new TextView(context);
        title.setText(context.getString(R.string.member_info));
        title.setTextSize(24);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        titleLayout.addView(title);

        ImageButton btnClose = new ImageButton(context);
        btnClose.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        btnClose.setBackgroundColor(Color.TRANSPARENT);
        btnClose.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        btnClose.setPadding(20, 0, 0, 0);
        titleLayout.addView(btnClose);

        builder.setCustomTitle(titleLayout);

        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 30);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        scrollView.addView(layout);

        ImageView imageView = new ImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(SIZE_200, SIZE_200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackgroundResource(android.R.drawable.editbox_background_normal);
        layout.addView(imageView);
        layout.addView(SizeUtils.createSpaceHeight(30, context));

        Bitmap bitmap = ConvertByteToImage.imgDataGet(memberModel.getFilePath(), context);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.photo));
        }

        RoundedEditText edtName = new RoundedEditText(context, SIZE_40);
        edtName.setup(R.drawable.baseline_person_24,  context.getString(R.string.full_name), InputType.TYPE_CLASS_TEXT, false, true);
        edtName.setText(memberModel.getName());
        layout.addView(edtName);
        //
        RoundedEditText edtMemberCode = new RoundedEditText(context, SIZE_40);
        edtMemberCode.setup(R.drawable.baseline_code_24, context.getString(R.string.staff_code), InputType.TYPE_CLASS_TEXT, false, true);
        edtMemberCode.setText(memberModel.getMemberCode());
        layout.addView(edtMemberCode);
        //
        RoundedEditText edtPhone = new RoundedEditText(context, SIZE_40);
        edtPhone.setup(R.drawable.outline_phone_in_talk_24, context.getString(R.string.phone_number), InputType.TYPE_CLASS_PHONE, false, true);
        edtPhone.setText(memberModel.getPhoneNumber());
        layout.addView(edtPhone);
        //
        RoundedEditText edtPosition = new RoundedEditText(context, SIZE_40);
        edtPosition.setup(R.drawable.outline_family_group_24, context.getString(R.string.position), InputType.TYPE_CLASS_TEXT, false, true);
        edtPosition.setText(memberModel.getPosition());
        layout.addView(edtPosition);
        //
        RoundedEditText edtCardId = new RoundedEditText(context, SIZE_40);
        edtCardId.setup(R.drawable.outline_id_card_24, context.getString(R.string.card_code), InputType.TYPE_CLASS_TEXT, true, true);
        edtCardId.setText(memberModel.getIdentityCard());
        layout.addView(edtCardId);
        currentEdtCardId = edtCardId;
        //
        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setPadding(0, 40, 0, 0);

        Button btnDelete = new Button(context);
        btnDelete.setText(context.getString(R.string.delete));
        btnDelete.setBackgroundColor(ColorsUtil.GRAY_MEDIUM);
        btnDelete.setBackground(createRippleButtonBackground(ColorsUtil.GRAY_MEDIUM));
        btnDelete.setTextColor(Color.WHITE);

        Button btnUpdate = new Button(context);
        btnUpdate.setText(context.getString(R.string.update));
        btnUpdate.setBackgroundColor(ColorsUtil.BLUE);
        btnUpdate.setBackground(createRippleButtonBackground(ColorsUtil.BLUE));
        btnUpdate.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                0, SizeUtils.dpToPx(context, 60), 1
        );
        btnParams.setMargins(0, 0, 0, 0);
        buttonLayout.addView(btnDelete, btnParams);
        buttonLayout.addView(SizeUtils.createSpaceWidth(30, context));
        buttonLayout.addView(btnUpdate, btnParams);

        layout.addView(buttonLayout);

        builder.setView(scrollView);

        AlertDialog dialog = builder.create();
        dialog.show();
        currentDialog = dialog;

        btnClose.setOnClickListener(v -> {
            currentEdtCardId = null;
            currentDialog = null;
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.confirm_delete))
                .setMessage(context.getString(R.string.confirm_delete_member))
                .setPositiveButton(context.getText(R.string.yes), (d, w) -> {
                    boolean isDelete = db.deleteMemberByID(memberModel.getId());
                    if(isDelete) {
                        ((MembersActivity) context).refreshMembersList();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getText(R.string.no), null)
                .show());

        btnUpdate.setOnClickListener(v -> {
            String nameStr = edtName.getText();
            String memberCodeStr = edtMemberCode.getText();
            String phoneStr = edtPhone.getText();
            String positionStr = edtPosition.getText();
            String cardIDStr = edtCardId.getText();
            boolean isUpdate = db.updateMember(memberModel, nameStr, memberCodeStr, phoneStr, positionStr, cardIDStr);
            if(isUpdate) {
                dialog.dismiss();
                memberModel.setName(nameStr);
                memberModel.setMemberCode(memberCodeStr);
                memberModel.setPhoneNumber(phoneStr);
                memberModel.setPosition(positionStr);
                memberModel.setIdentityCard(cardIDStr);

                notifyDataSetChanged();
                DialogUtil.showResultDialog(R.drawable.success, context.getString(R.string.success), context, true, null);
            } else {
                DialogUtil.showResultDialog(R.drawable.failed, context.getString(R.string.failed), context, false, null);
            }
        });
    }

    @NonNull
    @Contract("_ -> new")
    private Drawable createRippleButtonBackground(int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(16f);
        shape.setColor(backgroundColor);

        ColorStateList rippleColor = ColorStateList.valueOf(Color.parseColor("#33000000"));
        return new RippleDrawable(rippleColor, shape, null);
    }

    public void updateCardIdFromNFC(String newCardId) {
        if (currentEdtCardId != null && currentDialog != null && currentDialog.isShowing()) {
            currentEdtCardId.setText(newCardId);
        }
    }

}
