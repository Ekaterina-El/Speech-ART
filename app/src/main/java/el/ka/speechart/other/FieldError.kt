package el.ka.speechart.other

import el.ka.speechart.R

data class FieldError(val field: Field, var errorType: FieldErrorType?)

enum class FieldErrorType(val messageRes: Int) {
  SHORT(R.string.value_short),
  IS_REQUIRE(R.string.is_require),
  PASSWORD_NO_EQUAL_REPEAT(R.string.password_repeat_no_equal),
  IS_NOT_EMAIL(R.string.is_no_email)

}

enum class Field {
  NAME, EMAIL, PASSWORD, PASSWORD_REPEAT, CONCLUSION,
}

