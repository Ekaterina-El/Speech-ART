package el.ka.speechart.service.repository

import android.accounts.NetworkErrorException
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
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

}