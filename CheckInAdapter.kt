package com.example.diplomat.wajure

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class CheckInAdapter(context: Context, checkInList: MutableList<CheckIn>) : BaseAdapter(){
    var checkList = checkInList
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val checkInID: String = checkList.get(position).checkInID as String
        val checkInName: String = checkList.get(position).checkInName as String
        val wajureID: String = checkList.get(position).wajureID as String
        val checkInDate: String = checkList.get(position).checkInDate as String
        val view: View? = null
        if (convertView == null) {
        } else {

        }

        return view!!
    }

    override fun getItemId(position: Int): Long {
        return  checkList.get(position).checkInID!!.toLong()
    }

    override fun getCount(): Int {
        return checkList.size
    }

    override fun getItem(position: Int): Any {
        return checkList.get(position)
    }
}
