package com.example.blogmultiplatform.api

import com.example.blogmultiplatform.data.MongoDB
import com.example.blogmultiplatform.models.Payment
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

@Api(routeOverride = "savepayment")
suspend fun savePayment(context: ApiContext) {
    try {
        val body = context.req.body?.decodeToString()
        if (body == null) {
            context.res.setBodyText(Json.encodeToString("Invalid request body."))
            return
        }
        val payment = Json.decodeFromString<Payment>(body)
        val saved = context.data.getValue<MongoDB>().savePayment(payment)
        context.res.setBodyText(Json.encodeToString(saved))
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(e.message))
    }
}

@Api(routeOverride = "readpayments")
suspend fun readPayments(context: ApiContext) {
    try {
        val limitParam = context.req.params["limit"]
        val limit = limitParam?.toIntOrNull() ?: 100
        val payments = context.data.getValue<MongoDB>().readPayments(limit = limit)
        context.res.setBodyText(Json.encodeToString(payments))
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(mapOf("error" to (e.message ?: "unknown"))))
    }
}
