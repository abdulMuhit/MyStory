package com.dicoding.mystory

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.dicoding.mystory.databinding.ActivityRegisterBinding
import com.dicoding.mystory.network.ApiConfig
import com.dicoding.mystory.network.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering...")
        progressDialog.setCancelable(false)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.isEnabled = false

        binding.edtRegisterName.addTextChangedListener { validateInput() }
        binding.edtRegisterEmail.addTextChangedListener { validateInput() }
        binding.edtRegisterPassword.addTextChangedListener { validateInput() }

        // Tombol Register
        binding.btnRegister.setOnClickListener {
            val name = binding.edtRegisterName.text.toString()
            val email = binding.edtRegisterEmail.text.toString()
            val password = binding.edtRegisterPassword.text.toString()

            if(name.length < 1 || email.length < 1 || password.length < 8) {
                val errorMessage = resources.getString(R.string.errorMessageRegister)
                Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            } else {

                progressDialog.show()
                val apiService = ApiConfig().getApiService(sharedPreferences)
                val registerUser = apiService.registerUser(name, email, password)

                registerUser.enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        Log.d("REGISTERACTIVITY", response.toString())
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                Toast.makeText(this@RegisterActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                            }
                            progressDialog.dismiss()
                            gotoLogin()
                        } else {
                            Toast.makeText(this@RegisterActivity, response.message(), Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Log.e("REGISTERACTIVITY", t.message.toString())
                        Toast.makeText(this@RegisterActivity, t.message.toString(), Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }

                })
            }

        }

        // Tombol Login
        binding.btnLogin.setOnClickListener {
            // Arahkan pengguna ke halaman login
            gotoLogin()
        }
    }

    private fun validateInput() {
        val nameError = binding.edtRegisterName.error
        val emailError = binding.edtRegisterEmail.error
        val passwordError = binding.edtRegisterPassword.error
        binding.btnRegister.isEnabled = nameError.isNullOrEmpty() && emailError.isNullOrEmpty() && passwordError.isNullOrEmpty()
    }

    private fun gotoLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
