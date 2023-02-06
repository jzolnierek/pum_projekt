package pl.edu.uwr.pum.projekt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView

class ShoppingListAdapter(private val dbHandler: DBHandler, private val listId: Int, private val onClick: OnItemClickListener): RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    private var ItemsList: MutableList<ShoppingListItem> = dbHandler.getItems(listId) as MutableList<ShoppingListItem>
    private var sortType: String = "none"

    inner class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView = itemView.findViewById(R.id.itemName)
        var itemShopAndPrice: TextView = itemView.findViewById(R.id.itemShopAndPrice)
        var deleteItemButton: AppCompatImageButton = itemView.findViewById(R.id.deleteItemButton)
        var boughtCheckBox: CheckBox = itemView.findViewById(R.id.boughtCheckBox)
    }

    interface OnItemClickListener {
        fun onItemClick(editMode: Boolean, item: ShoppingListItem?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shopping_list_item, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ItemsList.size
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val item = ItemsList[position]
        holder.itemName.text = item.name
        holder.itemShopAndPrice.text = item.shop + " - " + item.price.toString() + " PLN"
        holder.deleteItemButton.setOnClickListener {
            dbHandler.deleteItem(item)
            refreshItems()
            notifyItemRemoved(position)
            notifyItemRangeRemoved(position, itemCount)
        }
        holder.boughtCheckBox.isChecked = item.bought

        holder.boughtCheckBox.setOnClickListener {
            item.bought = holder.boughtCheckBox.isChecked
            dbHandler.updateItem(item)
        }

        holder.itemName.setOnClickListener {
            onClick.onItemClick(true, item)
            notifyItemChanged(position)
        }
    }

    fun sortItems(sortBy: String) {
        sortType = sortBy
        if(sortBy == "Shop")
            ItemsList.sortBy {it.shop }
        else if (sortBy == "A-Z")
            ItemsList.sortBy { it.name }
        else if (sortBy == "Price")
            ItemsList.sortBy { it.price }
        else if (sortBy == "Bought")
            ItemsList.sortBy { it.bought }
        notifyDataSetChanged()
    }

    fun refreshItems() {
        ItemsList = dbHandler.getItems(listId) as MutableList<ShoppingListItem>
        if (sortType != "none")
            sortItems(sortType)
    }
}