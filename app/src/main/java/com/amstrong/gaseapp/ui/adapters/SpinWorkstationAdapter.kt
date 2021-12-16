package com.amstrong.gaseapp.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.amstrong.gaseapp.data.db.entities.Workstation


class SpinWorkstationAdapter(context: Context, textViewResourceId: Int,
                             values: List<Workstation>) : ArrayAdapter<Workstation>(context, textViewResourceId, values) {

    override fun getContext(): Context {
        return super.getContext()
    }

    private val context: Context

    // Your custom values for the spinner (Workstation)
    private val values: List<Workstation>
    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): Workstation {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent!!) as TextView
        label.setTextColor(Color.BLACK)

        // Then you can get the current item using the values array (Workstations array) and the current position
        // You can NOW reference each method you has created in your bean object (Workstation class)
        label.setText(values[position].name)

        // And finally return your dynamic (or custom) view for each spinner item
        return label
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    override fun getDropDownView(position: Int, convertView: View?,
                                 parent: ViewGroup?): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.setText(values[position].name)
        return label
    }

    init {
        this.context = context
        this.values = values
    }
}