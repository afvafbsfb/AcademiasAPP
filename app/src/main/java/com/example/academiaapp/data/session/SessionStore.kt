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
        val ACADEMIA = stringPreferencesKey("academia_id")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { it[Keys.ACCESS] }
    val role: Flow<String?> = context.dataStore.data.map { it[Keys.ROLE] }
    val name: Flow<String?> = context.dataStore.data.map { it[Keys.NAME] }

    suspend fun saveSession(access: String?, refresh: String?, role: String?, name: String?) {
        context.dataStore.edit { prefs ->
            if (access != null) prefs[Keys.ACCESS] = access else prefs.remove(Keys.ACCESS)
            if (refresh != null) prefs[Keys.REFRESH] = refresh else prefs.remove(Keys.REFRESH)
            if (role != null) prefs[Keys.ROLE] = role else prefs.remove(Keys.ROLE)
            if (name != null) prefs[Keys.NAME] = name else prefs.remove(Keys.NAME)
        }
    }

    suspend fun clear() { context.dataStore.edit { it.clear() } }
}
