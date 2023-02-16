package el.ka.speechart.view.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import el.ka.speechart.R
import el.ka.speechart.databinding.SingUpFragmentBinding
import el.ka.speechart.other.Action
import el.ka.speechart.other.Field
import el.ka.speechart.other.FieldError
import el.ka.speechart.view.dialog.RecoveryPasswordDialog
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.SignUpViewModel

class SignUpFragment : BaseFragment() {
  private lateinit var viewModel: SignUpViewModel
  private lateinit var binding: SingUpFragmentBinding

  private val externalActionObserver = Observer<Action?> {
    when (it) {
      Action.GO_NEXT -> findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
      Action.REQUEST_TO_REGISTRATION_ADDED -> notifyAboutAddedRequestToRegistration()
      else -> Unit
    }
  }

  private fun notifyAboutAddedRequestToRegistration() {
    val title = getString(R.string.request_sent)
    val message = getString(R.string.requests_to_specialist_added)
    showInformDialog(title, message)
  }

  private val fieldErrorsObserver = Observer<List<FieldError>> {
    showErrors(it)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
    binding = SingUpFragmentBinding.inflate(layoutInflater)
    binding.apply {
      viewModel = this@SignUpFragment.viewModel
      master = this@SignUpFragment
      lifecycleOwner = viewLifecycleOwner

    }
    return binding.root
  }

  override fun onResume() {
    super.onResume()
    viewModel.work.observe(viewLifecycleOwner, workObserver)
    viewModel.error.observe(viewLifecycleOwner, errorObserver)
    viewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
    viewModel.fieldErrors.observe(viewLifecycleOwner, fieldErrorsObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.work.removeObserver(workObserver)
    viewModel.error.removeObserver(errorObserver)
    viewModel.externalAction.removeObserver(externalActionObserver)
    viewModel.fieldErrors.removeObserver(fieldErrorsObserver)
  }

  private fun showErrors(errors: List<FieldError>?) {
    binding.layoutEmail.error = ""
    binding.layoutUserName.error = ""
    binding.layoutPassword.error = ""

    if (errors == null) return

    errors.forEach {
      val field = when (it.field) {
        Field.EMAIL -> binding.layoutEmail
        Field.NAME -> binding.layoutUserName
        Field.PASSWORD -> binding.layoutPassword
        else -> return
      }

      field.error = getString(it.errorType!!.messageRes)
    }
  }
}