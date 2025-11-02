package com.example.blogmultiplatform.util

import com.example.blogmultiplatform.models.ApiListResponse
import com.example.blogmultiplatform.models.ApiResponse
import com.example.shared.Category
import com.example.blogmultiplatform.models.Constants.AUTHOR_PARAM
import com.example.blogmultiplatform.models.Constants.CATEGORY_PARAM
import com.example.blogmultiplatform.models.Constants.POST_ID_PARAM
import com.example.blogmultiplatform.models.Constants.QUERY_PARAM
import com.example.blogmultiplatform.models.Constants.SKIP_PARAM
import com.example.blogmultiplatform.models.Newsletter
import com.example.blogmultiplatform.models.Post
import com.example.blogmultiplatform.models.RandomJoke
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.models.UserWithoutPassword
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.browser.http.http
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.js.Date

suspend fun checkUserExistence(user: User): UserWithoutPassword? {
    return try {
        window.api.tryPost(
            apiPath = "usercheck",
            body = Json.encodeToString(user).encodeToByteArray()
        )?.decodeToString().parseData()
    } catch (e: Exception) {
        println("CURRENT_USER")
        println(e.message)
        null
    }
}

suspend fun checkUserId(id: String): Boolean {
    return try {
        window.api.tryPost(
            apiPath = "checkuserid",
            body = Json.encodeToString(id).encodeToByteArray()
        )?.decodeToString().parseData()
    } catch (e: Exception) {
        println(e.message.toString())
        false
    }
}

suspend fun fetchRandomJoke(onComplete: (RandomJoke) -> Unit) {
    val date = localStorage["date"]
    if (date != null) {
        val difference = (Date.now() - date.toDouble())
        val dayHasPassed = difference >= 86400000
        if (dayHasPassed) {
            try {
                val result = window.http.get(Constants.HUMOR_API_URL).decodeToString()
                onComplete(result.parseData())
                localStorage["date"] = Date.now().toString()
                localStorage["joke"] = result
            } catch (e: Exception) {
                onComplete(RandomJoke(id = -1, joke = e.message.toString()))
                println(e.message)
            }
        } else {
            try {
                localStorage["joke"]?.parseData<RandomJoke>()?.let { onComplete(it) }
            } catch (e: Exception) {
                onComplete(RandomJoke(id = -1, joke = e.message.toString()))
                println(e.message)
            }
        }
    } else {
        try {
            val result = window.http.get(Constants.HUMOR_API_URL).decodeToString()
            onComplete(result.parseData())
            localStorage["date"] = Date.now().toString()
            localStorage["joke"] = result
        } catch (e: Exception) {
            onComplete(RandomJoke(id = -1, joke = e.message.toString()))
            println(e.message)
        }
    }
}

suspend fun addPost(post: Post): Boolean {
    return try {
        window.api.tryPost(
            apiPath = "addpost",
            body = Json.encodeToString(post).encodeToByteArray()
        )?.decodeToString().toBoolean()
    } catch (e: Exception) {
        println(e.message)
        false
    }
}

suspend fun updatePost(post: Post): Boolean {
    return try {
        window.api.tryPost(
            apiPath = "updatepost",
            body = Json.encodeToString(post).encodeToByteArray()
        )?.decodeToString().toBoolean()
    } catch (e: Exception) {
        println(e.message)
        false
    }
}

suspend fun fetchMyPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "readmyposts?${SKIP_PARAM}=$skip&${AUTHOR_PARAM}=${localStorage["username"]}"
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e)
        onError(e)
    }
}

suspend fun fetchMainPosts(
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(apiPath = "readmainposts")?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e)
        onError(e)
    }
}

suspend fun fetchLatestPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result =
            window.api.tryGet(apiPath = "readlatestposts?${SKIP_PARAM}=$skip")?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e)
        onError(e)
    }
}

suspend fun fetchSponsoredPosts(
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(apiPath = "readsponsoredposts")?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e)
        onError(e)
    }
}

suspend fun fetchPopularPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result =
            window.api.tryGet(apiPath = "readpopularposts?${SKIP_PARAM}=$skip")?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e)
        onError(e)
    }
}

suspend fun deleteSelectedPosts(ids: List<String>): Boolean {
    return try {
        val result = window.api.tryPost(
            apiPath = "deleteselectedposts",
            body = Json.encodeToString(ids).encodeToByteArray()
        )?.decodeToString()
        result.toBoolean()
    } catch (e: Exception) {
        println(e.message)
        false
    }
}

suspend fun searchPostsByTitle(
    query: String,
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "searchposts?${QUERY_PARAM}=$query&${SKIP_PARAM}=$skip"
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e.message)
        onError(e)
    }
}

suspend fun searchPostsByCategory(
    category: Category,
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "searchpostsbycategory?${CATEGORY_PARAM}=${category.name}&${SKIP_PARAM}=$skip"
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e.message)
        onError(e)
    }
}

suspend fun fetchSelectedPost(id: String): ApiResponse {
    return try {
        val result = window.api.tryGet(
            apiPath = "readselectedpost?${POST_ID_PARAM}=$id"
        )?.decodeToString()
        result?.parseData() ?: ApiResponse.Error(message = "Result is null")
    } catch (e: Exception) {
        println(e)
        ApiResponse.Error(message = e.message.toString())
    }
}

suspend fun subscribeToNewsletter(newsletter: Newsletter): String {
    return window.api.tryPost(
        apiPath = "subscribe",
        body = Json.encodeToString(newsletter).encodeToByteArray()
    )?.decodeToString().toString().replace("\"", "")
}

sealed class SignUpResult {
    data class Loading(val isLoading: Boolean) : SignUpResult()
    data class Success(val user: UserWithoutPassword) : SignUpResult()
    data class Error(val message: String) : SignUpResult()
}

suspend fun createUserAccount(user: User): SignUpResult {
    return try {
        val response = window.api.tryPost(
            apiPath = "signup",
            body = Json.encodeToString(user).encodeToByteArray()
        )?.decodeToString()
        // Check for known error messages
        if (response != null && (response.contains("Username already exists", ignoreCase = true) || response.contains("User already exists", ignoreCase = true))) {
            return SignUpResult.Error("Username already exists.")
        }
        // Try to parse as UserWithoutPassword
        return try {
            val userObj = response?.parseData<UserWithoutPassword>()
            if (userObj != null) {
                SignUpResult.Success(userObj)
            } else {
                SignUpResult.Error(response ?: "Unknown error")
            }
        } catch (e: Exception) {
            SignUpResult.Error(response ?: "Unknown error")
        }
    } catch (e: Exception) {
        SignUpResult.Error(e.message ?: "Unknown error")
    }
}

suspend fun fetchProfile(key: String): com.example.blogmultiplatform.models.Profile? {
    return try {
        val body = Json.encodeToString(key)
        val result = window.api.tryPost(apiPath = "getprofile", body = body.encodeToByteArray())?.decodeToString()
        if (result == null || result == "null") null else result.parseData()
    } catch (e: Exception) {
        println("fetchProfile error: ${e.message}")
        null
    }
}

suspend fun ensureProfileExists(username: String) : com.example.blogmultiplatform.models.Profile? {
    // Try fetch first
    val existing = fetchProfile(username)
    if (existing != null) return existing
    // Create minimal profile object and save via saveprofile API
    try {
        val profileUser = com.example.blogmultiplatform.models.User(
            _id = "",
            username = username,
            password = "",
            role = "client",
            displayName = "",
            bio = "",
            avatarUrl = ""
        )
        val saved = window.api.tryPost(apiPath = "saveprofile", body = Json.encodeToString(profileUser).encodeToByteArray())?.decodeToString()
        return if (saved != null && saved.toBoolean()) fetchProfile(username) else null
    } catch (e: Exception) {
        println("ensureProfileExists error: ${e.message}")
        return null
    }
}

inline fun <reified T> String?.parseData(): T {
    return Json.decodeFromString(this.toString())
}