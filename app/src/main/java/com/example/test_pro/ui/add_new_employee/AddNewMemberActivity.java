package com.example.test_pro.ui.add_new_employee;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.example.test_pro.R;
import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.model.database.FaceFeatureModel;
import com.example.test_pro.model.database.MemberModel;
import com.example.test_pro.ui.base.BaseActivity;
import com.example.test_pro.ui.component.Appbar;
import com.example.test_pro.ui.component.RoundedEditText;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.ConvertByteToImage;
import com.example.test_pro.ultis.DatetimeUtil;
import com.example.test_pro.ultis.DialogUtil;
import com.example.test_pro.ultis.FunctionUtil;
import com.example.test_pro.ultis.SizeUtils;
import com.example.test_pro.ultis.ToastUtil;
import java.util.Arrays;
import java.util.UUID;

public class AddNewMemberActivity extends BaseActivity {
    private RoundedEditText name;
    private RoundedEditText memberCode;
    private RoundedEditText position;
    private RoundedEditText phoneNumber;
    private RoundedEditText identityCard;
    private String filePath;
    private byte[] faceFeature;
    private DatabaseLocal db;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private final String TAG = "ADD_NEW_MEMBER";

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
        loadIntent();
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
                byte[] tagId = tag.getId();
                String hexId = FunctionUtil.bytesToHex(tagId);
                identityCard.setText(hexId);
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
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }

    }

    private void loadIntent() {
        Intent intent = getIntent();
        filePath = intent.getStringExtra(ConstantString.FILE_PATH);
        faceFeature = intent.getByteArrayExtra(ConstantString.BYTE_FACE);
        Log.i(TAG, "code" + " " + filePath + " " + Arrays.toString(faceFeature));
    }

    // TODO [Lay out]
    private void layoutScreen() {
        int HEIGHT = SizeUtils.dpToPx(this, 60);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#E9E9FF"));
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        Appbar appbar = new Appbar(this, true, false, null, 0, v -> finish());
        LinearLayout.LayoutParams appbarParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        appbar.setLayoutParams(appbarParams);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        scrollView.setFillViewport(true);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_HORIZONTAL);
        container.setPadding(32, 0, 32, 48);
        container.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ImageView imgAvatar = new ImageView(this);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(
                SizeUtils.dpToPx(this, 200),
                SizeUtils.dpToPx(this, 200)
        );
        avatarParams.topMargin = SizeUtils.dpToPx(this, 24);
        avatarParams.bottomMargin = SizeUtils.dpToPx(this, 32);
        imgAvatar.setLayoutParams(avatarParams);
        imgAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap bitmap = ConvertByteToImage.imgDataGet(filePath, this);
        if (bitmap != null) {
            imgAvatar.setImageBitmap(bitmap);
        } else {
            imgAvatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.add_user));
        }
        imgAvatar.setBackgroundResource(android.R.drawable.editbox_background_normal);
        imgAvatar.setPadding(12, 12, 12, 12);
        imgAvatar.setClickable(false);
        imgAvatar.setFocusable(false);

        name = new RoundedEditText(this, HEIGHT);
                name.setup(R.drawable.baseline_person_24, getString(R.string.full_name), InputType.TYPE_CLASS_TEXT, false, false);

        memberCode = new  RoundedEditText(this, HEIGHT);
                memberCode.setup(R.drawable.baseline_code_24, getString(R.string.staff_code), InputType.TYPE_CLASS_TEXT, false, false);

        phoneNumber = new  RoundedEditText(this, HEIGHT);
                phoneNumber.setup(R.drawable.outline_phone_in_talk_24, getString(R.string.phone_number), InputType.TYPE_CLASS_PHONE, false, false);

        position = new RoundedEditText(this, HEIGHT);
                position.setup(R.drawable.outline_family_group_24, getString(R.string.position), InputType.TYPE_CLASS_TEXT, false, true);

        identityCard = new RoundedEditText(this, HEIGHT);
                identityCard.setup(R.drawable.outline_id_card_24, getString(R.string.card_code), InputType.TYPE_CLASS_TEXT, true, true);

        Button btnCreateNew = new Button(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                SizeUtils.dpToPx(this, 60)
        );
        btnParams.topMargin = 48;
        btnCreateNew.setLayoutParams(btnParams);
        btnCreateNew.setText(getText(R.string.register_low));
        btnCreateNew.setTextColor(Color.WHITE);
        btnCreateNew.setTextSize(18);
        btnCreateNew.setAllCaps(true);
        btnCreateNew.setBackground(makeGradientButton());

        container.addView(imgAvatar);
        container.addView(name);
        container.addView(memberCode);
        container.addView(phoneNumber);
        container.addView(position);
        container.addView(identityCard);
        container.addView(btnCreateNew);

        scrollView.addView(container);

        root.addView(appbar);
        root.addView(scrollView);

        setContentView(root);

        btnCreateNew.setOnClickListener(v -> createNewUser());
    }
    @NonNull
    private GradientDrawable makeGradientButton() {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{ColorsUtil.PURPLE_LIGHT, ColorsUtil.PURPLE_MEDIUM});
        gradient.setCornerRadius(20f);
        return gradient;
    }

    // TODO [Process]
    private void createNewUser() {
        boolean isValidate = validateForm();
        if (!isValidate) {
            ToastUtil.show(this, getString(R.string.complete_information));
            return;
        }
        String id = UUID.randomUUID().toString();
        MemberModel memberModel = createNewMember(id);
        FaceFeatureModel faceFeatureModel = createNewFaceFeature(id);
        boolean isResult = db.registerMember(memberModel, faceFeatureModel);
        if (isResult) {
            DialogUtil.showResultDialog(R.drawable.success, getString(R.string.create_success), this, true, null);
            new Handler(Looper.getMainLooper()).postDelayed(this::finish, 3000);
        } else {
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.create_failed_try_again), this, false, null);
        }
    }

    @NonNull
    private MemberModel createNewMember(String id) {
        String nameStr = name.getText();
        String memberCodeStr = memberCode.getText();
        String phoneNumberStr = phoneNumber.getText();
        String identityCardStr = identityCard.getText();
        String positionStr = position.getText();
        return new MemberModel(id, nameStr, memberCodeStr, filePath, phoneNumberStr, identityCardStr, positionStr, DatetimeUtil.nowToString());
    }

    @NonNull
    private FaceFeatureModel createNewFaceFeature(String id) {
        return new FaceFeatureModel(id, faceFeature);
    }

    private boolean validateForm() {
        if (name.getText().trim().isEmpty()) {
            return false;
        }

        if (memberCode.getText().trim().isEmpty()) {
            return false;
        }

        String positionVal = position.getText().trim();
        if (positionVal.isEmpty()) {
            return false;
        }

        String phoneVal = phoneNumber.getText().trim();
        if (phoneVal.isEmpty()) {
            return false;
        } else return Patterns.PHONE.matcher(phoneVal).matches();
    }

}
