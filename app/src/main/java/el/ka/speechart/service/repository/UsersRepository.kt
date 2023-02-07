package el.ka.speechart.service.repository

import android.accounts.NetworkErrorException
import android.util.Log
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
      FirebaseService.requestsToRegSpecialigsCollection.whereEqualTo(Constants.FIELD_EMAIL, email)
        .limit(1).get().await().count()

    if (equalRequest > 0) return false

    // check users
    val equalUsers =
      FirebaseService.usersCollection.whereEqualTo(Constants.FIELD_EMAIL, email)
        .limit(1).get().await().count()

    return equalUsers <= 0
  }

  suspend fun loadUser(currentUid: String, onSuccess: (User) -> Unit): ErrorApp? = try {
    val doc = FirebaseService.usersCollection.document(currentUid).get().await()
    val user = doc.toObject(User::class.java)!!
    user.uid = doc.id
    onSuccess(user)

    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
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

}