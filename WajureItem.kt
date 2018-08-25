package com.example.diplomat.wajure

class WajureItem {
    companion object Factory {
        fun create(): WajureItem = WajureItem()
    }
    var wajureID: String? = null
    var wajureName: String? = null
    var wajureCreationDate: String? = null
    var wajureTotal: Int? = null
    var wajureDayComplete: Boolean? = null
    var checkIn: String? = null
}