package el.ka.speechart.service.repository

import android.accounts.NetworkErrorException
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
import el.ka.speechart.service.model.RequestToRegSpecialist
import kotlinx.coroutines.tasks.await

object RequestsRepository {
  suspend fun addRequestToRegistrationSpecialist(request: RequestToRegSpecialist): ErrorApp? = try {
    FirebaseService.requestsToRegSpecialigsCollection.add(request).await()
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun loadRequests(onSuccess: (List<RequestToRegSpecialist>) -> Unit): ErrorApp? = try {
    val requests =  FirebaseService.requestsToRegSpecialigsCollection.get().await().mapNotNull {
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

  suspend fun disagreeRequest(request: RequestToRegSpecialist, onSuccess: () -> Unit): ErrorApp? = try {
    FirebaseService.requestsToRegSpecialigsCollection.document(request.id).delete().await()
    onSuccess()
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun agreeRequest(request: RequestToRegSpecialist, onSuccess: (RequestToRegSpecialist) -> Unit): ErrorApp? = try {
    val doc = FirebaseService.requestsToRegSpecialigsCollection.add(request).await()
    request.id = doc.id
    onSuccess(request)
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }
}