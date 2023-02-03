package el.ka.speechart.view.ui.core.owner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import el.ka.speechart.R
import el.ka.speechart.databinding.OwnerMainFragmentBinding
import el.ka.speechart.other.Action
import el.ka.speechart.other.AdapterDeleter
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.User
import el.ka.speechart.view.adapter.list.admin.AdminAdapter
import el.ka.speechart.view.adapter.list.admin.AdminViewHolder
import el.ka.speechart.view.dialog.AddAdminDialog
import el.ka.speechart.view.dialog.ConfirmDialog
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.AdminViewModel
import el.ka.speechart.viewModel.UserViewModel

class OwnerMainFragment : BaseFragment() {
  private lateinit var binding: OwnerMainFragmentBinding
  private val userViewModel by activityViewModels<UserViewModel>()
  private val adminViewModel by activityViewModels<AdminViewModel>()

  private val addAdminDialogListener by lazy {
    object: AddAdminDialog.Companion.ConfirmListener {
      override fun onContinue(user: User) {
        adminViewModel.addAdmin(user, getCredentials()!!)
      }
    }
  }
  private val addAdminDeleter by lazy { AddAdminDialog(requireContext(), addAdminDialogListener) }

  private lateinit var adminAdapter: AdminAdapter

  private val deletedUserObserver = Observer<User?> {
    if (it == null) return@Observer
    adminAdapter.removeItem(it)
    adminViewModel.afterNotifyAboutUserDeleter()
  }

  override val workObserver = Observer<List<Work>> {
    Log.d("workObserver", it.joinToString(", "))
    if (!it.contains(Work.LOAD_ADMINS)) {
      if (it.isEmpty()) hideLoadingDialog() else showLoadingDialog()
    } else if (it.isEmpty()) hideLoadingDialog()
  }

  private val adminsObserver = Observer<List<User>> {
    adminAdapter.setItems(it)
    binding.swipeRefreshLayout.isRefreshing = false
    binding.swipeRefreshLayout2.isRefreshing = false
  }

  private val adminAdapterCallback = AdapterDeleter {
    val admin = (it as AdminViewHolder).binding.user
    if (admin != null) adminViewModel.deleteAdmin(admin)
  }

  private val externalActionObserver = Observer<Action?> {
    if (it == Action.RESTART) {
      setCredentials(null)
      restartApp()
    }
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
    loadAdmins()
    initUI()
  }

  private fun initUI() {
    val helper = ItemTouchHelper(adminAdapterCallback)
    helper.attachToRecyclerView(binding.recyclerViewAdmin)

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.recyclerViewAdmin.addItemDecoration(decorator)

    binding.swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.secondary_color))
    binding.swipeRefreshLayout.setOnRefreshListener { loadAdmins() }

    binding.swipeRefreshLayout2.setColorSchemeColors(requireContext().getColor(R.color.secondary_color))
    binding.swipeRefreshLayout2.setOnRefreshListener { loadAdmins() }
  }

  private fun loadAdmins() {
    adminViewModel.loadAdmins()
    binding.swipeRefreshLayout.isRefreshing = true
    binding.swipeRefreshLayout2.isRefreshing = true
  }

  override fun onResume() {
    super.onResume()
    adminViewModel.filteredAdmins.observe(viewLifecycleOwner, adminsObserver)
    adminViewModel.error.observe(viewLifecycleOwner, errorObserver)
    adminViewModel.work.observe(viewLifecycleOwner, workObserver)
    adminViewModel.deletedUser.observe(viewLifecycleOwner, deletedUserObserver)

    userViewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
  }

  override fun onStop() {
    super.onStop()
    adminViewModel.filteredAdmins.removeObserver(adminsObserver)
    adminViewModel.error.removeObserver(errorObserver)
    adminViewModel.work.removeObserver(workObserver)
    adminViewModel.deletedUser.removeObserver(deletedUserObserver)

    userViewModel.externalAction.removeObserver(externalActionObserver)

  }

  fun showDialogForAddAdmin() {
    addAdminDeleter.openConfirmDialog()
  }

  private val exitDialogListener by lazy {
    object : ConfirmDialog.Companion.ConfirmListener {
      override fun onAgree(value: Any?) {
        userViewModel.logout()
        closeConfirmDialog()
      }

      override fun onDisagree() {
        closeConfirmDialog()
      }

    }
  }

  fun showExitDialog() {
    openConfirmDialog(getString(R.string.exit_message), exitDialogListener)
  }

}