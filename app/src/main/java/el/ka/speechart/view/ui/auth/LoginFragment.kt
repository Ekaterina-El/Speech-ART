package el.ka.speechart.view.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import el.ka.speechart.R
import el.ka.speechart.databinding.LoginFragmentBinding
import el.ka.speechart.databinding.WelcomeFragmentBinding
import el.ka.speechart.other.Action
import el.ka.speechart.other.Field
import el.ka.speechart.other.FieldError
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.LoginViewModel

class LoginFragment: BaseFragment() {
  private lateinit var viewModel: LoginViewModel
  private lateinit var binding: LoginFragmentBinding

  private val externalActionObserver = Observer<Action?> {
    if (it == Action.GO_NEXT) Toast.makeText(requireContext(), "You`re logined", Toast.LENGTH_SHORT).show()
  }

  private val fieldErrorsObserver = Observer<List<FieldError>> {
    showErrors(it)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    requireActivity().window.statusBarColor = requireContext().getColor(R.color.secondary_color)

    viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    binding = LoginFragmentBinding.inflate(layoutInflater)
    binding.apply {
      viewModel = this@LoginFragment.viewModel
      master = this@LoginFragment
      lifecycleOwner = viewLifecycleOwner

    }
    return binding.root
  }

  fun forgetPassword() {
    Toast.makeText(requireContext(), "А голову ты дома не забыл?", Toast.LENGTH_SHORT).show()
  }

  override fun onResume() {
    super.onResume()
    viewModel.error.observe(viewLifecycleOwner, errorObserver)
    viewModel.work.observe(viewLifecycleOwner, workObserver)
    viewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
    viewModel.fieldErrors.observe(viewLifecycleOwner, fieldErrorsObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.error.removeObserver(errorObserver)
    viewModel.work.removeObserver(workObserver)
    viewModel.externalAction.removeObserver(externalActionObserver)
    viewModel.fieldErrors.removeObserver(fieldErrorsObserver)
  }

  private fun showErrors(errors: List<FieldError>?) {
    binding.layoutEmail.error = ""
    binding.layoutPassword.error = ""

    if (errors == null) return

    errors.forEach {
      val field = when(it.field) {
        Field.EMAIL -> binding.layoutEmail
        Field.PASSWORD -> binding.layoutPassword
        else -> return
      }

      field.error = getString(it.errorType!!.messageRes)
    }
  }
}