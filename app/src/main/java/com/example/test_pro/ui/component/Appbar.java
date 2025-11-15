package com.example.test_pro.ui.component;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.test_pro.R;

@SuppressLint("ViewConstructor")
public class Appbar extends LinearLayout {

///    private ImageView logoImageView;
///    private String imagePath;

    public Appbar(Context context, boolean isExitsBack, boolean isIcon, OnClickListener listener, int resource, OnClickListener listenerBack) {
        super(context);
        appbar(isExitsBack, isIcon, listener, resource, listenerBack);
    }

    private void appbar(boolean isExitsBack, boolean isIcon, OnClickListener listener, int resource, OnClickListener listenerBack) {
        setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics())
        ));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(20, 16, 20, 0);

        if (isExitsBack) {
            ImageView backButton = new ImageView(getContext());
            backButton.setImageResource(R.drawable.twotone_arrow_back_24);
            backButton.setLayoutParams(new LayoutParams(60, 60));
            backButton.setOnClickListener(listenerBack);
            addView(backButton);
        }
        ImageView logoImageView = new ImageView(getContext());
        logoImageView.setImageResource(R.drawable.logo);
        logoImageView.setLayoutParams(new LayoutParams(
                0,
                LayoutParams.MATCH_PARENT,
                3
        ));
        logoImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(logoImageView);
        if(isIcon) {
            ImageView icon = new ImageView(getContext());
            icon.setImageResource(resource);
            icon.setLayoutParams(new LayoutParams(60, 60));
            icon.setOnClickListener(listener);
            addView(icon);
        }
    }

    /**
    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateLogo(context);
        }
    };

    private void updateLogo(Context context) {
        LicenseModel licenseModel = SharedPreferencesManager.getLicense(getContext());
        if(licenseModel == null) return;
        imagePath  = licenseModel.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Uri imageUri;

                if (imagePath.startsWith(ConstantString.PREFIX)) {
                    imageUri = Uri.parse(imagePath);
                } else {
                    imageUri = Uri.fromFile(new File(imagePath));
                }
                InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
                if(imageStream == null) return;
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                if(bitmap == null) return;
                logoImageView.setImageBitmap(bitmap);
                logoImageView.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        3
                ));
                logoImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } catch (FileNotFoundException e) {
                logoImageView.setImageResource(R.drawable.logo);
            }
        }
    }

    @Nullable
    @Contract(pure = true)
    private Bitmap convertPathToBitmap(Context context, String imagePathVal) {
        if(imagePathVal == null || imagePathVal.isEmpty()) return null;
        try {
            Uri imageUri;

            if (imagePathVal.startsWith(ConstantString.PREFIX)) {
                imageUri = Uri.parse(imagePathVal);
            } else {
                imageUri = Uri.fromFile(new File(imagePathVal));
            }
            InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
            if(imageStream == null) return null;
            return BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            return null;
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(updateReceiver);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(updateReceiver, new IntentFilter(ConstantString.UPDATE_INFO));
    }
    */

}
