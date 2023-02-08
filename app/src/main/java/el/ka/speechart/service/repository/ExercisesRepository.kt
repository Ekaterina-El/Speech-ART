package el.ka.speechart.service.repository

import android.accounts.NetworkErrorException
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

}