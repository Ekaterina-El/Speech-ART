package el.ka.speechart.view.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import el.ka.speechart.R
import el.ka.speechart.databinding.SingUpFragmentBinding
import el.ka.speechart.other.Action
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.SignUpViewModel

class SignUpFragment : BaseFragment() {
  private lateinit var viewModel: SignUpViewModel
  private lateinit var binding: SingUpFragmentBinding

  private val externalActionObserver = Observer<Action?> {
    if (it == Action.GO_NEXT) findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    requireActivity().window.statusBarColor = requireContext().getColor(R.color.secondary_color)

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
  }

  override fun onStop() {
    super.onStop()
    viewModel.work.removeObserver(workObserver)
    viewModel.error.removeObserver(errorObserver)
    viewModel.externalAction.removeObserver(externalActionObserver)
  }
}