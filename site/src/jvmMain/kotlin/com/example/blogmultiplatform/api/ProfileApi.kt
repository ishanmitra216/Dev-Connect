package com.example.blogmultiplatform.api

import com.example.blogmultiplatform.data.MongoDB
import com.example.blogmultiplatform.models.User
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@Api(routeOverride = "saveprofile")
suspend fun saveProfile(context: ApiContext) {
    try {
        val userRequest = context.req.body?.decodeToString()?.let { Json.decodeFromString<User>(it) }
        if (userRequest == null) {
            context.res.setBodyText(Json.encodeToString("Invalid request body."))
            return
        }
        // convert User to Profile and upsert into profile collection
        val profile = com.example.blogmultiplatform.models.Profile(
            _id = userRequest._id ?: "",
            username = userRequest.username,
            displayName = userRequest.displayName,
            bio = userRequest.bio,
            avatarUrl = userRequest.avatarUrl,
            role = userRequest.role
        )
        val result = context.data.getValue<MongoDB>().saveProfile(userRequest)
        context.res.setBodyText(Json.encodeToString(result))
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(e.message))
    }
}

@Api(routeOverride = "getprofile")
suspend fun getProfile(context: ApiContext) {
    try {
        val body = context.req.body?.decodeToString()
        if (body == null) {
            context.res.setBodyText(Json.encodeToString(null))
            return
        }
        // body is expected to be a plain string representing username or id
        val key = Json.decodeFromString<String>(body)
        val db = context.data.getValue<MongoDB>()
        val profile = if (key.length < 24) {
            // probably username
            db.getProfileByUsername(key)
        } else {
            db.getProfileById(key)
        }
        context.res.setBodyText(Json.encodeToString(profile))
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(null))
    }
}
