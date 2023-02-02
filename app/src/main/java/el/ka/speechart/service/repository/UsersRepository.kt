package el.ka.speechart.service.repository

import android.accounts.NetworkErrorException
import el.ka.speechart.other.Constants
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
import el.ka.speechart.other.UserRole
import el.ka.speechart.service.model.User
import kotlinx.coroutines.tasks.await

object UsersRepository {
  suspend fun addUser(user: User): ErrorApp? = try {
    FirebaseService.usersCollection.document(user.uid).set(user).await()
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
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

}