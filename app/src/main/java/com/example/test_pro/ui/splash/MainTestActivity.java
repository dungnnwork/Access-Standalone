package com.example.test_pro.ui.splash;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.ImageQualitySimilar;
import com.arcsoft.face.MaskInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.ExtractType;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.example.test_pro.R;
import com.example.test_pro.common.constants.Constants;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.ui.home.HomeActivity;
import com.example.test_pro.ui.face_identity.FaceIdentityActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainTestActivity extends AppCompatActivity {
    private static final int ACTION_ADD_RECYCLER_ITEM_IMAGE = 0x202;
    private static final int ACTION_CHOOSE_MAIN_IMAGE = 0x201;
    private FaceEngine mainFaceEngine;
    private ImageView imageViewFirst;
    private ImageView imageViewSeconds;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_MAIN = 0;
    private int faceEngineCode = -1;
    private FaceFeature mainFeature;
    private Bitmap mainBitmap;
    private TextView tvMainImageInfo;
    private CardView cardViewCheckIn, cardViewCheckOut;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private final Set<String> processedImages = new HashSet<>();
    private static final int INIT_MASK = FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_GENDER |
            FaceEngine.ASF_AGE | FaceEngine.ASF_MASK_DETECT | FaceEngine.ASF_IMAGEQUALITY;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSDK();
        EdgeToEdge.enable(this);
//        processAllImagesInBackground();
        setContentView(R.layout.activity_main);
        Button buttonFirst = findViewById(R.id.buttonFirst);
        cardViewCheckIn = findViewById(R.id.cardCheckIn);
        cardViewCheckOut = findViewById(R.id.cardCheckOut);
        tvMainImageInfo = findViewById(R.id.tvMainImageInfo);
        Button buttonSeconds = findViewById(R.id.buttonSeconds);
        buttonSeconds.setOnClickListener(v -> intentNewFaceActivity());
        buttonFirst.setOnClickListener(v -> intentHomeActivity());
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            tvMainImageInfo.setText(getText(R.string.not_supported_nfc));
            return;
        }

        pendingIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
        );

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void intentNewFaceActivity() {
        Intent intent = new Intent(MainTestActivity.this, FaceIdentityActivity.class);
//        Intent intent = new Intent(MainActivity.this, NewFaceActivity.class);
        startActivity(intent);
    }

    private void intentHomeActivity() {
        Intent intent = new Intent(MainTestActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
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
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }

    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                Log.i("favorite", "read nfc" + " " + "NFC");
                byte[] tagId = tag.getId();
                String hexId = bytesToHex(tagId);
                tvMainImageInfo.setText("Mã thẻ: " + hexId);
            } else {
                Log.i("favorite", "read nfc" + " " + "tag null");
            }
        }
    }
    @NonNull
    private String bytesToHex(@NonNull byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
            sb.append(" ");
        }
        return sb.toString().trim();
    }
    public void chooseLocalImage(int action) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, action);
    }

    public void addItemFace(View view) {
        if (faceEngineCode != ErrorInfo.MOK) {
            Log.d("image", "engine_not_initialized");
            return;
        }
        if (mainBitmap == null) {
            Log.d("image", "notice_choose_main_img");
            return;
        }
        chooseLocalImage(ACTION_ADD_RECYCLER_ITEM_IMAGE);
    }

    public void chooseMainImage(View view) {

        if (faceEngineCode != ErrorInfo.MOK) {
            Log.d("image", "engine_not_initialized");
            return;
        }
        chooseLocalImage(ACTION_CHOOSE_MAIN_IMAGE);
    }

    private void unInitEngine() {
        if (mainFaceEngine != null) {
            faceEngineCode = mainFaceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + faceEngineCode);
        }
    }

    private void clearBitmap() {
        if (mainBitmap != null && !mainBitmap.isRecycled()) {
            mainBitmap.recycle();
            mainBitmap = null;
        }
        System.gc();
    }

    @Override
    protected void onDestroy() {
        clearBitmap();
        unInitEngine();
        super.onDestroy();
    }

    private void autoSelectLatestImage() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        Cursor cursor = getContentResolver().query(uri, projection, null, null, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                String newImageId = String.valueOf(cursor.getLong(columnIndex));

                if (!processedImages.contains(newImageId)) {
                    processedImages.add(newImageId);
                    cursor.close();

                    Uri imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newImageId);
                    String imagePath = imageUri.getPath();
                    Bitmap bitmap = getBitmapFromUri(imageUri);
                    if (bitmap != null) {
                        imageViewFirst.setImageBitmap(bitmap);
                        processImage(bitmap, TYPE_MAIN, imagePath);
                    } else {
                        Log.d("image", "Not thể lấy bitmap từ ảnh.");
                    }
                    return;
                }
            }
            cursor.close();
        }
        Log.d("ImageSelection", "Not tìm found ảnh mới.");
    }
    @Nullable
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getData() == null) {
            Log.d("image", "get picture failed");
            return;
        }
        Uri uri =  data.getData();
        String imagePath = uri.getPath();
        if (requestCode == ACTION_CHOOSE_MAIN_IMAGE) {
            try {
                mainBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (mainBitmap == null) {
                Log.d("image", "get picture failed");
                return;
            }
            processImage(mainBitmap, TYPE_MAIN, "");
            imageViewFirst.setImageBitmap(mainBitmap);
        } else if (requestCode == ACTION_ADD_RECYCLER_ITEM_IMAGE) {
            Bitmap bitmap;
            Log.d("image", "get picture 152");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (bitmap == null) {
                Log.d("image", "get picture failed");
                return;
            }
            if (mainFeature == null) {
                return;
            }
            imageViewSeconds.setImageBitmap(bitmap);
            processImage(bitmap, TYPE_ITEM, "");
        }
    }

    private void initSDK() {
        DatabaseLocal.getInstance(this);
        initEngine();
        boolean isActive = Constants.activeEngineOffline(this);
        boolean isInitRgb = Constants.initRGBEngine(this);
        boolean isInitIr = Constants.initIREngine(this);
        if (!isActive || !isInitRgb || !isInitIr) {
            Log.d("FILE_LIST", "File: " + " " + isActive + isInitRgb + isInitIr);
            showCustomToast("failed");
        } else {
            Log.d("FILE_LIST", "File: " + " " + "success");
            showCustomToast("success");
        }
    }

    private void initEngine() {
        mainFaceEngine = new FaceEngine();
        faceEngineCode = mainFaceEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                6, INIT_MASK);
        if (faceEngineCode != ErrorInfo.MOK) {
            Log.i("image", "faceEngineCode" + " " + faceEngineCode);
        }
    }

    /**
    private void loadImageNotExits() {
        List<FaceFeatureModel> faceFeatureModelList = new ArrayList<>();
        Map<String, byte[]> map = new HashMap<>();
        try {
            File folder = new File(Environment.getExternalStorageDirectory(), "Images");
            File[] files = folder.listFiles();

            if (files != null) {
                for (int i = files.length - 1; i >= 0; i--) {
                    File file = files[i];
                    if (file.isFile() && file.getName().endsWith(".jpg")) {
                        String imagePath = file.getAbsolutePath();
                        if(!processedImages.contains(imagePath)) {
                            String id = UUID.randomUUID().toString();
                            byte[] bytes = convertImageToBytes(imagePath);
                            faceFeatureModelList.add(new FaceFeatureModel(id, imagePath, bytes, 0));
                            processedImages.add(imagePath);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("code", "code" + " " + e.getMessage());
        }
        if(!SharedPreferencesStorage.getBool(this)) {
            SharedPreferencesStorage.saveBool(this, true);
            DatabaseLocal.getInstance(this).insertManyFaceFeature(faceFeatureModelList);
        }
    }

    private void processAllImagesInBackground() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            File folder = new File(Environment.getExternalStorageDirectory(), "Images");
            File[] files = folder.listFiles();

            if (files == null) return;

            DatabaseLocal db = DatabaseLocal.getInstance(this);
            List<FaceFeatureModel> batch = new ArrayList<>();
            int batchSize = 100;

            for (int i = files.length - 1; i >= 0; i--) {
                File file = files[i];
                if (!file.getName().endsWith(".jpg")) continue;

                String imagePath = file.getAbsolutePath();
                if (db.isImageProcessed(imagePath)) continue;

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap == null) continue;

                bitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);
                byte[] nv21 = ImageConverterUtils.bitmapToNV21(bitmap);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                List<FaceInfo> faceInfoList = new ArrayList<>();
                int detectCode = FaceUtil.faceDetectEngine.detectFaces(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList);

                if (detectCode == ErrorInfo.MOK && !faceInfoList.isEmpty()) {
                    FaceFeature faceFeature = new FaceFeature();
                    int extractCode = FaceUtil.faceDetectEngine.extractFaceFeature(
                            nv21, width, height, FaceEngine.CP_PAF_NV21,
                            faceInfoList.get(0), ExtractType.RECOGNIZE,
                            MaskInfo.NOT_WORN, faceFeature
                    );

                    if (extractCode == ErrorInfo.MOK) {
                        String id = UUID.randomUUID().toString();
                        byte[] featureData = faceFeature.getFeatureData();

                        FaceFeatureModel model = new FaceFeatureModel(id, imagePath, featureData, 0);
                        batch.add(model);
                        processedImages.add(imagePath);

                        if (batch.size() >= batchSize) {
                            db.insertManyFaceFeature(batch);
                            batch.clear();
                        }
                    }
                }

                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                System.gc();
            }

            if (!batch.isEmpty()) {
                db.insertManyFaceFeature(batch);
            }

            runOnUiThread(() -> Toast.makeText(this, "✅ Complete image processing", Toast.LENGTH_LONG).show());
        });
    }
    */

    public byte[] convertImageToBytes(String imagePath) {
        File file = new File(imagePath);
        byte[] bytes = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        } catch (IOException e) {
            Log.i("image", "put mapImage " + e);
        }
        return bytes;
    }
    public void processImage(Bitmap bitmap, int type, String imagePath) {
        if (bitmap == null) {
            return;
        }
        if (mainFaceEngine == null) {
            return;
        }
        bitmap = ArcSoftImageUtil.getAlignedBitmap(bitmap, true);
        if (bitmap == null) {
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] bgr24 = ArcSoftImageUtil.createImageData(width, height, ArcSoftImageFormat.BGR24);
        int transformCode = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24);
        if (transformCode != ArcSoftImageUtilError.CODE_SUCCESS) {
            Log.i("image", "failed to transform bitmap to imageData, code is " + " " + transformCode);
            return;
        }
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int detectCode = mainFaceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
        if (detectCode != ArcSoftImageUtilError.CODE_SUCCESS || faceInfoList.isEmpty()) {
            Log.i("image", "face detection finished, code is " + detectCode + ", face num is " + faceInfoList.size());
            return;
        }
        bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setColor(Color.YELLOW);

        if (!faceInfoList.isEmpty()) {
            for (int i = 0; i < faceInfoList.size(); i++) {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(faceInfoList.get(i).getRect(), paint);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTextSize((float) faceInfoList.get(i).getRect().width() / 2);
                canvas.drawText("" + i, faceInfoList.get(i).getRect().left, faceInfoList.get(i).getRect().top, paint);
            }
        }
        int faceProcessCode = mainFaceEngine.process(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList,
                FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_MASK_DETECT);
        if (faceProcessCode != ErrorInfo.MOK) {
            Log.i("image", "face process finished, code is " + " " + faceProcessCode);
            return;
        }
        List<AgeInfo> ageInfoList = new ArrayList<>();
        List<GenderInfo> genderInfoList = new ArrayList<>();
        List<MaskInfo> maskInfoList = new ArrayList<>();
        int ageCode = mainFaceEngine.getAge(ageInfoList);
        int genderCode = mainFaceEngine.getGender(genderInfoList);
        int maskInfoCode = mainFaceEngine.getMask(maskInfoList);
        if ((ageCode | genderCode | maskInfoCode) != ErrorInfo.MOK) {
            Log.i("image", "at lease one of age、gender、mask detect failed! codes are: " + ageCode
                    + " ," + genderCode + " ," + maskInfoCode);
            return;
        }

        int isMask = MaskInfo.UNKNOWN;
        if (!maskInfoList.isEmpty()) {
            isMask = maskInfoList.get(0).getMask();
        }
        if (isMask == MaskInfo.UNKNOWN) {
            Log.i("image", "mask is unknown");
            return;
        }

        if (type == TYPE_MAIN && isMask == MaskInfo.WORN) {
            Log.i("image", "register image no mask");
            return;
        }

        ImageQualitySimilar imageQualitySimilar = new ImageQualitySimilar();
        int qualityCode = mainFaceEngine.imageQualityDetect(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0),
                isMask, imageQualitySimilar);
        if (qualityCode != ErrorInfo.MOK) {
            Log.i("image", "imageQualityDetect failed! code is " + qualityCode);
            return;
        }
        float quality = imageQualitySimilar.getScore();
        float destQuality = 0.8f;
        if (quality < destQuality) {
            showCustomToast("image quality invalid");
            return;
        }

        if (!faceInfoList.isEmpty()) {
            if (type == TYPE_MAIN) {
                mainFeature = new FaceFeature();
                int res = mainFaceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0),
                        ExtractType.REGISTER, isMask, mainFeature);
                if (res != ErrorInfo.MOK) {
                    mainFeature = null;
                }

                if(mainFeature ==  null) {
                    return;
                }
                Log.i("image", "image" + " " +  SharedPreferencesStorage.loadMapFromPreferences(this).size());
                if (!SharedPreferencesStorage.isImagePathExists(this, imagePath)) {
                    Constants.mapImage.put(imagePath, mainFeature.getFeatureData());
                    Log.i("image", "put mapImage " + Constants.mapImage.size());

                    SharedPreferencesStorage.saveMapToPreferences(this, Constants.mapImage);
                }


                StringBuilder stringBuilder = new StringBuilder();
                if (!faceInfoList.isEmpty()) {
                    stringBuilder.append("face info first:\n\n");
                }
                for (int i = 0; i < faceInfoList.size(); i++) {
                    stringBuilder.append("face[")
                            .append(i)
                            .append("]:\n")
                            .append(faceInfoList.get(i))
                            .append("\nage:")
                            .append(ageInfoList.get(i).getAge())
                            .append("\ngender:")
                            .append(genderInfoList.get(i).getGender() == GenderInfo.MALE ? "MALE"
                                    : (genderInfoList.get(i).getGender() == GenderInfo.FEMALE ? "FEMALE" : "UNKNOWN"))
                            .append("\nmaskInfo:")
                            .append(maskInfoList.get(i).getMask() == MaskInfo.WORN ? "Mask"
                                    : (maskInfoList.get(i).getMask() == MaskInfo.NOT_WORN ? "No Mask" : "UNKNOWN"))
                            .append("\n\n");
                }
                tvMainImageInfo.setText(stringBuilder);
            } else if (type == TYPE_ITEM) {
                FaceFeature faceFeature = new FaceFeature();
                int res = mainFaceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0),
                        ExtractType.RECOGNIZE, isMask, faceFeature);

                if (res == ErrorInfo.MOK) {
                    FaceSimilar faceSimilar = new FaceSimilar();
                    int compareResult = mainFaceEngine.compareFaceFeature(mainFeature, faceFeature, faceSimilar);
                    float similarities = faceSimilar.getScore();
                    String message;
                    if (compareResult == ErrorInfo.MOK && similarities >= destQuality) {
                        message = "Matching Identity";
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle("Success")
                                .setMessage(message)
                                .setCancelable(false)
                                .show();

                        new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 3000);

                        Log.i("image", "compare result code is " + compareResult + " " + faceSimilar.getScore());
                    } else {
                        message = "Identity mismatch";
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle("Failed")
                                .setMessage(message)
                                .setCancelable(false)
                                .show();

                        new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 3000);

                        Log.i("image", "compare result code is " + compareResult + " " + faceSimilar.getScore());
                    }
                }
            }
        } else {
            if (type == TYPE_MAIN) {
                mainBitmap = null;
            }
        }
    }
    private void showCustomToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }
}