package com.orbatao.app.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val content: String = "",
    val timestamp: Long = 0L
)

interface DataRepository {
    val posts: Flow<List<Post>>
    suspend fun addPost(authorName: String, content: String)
}

class FirestoreDataRepository : DataRepository {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override val posts: Flow<List<Post>> = callbackFlow {
        val listener = db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val postsList = snapshot.documents.mapNotNull { doc ->
                        val post = doc.toObject(Post::class.java)
                        post?.copy(id = doc.id)
                    }
                    trySend(postsList)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addPost(authorName: String, content: String) {
        val user = auth.currentUser ?: throw IllegalStateException("User not logged in")
        val post = Post(
            userId = user.uid,
            authorName = authorName.ifBlank { user.displayName ?: "Dost" },
            content = content,
            timestamp = System.currentTimeMillis()
        )
        db.collection("posts").add(post)
    }
}
