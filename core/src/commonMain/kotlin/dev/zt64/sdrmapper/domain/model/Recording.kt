package dev.zt64.sdrmapper.domain.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Recording(
    @Contextual
    val createdAt: Instant,
    @Contextual
    val endedAt: Instant,
    val id: String
)