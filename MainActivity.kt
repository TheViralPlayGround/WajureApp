package com.example.diplomat.wajure

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(), WajureRowListener {

    lateinit var mDatabase: DatabaseReference
    var wajureItemList: MutableList<WajureItem>? = null
    var checkInItemList: MutableList<CheckIn>? = null
    lateinit var dateTextView: TextView
    lateinit var adapter: WajureItemAdapter
    lateinit var checkInAdapter: CheckInAdapter
    lateinit var circle: ImageView
    private var listViewItems: ListView? = null
    var currentDate = LocalDateTime.now()
    var formatter = DateTimeFormatter.ofPattern("MMddyyyy")
    var formatterDate = DateTimeFormatter.ofPattern("MM/dd/yy")
    var date = currentDate.format(formatter)
    lateinit var topHeader: LinearLayout
    lateinit var totalCompleteTodayView: TextView
    lateinit var totalWajures: TextView
     lateinit var fa: Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        totalCompleteTodayView = findViewById(R.id.completeToday)
        fa = this
        totalWajures = findViewById(R.id.totalWajureComplete)
        dateTextView = this.findViewById(R.id.todayDate)
        dateTextView.text = currentDate.format(formatterDate)
        topHeader = findViewById(R.id.topHeader)
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        listViewItems = findViewById<View>(R.id.wajures_list) as ListView
        circle = findViewById(R.id.circleProgress)
        supportActionBar!!.setIcon(R.drawable.wajurelogofinal)
        mDatabase = FirebaseDatabase.getInstance().reference

        wajureItemList = mutableListOf()
        checkInItemList = mutableListOf()
        adapter = WajureItemAdapter(this, wajureItemList!!)
        checkInAdapter = CheckInAdapter(this, checkInItemList!!)
        listViewItems!!.adapter = adapter

        mDatabase.addValueEventListener(itemListener)

        topHeader.setOnClickListener{_->
                    val i = Intent(applicationContext, ReportActivity::class.java)

                    startActivity(i)
            setContentView(R.layout.report_activity)


        }

        fab.setOnClickListener { _ ->
            addNewWajureDialog()
        }

        listViewItems!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val wajureRef = adapter.itemList.get(position)
            updateWajure(wajureRef)
        }

        listViewItems!!.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            val wajureRef = adapter.itemList.get(position)
            deleteWajureDialog(wajureRef.wajureID)
            true
        }
    }

    var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val wajureNode = dataSnapshot.child("wajure_item")
            val wajureIterator = wajureNode.children.iterator()
            val totalComplete = totalCompleteToday(dataSnapshot)
            totalCompleteTodayView.setText(totalComplete)
            val total =  totalWajures(dataSnapshot)
            totalWajureComplete.text = total

            if (wajureIterator.hasNext()) {
                addDataToList(dataSnapshot)
                createCheckInList(dataSnapshot)
                resetWajureTotal(dataSnapshot)

            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        wajureItemList!!.clear()

        //Check if current database contains any collection
        if (items.hasNext()) {
            val wajureIndex = items.next()
            val itemsIterator = wajureIndex.children.iterator()
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()
                val newWajure = WajureItem.create()
                val map = currentItem.value as HashMap<*, *>

                if (map.containsKey("wajureName")) {
                    newWajure.wajureID = currentItem.key
                    newWajure.wajureName = map.get("wajureName") as String?
                    newWajure.wajureTotal = map.get("wajureTotal").toString().toInt()
                    newWajure.wajureDayComplete = map.get("wajureDayComplete") as Boolean?
                    newWajure.checkIn = map.get("checkIn") as String?
                    wajureItemList!!.add(newWajure)
                }
            }
        }
        wajureItemList!!.sortBy { it.wajureName }
        adapter.notifyDataSetChanged()
    }

    private fun addNewWajureDialog() {
        val alert = AlertDialog.Builder(this)
        val inputWajureField = EditText(this)
        alert.setMessage("Add New Wajure")
        alert.setTitle("Wajure")
        alert.setView(inputWajureField)

        alert.setPositiveButton("Submit") { dialog, _ ->
            val newWajure = WajureItem.create()
            newWajure.wajureName = inputWajureField.text.toString()
            newWajure.wajureCreationDate = date
            newWajure.wajureTotal = 0
            newWajure.wajureDayComplete = false
            newWajure.checkIn = "0"
            val wajureNode = mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).push()
            newWajure.wajureID = wajureNode.key
            wajureNode.setValue(newWajure)
            dialog.dismiss()
            Toast.makeText(this, newWajure.wajureName + " saved!", Toast.LENGTH_SHORT).show()
        }
        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alert.show()
    }

    private fun deleteWajureDialog(wajureID: String?) {
        val alert = AlertDialog.Builder(this)
        alert.setMessage("Delete Wajure and its history?")
        alert.setTitle("Delete")
        alert.setPositiveButton("Delete") { dialog, _ ->
            val wajureNode = mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(wajureID.toString())
            wajureNode.removeValue().addOnSuccessListener { }
            dialog.dismiss()
            Toast.makeText(this, "Wajure Deleted", Toast.LENGTH_SHORT).show()
        }
        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alert.show()
        adapter.notifyDataSetChanged()
    }

    private fun updateWajure(wajure: WajureItem) {
        val wajureID = wajure.wajureID
        val wajureName = wajure.wajureName
        val checkInItem = CheckIn.create()

        if (wajure.wajureDayComplete == false) {
            checkInItem.wajureID = wajureID
            checkInItem.checkInDate = date
            checkInItem.checkInName = wajureName
            val checkInNode = mDatabase.child(Constants.FIREBASE_CHECKIN_ITEM).child(date).push()
            checkInItem.checkInID = checkInNode.key
            checkInNode.setValue(checkInItem)
            checkInItemList!!.add(checkInItem)
            val newTotal = (wajure.wajureTotal!!.toInt() + 1).toString()
            mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(wajure.wajureID!!).child("checkIn").setValue(checkInItem.checkInID)
            mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(wajure.wajureID!!).child("wajureDayComplete").setValue(true)
            mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(wajure.wajureID!!).child("wajureTotal").setValue(newTotal)

        } else if (wajure.wajureDayComplete == true) {
            val newTotal = (wajure.wajureTotal!!.toInt() - 1).toString()

            mDatabase.child(Constants.FIREBASE_CHECKIN_ITEM).child(date).child(wajure.checkIn!!.toString()).removeValue()
            mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(wajure.wajureID!!).child("wajureDayComplete").setValue(false)
            mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(wajure.wajureID!!).child("wajureTotal").setValue(newTotal)
        }

    }

    fun createCheckInList(dataSnapshot: DataSnapshot) {
        val ref = dataSnapshot.child(Constants.FIREBASE_CHECKIN_ITEM).children.iterator()
        if (checkInItemList != null) {
            checkInItemList!!.clear()
        }
        //Check if current database contains any collectionc
        if (ref.hasNext()) {
            val checkInIndex = ref.next()
            val itemsIterator = checkInIndex.children.iterator()
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()
                val newCheckIn = CheckIn.create()
                val map = currentItem.value as HashMap<*, *>
                if (map.containsKey(date)) {
                    newCheckIn.checkInID = currentItem.key
                    newCheckIn.checkInName = map.get("checkInName") as String?
                    newCheckIn.wajureID = map.get("wajureID") as String?
                    newCheckIn.checkInDate = map.get("checkInDate") as String?
                    checkInItemList!!.add(newCheckIn)
                }
            }
        }

    }


    override fun modifyItemState(itemObjectId: String) {

    }

    //    //delete an item
    override fun onItemDelete(itemObjectId: String) {
        //get child reference in database via the ObjectID
        val itemReference = mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(itemObjectId)
        //deletion can be done via removeValue() method
        itemReference.removeValue()
    }

    fun circularImageBar(iv2: ImageView, i: Int) {

        val b = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(b)
        val paint = Paint()

        paint.color = Color.parseColor("#c4c4c4")
        paint.strokeWidth = 10F
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(150F, 150F, 140F, paint)
        paint.color = Color.parseColor("#FFDB4C")
        paint.strokeWidth = 10F
        paint.style = Paint.Style.FILL
        val oval = RectF()
        paint.style = Paint.Style.STROKE
        oval.set(10.0F, 10F, 290F, 290F)
        canvas.drawArc(oval, 270F, ((i * 360F) / 100F), false, paint)
        paint.strokeWidth = 0F
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.parseColor("#8E8E93")
        paint.textSize = 140F
//        canvas.drawText(""+i, 150F, 150+(paint.textSize /3), paint)
        iv2.setImageBitmap(b)
    }

    override fun onResume() {
        super.onResume()
        val lastDate = date
        date = currentDate.format(formatter)
        if (lastDate != date) {
            dateTextView.text = currentDate.format(formatterDate)
        }

}

    private fun resetWajureTotal(datasnapshot: DataSnapshot){
        val lastDate = date
        date = currentDate.format(formatter)
        if (lastDate != date) {
        val ref = datasnapshot.child(Constants.FIREBASE_WAJURE_ITEM).children.iterator()
        //Check if current database contains any collectionc
        if (ref.hasNext()) {
            val checkInIndex = ref.next()
            val itemsIterator = checkInIndex.children.iterator()
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()
                val map = currentItem.value as HashMap<*, *>
                mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(map.get("wajureID") as String).child("wajureDayComplete").setValue(false)
            }}}
    }

    private fun totalCompleteToday(dataSnapshot: DataSnapshot): String{
        return dataSnapshot.child(Constants.FIREBASE_CHECKIN_ITEM).child(date).childrenCount.toString()

    }

    private fun totalWajures(dataSnapshot: DataSnapshot): String {
        var newTotal = 0

        val items = dataSnapshot.children.iterator()
        //Check if current database contains any collection
        if (items.hasNext()) {
            val wajureIndex = items.next()
            val itemsIterator = wajureIndex.children.iterator()
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()
                val map = currentItem.value as HashMap<*, *>

                if (map.containsKey("wajureName")) {
                    val thus = map.get("wajureTotal").toString() as String?
                    newTotal = newTotal + thus!!.toInt()

                }

            }
        }
        return newTotal.toString()

    }

    private fun topDayTotal(dataSnapshot: DataSnapshot){

    }



    //If yesterdays total is greater than the total that we have registered as the top total, then make yesterdays child count be
    // the top day total
}