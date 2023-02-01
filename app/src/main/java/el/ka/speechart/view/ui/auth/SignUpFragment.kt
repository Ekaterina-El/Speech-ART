package el.ka.speechart.view.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import el.ka.speechart.R
import el.ka.speechart.databinding.SingUpFragmentBinding
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.SignUpViewModel

class SignUpFragment : BaseFragment() {
  private lateinit var viewModel: SignUpViewModel
  private lateinit var binding: SingUpFragmentBinding

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
  }

  override fun onStop() {
    super.onStop()
    viewModel.work.removeObserver(workObserver)
    viewModel.error.removeObserver(errorObserver)
  }
}