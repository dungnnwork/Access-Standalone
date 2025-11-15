//package com.example.test_pro.ui;
//
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.test_pro.R;
//
//
//public class RegisterActivity extends AppCompatActivity {
//
//    private ImageView imgFace;
//    private EditText edtTenNhanVien, edtMaThe;
//    private Button btnCapture, btnDangKy;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        imgFace = findViewById(R.id.imgFace);
//        edtTenNhanVien = findViewById(R.id.edtTenNhanVien);
//        edtMaThe = findViewById(R.id.edtMaThe);
//        btnCapture = findViewById(R.id.btnCapture);
//        btnDangKy = findViewById(R.id.btnDangKy);
//
//        btnCapture.setOnClickListener(v -> {
//            Toast.makeText(this, "Chụp khuôn mặt", Toast.LENGTH_SHORT).show();
//        });
//
//        btnDangKy.setOnClickListener(v -> {
//            String ten = edtTenNhanVien.getText().toString().trim();
//            String maThe = edtMaThe.getText().toString().trim();
//
//            if (ten.isEmpty() || maThe.isEmpty()) {
//                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
