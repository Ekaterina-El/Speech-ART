package el.ka.speechart.view.ui.core.owner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import el.ka.speechart.R
import el.ka.speechart.databinding.OwnerMainFragmentBinding
import el.ka.speechart.other.Credentials
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.User
import el.ka.speechart.view.adapter.list.admin.AdminAdapter
import el.ka.speechart.view.adapter.list.admin.AdminViewHolder
import el.ka.speechart.view.dialog.AddAdminDialog
import el.ka.speechart.view.dialog.ConfirmDialog
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.OwnerViewModel
import el.ka.speechart.viewModel.UserViewModel

class OwnerMainFragment : UserBaseFragment() {
  private lateinit var binding: OwnerMainFragmentBinding
  override val userViewModel by activityViewModels<UserViewModel>()
  private val ownerViewModel by activityViewModels<OwnerViewModel>()

  private val addAdminDialogListener by lazy {
    object : AddAdminDialog.Companion.ConfirmListener {
      override fun onContinue(user: User) {
        ownerViewModel.addAdmin(user, getCredentials()!!)
      }
    }
  }
  private val addAdminDialog by lazy { AddAdminDialog(requireContext(), addAdminDialogListener) }

  private val newUserCredentialsObserver = Observer<Credentials?> {
    if (it == null) return@Observer

    showNewAdminCredentials(it)
    addAdminDialog.close()
    ownerViewModel.afterNotifyAddedUser()
  }

  private fun showNewAdminCredentials(credentials: Credentials) {

    val title = getString(R.string.login_password_new_admin)
    val message = getString(
      R.string.login_password_new_admin_credentials,
      credentials.email,
      credentials.password
    )
    val warningText = getString(R.string.new_admin_warning)

    showInformDialog(title, message, warningText) {
      copyToClipboard(message)
    }
  }

  private lateinit var adminAdapter: AdminAdapter

  override val workObserver = Observer<List<Work>> {
    Log.d("workObserver", it.joinToString(", "))
    if (!it.contains(Work.LOAD_USERS)) {
      if (it.isEmpty()) hideLoadingDialog() else showLoadingDialog()
    } else if (it.isEmpty()) hideLoadingDialog()
  }

  private val adminsObserver = Observer<List<User>> {
    adminAdapter.setItems(it)
    binding.swipeRefreshLayout.isRefreshing = false
    binding.swipeRefreshLayout2.isRefreshing = false
  }

  private val adminAdapterListener = object: AdminViewHolder.Companion.Listener {
    override fun delete(user: User) {
      val message = getString(R.string.delete_admin_message, user.fullName)
      openConfirmDialog(message, object : ConfirmDialog.Companion.ConfirmListener {
        override fun onAgree(value: Any?) {
          ownerViewModel.deleteUser(user)
          closeConfirmDialog()
        }

        override fun onDisagree() {
          closeConfirmDialog()
        }
      })
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    adminAdapter = AdminAdapter(requireContext(), adminAdapterListener)

    binding = OwnerMainFragmentBinding.inflate(layoutInflater)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      adminAdapter = this@OwnerMainFragment.adminAdapter
      master = this@OwnerMainFragment
      viewModel = this@OwnerMainFragment.ownerViewModel
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loadAdmins()
    initUI()
  }

  private fun initUI() {
    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.recyclerViewAdmin.addItemDecoration(decorator)

    binding.swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.primary_color))
    binding.swipeRefreshLayout.setOnRefreshListener { loadAdmins() }

    binding.swipeRefreshLayout2.setColorSchemeColors(requireContext().getColor(R.color.primary_color))
    binding.swipeRefreshLayout2.setOnRefreshListener { loadAdmins() }

    binding.noFound.findViewById<TextView>(R.id.message).text = getString(R.string.admins_no_found)
  }

  private fun loadAdmins() {
    ownerViewModel.loadUsers()
    binding.swipeRefreshLayout.isRefreshing = true
    binding.swipeRefreshLayout2.isRefreshing = true
  }

  override fun onResume() {
    super.onResume()
    ownerViewModel.filteredUsers.observe(viewLifecycleOwner, adminsObserver)
    ownerViewModel.error.observe(viewLifecycleOwner, errorObserver)
    ownerViewModel.work.observe(viewLifecycleOwner, workObserver)
    ownerViewModel.newUserCredentials.observe(viewLifecycleOwner, newUserCredentialsObserver)
  }

  override fun onStop() {
    super.onStop()
    ownerViewModel.filteredUsers.removeObserver(adminsObserver)
    ownerViewModel.error.removeObserver(errorObserver)
    ownerViewModel.work.removeObserver(workObserver)
    ownerViewModel.newUserCredentials.removeObserver(newUserCredentialsObserver)
  }

  fun showDialogForAddAdmin() {
    addAdminDialog.openConfirmDialog()
  }
}