package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.UserRole
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.RequestToRegSpecialist
import el.ka.speechart.service.model.filterByUser
import el.ka.speechart.service.repository.RequestsRepository
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) :
  ViewModelWithSearchUsers(application, UserRole.SPECIALIST) {
  private val requestsToRegSpecialists = MutableLiveData<List<RequestToRegSpecialist>>(listOf())

  private val _filteredRequestsToRegSpecialist =
    MutableLiveData<List<RequestToRegSpecialist>>(listOf())
  val filteredRequestsToRegSpecialist: LiveData<List<RequestToRegSpecialist>> get() = _filteredRequestsToRegSpecialist

  fun loadRequestsToRegSpecialist() {
    val work = Work.LOAD_REQUESTS
    addWork(work)

    viewModelScope.launch {
      _error.value = RequestsRepository.loadRequests {
        requestsToRegSpecialists.value = it
        clearSearchRequest()
      }
      removeWork(work)
    }
  }

  val searchRequests = MutableLiveData("")

  fun clearSearchRequest() {
    searchRequests.value = ""
    filterRequests()
  }

  fun filterRequests() {
    val search = searchRequests.value!!
    val items = requestsToRegSpecialists.value!!

    _filteredRequestsToRegSpecialist.value = when {
      search.isEmpty() -> items
      else -> items.filterByUser(search)
    }
  }



  fun deleteRequest(request: RequestToRegSpecialist) {

  }
}