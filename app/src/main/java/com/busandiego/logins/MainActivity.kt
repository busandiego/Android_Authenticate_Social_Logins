package com.busandiego.logins

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.NidOAuthLoginState
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var mAuth: FirebaseAuth? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("813585541116-b34ioq14n6orf14oh1074jmshutv9ngt.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // 기존에 로그인 했던 계정을 확인한다.
        // val gsa: GoogleSignInAccount = (GoogleSignIn.getLastSignedInAccount(this)?: null) as GoogleSignInAccount

        // 로그인 되있는 경우
        /*if (gsa != null) {
            Log.d(TAG, "로그인 되있는 경우")
            // 로그인 성공입니다.
            
        } else {
            Log.d(TAG, "로그인 안돼있는 경우 = 로그아웃 했던경우")
        }*/


        //   signOut()

        if (NidOAuthLoginState.OK.equals(NaverIdLoginSDK.getState())) {
            Log.d(TAG, "NidOAuthLoginState.OK >>>>")
            // OK: 접근 토큰이 있는 상태.
            // 단, 사용자가 네이버의 내정보 > 보안설정 > 외부 사이트 연결 페이지에서 연동을 해제했다면
            // 서버에서는 상태 값이 유효하지 않을 수 있습니다.
            // 에러 처리 확인 -> OK로 떨어짐
            // NidOAuthLoginState
        }

        val naverButton = findViewById<Button>(R.id.naver_button)
        naverButton.setOnClickListener {
            NaverIdLoginSDK.authenticate(
                this, callback = naverLoginCallback
            )
        }

        val naverLogoutButton = findViewById<Button>(R.id.naver_logout_button)
        naverLogoutButton.setOnClickListener {
            // NaverIdLoginSDK.logout()
            // Log.d(TAG, "NidOAuthLoginState.OK.equals(NaverIdLoginSDK.getState()) >>>>>>> ${NidOAuthLoginState.OK.equals(NaverIdLoginSDK.getState())}")
            // 처리

        }


        val googleButton = findViewById<Button>(R.id.google_login_button)
        googleButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val googleLogoutButton = findViewById<Button>(R.id.google_logout_button)
        googleLogoutButton.setOnClickListener {
            signOut()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                account.idToken
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    //  Snackbar.make(requireActivity().findViewById(R.id.layout_main), "Authentication Successed.", Snackbar.LENGTH_SHORT).show()
                    val user = mAuth!!.currentUser

                    user!!.email



                    Log.d("", "user >> $user")
                    Log.d("", "displayName >> ${user!!.displayName}")
                    Log.d("", "uid >> ${user!!.uid}")
                    //  updateUI(user)
                } else {
                    Log.d("", "task.isSuccessful >>> ${task.isSuccessful}")
                    // If sign in fails, display a message to the user.
                    //  Snackbar.make(findViewById(R.id.layout_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    //  updateUI(null)
                }
            })
    }


    /**
     * OAuthLoginCallback을 authenticate() 메서드 호출 시 파라미터로 전달하거나 NidOAuthLoginButton 객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다.
     */

    val naverLoginCallback = object : OAuthLoginCallback {

        // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
        override fun onSuccess() {
            Log.d(TAG, "NaverIdLoginSDK.getAccessToken() >>> ${NaverIdLoginSDK.getAccessToken()}")
            Log.d(TAG, "NaverIdLoginSDK.getRefreshToken() >>> ${NaverIdLoginSDK.getRefreshToken()}")
            Log.d(
                TAG,
                "NaverIdLoginSDK.getExpiresAt().toString() >>> ${
                    NaverIdLoginSDK.getExpiresAt().toString()
                }"
            )
            Log.d(TAG, "NaverIdLoginSDK.getTokenType() >>> ${NaverIdLoginSDK.getTokenType()}")
            Log.d(
                TAG,
                "NaverIdLoginSDK.getState().toString() >>> ${NaverIdLoginSDK.getState().toString()}"
            )

            NidOAuthLogin().callProfileApi(nidProfileCallback)
        }

        override fun onFailure(httpStatus: Int, message: String) {
            val errorCode = NaverIdLoginSDK.getLastErrorCode().code
            val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
            Toast.makeText(
                applicationContext,
                "errorCode:$errorCode, errorDesc:$errorDescription",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onError(errorCode: Int, message: String) {
            onFailure(errorCode, message)
        }


        val nidProfileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                Log.d(TAG, "result.message >>> ${result.message}")
                Log.d(TAG, "result.profile >>> ${result.profile}")
                Log.d(TAG, "result.resultCode >>> ${result.resultCode}")
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    applicationContext,
                    "errorCode:$errorCode, errorDesc:$errorDescription",
                    Toast.LENGTH_SHORT
                ).show()
            }


            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }

        }

    }

    // 토큰 날려버리기
    fun deleteToken() {
        NidOAuthLogin().callDeleteTokenApi(this, object : OAuthLoginCallback {
            override fun onSuccess() {
                // 서버에서 토큰 삭제에 성공한 상태입니다.
                // updateView()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    this@MainActivity,
                    "errorCode: $errorCode, errorDesc: $errorDescription",
                    Toast.LENGTH_SHORT
                ).show()
                // updateView()
            }

            override fun onError(errorCode: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                onFailure(errorCode, message)
            }
        })

    }

    private fun signOut() {
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this) {
                // ...
            }
    }
}