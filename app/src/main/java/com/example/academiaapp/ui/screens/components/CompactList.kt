package com.example.academiaapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Renderizador de tabla compacta genérica con paginación
 * Muestra lista de items con dos campos principales y botón de detalles
 */
@Composable
fun CompactList(
    items: List<Map<String, Any?>>,
    summaryFields: List<String>,
    onOpenDetails: (Map<String, Any?>) -> Unit,
    itemsToShow: Int,
    onSetItemsToShow: (Int) -> Unit,
    selectedItemId: Int?,
    onSelectItem: (Int?) -> Unit
) {
    // ✅ NUEVO: Función para acceder a campos anidados con dot notation
    fun getNestedValue(item: Map<String, Any?>, path: String): Any? {
        if (!path.contains(".")) return item[path]

        val parts = path.split(".")
        var current: Any? = item

        for (part in parts) {
            current = (current as? Map<*, *>)?.get(part)
            if (current == null) break
        }

        return current
    }

    // ✅ Función auxiliar para formatear valores (eliminar decimales innecesarios)
    fun formatValue(value: Any?): String {
        return when (value) {
            null -> ""
            is Number -> {
                val doubleValue = value.toDouble()
                // Si el número es entero (no tiene decimales), mostrarlo sin .0
                if (doubleValue % 1.0 == 0.0) {
                    doubleValue.toLong().toString()
                } else {
                    doubleValue.toString()
                }
            }
            else -> value.toString()
        }
    }

    // Estado para controlar cuántos items mostrar (por defecto 5)
    val allItemsShown = itemsToShow >= items.size

    // Estado para el scroll de la LazyColumn de la tabla
    val tableListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Contador de registros
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mostrando ${itemsToShow.coerceAtMost(items.size)} de ${items.size} registros",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF555555)
            )
            if (!allItemsShown) {
                Text(
                    text = "${items.size - itemsToShow} más",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Cabecera de la tabla
        items.firstOrNull()?.let { firstItem ->
            val keys = firstItem.keys.toList()
            fun findKeyIgnoreCase(target: String): String? = keys.firstOrNull { it.equals(target, ignoreCase = true) }

            val summary = summaryFields.take(2)
            val (primaryKey, secondaryKey) = if (summary.isNotEmpty()) {
                // ✅ Usar directamente los paths (pueden contener dot notation)
                val first = summary.getOrNull(0)
                val second = summary.getOrNull(1)
                first to second
            } else {
                // ✅ MEJORADO: Heurística que excluye objetos anidados y campos _id
                // Filtrar claves: solo primitivas (no Maps), no terminan en _id
                val simpleKeys = keys.filter { key ->
                    val value = firstItem[key]
                    value !is Map<*, *> && !key.endsWith("_id", ignoreCase = true)
                }

                fun pick(vararg candidates: String): String? =
                    candidates.firstOrNull { c -> simpleKeys.any { it.equals(c, ignoreCase = true) } }

                val primary = pick("descripcion", "nombre", "title", "name", "full_name", "displayName", "email")
                val secondary = pick("precio_base", "precio", "importe", "rol", "role", "estado", "status",
                                    "ciudad", "city", "telefono", "phone", "codigo")

                primary to secondary
            }

            // Fila de cabecera
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Formatear nombres de columnas: capitalizar y limpiar guiones bajos
                fun formatColumnName(name: String?): String {
                    if (name == null) return "Campo principal"
                    return name.replace("_", " ").split(" ").joinToString(" ") { word ->
                        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    }
                }

                Text(
                    text = formatColumnName(primaryKey),
                    modifier = Modifier.weight(0.6f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(
                    text = formatColumnName(secondaryKey) ?: "Detalle",
                    modifier = Modifier.weight(0.35f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Espacio para el icono de acción
                Spacer(Modifier.size(32.dp))
            }
        }

        // Calcular altura dinámica: crece con los registros hasta un máximo de 600dp
        val dynamicMaxHeight = remember(itemsToShow) {
            val estimatedRowHeight = 52.dp
            val headerHeight = 50.dp
            val calculatedHeight = headerHeight + (estimatedRowHeight * itemsToShow)
            // Crecimiento: desde 280dp hasta máximo 600dp
            calculatedHeight.coerceIn(280.dp, 600.dp)
        }

        LazyColumn(
            state = tableListState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = dynamicMaxHeight),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(items.take(itemsToShow)) { index, item ->
                val keys = item.keys.toList()
                fun findKeyIgnoreCase(target: String): String? = keys.firstOrNull { it.equals(target, ignoreCase = true) }

                // ✅ NUEVO: valueOf con soporte para dot notation
                fun valueOf(path: String?): String? {
                    if (path == null) return null

                    // Usar getNestedValue para soportar "academia.nombre"
                    val rawValue = getNestedValue(item, path)
                    val formatted = formatValue(rawValue)

                    // Si el campo es de tipo deuda/euros, agregar símbolo €
                    return if (path.contains("deuda", ignoreCase = true) || path.contains("euros", ignoreCase = true)) {
                        if (formatted.isNotBlank()) "${formatted}€" else formatted
                    } else {
                        formatted
                    }
                }

                val summary = summaryFields.take(2)
                val (primaryKey, secondaryKey) = if (summary.isNotEmpty()) {
                    // ✅ Usar directamente el path de summary_fields (puede contener dot notation)
                    val first = summary.getOrNull(0)
                    val second = summary.getOrNull(1)
                    first to second
                } else {
                    // ✅ MEJORADO: Heurística que excluye objetos anidados y campos _id
                    val simpleKeys = keys.filter { key ->
                        val value = item[key]
                        value !is Map<*, *> && !key.endsWith("_id", ignoreCase = true)
                    }

                    fun pick(vararg candidates: String): String? =
                        candidates.firstOrNull { c -> simpleKeys.any { it.equals(c, ignoreCase = true) } }

                    val primary = pick("descripcion", "nombre", "title", "name", "full_name", "displayName", "email")
                    val secondary = pick("precio_base", "precio", "importe", "rol", "role", "estado", "status",
                                        "ciudad", "city", "telefono", "phone", "codigo")

                    primary to secondary
                }

                val primary = valueOf(primaryKey) ?: item.values.mapNotNull { formatValue(it).takeIf { it.isNotBlank() } }.firstOrNull() ?: "(sin datos)"
                val secondary = valueOf(secondaryKey) ?: item.values.mapNotNull { formatValue(it).takeIf { it.isNotBlank() } }.distinct().drop(1).firstOrNull()

                // ✅ PASO 2: Obtener ID del item y determinar si está seleccionado
                val itemId = (item["id"] as? Number)?.toInt()
                val isSelected = itemId != null && itemId == selectedItemId

                // ✅ PASO 3: Color de fila según estado de selección
                val rowColor = when {
                    isSelected -> Color(0xFFBBDEFB) // Azul claro cuando está seleccionado
                    index % 2 == 0 -> Color(0xFFF5F5F5) // Gris muy claro para pares
                    else -> Color(0xFFFFFFFF) // Blanco para impares
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowColor)
                        .clickable {
                            // Toggle selección: si ya está seleccionado, deseleccionar
                            onSelectItem(if (isSelected) null else itemId)
                        }
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = primary,
                        modifier = Modifier.weight(0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF212121)
                    )
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = secondary ?: "",
                        modifier = Modifier.weight(0.35f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )
                    IconButton(onClick = { onOpenDetails(item) }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Ver detalles",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Separador entre filas (excepto la última)
                if (index < itemsToShow - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = Color(0xFFE0E0E0)
                    )
                }
            }
        }

        // Botón para expandir la tabla si hay más items
        if (!allItemsShown) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        val previousCount = itemsToShow
                        // Mostrar 5 items más cada vez que se pulsa
                        onSetItemsToShow((itemsToShow + 5).coerceAtMost(items.size))

                        // Hacer scroll automático hacia los nuevos registros después de actualizar
                        coroutineScope.launch {
                            delay(150) // Delay para que se rendericen los nuevos items

                            // Calcular si necesitamos hacer scroll al final de la tabla
                            val newItemIndex = (itemsToShow - 1).coerceAtLeast(0)

                            // Si ya estamos mostrando más de ~11 registros (cuando la tabla alcanza su altura máxima)
                            // hacer scroll hasta el último registro para que se vean los nuevos
                            if (itemsToShow > 11) {
                                // Scroll hasta el último registro visible
                                tableListState.animateScrollToItem(newItemIndex)
                            } else {
                                // Si aún no hemos alcanzado la altura máxima, scroll suave al primer registro nuevo
                                tableListState.animateScrollToItem(previousCount.coerceAtMost(newItemIndex))
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.UnfoldMore,
                        contentDescription = "Mostrar más",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Mostrar más registros")
                }
            }
        }
    }
}
