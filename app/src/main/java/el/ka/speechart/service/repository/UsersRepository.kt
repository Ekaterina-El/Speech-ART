package el.ka.speechart.service.repository

import android.accounts.NetworkErrorException
import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import el.ka.speechart.other.Constants
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
import el.ka.speechart.other.UserRole
import el.ka.speechart.service.model.User
import kotlinx.coroutines.tasks.await

object UsersRepository {
  suspend fun addUser(user: User, onSuccess: suspend (User) -> Unit): ErrorApp? = try {
    FirebaseService.usersCollection.document(user.uid).set(user).await()
    onSuccess(user)
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun isUniqueEmail(email: String): Boolean {
    // check requests
    val equalRequest =
      FirebaseService.requestsToRegSpecialistsCollection.whereEqualTo(Constants.FIELD_EMAIL, email)
        .limit(1).get().await().count()

    if (equalRequest > 0) return false

    // check users
    val equalUsers =
      FirebaseService.usersCollection.whereEqualTo(Constants.FIELD_EMAIL, email)
        .limit(1).get().await().count()

    return equalUsers <= 0
  }

  suspend fun loadUser(currentUid: String? = null, onSuccess: (User) -> Unit): ErrorApp? = try {
    val uid = currentUid ?: AuthRepository.currentUid!!
    val doc = FirebaseService.usersCollection.document(uid).get().await()
    val user = doc.toObject(User::class.java)

    val error = if (user == null) {
      Errors.documentNoFound
    } else {
      user.uid = doc.id
      onSuccess(user)
      null
    }

    error
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Log.d("loadCurrentUser", "error")
    Errors.unknown
  }

  suspend fun loadUsersByRole(role: UserRole, onSuccess: (List<User>) -> Unit): ErrorApp? = try {
    val docs =
      FirebaseService.usersCollection.whereEqualTo(Constants.FIELD_ROLE, role).get().await()
    val users = docs.map {
      val user = it.toObject(User::class.java)
      user.uid = it.id
      return@map user
    }
    onSuccess(users)
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun deleteUser(user: User, onSuccess: () -> Unit): ErrorApp? = try {
    FirebaseService.usersCollection.document(user.uid).delete().await()
    onSuccess()
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun updateProfileImage(
    oldProfileUrl: String,
    uri: Uri,
    onSuccess: (String) -> Unit
  ): ErrorApp? = try {
    val uid = AuthRepository.currentUid!!

    if (oldProfileUrl != "") FirebaseService.deleteByUrl(oldProfileUrl)

    val url = FirebaseService.uploadToStorage(uri, "users/profiles/")
    FirebaseService.updateUsersField(uid, Constants.FIELD_PROFILE_URL, url)
    onSuccess(url)

    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun updateUserDescription(newDescription: String, onSuccess: () -> Unit): ErrorApp? =
    try {
      FirebaseService.updateUsersField(
        AuthRepository.currentUid!!,
        Constants.FIELD_DESCRIPTION,
        newDescription
      )
      onSuccess()
      null
    } catch (e: FirebaseNetworkException) {
      Errors.network
    } catch (e: Exception) {
      Errors.unknown
    }

  suspend fun getUserById(id: String): User? {
    val doc = FirebaseService.usersCollection.document(id).get().await()
    val user = doc.toObject(User::class.java) ?: return null
    user.uid = doc.id
    return user
  }
}