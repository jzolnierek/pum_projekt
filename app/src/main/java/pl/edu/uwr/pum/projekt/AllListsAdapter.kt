package pl.edu.uwr.pum.projekt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class AllListsAdapter(private val dbHandler: DBHandler) : RecyclerView.Adapter<AllListsAdapter.AllListsViewHolder>() {

    inner class AllListsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var listTitle: TextView = itemView.findViewById(R.id.listTitle)
        var listDate: TextView = itemView.findViewById(R.id.listDate)
        var deleteListButton: AppCompatImageButton = itemView.findViewById(R.id.deleteListButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllListsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_lists_item, parent, false)
        return AllListsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dbHandler.getLists().size
    }

    override fun onBindViewHolder(holder: AllListsViewHolder, position: Int) {
        val item = dbHandler.getLists()[position]
        holder.listTitle.text = item.title
        holder.listDate.text = item.date.toString()
        holder.deleteListButton.setOnClickListener {
            dbHandler.deleteList(item)
            notifyItemRemoved(position)
            notifyItemRangeRemoved(position, itemCount)
        }
        holder.listTitle.setOnClickListener {
            val action = AllListsFragmentDirections.actionAllListsFragmentToShoppingListFragment(
                listId = item.id
            )
            holder.itemView.findNavController().navigate(action)
        }
    }
}