package el.ka.speechart.view.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import el.ka.speechart.R
import el.ka.speechart.databinding.LoginFragmentBinding
import el.ka.speechart.databinding.SingUpFragmentBinding
import el.ka.speechart.databinding.WelcomeFragmentBinding
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.LoginViewModel
import el.ka.speechart.viewModel.SignUpViewModel

class SignUpFragment: BaseFragment() {
  private lateinit var viewModel: SignUpViewModel
  private lateinit var binding: SingUpFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    requireActivity().window.statusBarColor = requireContext().getColor(R.color.primary_color_dark)

    viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
    binding = SingUpFragmentBinding.inflate(layoutInflater)
    binding.apply {
      viewModel = this@SignUpFragment.viewModel
      master = this@SignUpFragment
      lifecycleOwner = viewLifecycleOwner

    }
    return binding.root
  }


  fun goRegistration() {
    val s = "${viewModel.email.value} : ${viewModel.password.value} : ${viewModel.fullName.value} : ${viewModel.passwordRepeat.value}"
    Log.d("goRegistration", s)
  }
}