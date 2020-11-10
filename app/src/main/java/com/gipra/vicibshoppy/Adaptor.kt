package com.gipra.vicibshoppy

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.gipra.vicibshoppy.utlis.showToast
import com.gipra.vicibshoppy.models.catSupplier
import kotlinx.android.synthetic.main.shoppy_search_list.view.*

data class Adaptor(var categoryList: catSupplier, var activity: Activity) : BaseAdapter() {

    override fun getItem(position: Int): Any {
        return categoryList.category.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return categoryList.category.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(activity,R.layout.shoppy_search_list,null)

        val country: TextView = view.textView
        val copyButton: ImageButton = view.button

        country.text = categoryList.category.get(position).toString()

        copyButton.setOnClickListener {
            activity.showToast("Clicked")
        }

        return view
    }

//    override fun getFilter(): Filter {
//
//        return object : Filter() {
//            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
//                mPois = filterResults.values as List<PoiDao>
//                notifyDataSetChanged()
//            }
//
//            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
//                val queryString = charSequence?.toString()?.toLowerCase()
//
//                val filterResults = Filter.FilterResults()
//                filterResults.values = if (queryString==null || queryString.isEmpty())
//                    allPois
//                else
//                    allPois.filter {
//                        it.name.toLowerCase().contains(queryString) ||
//                                it.city.toLowerCase().contains(queryString) ||
//                                it.category_name.toLowerCase().contains(queryString)
//                    }
//                return filterResults
//            }
//        }
//    }

}
//    inner class SearchAdaptor(context: Context, @LayoutRes private val layoutResource: Int,
//                              private val allPois: List<String>):
//        ArrayAdapter<String>(context, layoutResource, allPois),
//        Filterable {
//        private var mPois: List<String> = allPois
//
//        override fun getCount(): Int {
//            return mPois.size
//        }
//
//        override fun getItem(p0: Int): String? {
//            return mPois.get(p0)
//
//        }
//        override fun getItemId(p0: Int): Long {
//            // Or just return p0
//            return mPois.get(p0).length.toLong()
//        }
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//            val view: ConstraintLayout = convertView as ConstraintLayout? ?:
//            LayoutInflater.from(context).inflate(R.layout.shoppy_search_list,
//                parent, false) as ConstraintLayout
//
//            view.textView.text = "${mPois.get(position)}"
//
//
//            view.textView.setOnClickListener {
//
//                var value = "${mPois.get(position)}"
//
//
//            }
//            return view
//        }
//
//
//
//        override fun getFilter(): Filter {
//            return object : Filter() {
//                override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
//                    mPois = filterResults.values as List<String>
//                    notifyDataSetChanged()
//                }
//
//                override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
//                    val queryString = charSequence?.toString()?.toLowerCase()
//
//                    val filterResults = Filter.FilterResults()
//                    filterResults.values = if (queryString==null || queryString.isEmpty())
//                        allPois
//                    else
//                        allPois.filter {
//                            it.toLowerCase().contains(queryString)
//                        }
//
//                    return filterResults
//                }
//
//            }
//        }
//    }


//        val adapter = SearchAdaptor(this, R.layout.shoppy_search_list, countries)
//        autoCompleteText.setAdapter(adapter)
//
//        autoCompleteText.setOnItemClickListener() { parent, _, position, id ->
//            val selectedPoi = parent.adapter.getItem(position)
//            autoCompleteText.setText(selectedPoi?.toString())
//
//
//        }



