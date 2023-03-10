package el.ka.speechart.view.ui.core.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import el.ka.speechart.R
import el.ka.speechart.databinding.AdminRequestsFragmentBinding
import el.ka.speechart.other.AdapterDeleter
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.RequestToRegSpecialist
import el.ka.speechart.view.adapter.list.requests.RequestToRegSpecialistAdapter
import el.ka.speechart.view.adapter.list.requests.RequestViewHolder
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.AdminViewModel
import el.ka.speechart.viewModel.UserViewModel

class AdminRequestsFragment : UserBaseFragment() {
  private lateinit var binding: AdminRequestsFragmentBinding
  private lateinit var requestsAdapter: RequestToRegSpecialistAdapter

  override val userViewModel by activityViewModels<UserViewModel>()
  private val adminViewModel by activityViewModels<AdminViewModel>()

  private val requestsObserver = Observer<List<RequestToRegSpecialist>> {
    requestsAdapter.setItems(it)
  }

  private val requestAdapterListener by lazy {
    object: RequestViewHolder.Companion.Listener {
      override fun agree(request: RequestToRegSpecialist) {
        adminViewModel.agreeRequest(request, getCredentials()!!)
      }

      override fun disagree(request: RequestToRegSpecialist) {
        adminViewModel.disagreeRequest(request)
      }
    }
  }

  val list = listOf(Work.LOAD_USERS, Work.LOAD_REQUESTS, Work.AGREE_REQUEST, Work.DISAGREE_REQUEST)


  override val workObserver = Observer<List<Work>> {

    val isLoad =
      when {
        it.isEmpty() -> false
        else -> it.map { item -> if (list.contains(item)) 1 else 0 }.reduce { a, b -> a + b } > 0
      }
    binding.swipeRefreshLayout.isRefreshing = isLoad
    binding.swipeRefreshLayout2.isRefreshing = isLoad
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    requestsAdapter = RequestToRegSpecialistAdapter(requireContext(), requestAdapterListener)
    binding =
      AdminRequestsFragmentBinding.inflate(LayoutInflater.from(requireContext()), container, false)

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@AdminRequestsFragment
      viewModel = this@AdminRequestsFragment.adminViewModel
      requestsAdapter = this@AdminRequestsFragment.requestsAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (adminViewModel.countOfLoadedRequests == 0) adminViewModel.loadRequestsToRegSpecialist()

    val color = requireContext().getColor(R.color.primary_color)
    binding.swipeRefreshLayout.setColorSchemeColors(color)
    binding.swipeRefreshLayout.setOnRefreshListener { adminViewModel.loadRequestsToRegSpecialist() }

    binding.swipeRefreshLayout2.setColorSchemeColors(color)
    binding.swipeRefreshLayout2.setOnRefreshListener { adminViewModel.loadRequestsToRegSpecialist() }

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.recyclerViewRequests.addItemDecoration(decorator)

    binding.noFound.findViewById<TextView>(R.id.message).text = getString(R.string.requests_no_found)
  }

  override fun onResume() {
    super.onResume()
    adminViewModel.filteredRequestsToRegSpecialist.observe(viewLifecycleOwner, requestsObserver)
    adminViewModel.work.observe(viewLifecycleOwner, workObserver)
  }

  override fun onStop() {
    super.onStop()
    adminViewModel.filteredRequestsToRegSpecialist.removeObserver(requestsObserver)
    adminViewModel.work.removeObserver(workObserver)
  }
}