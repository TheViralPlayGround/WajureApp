package com.example.diplomat.wajure

class CheckIn {
    companion object Factory {
        fun create(): CheckIn = CheckIn()
    }
    var checkInID: String? = null
    var checkInDate: String? = null
    var wajureID: String? = null
    var checkInTotal: Int? = null

}