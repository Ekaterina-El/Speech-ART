package el.ka.speechart.view.ui.core.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.navigation.NavigationBarView
import el.ka.speechart.R
import el.ka.speechart.databinding.SpecialistMainFragmentBinding
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.SpecialistViewModel
import el.ka.speechart.viewModel.UserViewModel

class SpecialistMainFragment : UserBaseFragment() {
  private lateinit var binding: SpecialistMainFragmentBinding

  private val specialistViewModel by activityViewModels<SpecialistViewModel>()
  override val userViewModel by activityViewModels<UserViewModel>()

  private val profileFragment: SpecialistProfileFragment by lazy { SpecialistProfileFragment() }
  private val listOfExerciseFragment: SpecialistListOfExercisesFragment by lazy {
    SpecialistListOfExercisesFragment(
      onItemSelected = { navigateTo(exerciseFragment) },
      openAddExerciseFragment = {
        navigateTo(addExerciseFragment)
      })
  }

  private val addExerciseFragment: AddExerciseFragment by lazy {
    AddExerciseFragment(onGoBack = {
      navigateTo(listOfExerciseFragment)
    })
  }
  private val listOfRequestsFragment: SpecialistRequestsToCheckFragment by lazy {
    SpecialistRequestsToCheckFragment {
      navigateTo(performedExerciseFragment)
    }
  }

  private val performedExerciseFragment: SpecialistPerformedExerciseFragment by lazy {
    SpecialistPerformedExerciseFragment {
      navigateTo(listOfRequestsFragment)
    }
  }

  private val exerciseFragment: SpecialistExerciseFragment by lazy {
    SpecialistExerciseFragment { navigateTo(listOfExerciseFragment) }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SpecialistMainFragmentBinding.inflate(layoutInflater)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    fm.beginTransaction()
      .add(
        R.id.fragmentContainerViewSpecialist,
        performedExerciseFragment,
        "performedExerciseFragment"
      )
      .hide(performedExerciseFragment).commit()
    fm.beginTransaction()
      .add(R.id.fragmentContainerViewSpecialist, listOfRequestsFragment, "listOfRequests")
      .hide(listOfRequestsFragment).commit()
    fm.beginTransaction()
      .add(R.id.fragmentContainerViewSpecialist, listOfExerciseFragment, "listOfExercise")
      .hide(listOfExerciseFragment).commit()
    fm.beginTransaction()
      .add(R.id.fragmentContainerViewSpecialist, exerciseFragment, "exerciseFragment")
      .hide(exerciseFragment).commit()
    fm.beginTransaction()
      .add(R.id.fragmentContainerViewSpecialist, addExerciseFragment, "addExerciseFragment")
      .hide(addExerciseFragment).commit()

    fm.beginTransaction().add(R.id.fragmentContainerViewSpecialist, profileFragment, "profile")
      .commit()
    active = profileFragment

    binding.bottomNavigationView.setOnItemSelectedListener(mOnNavigationSelectedListener)
  }

  private
  val mOnNavigationSelectedListener = NavigationBarView.OnItemSelectedListener {
    val newActive = when (it.itemId) {
      R.id.profile -> profileFragment
      R.id.listOfExercises -> listOfExerciseFragment
      R.id.listOfRequests -> listOfRequestsFragment
      else -> null
    }
    return@OnItemSelectedListener if (newActive != null) {
      navigateTo(newActive)
      true
    } else false
  }

  override fun onResume() {
    super.onResume()
    userViewModel.error.observe(viewLifecycleOwner, errorObserver)
    specialistViewModel.error.observe(viewLifecycleOwner, errorObserver)
  }

  override fun onStop() {
    super.onStop()
    userViewModel.error.removeObserver(errorObserver)
    specialistViewModel.error.removeObserver(errorObserver)
  }
}