package el.ka.speechart.view.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import el.ka.speechart.R
import el.ka.speechart.databinding.WelcomeFragmentBinding
import el.ka.speechart.view.ui.BaseFragment

class WelcomeFragment: BaseFragment() {
  private lateinit var binding: WelcomeFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    requireActivity().window.statusBarColor = requireContext().getColor(R.color.primary_color_dark)

    binding = WelcomeFragmentBinding.inflate(layoutInflater)
    binding.apply {
      master = this@WelcomeFragment
      lifecycleOwner = viewLifecycleOwner

    }
    return binding.root
  }

  fun goSignUp() {

  }

  fun goLogIn() {
    findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
  }
}