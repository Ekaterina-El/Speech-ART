package el.ka.speechart.service.repository

import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import el.ka.speechart.other.Constants
import kotlinx.coroutines.tasks.await
import java.util.*

object FirebaseService {
  private val storage = Firebase.storage


  suspend fun uploadToStorage(uri: Uri, prefPath: String): String {
    val time = Calendar.getInstance().time
    val path = prefPath + "$time"

    val doc = storage.reference.child(path).putFile(uri).await()
    return doc.storage.downloadUrl.await().toString()
  }

  suspend fun deleteByUrl(url: String) {
    storage.getReferenceFromUrl(url).delete().await()
  }

  val usersCollection by lazy { Firebase.firestore.collection(Constants.USERS_COLLECTION) }
  val  requestsToRegSpecialigsCollection by lazy { Firebase.firestore.collection(Constants.REQUESTS_TO_REGISTRATION_SPECIALIST_COLLECTION) }
}