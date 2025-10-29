package com.example.academiaapp.data.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

class SessionStore(private val context: Context) {
    object Keys {
        val ACCESS = stringPreferencesKey("access_token")
        val REFRESH = stringPreferencesKey("refresh_token")
        val ROLE = stringPreferencesKey("role")
        val NAME = stringPreferencesKey("name")
        val ACADEMIA_ID = stringPreferencesKey("academia_id")
        val ACADEMIA_NAME = stringPreferencesKey("academia_name")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { it[Keys.ACCESS] }
    val role: Flow<String?> = context.dataStore.data.map { it[Keys.ROLE] }
    val name: Flow<String?> = context.dataStore.data.map { it[Keys.NAME] }
    val academiaId: Flow<String?> = context.dataStore.data.map { it[Keys.ACADEMIA_ID] }
    val academiaName: Flow<String?> = context.dataStore.data.map { it[Keys.ACADEMIA_NAME] }

    suspend fun getAccessToken(): String? {
        var token: String? = null
        context.dataStore.data.map { it[Keys.ACCESS] }.collect { token = it }
        return token
    }

    suspend fun saveSession(access: String?, refresh: String?, role: String?, name: String?, academiaId: Int? = null, academiaName: String? = null) {
        context.dataStore.edit { prefs ->
            if (access != null) prefs[Keys.ACCESS] = access else prefs.remove(Keys.ACCESS)
            if (refresh != null) prefs[Keys.REFRESH] = refresh else prefs.remove(Keys.REFRESH)
            if (role != null) prefs[Keys.ROLE] = role else prefs.remove(Keys.ROLE)
            if (name != null) prefs[Keys.NAME] = name else prefs.remove(Keys.NAME)
            if (academiaId != null) prefs[Keys.ACADEMIA_ID] = academiaId.toString() else prefs.remove(Keys.ACADEMIA_ID)
            if (academiaName != null) prefs[Keys.ACADEMIA_NAME] = academiaName else prefs.remove(Keys.ACADEMIA_NAME)
        }
    }

    suspend fun saveAcademiaInfo(id: Int?, name: String?) {
        context.dataStore.edit { prefs ->
            if (id != null) prefs[Keys.ACADEMIA_ID] = id.toString() else prefs.remove(Keys.ACADEMIA_ID)
            if (name != null) prefs[Keys.ACADEMIA_NAME] = name else prefs.remove(Keys.ACADEMIA_NAME)
        }
    }

    suspend fun clear() { context.dataStore.edit { it.clear() } }
}
