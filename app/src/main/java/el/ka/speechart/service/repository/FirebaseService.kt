package el.ka.speechart.service.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import el.ka.speechart.other.Constants

object FirebaseService {
  val usersCollection by lazy { Firebase.firestore.collection(Constants.USERS_COLLECTION) }
}