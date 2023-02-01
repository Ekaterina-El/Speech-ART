package el.ka.speechart.view.ui.spash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import el.ka.speechart.R
import el.ka.speechart.databinding.SplashFragmentBinding
import el.ka.speechart.view.ui.BaseFragment

class SplashFragment : BaseFragment() {
  private lateinit var binding: SplashFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    requireActivity().window.statusBarColor = requireContext().getColor(R.color.primary_color_dark)

    binding = SplashFragmentBinding.inflate(layoutInflater)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }

    return binding.root
  }

  override fun onResume() {
    super.onResume()
    Handler(Looper.getMainLooper()).postDelayed({
      findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
    }, 3000)
  }
}