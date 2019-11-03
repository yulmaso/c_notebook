package com.example.notebook.POJO

import java.io.Serializable

class Note : Serializable {
    var id: Int = 0
    var text: String? = null
    var date: String? = null
    var time: String? = null

    constructor() {}

    constructor(text: String, date: String, time: String) {
        this.text = text
        this.date = date
        this.time = time
    }

    constructor(id: Int, text: String, date: String, time: String) {
        this.id = id
        this.text = text
        this.date = date
        this.time = time
    }
}
