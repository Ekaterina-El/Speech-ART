package el.ka.speechart.view.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import el.ka.speechart.databinding.StudyMainFragmentBinding
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.UserViewModel

class StudyMainFragment: BaseFragment() {
  private lateinit var binding: StudyMainFragmentBinding
  private val userViewModel by activityViewModels<UserViewModel>()

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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.textViewRole.text = userViewModel.user.value!!.role.toString()
  }

}