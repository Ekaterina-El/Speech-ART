package el.ka.speechart.view.ui.core.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import el.ka.speechart.R
import el.ka.speechart.databinding.AdminSpecialistsFragmentBinding
import el.ka.speechart.other.AdapterDeleter
import el.ka.speechart.service.model.User
import el.ka.speechart.view.adapter.list.specialist.SpecialistViewHolder
import el.ka.speechart.view.adapter.list.specialist.SpecialistsAdapter
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.AdminViewModel
import el.ka.speechart.viewModel.UserViewModel

class AdminSpecialistsFragment : UserBaseFragment() {
  private lateinit var binding: AdminSpecialistsFragmentBinding
  override val userViewModel by activityViewModels<UserViewModel>()
  private val adminViewModel by activityViewModels<AdminViewModel>()

  private lateinit var specialistsAdapter: SpecialistsAdapter


  private val specialistsObserver = Observer<List<User>> {
    specialistsAdapter.setItems(it)
    binding.swipeRefreshLayout.isRefreshing = false
    binding.swipeRefreshLayout2.isRefreshing = false
  }

  private val specialistsAdapterCallback = AdapterDeleter(
    onLeft = {
      val user = (it as SpecialistViewHolder).binding.user
      if (user != null) adminViewModel.deleteUser(user)
    }
  )

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    specialistsAdapter = SpecialistsAdapter()
    binding = AdminSpecialistsFragmentBinding.inflate(
      LayoutInflater.from(requireContext()),
      container,
      false
    )
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@AdminSpecialistsFragment
      viewModel = this@AdminSpecialistsFragment.adminViewModel
      specialistsAdapter = this@AdminSpecialistsFragment.specialistsAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loadSpecialists()


    binding.swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.secondary_color))
    binding.swipeRefreshLayout.setOnRefreshListener { loadSpecialists() }

    binding.swipeRefreshLayout2.setColorSchemeColors(requireContext().getColor(R.color.secondary_color))
    binding.swipeRefreshLayout2.setOnRefreshListener { loadSpecialists() }

    val helper = ItemTouchHelper(specialistsAdapterCallback)
    helper.attachToRecyclerView(binding.recyclerViewSpecialists)

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.recyclerViewSpecialists.addItemDecoration(decorator)

    binding.noFound.findViewById<TextView>(R.id.message).text = getString(R.string.specialists_no_found)
  }

  override fun onResume() {
    super.onResume()
    adminViewModel.filteredUsers.observe(viewLifecycleOwner, specialistsObserver)
  }

  override fun onStop() {
    super.onStop()
    adminViewModel.filteredUsers.removeObserver(specialistsObserver)
  }

  private fun loadSpecialists() {
    adminViewModel.loadUsers()
    binding.swipeRefreshLayout.isRefreshing = true
    binding.swipeRefreshLayout2.isRefreshing = true
  }

}