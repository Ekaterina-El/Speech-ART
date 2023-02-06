package el.ka.speechart.other

object Constants {
  const val FIELD_ROLE = "role"
  const val LOAD_DELAY = 2000L
  const val USERS_COLLECTION = "users"

  const val SEPARATOR = ":"
  const val CREDENTIALS = "credentials"
  const val SP_NAME = "SpeechApp"
}

// Owner
/*
* Add admin +
* Remove admin +
* View list of admins +
*
*  Sections: admins
*/

// Admin
/*
* Agree/disagree request for create specialists
* View list of request to create specialist
* Remove specialists
* View list of specialist
*
* *  Sections: specialist
*/

// Specialist
/*
* Add exercise
* Remove exercise
* View list of exercise
* View list of self reviews
* Adding a characteristic on the exercise performed
*
* Sections: exercise, self account (photo, fullName, description, reviews), characteristics
* */

// Study
/*
* View list of exercise
* Perform exercise and send to specialists
* Add reviews about specialist
*
* * Sections: exercise, self account (photo, fullName, preformed exercise (checked or unchecked), level)
*/