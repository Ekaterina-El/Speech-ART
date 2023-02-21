package el.ka.speechart.view.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.media.audiofx.Visualizer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import el.ka.speechart.R

@SuppressLint("ResourceAsColor")
class WaveFormView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
  private var amplitudes = mutableListOf<Int>()
  private val spikes = ArrayList<RectF>()

  private var secondColor = Color.rgb(244, 81, 30)

  private val radius = 6f
  private val defaultSpikeW = 9f
  private var spikesDelta = 6f

  private var sw = 0f
  private var sh = 0f
  private var middleY = 0

  init {
    val typedStyle = context!!.obtainStyledAttributes(attrs, R.styleable.WaveFormView)
    secondColor = typedStyle.getColor(R.styleable.WaveFormView_waveColor, R.color.primary_color)
    typedStyle.recycle()

      sw = resources.displayMetrics.widthPixels.toFloat()
//    sh = resources.displayMetrics.heightPixels.toFloat()
  }

  private val paint by lazy {
    val paint = Paint()
    paint.color = secondColor
    return@lazy paint
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    sh = heightMeasureSpec.toFloat()
  }

  fun showAmplitude(list: List<Int>) {
    amplitudes = list.toMutableList()
    spikes.clear()
    val spikesDelta = 0f
    val spikesW = (sw - amplitudes.size * spikesDelta) / amplitudes.size
    middleY = layoutParams.height / 2
    createAmplitude(amplitudes, spikesW, spikesDelta)

    invalidate()
  }

  private fun createAmplitude(amplitudes: List<Int>, spikesW: Float, spikesDelta: Float) {
    spikes.clear()
    for (i in amplitudes.indices) {
      val ampHalf = amplitudes[i] / 2

      val left = i * (spikesW + spikesDelta)
      val top = middleY - ampHalf
      val right = left + spikesW
      val bottom = middleY + ampHalf
      spikes.add(RectF(left, top.toFloat(), right, bottom.toFloat()))
    }
  }

  fun addAmplitude(amp: Int) {
    if (amp == 0) return

//    max
    amplitudes.add(Math.min(amp / 7, height))

    val maxSpikes = (width / (defaultSpikeW + spikesDelta)).toInt()
    createAmplitude(amplitudes.takeLast(maxSpikes), defaultSpikeW, spikesDelta)

    invalidate()
  }

  override fun draw(canvas: Canvas?) {
    super.draw(canvas)

    if (middleY == 0) middleY = height / 2

    for (spike in spikes) {
      canvas?.drawRoundRect(spike, radius, radius, paint)
    }
  }

  fun release() {
    amplitudes.clear()
    spikes.clear()

    invalidate()
  }

  fun getWave(): List<Int> = amplitudes

  fun prepare() {
    release()
  }
}