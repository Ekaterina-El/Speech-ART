package el.ka.speechart.other

import el.ka.speechart.R

enum class UserRole {
  OWNER, ADMIN, SPECIALIST, STUDY
}

val UserRole.actionFromSplash get() = when (this) {
  UserRole.OWNER -> R.id.action_splashFragment_to_ownerMainFragment
  UserRole.ADMIN -> R.id.action_splashFragment_to_adminMainFragment
  UserRole.SPECIALIST -> R.id.action_splashFragment_to_specialistMainFragment
  UserRole.STUDY -> R.id.action_splashFragment_to_studyMainFragment
}

val UserRole.actionFromLogin get() = when (this) {
  UserRole.OWNER -> R.id.action_loginFragment_to_ownerMainFragment
  UserRole.ADMIN -> R.id.action_loginFragment_to_adminMainFragment
  UserRole.SPECIALIST -> R.id.action_loginFragment_to_specialistMainFragment
  UserRole.STUDY -> R.id.action_loginFragment_to_studyMainFragment
}