package el.ka.speechart.service.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
  } catch (e: FirebaseAuthWeakPasswordException) {
    Errors.weakPassword
  } catch (e: FirebaseAuthUserCollisionException) {
    Errors.userCollision
  }
  catch (e: Exception) {
    Errors.unknown
  }

  suspend fun login(email: String, password: String): ErrorApp? = try {
    auth.signInWithEmailAndPassword(email, password).await().user!!.uid
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: FirebaseAuthInvalidCredentialsException) {
    Errors.invalidEmailPassword
  } catch (e: FirebaseAuthInvalidUserException) {
    Errors.invalidEmailPassword
  } catch (e: Exception) {
    Errors.unknown
  }

}