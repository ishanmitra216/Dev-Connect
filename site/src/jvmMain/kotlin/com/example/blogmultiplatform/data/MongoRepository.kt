package com.example.blogmultiplatform.data

import com.example.shared.Category
import com.example.blogmultiplatform.models.Newsletter
import com.example.blogmultiplatform.models.Post
import com.example.blogmultiplatform.models.PostWithoutDetails
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.models.Profile
import com.example.blogmultiplatform.models.Payment

interface MongoRepository {
    suspend fun addPost(post: Post): Boolean
    suspend fun updatePost(post: Post): Boolean
    suspend fun readMyPosts(skip: Int, author: String): List<PostWithoutDetails>
    suspend fun readMainPosts(): List<PostWithoutDetails>
    suspend fun readLatestPosts(skip: Int): List<PostWithoutDetails>
    suspend fun readSponsoredPosts(): List<PostWithoutDetails>
    suspend fun readPopularPosts(skip: Int): List<PostWithoutDetails>
    suspend fun deleteSelectedPosts(ids: List<String>): Boolean
    suspend fun searchPostsByTittle(query: String, skip: Int): List<PostWithoutDetails>
    suspend fun searchPostsByCategory(category: Category, skip: Int): List<PostWithoutDetails>
    suspend fun readSelectedPost(id: String): Post
    suspend fun checkUserExistence(user: User): User?
    suspend fun checkUserId(id: String): Boolean
    suspend fun subscribe(newsletter: Newsletter): String
    suspend fun createUser(user: User): User?

    // Save or update a user's profile (upsert by username or _id)
    suspend fun saveProfile(user: User): Boolean

    // Save or update profile document in 'profile' collection
    suspend fun saveProfile(profile: Profile): Boolean

    // Payment persistence
    suspend fun savePayment(payment: Payment): Boolean
    suspend fun readPayments(limit: Int = 100): List<Payment>
}