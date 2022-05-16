package com.busandiego.logins

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.NidOAuthLoginState
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (NidOAuthLoginState.OK.equals(NaverIdLoginSDK.getState())){
            Log.d(TAG, "NidOAuthLoginState.OK >>>>")
        }

        val naverButton = findViewById<Button>(R.id.naver_button)
        naverButton.setOnClickListener {
            NaverIdLoginSDK.authenticate(
                this, callback = naverLoginCallback
            )
        }
    }

    /**
     * OAuthLoginCallback을 authenticate() 메서드 호출 시 파라미터로 전달하거나 NidOAuthLoginButton 객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다.
     */

    val naverLoginCallback = object : OAuthLoginCallback {

        // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
        override fun onSuccess() {
           Log.d(TAG, "NaverIdLoginSDK.getAccessToken() >>> ${NaverIdLoginSDK.getAccessToken()}")
           Log.d(TAG, "NaverIdLoginSDK.getRefreshToken() >>> ${NaverIdLoginSDK.getRefreshToken()}")
           Log.d(TAG, "NaverIdLoginSDK.getExpiresAt().toString() >>> ${NaverIdLoginSDK.getExpiresAt().toString()}")
           Log.d(TAG, "NaverIdLoginSDK.getTokenType() >>> ${NaverIdLoginSDK.getTokenType()}")
           Log.d(TAG, "NaverIdLoginSDK.getState().toString() >>> ${NaverIdLoginSDK.getState().toString()}")

            NidOAuthLogin().callProfileApi(nidProfileCallback)
        }

        override fun onFailure(httpStatus: Int, message: String) {
            val errorCode = NaverIdLoginSDK.getLastErrorCode().code
            val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
            Toast.makeText(applicationContext,"errorCode:$errorCode, errorDesc:$errorDescription",Toast.LENGTH_SHORT).show()
        }

        override fun onError(errorCode: Int, message: String) {
            onFailure(errorCode, message)
        }



        val nidProfileCallback = object: NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                Log.d(TAG, "result.message >>> ${result.message}")
                Log.d(TAG, "result.profile >>> ${result.profile}")
                Log.d(TAG, "result.resultCode >>> ${result.resultCode}")
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(applicationContext,"errorCode:$errorCode, errorDesc:$errorDescription",Toast.LENGTH_SHORT).show()
            }


            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }

        }

    }
}