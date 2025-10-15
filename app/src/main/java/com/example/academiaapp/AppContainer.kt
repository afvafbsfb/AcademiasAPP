package com.example.academiaapp

import android.app.Application
import com.example.academiaapp.data.remote.NetworkModule
import com.example.academiaapp.data.session.SessionStore
import com.example.academiaapp.domain.LoginRepository
import com.example.academiaapp.domain.ChatRepository

class AppContainer(app: Application) {
    private val okHttp = NetworkModule.createOkHttpClient()
    private val authApi = NetworkModule.createAuthApi(okHttp)
    private val chatApi = NetworkModule.createChatApi(okHttp)
    val session = SessionStore(app.applicationContext)
    val loginRepository = LoginRepository(authApi, session)
    val chatRepository = ChatRepository(chatApi, session)
}

class AcademiaApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
