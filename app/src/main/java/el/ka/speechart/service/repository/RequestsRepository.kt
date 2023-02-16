package el.ka.speechart.service.repository

import android.accounts.NetworkErrorException
import el.ka.speechart.other.Constants.FIELD_EMAIL
import el.ka.speechart.other.Constants.FIELD_PASSWORD
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
import el.ka.speechart.service.model.RequestToRegSpecialist
import kotlinx.coroutines.tasks.await

object RequestsRepository {
  suspend fun addRequestToRegistrationSpecialist(request: RequestToRegSpecialist): ErrorApp? = try {
    FirebaseService.requestsToRegSpecialistsCollection.add(request).await()
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun loadRequests(onSuccess: (List<RequestToRegSpecialist>) -> Unit): ErrorApp? = try {
    val requests = FirebaseService.requestsToRegSpecialistsCollection.get().await().mapNotNull {
      val request = it.toObject(RequestToRegSpecialist::class.java)
      request.id = it.id
      return@mapNotNull request
    }
    onSuccess(requests)
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun disagreeRequest(request: RequestToRegSpecialist, onSuccess: () -> Unit): ErrorApp? =
    try {
      FirebaseService.requestsToRegSpecialistsCollection.document(request.id).delete().await()
      onSuccess()
      null
    } catch (e: NetworkErrorException) {
      Errors.network
    } catch (e: Exception) {
      Errors.unknown
    }

  suspend fun agreeRequest(request: RequestToRegSpecialist, onSuccess: () -> Unit): ErrorApp? =
    try {
      AuthRepository.createAccount(request.userData, request.password)
      onSuccess()
      null
    } catch (e: NetworkErrorException) {
      Errors.network
    } catch (e: Exception) {
      Errors.unknown
    }

  suspend fun hasRequestWithEmail(email: String, password: String): Boolean {
    val requests = FirebaseService.requestsToRegSpecialistsCollection
      .whereEqualTo(FIELD_EMAIL, email)
      .whereEqualTo(FIELD_PASSWORD, password)
      .limit(1)
      .get()
      .await()

    return requests.size() > 0
  }
}