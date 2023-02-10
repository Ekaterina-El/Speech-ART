package el.ka.speechart.service.repository

import android.accounts.NetworkErrorException
import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.rpc.context.AttributeContext.Auth
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
import el.ka.speechart.service.model.Exercise
import kotlinx.coroutines.tasks.await

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
  } catch (e: FirebaseNetworkException)  {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

}