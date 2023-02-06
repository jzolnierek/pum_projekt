package pl.edu.uwr.pum.projekt

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.Date

class DBHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ShoppingLists.db"
        private const val TABLE_LISTS = "ListsTable"
        private const val TABLE_ITEMS = "ItemsTable"

        private const val COLUMN_ID = "_id"
        private const val COLUMN_LIST_TITLE = "title"
        private const val COLUMN_LIST_DATE = "date"
        private const val COLUMN_ITEM_NAME = "item"
        private const val COLUMN_ITEM_LIST_ID = "listId"
        private const val COLUMN_ITEM_PRICE = "price"
        private const val COLUMN_ITEM_SHOP = "shop"
        private const val COLUMN_ITEM_BOUGHT = "bought"

    }

    override fun onCreate(p0: SQLiteDatabase?) {
        val CREATE_LISTS_TABLE =
            "CREATE TABLE $TABLE_LISTS($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, $COLUMN_LIST_TITLE TEXT, $COLUMN_LIST_DATE TEXT)"
        val CREATE_ITEMS_TABLE =
            "CREATE TABLE $TABLE_ITEMS($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, $COLUMN_ITEM_NAME TEXT, $COLUMN_ITEM_LIST_ID INTEGER, $COLUMN_ITEM_PRICE REAL, $COLUMN_ITEM_SHOP TEXT, $COLUMN_ITEM_BOUGHT INTEGER)"
        p0?.execSQL(CREATE_LISTS_TABLE)
        p0?.execSQL(CREATE_ITEMS_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_LISTS")
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(p0)
    }

    fun addList(title: String, date: Date) {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LISTS WHERE $COLUMN_LIST_TITLE='$title'", null)
        if (!cursor.moveToFirst()) {
            val contentValues = ContentValues()
            contentValues.put(COLUMN_LIST_TITLE, title)
            contentValues.put(COLUMN_LIST_DATE, date.toString())
            db.insert(TABLE_LISTS, null, contentValues)
        }
        db.close()
    }

    fun deleteList(list: ShoppingList) {
        val db = this.writableDatabase
        db.delete(TABLE_LISTS, "$COLUMN_ID=${list.id}", null)
        db.delete(TABLE_ITEMS, "$COLUMN_ITEM_LIST_ID='${list.id}'", null)
        db.close()
    }

    fun addItem(item: ShoppingListItem) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_ITEM_NAME, item.name)
        contentValues.put(COLUMN_ITEM_LIST_ID, item.shoppingListId)
        contentValues.put(COLUMN_ITEM_PRICE, item.price)
        contentValues.put(COLUMN_ITEM_SHOP, item.shop)
        contentValues.put(COLUMN_ITEM_BOUGHT, item.bought)
        db.insert(TABLE_ITEMS, null, contentValues)
        db.close()
    }

    fun deleteItem(item: ShoppingListItem) {
        val db = this.writableDatabase
        db.delete(TABLE_ITEMS,
            "$COLUMN_ID=${item.id}",
            null)
        db.close()
    }

    fun getLists() : List<ShoppingList> {
        val lists: MutableList<ShoppingList> = ArrayList()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LISTS", null)
        if (cursor.moveToFirst()) {
            do {
                lists.add(ShoppingList(cursor.getInt(0), cursor.getString(1), Date.valueOf(cursor.getString(2))))
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return lists
    }

    fun getItems(listId: Int) : List<ShoppingListItem> {
        val items : MutableList<ShoppingListItem> = ArrayList()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ITEMS WHERE $COLUMN_ITEM_LIST_ID='$listId'", null)
        if (cursor.moveToFirst()) {
            do {
                items.add(ShoppingListItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getDouble(3), cursor.getString(4), cursor.getInt(5) == 1))
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return items
    }

    fun updateItem(item: ShoppingListItem) {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_ITEM_NAME, item.name)
        contentValues.put(COLUMN_ITEM_LIST_ID, item.shoppingListId)
        contentValues.put(COLUMN_ITEM_PRICE, item.price)
        contentValues.put(COLUMN_ITEM_SHOP, item.shop)
        contentValues.put(COLUMN_ITEM_BOUGHT, item.bought)
        db.update(TABLE_ITEMS, contentValues, "$COLUMN_ID=${item.id}", null)
        db.close()

    }

}