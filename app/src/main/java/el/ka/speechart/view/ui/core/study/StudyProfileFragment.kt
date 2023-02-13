package el.ka.speechart.view.ui.core.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.speechart.R
import el.ka.speechart.databinding.StudyProfileFragmentBinding
import el.ka.speechart.other.CropOptions
import el.ka.speechart.other.ImageChanger
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.User
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.StudyViewModel
import el.ka.speechart.viewModel.UserViewModel

class StudyProfileFragment : UserBaseFragment() {
  private lateinit var binding: StudyProfileFragmentBinding

  private val hasLoad: Boolean
    get() =
      userViewModel.work.value!!.isNotEmpty()
          && studyViewModel.work.value!!.isNotEmpty()

  private val studyViewModel: StudyViewModel by activityViewModels()
  override val userViewModel: UserViewModel by activityViewModels()

  val list = listOf(Work.LOAD_USER, Work.LOAD_PERFORMED_EXERCISE, Work.UPDATE_USER)
  override val workObserver = Observer<List<Work>> {
    val isLoad =
      when {
        it.isEmpty() -> false
        else -> it.map { item -> if (list.contains(item)) 1 else 0 }.reduce { a, b -> a + b } > 0
      }
    binding.swipeRefreshLayout.isRefreshing = isLoad
  }

  private val userObserver = Observer<User?> {
    studyViewModel.setProfile(it)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = StudyProfileFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@StudyProfileFragment
      viewModel = this@StudyProfileFragment.studyViewModel
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    imageChanger = ImageChanger(this)
    if (userViewModel.user.value == null) userViewModel.loadCurrentUser()

    val color = requireContext().getColor(R.color.loader_color)
    binding.swipeRefreshLayout.setColorSchemeColors(color)
    binding.swipeRefreshLayout.setOnRefreshListener { userViewModel.loadCurrentUser() }
  }

  override fun onResume() {
    super.onResume()
    studyViewModel.work.observe(viewLifecycleOwner, workObserver)
    userViewModel.work.observe(viewLifecycleOwner, workObserver)
    userViewModel.user.observe(viewLifecycleOwner, userObserver)
  }

  override fun onStop() {
    super.onStop()
    studyViewModel.work.removeObserver(workObserver)
    userViewModel.work.removeObserver(workObserver)
    userViewModel.user.removeObserver(userObserver)
  }

  // region Image Changer
  private lateinit var imageChanger: ImageChanger
  private val user: User? get() = userViewModel.user.value

  fun changeProfileImage() {
    if (user == null || hasLoad) return

    imageChanger.change(CropOptions.rectCropImageOptions) { uri ->
      userViewModel.updateProfileImage(uri)
    }
  }
  // endregion
}