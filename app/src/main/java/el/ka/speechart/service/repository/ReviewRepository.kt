package el.ka.speechart.service.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FieldValue
import el.ka.speechart.other.Constants.FIELD_RATING
import el.ka.speechart.other.Constants.FIELD_REVIEWS
import el.ka.speechart.other.Constants.FIELD_REVIEW_ID
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
import el.ka.speechart.service.model.PerformedExercise
import el.ka.speechart.service.model.Review
import kotlinx.coroutines.tasks.await

object ReviewRepository {
  suspend fun sendReview(
    performedExercise: PerformedExercise,
    reviewRating: Int,
    reviewText: String,
    onSuccess: (Review) -> Unit
  ): ErrorApp? = try {
    val review = Review(
      userId = AuthRepository.currentUid!!,
      specialistId = performedExercise.specialistId!!,
      performedExerciseId = performedExercise.id,
      rating = reviewRating,
      text = reviewText
    )

    // send review
    val reviewDoc = FirebaseService.reviewCollection.add(review).await()
    review.id = reviewDoc.id

    // add review id to performedExercise note
    FirebaseService.performedExercisesCollection.document(performedExercise.id)
      .update(FIELD_REVIEW_ID, review.id).await()

    // update specialist rating
    val specialist = UsersRepository.getUserById(performedExercise.specialistId!!)!!
    val countOfReviews = specialist.reviews.size
    val newRating =
      specialist.rating + reviewRating / (if (countOfReviews == 0) 1 else countOfReviews)
    specialist.rating = newRating

    val reviews = specialist.reviews.toMutableList()
    reviews.add(review.id)
    specialist.reviews = reviews

    FirebaseService.usersCollection.document(performedExercise.specialistId!!)
      .update(FIELD_RATING, newRating)

    // add review id to specialist note
    FirebaseService.usersCollection.document(performedExercise.specialistId!!)
      .update(FIELD_REVIEWS, FieldValue.arrayUnion(review.id))

    review.userLocal = UsersRepository.getUserById(performedExercise.user)
    review.specialistLocal = specialist

    onSuccess(review)

    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }
}