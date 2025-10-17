package com.example.academiaapp.domain

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
        if (cachedId == academiaId && !cachedName.isNullOrBlank()) return cachedName

        return try {
            val dto = api.getAcademia(academiaId)
            val name = dto.nombre
            session.saveAcademiaInfo(id = academiaId, name = name)
            name
        } catch (_: Throwable) {
            null
        }
    }
}
