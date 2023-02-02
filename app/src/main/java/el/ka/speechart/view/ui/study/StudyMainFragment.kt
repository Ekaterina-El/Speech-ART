package el.ka.speechart.view.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import el.ka.speechart.databinding.StudyMainFragmentBinding
import el.ka.speechart.view.ui.BaseFragment

class StudyMainFragment: BaseFragment() {
  private lateinit var binding: StudyMainFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = StudyMainFragmentBinding.inflate(layoutInflater)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

}