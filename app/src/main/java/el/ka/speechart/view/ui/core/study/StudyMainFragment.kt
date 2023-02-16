package el.ka.speechart.view.ui.core.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.navigation.NavigationBarView
import el.ka.speechart.R
import el.ka.speechart.databinding.StudyMainFragmentBinding
import el.ka.speechart.view.ui.BaseFragment

class StudyMainFragment : BaseFragment() {
  private lateinit var binding: StudyMainFragmentBinding

  private val listOfExercisesFragment by lazy {
    StudyListOfExercisesFragment { navigateTo(exerciseFragment) }
  }

  private val exerciseFragment: StudyExerciseFragment by lazy {
    StudyExerciseFragment { navigateTo(listOfExercisesFragment) }
  }

  private val performedExerciseFragment: StudyPerformedExerciseFragment by lazy {
    StudyPerformedExerciseFragment { navigateTo(profile) }
  }

  private val profile by lazy {
    StudyProfileFragment {
      navigateTo(performedExerciseFragment)
    }
  }

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

    fm.beginTransaction().add(R.id.study_container, listOfExercisesFragment, "listOfExercises")
      .hide(listOfExercisesFragment).commit()
    fm.beginTransaction().add(R.id.study_container, exerciseFragment, "exercise")
      .hide(exerciseFragment).commit()
    fm.beginTransaction().add(R.id.study_container, performedExerciseFragment, "performedExercises")
      .hide(performedExerciseFragment).commit()
    fm.beginTransaction().add(R.id.study_container, profile, "profile").commit()
    active = profile
  }

  private val mOnNavigationSelectedListener = NavigationBarView.OnItemSelectedListener {
    val newActive = when (it.itemId) {
      R.id.profile -> profile
      R.id.listOfExercises -> listOfExercisesFragment
      else -> null
    }
    return@OnItemSelectedListener if (newActive != null) {
      navigateTo(newActive)
      true
    } else false
  }
}