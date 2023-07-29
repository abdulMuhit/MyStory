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
import com.dicoding.mystory.databinding.ActivityLoginBinding
import com.dicoding.mystory.network.ApiConfig
import com.dicoding.mystory.network.LoginResponseError
import com.dicoding.mystory.network.LoginResponseSuccess
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Logging in...")
        progressDialog.setCancelable(false)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.isEnabled = false

        binding.edLoginEmail.addTextChangedListener { validateInput() }
        binding.edLoginPassword.addTextChangedListener { validateInput() }

        // Tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            progressDialog.show()

            if(email.isEmpty() || password.isEmpty()) {
                val errorMessageEmail = resources.getString(R.string.errorMessageRegister)
                Toast.makeText(this@LoginActivity, errorMessageEmail, Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            } else if (password.length < 8) {
                val errorMessage = resources.getString(R.string.errorMessagePassword)
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            } else {
                val apiService = ApiConfig().getApiService(sharedPreferences)
                val loginUser = apiService.loginUser(email, password)

                loginUser.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()?.string()
                            if (responseBody != null) {
                                val gson = Gson()

                                try {
                                    val loginSuccessResponse = gson.fromJson(responseBody, LoginResponseSuccess::class.java)
                                    Toast.makeText(this@LoginActivity, loginSuccessResponse.message, Toast.LENGTH_SHORT).show()
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                    sharedPreferences.edit().putString("token", loginSuccessResponse.loginResult.token).apply()

                                    // Beralih ke halaman utama
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                                    startActivity(intent)

                                    progressDialog.dismiss()
                                    finish()

                                } catch (e: JsonSyntaxException) {
                                    try {
                                        val loginErrorResponse = gson.fromJson(responseBody, LoginResponseError::class.java)
                                        // Handle response gagal di sini
                                        Toast.makeText(this@LoginActivity, loginErrorResponse.message, Toast.LENGTH_SHORT).show()
                                        progressDialog.dismiss()
                                    } catch (e: JsonSyntaxException) {
                                        // JSON tidak sesuai dengan keduanya, tangani error di sini
                                        Log.e("LOGINACTIVITY3", e.message.toString())
                                        progressDialog.dismiss()
                                    }
                                }
                            } else {
                                Log.e("LOGINACTIVITY4", "RESPONSE KOSONG")
                                progressDialog.dismiss()
                            }
                        } else {
                            Log.e("LOGINACTIVITY5", response.toString())
                            Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, t.message.toString(), Toast.LENGTH_SHORT).show()
                        Log.e("LOGINACTIVITY", t.message.toString())
                        progressDialog.dismiss()
                    }
                })
            }
        }

        // Cek status login dan alihkan ke halaman utama jika sudah login
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Tombol Register
        binding.tvRegister.setOnClickListener {
            // Arahkan pengguna ke halaman register
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput() {
        val emailError = binding.edLoginEmail.error
        val passwordError = binding.edLoginPassword.error
        binding.btnLogin.isEnabled = emailError.isNullOrEmpty() && passwordError.isNullOrEmpty()
    }
}
