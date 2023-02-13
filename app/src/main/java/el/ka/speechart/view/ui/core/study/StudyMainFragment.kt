package el.ka.speechart.view.ui.core.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.navigation.NavigationBarView
import el.ka.speechart.R
import el.ka.speechart.databinding.StudyMainFragmentBinding
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.UserViewModel

class StudyMainFragment : BaseFragment() {
  private lateinit var binding: StudyMainFragmentBinding
  private val userViewModel by activityViewModels<UserViewModel>()

  private val listOfExercises by lazy { StudyListOfExercisesFragment() }
  private val profile by lazy { StudyProfileFragment() }

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
    binding.bottomNavigationView.setOnItemSelectedListener(mOnNavigationSelectedListener)

    fm.beginTransaction().add(R.id.study_container, listOfExercises, "listOfExercises").hide(listOfExercises).commit();
    fm.beginTransaction().add(R.id.study_container, profile, "profile").commit()
    active = profile
  }

  private val mOnNavigationSelectedListener = NavigationBarView.OnItemSelectedListener {
    val newActive = when (it.itemId) {
      R.id.profile -> profile
      R.id.listOfExercises -> listOfExercises
      else -> null
    }
    return@OnItemSelectedListener if (newActive != null) {
      navigateTo(newActive)
      true
    } else false
  }
}