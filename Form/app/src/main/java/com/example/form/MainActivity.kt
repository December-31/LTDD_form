package com.example.form

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
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilPhone: TextInputLayout

    private lateinit var edtName: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var edtPhone: TextInputEditText

    private lateinit var btnRegister: Button

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
    }

    private fun initViews() {
        tilName = findViewById(R.id.tilName)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilPhone = findViewById(R.id.tilPhone)

        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtPhone = findViewById(R.id.edtPhone)

        btnRegister = findViewById(R.id.btnRegister)
    }

    // ===== 1. Kiểm tra thời gian thực khi người dùng gõ phím =====
    private fun setupRealTimeValidation() {
        edtName.doOnTextChanged { _, _, _, _ -> validateName() }
        edtEmail.doOnTextChanged { _, _, _, _ -> validateEmail() }
        edtPassword.doOnTextChanged { _, _, _, _ -> validatePassword() }
        edtPhone.doOnTextChanged { _, _, _, _ -> validatePhone() }
    }

    // ===== 2. Các hàm validate (trả về true nếu hợp lệ) =====
    private fun validateName(): Boolean {
        val name = edtName.text.toString().trim()
        return when {
            name.isEmpty() -> setError(tilName, "Họ và tên không được để trống")
            name.length < 2 -> setError(tilName, "Họ và tên quá ngắn")
            !name.matches(Regex("^[\\p{L} ]+$")) ->
                setError(tilName, "Họ và tên chỉ được chứa chữ cái")
            else -> clearError(tilName)
        }
    }

    private fun validateEmail(): Boolean {
        val email = edtEmail.text.toString().trim()
        return when {
            email.isEmpty() -> setError(tilEmail, "Email không được để trống")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                setError(tilEmail, "Email không hợp lệ")
            else -> clearError(tilEmail)
        }
    }

    private fun validatePassword(): Boolean {
        val pass = edtPassword.text.toString()
        return when {
            pass.isEmpty() -> setError(tilPassword, "Mật khẩu không được để trống")
            pass.length < 8 -> setError(tilPassword, "Mật khẩu phải có ít nhất 8 ký tự")
            !pass.any { it.isLetter() } || !pass.any { it.isDigit() } ->
                setError(tilPassword, "Mật khẩu phải gồm cả chữ và số")
            else -> clearError(tilPassword)
        }
    }

    private fun validatePhone(): Boolean {
        val phone = edtPhone.text.toString().trim()
        // Số điện thoại VN: 0xxxxxxxxx hoặc +84xxxxxxxxx
        val regex = Regex("^(0|\\+84)(3|5|7|8|9)\\d{8}$")
        return when {
            phone.isEmpty() -> setError(tilPhone, "Số điện thoại không được để trống")
            !phone.matches(regex) -> setError(tilPhone, "Số điện thoại không hợp lệ")
            else -> clearError(tilPhone)
        }
    }

    // ===== 3. Báo lỗi bằng TextInputLayout =====
    private fun setError(til: TextInputLayout, message: String): Boolean {
        til.isErrorEnabled = true
        til.error = message
        return false
    }

    private fun clearError(til: TextInputLayout): Boolean {
        til.error = null
        til.isErrorEnabled = false
        return true
    }

    // ===== 4. Xác nhận khi bấm nút Đăng ký =====
    private fun onRegisterClicked() {
        // Chạy hết cả 4 hàm để hiện toàn bộ lỗi cùng lúc
        val results = listOf(validateName(), validateEmail(), validatePassword(), validatePhone())

        if (results.all { it }) {
            Snackbar.make(btnRegister, "Đăng ký thành công!", Snackbar.LENGTH_LONG).show()
        } else {
            // Đưa con trỏ về ô đầu tiên bị lỗi
            when {
                !results[0] -> edtName.requestFocus()
                !results[1] -> edtEmail.requestFocus()
                !results[2] -> edtPassword.requestFocus()
                !results[3] -> edtPhone.requestFocus()
            }
            Snackbar.make(btnRegister, "Vui lòng kiểm tra lại thông tin", Snackbar.LENGTH_SHORT).show()
        }
    }
}