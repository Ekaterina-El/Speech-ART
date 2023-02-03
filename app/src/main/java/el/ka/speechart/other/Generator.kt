package el.ka.speechart.other

import java.security.SecureRandom
import kotlin.random.Random
import kotlin.random.nextInt

object Generator {
  const val SYMBOLS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#=+!£\$%&?"
  const val MIN_PASSWORD_LENGTH = 10
  const val MAX_PASSWORD_LENGTH = 20

  fun genPassword(): String {
    val length = Random.nextInt(MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH)

    val sb = StringBuilder(length)
    val rnd = SecureRandom.getInstance("SHA1PRNG")

    repeat(length) {
      val idx = rnd.nextInt(SYMBOLS.length)
      sb.append(SYMBOLS[idx])
    }

    return sb.toString()
  }
}