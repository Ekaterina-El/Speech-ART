package el.ka.speechart.view.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import el.ka.speechart.R
import el.ka.speechart.databinding.LoginFragmentBinding
import el.ka.speechart.databinding.WelcomeFragmentBinding
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.LoginViewModel

class LoginFragment: BaseFragment() {
  private lateinit var viewModel: LoginViewModel
  private lateinit var binding: LoginFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    requireActivity().window.statusBarColor = requireContext().getColor(R.color.primary_color_dark)

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

  fun goLogIn() {
    val s = "${viewModel.email.value} : ${viewModel.password.value}"
    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
  }
}