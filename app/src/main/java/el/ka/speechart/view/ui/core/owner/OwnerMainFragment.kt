package el.ka.speechart.view.ui.core.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import el.ka.speechart.databinding.AdminMainFragmentBinding
import el.ka.speechart.databinding.OwnerMainFragmentBinding
import el.ka.speechart.service.model.User
import el.ka.speechart.view.adapter.list.admin.AdminAdapter
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.AdminViewModel
import el.ka.speechart.viewModel.UserViewModel

class OwnerMainFragment: BaseFragment() {
  private lateinit var binding: OwnerMainFragmentBinding
  private val userViewModel by activityViewModels<UserViewModel>()
  private val adminViewModel by activityViewModels<AdminViewModel>()

  private lateinit var adminAdapter: AdminAdapter

  private val adminsObserver = Observer<List<User>> {
    adminAdapter.setItems(it)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    adminAdapter = AdminAdapter()

    binding = OwnerMainFragmentBinding.inflate(layoutInflater)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      adminAdapter = this@OwnerMainFragment.adminAdapter
      master = this@OwnerMainFragment
      viewModel = this@OwnerMainFragment.adminViewModel
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    adminViewModel.loadAdmins()

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.recyclerViewAdmin.addItemDecoration(decorator)
  }

  override fun onResume() {
    super.onResume()
    adminViewModel.filteredAdmins.observe(viewLifecycleOwner, adminsObserver)
    adminViewModel.error.observe(viewLifecycleOwner, errorObserver)
    adminViewModel.work.observe(viewLifecycleOwner, workObserver)
  }

  override fun onStop() {
    super.onStop()
    adminViewModel.filteredAdmins.removeObserver(adminsObserver)
    adminViewModel.error.removeObserver(errorObserver)
    adminViewModel.work.removeObserver(workObserver)
  }

  fun showDialogForAddAdmin() {
    Toast.makeText(requireContext(), "${adminViewModel.search.value}", Toast.LENGTH_SHORT).show()
  }

}