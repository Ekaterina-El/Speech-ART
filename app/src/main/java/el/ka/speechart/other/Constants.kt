package el.ka.speechart.other

object Constants {
  const val LOAD_DELAY = 2000L
  const val pickUpAudioType = "audio/*"

  // region DB Fields
  const val FIELD_DESCRIPTION = "description"
  const val FIELD_PROFILE_URL = "profileUrl"
  const val FIELD_PERFORMED_EXERCISES = "performedExercises"
  const val FIELD_EMAIL = "email"
  const val FIELD_PASSWORD = "password"
  const val FIELD_ROLE = "role"
  const val FIELD_SCORE = "score"
  const val FIELD_REVIEWS = "reviews"
  const val FIELD_RATING = "rating"

  const val FIELD_SPECIALIST_ANSWER = "specialistAnswer"
  const val FIELD_SPECIALIST_ID = "specialistId"
  const val FIELD_REVIEW_ID = "reviewId"
  // endregion

  // region DB Collections
  const val REVIEW_COLLECTION = "reviews"
  const val USERS_COLLECTION = "users"
  const val REQUESTS_TO_REGISTRATION_SPECIALIST_COLLECTION = "requests_to_reg_specialist"
  const val EXERCISES_COLLECTION = "exercises"
  const val PERFORMED_EXERCISES_COLLECTION = "performed_exercises"
  // endregion

  // region Shared Preferences
  const val SEPARATOR = ":"
  const val CREDENTIALS = "credentials"
  const val SP_NAME = "SpeechApp"
  // endregion
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
* Agree/disagree request for create specialists +
* View list of request to create specialist +
* Remove specialists +
* View list of specialist +
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
* * Sections: exercise, self account (photo, fullName, level, preformed exercise (checked or unchecked))
*/