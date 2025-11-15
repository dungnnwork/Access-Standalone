package com.example.test_pro.ui.result;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import com.example.test_pro.R;
import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.ui.base.BaseActivity;
import com.example.test_pro.ultis.ConvertByteToImage;
import java.io.File;

public class ResultActivity extends BaseActivity {
    private String filePathDB;
    private String filePathCamera;
    private ImageView imageViewFromCamera;
    private ImageView imageViewFromDB;
    private TextView titleToolbar;
    private TextView namePersonal;
    private TextView cardCode;
    private final String TAG = "RESULT_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        loadIntent();
        setContentView(R.layout.result_activity);
        setupToolbar();
        layoutImage();
    }

    private void loadIntent() {
        Intent intent = getIntent();
        filePathDB = intent.getStringExtra(ConstantString.FILE_PATH_FROM_DB);
        filePathCamera = intent.getStringExtra(ConstantString.FILE_PATH_FROM_CAMERA);
        if(filePathDB.isEmpty()) {
            Log.i(TAG, "Not found");
        } else {
            Log.i(TAG, "Match");
        }
    }

    private void readNFC() {
        Log.i(TAG, "Read NFC");
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            return;
        }

    }

    private void layoutImage() {
        // Init layout
        imageViewFromDB = findViewById(R.id.imageFromDB);
        imageViewFromCamera = findViewById(R.id.imageFromCamera);
        titleToolbar = findViewById(R.id.toolbar_title);
        titleToolbar.setText(getString(R.string.personal_info));
        namePersonal = findViewById(R.id.tv_name);
        ////
        Bitmap bitmap = ConvertByteToImage.imgDataGet(filePathCamera, this);
        if (bitmap != null) {
            imageViewFromCamera.setImageBitmap(bitmap);
        } else {
            imageViewFromCamera.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.not_found));
        }

        Log.d(TAG, "File path db : " + filePathDB);

        if (filePathDB != null && !filePathDB.isEmpty()) {
            Uri uri = Uri.fromFile(new File(filePathDB));
            imageViewFromDB.setImageURI(uri);

            File file = new File(filePathDB);
            String fileName = file.getName();

            int dotIndex = fileName.lastIndexOf(".");
            String nameWithoutExtension =
                    (dotIndex != -1) ? fileName.substring(0, dotIndex) : fileName;

            namePersonal.setText(nameWithoutExtension);
        } else {
            imageViewFromDB.setImageResource(R.drawable.not_found);
            namePersonal.setText(getText(R.string.unknown));
        }

    }
}
