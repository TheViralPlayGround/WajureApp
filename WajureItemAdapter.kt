package com.example.diplomat.wajure
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


class WajureItemAdapter(context: Context, wajureItemList: MutableList<WajureItem>) : BaseAdapter() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    var itemList = wajureItemList
    val context = context


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val wajureID: String = itemList.get(position).wajureID as String
        val wajureName: String = itemList.get(position).wajureName as String
        val wajureTotal: Int = itemList.get(position).wajureTotal as Int
        val wajureDayComplete: Boolean = itemList.get(position).wajureDayComplete as Boolean
        val checkIn: String = itemList.get(position).checkIn as String

        val view: View
        val vh: ListRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.wajure_item_view, parent, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }
        vh.name.text = wajureName
        if (wajureDayComplete == true){
            vh.dayTotal.setImageResource(R.drawable.checked_circle_primary)

        } else{ vh.dayTotal.setImageResource(R.drawable.unchecked_circle_primary)}
        return view
    }
    override fun getItem(index: Int): Any {
        return itemList.get(index)
    }
    override fun getItemId(index: Int): Long {
        return index.toLong()
    }
    override fun getCount(): Int {
        return itemList.size
    }

    fun updateDayBoolean(boolean: Boolean){
    }

    private class ListRowHolder(row: View?) {
        val name: TextView = row!!.findViewById<TextView>(R.id.wajureName) as TextView
        val dayTotal: ImageView = row!!.findViewById(R.id.wajure_status_checkmark) as ImageView
        val listView: LinearLayout = row!!.findViewById(R.id.wajureRow) as LinearLayout
    }
}