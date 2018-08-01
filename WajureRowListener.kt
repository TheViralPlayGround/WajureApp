package com.example.diplomat.wajure

interface WajureRowListener {

    fun modifyItemState(itemObjectId: String)
    fun onItemDelete(itemObjectId: String)

    }