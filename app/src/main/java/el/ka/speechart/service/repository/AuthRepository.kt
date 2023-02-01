package el.ka.speechart.service.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
import el.ka.speechart.service.model.User
import kotlinx.coroutines.tasks.await

object AuthRepository {
  private val auth = Firebase.auth

  suspend fun createAccount(user: User, password: String): ErrorApp? = try {
    val uid = auth.createUserWithEmailAndPassword(user.email, password).await().user!!.uid
    user.uid = uid
    UsersRepository.addUser(user)
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

}