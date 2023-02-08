package el.ka.speechart.other

import el.ka.speechart.R

enum class LevelOfDifficulty(val strRes: Int, val exp: Int) {
  EASY(R.string.easy, 10), MEDIUM(R.string.medium, 30), ADVANCED(R.string.advanced, 80)
}