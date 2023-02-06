package pl.edu.uwr.pum.projekt

import java.sql.Date

data class ShoppingList(val title: String, val date: Date) {
    var id: Int = 0

    constructor(id: Int, title: String, date: Date): this(title, date) {
        this.id = id
    }
}