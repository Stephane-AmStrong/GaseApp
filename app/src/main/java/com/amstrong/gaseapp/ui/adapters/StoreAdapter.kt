package com.amstrong.gaseapp.ui.adapters

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.amstrong.gaseapp.data.db.entities.Store
import kotlinx.android.synthetic.main.item_auto_complete_text_view.view.*
import java.util.*


class StoreAdapter(private val c: Context, @LayoutRes private val layoutResource: Int, private val suppliers: List<Store>) :
        ArrayAdapter<Store>(c, layoutResource, suppliers) {

    var filteredStores: List<Store> = listOf()

    override fun getCount(): Int = filteredStores.size

    override fun getItem(position: Int): Store = filteredStores[position]


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(c).inflate(layoutResource, parent, false)

        view.txt_name.text = filteredStores[position].name
        view.txt_contact.text = filteredStores[position].phone_number

        return view
    }



    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                try {
                    @Suppress("UNCHECKED_CAST")
                    filteredStores = filterResults.values as List<Store>
                }catch (ex: Exception){
                }
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase(Locale.ROOT)

                val filterResults = FilterResults()
                filterResults.values = if (queryString == null || queryString.isEmpty())
                    suppliers
                else
                    suppliers.filter {
                        it.name.toLowerCase(Locale.ROOT).contains(queryString)
                    }
                return filterResults
            }
        }
    }
}