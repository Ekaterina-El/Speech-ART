package el.ka.speechart.other

object Validator {
  fun isEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
  }

  fun checkPasswordField(password: String, passwordRepeat: String): FieldError? {
    val errorType = when {
      password.isEmpty() -> FieldErrorType.IS_REQUIRE
      password.length < 8 -> FieldErrorType.SHORT
      password != passwordRepeat -> FieldErrorType.PASSWORD_NO_EQUAL_REPEAT
      else -> null
    }

    return if (errorType != null) FieldError(Field.PASSWORD, errorType) else null
  }

  fun checkUserNameField(name: String): FieldError? {
    val errorType = when {
      name.isEmpty() -> FieldErrorType.IS_REQUIRE
      else -> null
    }

    return if (errorType != null) FieldError(Field.NAME, errorType) else null
  }

  fun checkEmailField(email: String): FieldError? {
    val errorType = when {
      email.isEmpty() -> FieldErrorType.IS_REQUIRE
      !Validator.isEmail(email) -> FieldErrorType.IS_NOT_EMAIL
      else -> null
    }

    return if (errorType != null) FieldError(Field.EMAIL, errorType) else null
  }
}