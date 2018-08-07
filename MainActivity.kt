package com.example.diplomat.wajure

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter




class MainActivity : AppCompatActivity(), WajureRowListener {

    lateinit var mDatabase: DatabaseReference
    var wajureItemList: MutableList<WajureItem>? = null
    lateinit var adapter: WajureItemAdapter
    private var listViewItems: ListView? = null
    var currentDate = LocalDateTime.now()
    var formatter = DateTimeFormatter.ofPattern("MMddyyyy")
    var date = currentDate.format(formatter)
    var prefs: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

//        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        if (prefs.getString("date","0") != dataDate){
//            prefs.edit().putString("date",date)
//            setDateToday = true
//
//        }



        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        listViewItems = findViewById<View>(R.id.wajures_list) as ListView

        mDatabase = FirebaseDatabase.getInstance().reference



        wajureItemList = mutableListOf<WajureItem>()
        adapter = WajureItemAdapter(this, wajureItemList!!)
        listViewItems!!.adapter = adapter

        mDatabase.addValueEventListener(itemListener)


        fab.setOnClickListener { view ->
            addNewWajureDialog()
        }


        listViewItems!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val itemRef = adapter.itemList.get(position)

//            mDatabase.child("wajure_item").child(itemRef.toString()).child("Total").setValue("Rowdy")

            updateWajureDialog(itemRef)
//            updateWajureTotal(itemRef)
        }


    }


    private fun addNewWajureDialog() {
        val alert = AlertDialog.Builder(this)
        val inputWajureField = EditText(this)

        alert.setMessage("Add New Wajure")
        alert.setTitle("Enter Wajure Name")
        alert.setView(inputWajureField)

        alert.setPositiveButton("Submit") { dialog, positiveButton ->

            val wajureItem = WajureItem.create()

            wajureItem.wajureName = inputWajureField.text.toString()
            wajureItem.wajureCreationDate = date
            wajureItem.wajureTotal = 0
            wajureItem.wajureDayTotal = 0


            //We first make a push so that a new item is made with a unique ID
            val wajureNode = mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).push()
            wajureItem.wajureID = wajureNode.key

            //then, we used the reference to set the value on that ID
            wajureNode.setValue(wajureItem)

            prefs!!.edit().putString("date",date)
            dialog.dismiss()
            Toast.makeText(this, "Item saved with ID " + wajureItem.wajureID, Toast.LENGTH_SHORT).show()
        }
        alert.show()
    }

    var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
//

            val wajuressss = dataSnapshot.child("wajure_item")
            val items = wajuressss.children.iterator()
            mapCheckIns(dataSnapshot)
            if(items.hasNext()) {
                addDataToList(dataSnapshot)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
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

            //check if the collection has any to do items or not
            while (itemsIterator.hasNext()) {

                //get current item
                val currentItem = itemsIterator.next()
                val wajureItem = WajureItem.create()
                //get current data in a map
                val map = currentItem.value as HashMap<String, Any>
                //key will return Firebase ID

                if (map.containsKey("wajureName")) {
                    wajureItem.wajureID = currentItem.key
                    wajureItem.wajureName = map.get("wajureName") as String?
                    wajureItem.wajureTotal = map.get("wajureTotal").toString().toInt()
                    wajureItem.wajureDayTotal = map.get("wajureTotal").toString().toInt()
                    wajureItemList!!.add(wajureItem)
                }

            }

        }
        //alert adapter that has changed
        adapter.notifyDataSetChanged()
    }


    private fun updateWajureTotal(checkinAmount: Int, wajure: WajureItem) {
        val wajureID = wajure.wajureID
        checkinAmount
        var wajureName = wajure.wajureName
        var wajureCr = wajure.wajureCreationDate
        var currentTotal = wajure.wajureTotal
        var currentDayTotal = wajure.wajureDayTotal
        mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(wajureID!!).child("wajureTotal").setValue(checkinAmount + currentTotal!!)
        mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(wajureID!!).child("wajureDayTotal").setValue(checkinAmount + currentDayTotal!!)

    }

    private fun updateWajureDialog(wajure: WajureItem) {
        val alert = AlertDialog.Builder(this)
        val itemEditText = EditText(this)
        val wajureID = wajure.wajureID
        val wajureName = wajure.wajureName


        alert.setMessage("Check-in")
        alert.setTitle(wajureName)
        alert.setView(itemEditText)

        alert.setPositiveButton("Submit") { dialog, positiveButton ->
            val checkInItem = CheckIn.create()

            val newTotal = itemEditText.text.toString().toInt()

            checkInItem.wajureID = wajureID
            checkInItem.checkInDate = date
            checkInItem.checkInTotal = newTotal

            mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(wajureID.toString()).child("wajureTotal").setValue(newTotal)


            //We first make a push so that a new item is made with a unique ID
            val checkInNode = mDatabase.child(Constants.FIREBASE_CHECKIN_ITEM).push()
            checkInItem.checkInID = checkInNode.key
            //then, we used the reference to set the value on that ID
            checkInNode.setValue(checkInItem)

            updateWajureTotal(newTotal, wajure)


            dialog.dismiss()

        }
        alert.show()
    }


    override fun modifyItemState(itemObjectId: String) {
        val itemReference = mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(itemObjectId)

    }

    //    //delete an item
    override fun onItemDelete(itemObjectId: String) {
        //get child reference in database via the ObjectID
        val itemReference = mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(itemObjectId)
        //deletion can be done via removeValue() method
        itemReference.removeValue()
    }


    fun mapCheckIns(db: DataSnapshot){

        val items = db.children.iterator()
        //Check if current database contains any collection
        if (items.hasNext()) {
            val checkIndex = items.next()
            val itemsIterator = checkIndex.children.iterator()

            //check if the collection has any to do items or not
            while (itemsIterator.hasNext()) {

                //get current item
                val currentItem = itemsIterator.next()
                //get current data in a map
                val map = currentItem.value as HashMap<String, Any>
                //key will return Firebase ID

                val currentSP = prefs!!.getString("date","0").toString()
                if(currentSP != date) {
                    val id = map.get("wajureID")
                    mDatabase.child(Constants.FIREBASE_WAJURE_ITEM).child(id.toString()).child("wajureDayTotal").setValue(0)

                }

            }

            prefs!!.edit().putString("date", date)


                }


        }


    }

