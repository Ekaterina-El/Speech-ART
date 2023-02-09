package el.ka.speechart.view.adapter.list.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import el.ka.speechart.R
import el.ka.speechart.databinding.DropdownSpinnerItemBinding
import el.ka.speechart.databinding.SpinnerItemBinding
import el.ka.speechart.other.SpinnerItem

class SpinnerAdapter(context: Context, private val items: List<SpinnerItem>) :
  ArrayAdapter<SpinnerItem>(context, 0, items) {

  private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

  fun selectItem(
    currentValue: Any?,
    spinnerMeasurementType: Spinner
  ) {
    if (currentValue == null) return
    val selected = items.firstOrNull { it.value == currentValue }
    val p = getPosition(selected)
    spinnerMeasurementType.setSelection(p)
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val binding: SpinnerItemBinding = if (convertView == null) {
      DataBindingUtil.inflate(
        layoutInflater,
        R.layout.spinner_item,
        parent,
        false
      )
    } else {
      convertView.tag as SpinnerItemBinding
    }

    binding.item = getItem(position)
    binding.root.tag = binding
    return binding.root
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    val binding: DropdownSpinnerItemBinding = if (convertView == null) {
      DataBindingUtil.inflate(
        layoutInflater,
        R.layout.dropdown_spinner_item,
        parent,
        false
      )
    } else {
      convertView.tag as DropdownSpinnerItemBinding
    }

    binding.root.tag = binding
    getItem(position)?.let { binding.item = it }
    return binding.root
  }
}