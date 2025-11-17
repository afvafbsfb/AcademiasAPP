package com.example.academiaapp.domain

import android.util.Log
import com.example.academiaapp.data.remote.AcademiasApi
import com.example.academiaapp.data.session.SessionStore
import kotlinx.coroutines.flow.first

class AcademiasRepository(
    private val api: AcademiasApi,
    private val session: SessionStore
) {
    suspend fun resolveAndCacheAcademiaName(academiaId: Int): String? {
        // Si ya está cacheado y corresponde al mismo id, devuelve directamente
        val cachedId = session.academiaId.first()?.toIntOrNull()
        val cachedName = session.academiaName.first()
        if (cachedId == academiaId && !cachedName.isNullOrBlank()) {
            Log.d("AcademiasRepo", "Academia name already cached: $cachedName")
            return cachedName
        }

        return try {
            Log.d("AcademiasRepo", "Fetching academia name for ID: $academiaId")
            
            // Obtener token de acceso
            val token = session.accessToken.first()
            if (token.isNullOrBlank()) {
                Log.e("AcademiasRepo", "No access token available")
                return null
            }
            
            // Llamar al API con el header de autorización
            val headers = mapOf("Authorization" to "Bearer $token")
            val response = api.getAcademia(academiaId, headers)
            val dto = response.result
            Log.d("AcademiasRepo", "Response DTO: id=${dto.id}, nombre=${dto.nombre}")
            val name = dto.nombre
            if (name.isNullOrBlank()) {
                Log.w("AcademiasRepo", "Academia name is null or blank from API")
                return null
            }
            Log.d("AcademiasRepo", "Academia fetched successfully: $name")
            session.saveAcademiaInfo(id = academiaId, name = name)
            name
        } catch (e: Throwable) {
            Log.e("AcademiasRepo", "Error fetching academia $academiaId: ${e.message}", e)
            null
        }
    }
}
