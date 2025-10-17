package com.example.academiaapp

import android.app.Application
import com.example.academiaapp.data.remote.NetworkModule
import com.example.academiaapp.data.session.SessionStore
import com.example.academiaapp.domain.LoginRepository
import com.example.academiaapp.domain.ChatRepository
import com.example.academiaapp.domain.AcademiasRepository

class AppContainer(app: Application) {
    // Inicializaciones perezosas para no hacer trabajo potencialmente costoso en el constructor
    private val okHttp by lazy { NetworkModule.createOkHttpClient() }
    private val authApi by lazy { NetworkModule.createAuthApi(okHttp) }
    private val chatApi by lazy { NetworkModule.createChatApi(okHttp) }
    private val academiasApi by lazy { NetworkModule.createAcademiasApi(okHttp) }

    val session by lazy { SessionStore(app.applicationContext) }
    private val academiasRepository by lazy { AcademiasRepository(academiasApi, session) }
    val loginRepository by lazy { LoginRepository(authApi, session, academiasRepository) }
    val chatRepository by lazy { ChatRepository(chatApi, session) }
}

class AcademiaApp : Application() {
    // Dejar container perezoso para minimizar el trabajo en onCreate
    val container: AppContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        // No forzar la creación aquí; se inicializará cuando se necesite
    }
}
