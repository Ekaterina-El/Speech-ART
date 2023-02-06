package el.ka.speechart.view.ui.core.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import el.ka.speechart.databinding.AdminRequestsFragmentBinding
import el.ka.speechart.databinding.AdminSpecialistsFragmentBinding
import el.ka.speechart.view.ui.BaseFragment

class AdminRequestsFragment: BaseFragment() {
  private lateinit var binding: AdminRequestsFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = AdminRequestsFragmentBinding.inflate(LayoutInflater.from(requireContext()), container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }
}