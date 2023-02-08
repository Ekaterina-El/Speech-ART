package el.ka.speechart.view.ui.core.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.speechart.R
import el.ka.speechart.databinding.SpecialistProfileFragmentBinding
import el.ka.speechart.other.Work
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.SpecialistViewModel
import el.ka.speechart.viewModel.UserViewModel

class SpecialistProfileFragment : UserBaseFragment() {
  private lateinit var binding: SpecialistProfileFragmentBinding

  private val specialistViewModel: SpecialistViewModel by activityViewModels()
  override val userViewModel: UserViewModel by activityViewModels()

  val list = listOf(Work.LOAD_USER, Work.LOAD_REVIEWS)
  override val workObserver = Observer<List<Work>> {
    val isLoad =
      when {
        it.isEmpty() -> false
        else -> it.map { item -> if (list.contains(item)) 1 else 0 }.reduce { a, b -> a + b } > 0
      }
    binding.swipeRefreshLayout.isRefreshing = isLoad
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SpecialistProfileFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@SpecialistProfileFragment
      viewModel = this@SpecialistProfileFragment.specialistViewModel
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (specialistViewModel.profile.value == null) specialistViewModel.loadProfile()

    val color = requireContext().getColor(R.color.loader_color)
    binding.swipeRefreshLayout.setColorSchemeColors(color)
    binding.swipeRefreshLayout.setOnRefreshListener { specialistViewModel.loadProfile() }
  }

  override fun onResume() {
    super.onResume()
    specialistViewModel.work.observe(viewLifecycleOwner, workObserver)
  }

  override fun onStop() {
    super.onStop()
    specialistViewModel.work.removeObserver(workObserver)
  }
}