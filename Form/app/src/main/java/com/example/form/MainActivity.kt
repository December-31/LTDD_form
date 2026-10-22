package com.example.form

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilMsv: TextInputLayout
    private lateinit var tilClass: TextInputLayout

    private lateinit var edtName: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtPhone: TextInputEditText
    private lateinit var edtMsv: TextInputEditText
    private lateinit var edtClass: TextInputEditText

    private lateinit var btnRegister: Button
    private lateinit var btnGoDetail: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRealTimeValidation()

        btnRegister.setOnClickListener { onRegisterClicked() }

        // Intent chuyển sang màn hình Chi tiết (không kèm dữ liệu)
        btnGoDetail.setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
        }
    }

    private fun initViews() {
        tilName = findViewById(R.id.tilName)
        tilEmail = findViewById(R.id.tilEmail)
        tilPhone = findViewById(R.id.tilPhone)
        tilMsv = findViewById(R.id.tilMsv)
        tilClass = findViewById(R.id.tilClass)

        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        edtMsv = findViewById(R.id.edtMsv)
        edtClass = findViewById(R.id.edtClass)

        btnRegister = findViewById(R.id.btnRegister)
        btnGoDetail = findViewById(R.id.btnGoDetail)
    }

    // ===== Validation thời gian thực khi người dùng gõ =====
    private fun setupRealTimeValidation() {
        edtName.doOnTextChanged { _, _, _, _ -> validateName() }
        edtEmail.doOnTextChanged { _, _, _, _ -> validateEmail() }
        edtPhone.doOnTextChanged { _, _, _, _ -> validatePhone() }
        edtMsv.doOnTextChanged { _, _, _, _ -> validateMsv() }
        edtClass.doOnTextChanged { _, _, _, _ -> validateClass() }
    }

    // ===== Các luật validate: trả về null nếu hợp lệ, ngược lại trả về thông báo lỗi =====
    private fun validateMsv() = check(tilMsv, edtMsv) { s ->
        when {
            s.isEmpty() -> "MSV không được để trống"
            // MSV: chữ + số, dài 6-12 ký tự (sửa regex nếu trường bạn quy định khác)
            !s.matches(Regex("^[A-Za-z0-9]{6,12}$")) -> "MSV gồm 6-12 ký tự chữ/số, không dấu cách"
            else -> null
        }
    }

    private fun validateClass() = check(tilClass, edtClass) { s ->
        when {
            s.isEmpty() -> "Lớp không được để trống"
            // Ví dụ hợp lệ: CNTT1-K65, D21CQCN01-B
            !s.matches(Regex("^[A-Za-z0-9][A-Za-z0-9._-]{1,19}$")) ->
                "Lớp không hợp lệ (vd: CNTT1-K65)"
            else -> null
        }
    }
    private fun validateName() = check(tilName, edtName) { s ->
        when {
            s.isEmpty() -> "Họ tên không được để trống"
            !s.matches(Regex("^[\\p{L} ]+$")) -> "Họ tên chỉ được chứa chữ cái"
            s.split(Regex("\\s+")).size < 2 -> "Vui lòng nhập đầy đủ họ và tên"
            else -> null
        }
    }

    private fun validateEmail() = check(tilEmail, edtEmail) { s ->
        when {
            s.isEmpty() -> "Email không được để trống"
            !Patterns.EMAIL_ADDRESS.matcher(s).matches() -> "Email không đúng định dạng"
            else -> null
        }
    }

    private fun validatePhone() = check(tilPhone, edtPhone) { s ->
        when {
            s.isEmpty() -> "Số điện thoại không được để trống"
            // SĐT Việt Nam: 0xxxxxxxxx hoặc +84xxxxxxxxx
            !s.matches(Regex("^(0|\\+84)(3|5|7|8|9)\\d{8}$")) -> "Số điện thoại không hợp lệ"
            else -> null
        }
    }


    /** Chạy luật, gán/xóa lỗi trên TextInputLayout, trả về true nếu hợp lệ. */
    private fun check(
        til: TextInputLayout,
        edt: TextInputEditText,
        rule: (String) -> String?
    ): Boolean {
        val error = rule(edt.text.toString().trim())
        til.error = error
        return error == null
    }

    // ===== Bấm Đăng ký: kiểm tra toàn bộ -> pass hết thì sang màn hình Chi tiết =====
    private fun onRegisterClicked() {
        // Chạy hết các hàm để hiện toàn bộ lỗi cùng lúc
        val results = listOf(
            validateName(), validateEmail(), validatePhone(), validateMsv(), validateClass()
        )

        if (results.all { it }) {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_NAME, edtName.text.toString().trim())
                putExtra(DetailActivity.EXTRA_EMAIL, edtEmail.text.toString().trim())
                putExtra(DetailActivity.EXTRA_PHONE, edtPhone.text.toString().trim())
                putExtra(DetailActivity.EXTRA_MSV, edtMsv.text.toString().trim())
                putExtra(DetailActivity.EXTRA_CLASS, edtClass.text.toString().trim())
            }
            startActivity(intent)
        } else {
            // Đưa con trỏ về ô đầu tiên bị lỗi
            val firstError = listOf(edtName, edtEmail, edtPhone, edtMsv, edtClass)[results.indexOf(false)]
            firstError.requestFocus()
            Snackbar.make(btnRegister, "Vui lòng kiểm tra lại thông tin", Snackbar.LENGTH_SHORT).show()
        }
    }
}