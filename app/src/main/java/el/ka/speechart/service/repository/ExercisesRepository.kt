package el.ka.speechart.service.repository

import android.accounts.NetworkErrorException
import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FieldValue
import el.ka.speechart.other.Constants.FIELD_PERFORMED_EXERCISES
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.service.model.PerformedExercise
import kotlinx.coroutines.tasks.await
import java.io.File

object ExercisesRepository {
  suspend fun loadExercises(onSuccess: (List<Exercise>) -> Unit): ErrorApp? = try {
    val items = FirebaseService.exercisesRepository.get().await().mapNotNull {
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
    val doc = FirebaseService.exercisesRepository.add(exercise).await()
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
    val doc = FirebaseService.performedExercisesRepository.add(performedExercise).await()

    // add note id to user node to user collection
    FirebaseService.usersCollection.document(performedExercise.user)
      .update(FIELD_PERFORMED_EXERCISES, FieldValue.arrayUnion(doc.id)).await()

    null
  } catch (e: NetworkErrorException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }
}