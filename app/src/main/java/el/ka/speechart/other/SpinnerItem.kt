package el.ka.speechart.other

data class SpinnerItem(
  val key: String,
  val value: Any
) {
  override fun toString(): String = key
}