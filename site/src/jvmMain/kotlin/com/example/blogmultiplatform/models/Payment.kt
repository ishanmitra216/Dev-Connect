package com.example.blogmultiplatform.models

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val _id: String = "",
    val amount: Double,
    val crypto: String,
    val note: String? = null,
    val releasedBy: String,
    val ts: Long
)

