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
  val countOfLoadedRequests: Int get() = requestsToRegSpecialists.value!!.size

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

  fun disagreeRequest(request: RequestToRegSpecialist) {
     val work = Work.AGREE_REQUEST
    addWork(work)
    viewModelScope.launch {
      _error.value = RequestsRepository.disagreeRequest(request) {
        editRequestList(request, isAdd = false)
      }
      removeWork(work)
    }
  }

  private fun editRequestList(request: RequestToRegSpecialist, isAdd: Boolean) {
    val requests = requestsToRegSpecialists.value!!.toMutableList()
    if (isAdd) requests.add(request) else requests.remove(request)

    requestsToRegSpecialists.value = requests
    clearSearchRequest()
  }

  fun agreeRequest(request: RequestToRegSpecialist) {
    val work = Work.DISAGREE_REQUEST
    addWork(work)
    viewModelScope.launch {
      _error.value = RequestsRepository.agreeRequest(request) {
        editRequestList(request, isAdd = false)
      }
      removeWork(work)
    }
  }
}