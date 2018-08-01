package com.example.diplomat.wajure
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class WajureItemAdapter(context: Context, wajureItemList: MutableList<WajureItem>) : BaseAdapter() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    var itemList = wajureItemList

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val wajureID: String = itemList.get(position).wajureID as String
        val wajureName: String = itemList.get(position).wajureName as String
        val wajureTotal: Int = itemList.get(position).wajureTotal as Int

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
        vh.avatar.text = wajureName.substring(0,1)
        vh.total.text = wajureTotal.toString()
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
    private class ListRowHolder(row: View?) {
        val name: TextView = row!!.findViewById<TextView>(R.id.wajureName) as TextView
        val avatar: TextView = row!!.findViewById<TextView>(R.id.wajure_title_letter) as TextView
        val total: TextView = row!!.findViewById(R.id.total_count) as TextView
    }
}