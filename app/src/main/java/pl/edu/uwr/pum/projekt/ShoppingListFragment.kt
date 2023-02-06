package pl.edu.uwr.pum.projekt

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ShoppingListFragment: Fragment() {
    private lateinit var dbHandler: DBHandler
    private var listId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = DBHandler(requireActivity())

        arguments?.let {
            listId = it.getInt("listId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shopping_list, container, false)
    }

    override fun onDestroy() {
        dbHandler.close()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val Rv: RecyclerView = view.findViewById(R.id.shoppingListRv)

        fun itemDialog(editMode: Boolean, item: ShoppingListItem?=null) {
            val builder = AlertDialog.Builder(requireParentFragment().context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.add_item_dialog, null)

            val itemNameInput = dialogLayout.findViewById<EditText>(R.id.itemNameInput)
            val itemShopInput = dialogLayout.findViewById<EditText>(R.id.itemShopInput)
            val itemPriceInput = dialogLayout.findViewById<EditText>(R.id.itemPriceInput)

            if (editMode && item!=null) {
                itemNameInput.setText(item.name)
                itemShopInput.setText(item.shop)
                itemPriceInput.setText(item.price.toString())
            }

            with(builder) {
                setTitle(if (editMode) "Edit item" else "Add new item")
                setPositiveButton(if (editMode) "Save" else "Add") {
                        dialog, which ->
                    if(!editMode) {
                        val item_ = ShoppingListItem(
                            itemNameInput.text.toString(),
                            listId,
                            itemPriceInput.text.toString().toDouble(),
                            itemShopInput.text.toString(),
                            false
                        )
                        dbHandler.addItem(item_)
                        Rv.adapter?.notifyItemInserted(dbHandler.getItems(listId).size)
                        val adapter = Rv.adapter as ShoppingListAdapter
                        adapter.refreshItems()
                    } else {
                        if (item != null) {
                            item.name = itemNameInput.text.toString()
                            item.price = itemPriceInput.text.toString().toDouble()
                            item.shop = itemShopInput.text.toString()
                            dbHandler.updateItem(item)
                            Rv.adapter?.notifyDataSetChanged()
                        }
                    }
                }
                setNegativeButton("Cancel") {
                        dialog, which -> Log.d("Main", "Canceled")
                }
                setView(dialogLayout)
                show()
            }
        }

        Rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ShoppingListAdapter(dbHandler, listId, object: ShoppingListAdapter.OnItemClickListener{
                override fun onItemClick(editMode: Boolean, item: ShoppingListItem?) {
                    itemDialog(editMode, item)
                }
            })
        }

        val addItemButton: FloatingActionButton = view.findViewById(R.id.addItemButton)
        val sortItemsButton: FloatingActionButton = view.findViewById(R.id.sortItemsButton)

        addItemButton.setOnClickListener {
            itemDialog(false)
            Rv.adapter?.notifyItemInserted(dbHandler.getItems(listId).size)
        }

        sortItemsButton.setOnClickListener {
            val items = arrayOf("Shop", "A-Z", "Price", "Bought")
            val builder = AlertDialog.Builder(requireActivity())
            with(builder) {
                setTitle("Sort by")
                setItems(items) { dialog, which ->
                    val adapter = Rv.adapter as ShoppingListAdapter
                    adapter.sortItems(items[which])
                }
                show()
            }
        }
    }
}