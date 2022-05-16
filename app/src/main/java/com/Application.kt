package com

import android.app.Application
import com.navercorp.nid.NaverIdLoginSDK

class Application: Application() {


    override fun onCreate() {
        super.onCreate()

        // OAUTH_CLIENT_ID: 애플리케이션 등록 후 발급받은 클라이언트 아이디
        // OAUTH_CLIENT_SECRET: 애플리케이션 등록 후 발급받은 클라이언트 시크릿
        // OAUTH_CLIENT_NAME: 네이버 앱의 로그인 화면에 표시할 애플리케이션 이름, 모바일 웹의 로그인 화면을 사용할 때는 서버에 저장된 애플리케이션 이름이 표시됩니다.
        NaverIdLoginSDK.initialize(this, "", "", "")
    }

}