package com.example.mio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.example.mio.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var mBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setResultSignUp()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            //.requestIdToken(R.string.defalut_client_id.toString())
            .requestServerAuthCode(getString(R.string.test_client_id))
            .requestProfile()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mBinding.signInButton.setOnClickListener {
            signIn()
        }


    }
    private fun setResultSignUp() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
                Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignInResult(completedTask : Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val authCode = account.serverAuthCode

            Toast.makeText(this, "tjd", Toast.LENGTH_SHORT).show()
            println(email)
            println(authCode.toString())

            getAccessToken(authCode!!)

        } catch (e : ApiException) {
            Log.w("failed", "signinresultfalied code = " + e.statusCode)
        }
    }

    private fun getAccessToken(authCode : String) {
        val client = OkHttpClient()
        val requestBody: RequestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add(
                "client_id",
                getString(R.string.test_client_id)
            )
            .add("client_secret", getString(R.string.test_client_secret))
                //https://oauth2.googleapis.com/token
                //https://accounts.google.com/o/oauth2/auth
            .add("redirect_uri", "")
            .add("code", authCode)
            .build()

        val request = Request.Builder()
            .url("https://oauth2.googleapis.com/token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                print("Failed")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonObject = JSONObject(response.body!!.string())
                    val message = jsonObject.toString(5)
                    Log.d("access", message)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

        })
    }
    private fun signIn() {
        val signIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signIntent)
    }
}