package pl.edu.uwr.pum.projekt

import java.sql.Date

data class ShoppingListItem(var name: String, var shoppingListId: Int, var price: Double, var shop: String, var bought: Boolean) {
    var id: Int = 0

    constructor(id: Int, name: String, shoppingListId: Int, price: Double, shop: String, bought: Boolean): this(name, shoppingListId, price, shop, bought) {
        this.id = id
    }
}