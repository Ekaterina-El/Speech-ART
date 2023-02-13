package el.ka.speechart.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.speechart.other.Action
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Work

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
  protected val context = MutableLiveData<Context>()
  fun setContext(context: Context) {
    this.context.value = context
  }

  protected val _externalAction = MutableLiveData<Action?>(null)
  val externalAction: LiveData<Action?> = _externalAction

  protected val _error = MutableLiveData<ErrorApp?>(null)
  val error: LiveData<ErrorApp?> = _error

  private val workStack = MutableLiveData<List<Work>>(listOf())
  val work: LiveData<List<Work>> get() = workStack

  protected fun addWork(work: Work) {
    changeWorks(work, Action.ADD)
  }

  protected fun removeWork(work: Work) {
    changeWorks(work, Action.REMOVE)
  }

  private fun changeWorks(work: Work, action: Action) {
    val works = workStack.value!!.toMutableList()
    when (action) {
      Action.ADD -> works.add(work)
      Action.REMOVE -> works.remove(work)
      else -> {}
    }
    workStack.postValue(works)
  }
}