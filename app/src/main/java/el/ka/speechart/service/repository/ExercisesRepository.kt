package el.ka.speechart.service.repository

import android.accounts.NetworkErrorException
import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FieldValue
import el.ka.speechart.other.Constants.FIELD_PERFORMED_EXERCISES
import el.ka.speechart.other.Constants.FIELD_SCORE
import el.ka.speechart.other.Constants.FIELD_SPECIALIST_ANSWER
import el.ka.speechart.other.Constants.FIELD_SPECIALIST_ID
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.service.model.PerformedExercise
import kotlinx.coroutines.tasks.await
import java.io.File

object ExercisesRepository {
  suspend fun loadExercises(onSuccess: (List<Exercise>) -> Unit): ErrorApp? = try {
    val items = FirebaseService.exercisesCollection.get().await().mapNotNull {
      val exercise = it.toObject(Exercise::class.java)
      exercise.id = it.id
      return@mapNotNull exercise
    }
    onSuccess(items)
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun addExercise(exercise: Exercise, onSuccess: (Exercise) -> Unit): ErrorApp? = try {
    exercise.createdByASpecialist = AuthRepository.currentUid!!

    // Load media file to store
    val uri = Uri.parse(exercise.referencePronunciationFile!!.url)
    val fileUrl = FirebaseService.uploadToStorage(uri, "exercises/referencePronunciationFileUrls/")

    exercise.referencePronunciationFile!!.url = fileUrl

    // Add note to database
    val doc = FirebaseService.exercisesCollection.add(exercise).await()
    exercise.id = doc.id

    onSuccess(exercise)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun removeUserAudioFile(url: String): ErrorApp? = try {
    FirebaseService.deleteByUrl(url)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun uploadUserAudioFile(url: String, onSuccess: (String) -> Unit): ErrorApp? = try {
    val uri = File(url).toUri()
    val newUrl = FirebaseService.uploadToStorage(uri, "exercises/usersAudioFiles/")
    onSuccess(newUrl)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun sendExercise(performedExercise: PerformedExercise): ErrorApp? = try {
    // add note performed exercise to collection
    val doc = FirebaseService.performedExercisesCollection.add(performedExercise).await()

    // add note id to user node to user collection
    FirebaseService.usersCollection.document(performedExercise.user)
      .update(FIELD_PERFORMED_EXERCISES, FieldValue.arrayUnion(doc.id)).await()

    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun getAllPerformedExercisesToCheck(
    onSuccess: (List<PerformedExercise>) -> Unit
  ): ErrorApp? = try {
    val list =
      FirebaseService.performedExercisesCollection.whereEqualTo(FIELD_SPECIALIST_ANSWER, null).get()
        .await().map {
        val performedExercise = it.toObject(PerformedExercise::class.java)
        performedExercise.id = it.id
        performedExercise.userLocal = UsersRepository.getUserById(performedExercise.user)
        if (performedExercise.specialistId != null) performedExercise.specialistLocal =
          UsersRepository.getUserById(performedExercise.specialistId!!)
        performedExercise.exerciseLocal = getExerciseById(performedExercise.exerciseId)
        return@map performedExercise
      }
    onSuccess(list)
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun getPerformedExercisesByListOfIds(
    listOfIds: List<String>,
    withDetails: Boolean = false,
    onSuccess: (List<PerformedExercise>) -> Unit,
  ): ErrorApp? = try {
    val list = listOfIds.map { id ->
      if (withDetails) loadPerformedExerciseByIdWithLocalData(id) else loadPerformedExerciseById(id)
    }
    onSuccess(list)
    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun loadPerformedExerciseByIdWithLocalData(id: String): PerformedExercise {
    val performedExercise = loadPerformedExerciseById(id)

    //    load user, specialist & exercise
    performedExercise.userLocal = UsersRepository.getUserById(performedExercise.user)
    if (performedExercise.specialistId != null) performedExercise.specialistLocal =
      UsersRepository.getUserById(performedExercise.specialistId!!)
    performedExercise.exerciseLocal = getExerciseById(performedExercise.exerciseId)

    if (performedExercise.reviewId != null) performedExercise.reviewLocal =
      ReviewRepository.getReviewById(
        performedExercise.reviewId!!,
        withUser = true,
        withSpecialist = true,
        withExercise = false
      )

    return performedExercise
  }

  private suspend fun getExerciseById(id: String): Exercise {
    val doc = FirebaseService.exercisesCollection.document(id).get().await()
    val exercise = doc.toObject(Exercise::class.java)!!
    exercise.id = doc.id
    return exercise
  }

  private suspend fun loadPerformedExerciseById(id: String): PerformedExercise {
    val doc = FirebaseService.performedExercisesCollection.document(id).get().await()
    val performedExercise = doc.toObject(PerformedExercise::class.java)!!
    performedExercise.id = id
    return performedExercise
  }

  suspend fun sendConclusion(
    performedExercise: PerformedExercise,
    conclusion: String,
    specialistId: String,
    onSuccess: () -> Unit
  ): ErrorApp? = try {

    val ref = FirebaseService.performedExercisesCollection.document(performedExercise.id)
    ref.update(FIELD_SPECIALIST_ANSWER, conclusion).await()
    ref.update(FIELD_SPECIALIST_ID, specialistId).await()

    // update user score
    val levelScore = performedExercise.exerciseLocal!!.levelOfDifficulty.exp
    val userScore = UsersRepository.getUserById(performedExercise.user)!!.score
    val newUserScore = levelScore + userScore
    FirebaseService.usersCollection.document(performedExercise.user)
      .update(FIELD_SCORE, newUserScore).await()

    onSuccess()
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }
}