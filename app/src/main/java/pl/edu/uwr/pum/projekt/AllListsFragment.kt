package pl.edu.uwr.pum.projekt

import android.app.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.Date
import java.time.LocalDate

class AllListsFragment: Fragment() {
    private lateinit var dbHandler: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = DBHandler(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_lists, container, false)
    }

    override fun onDestroy() {
        dbHandler.close()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val Rv: RecyclerView = view.findViewById(R.id.allListsRv)
        Rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = AllListsAdapter(dbHandler)
        }

        val addListButton: FloatingActionButton = view.findViewById(R.id.addListButton)

        addListButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireParentFragment().context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.add_list_dialog, null)
            val listTitleInput = dialogLayout.findViewById<EditText>(R.id.newListInput)
            val listDateInput = dialogLayout.findViewById<TextView>(R.id.newListDateInput)
            val dateButton = dialogLayout.findViewById<ImageButton>(R.id.dateButton)

            var date: LocalDate = LocalDate.now()
            listDateInput.text = date.toString()

            dateButton.setOnClickListener {
                DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, day ->
                        date = LocalDate.of(year, month+1, day)
                        listDateInput.text = date.toString()
                }, date.year, date.monthValue, date.dayOfMonth).show()
            }

            with(builder) {
                setTitle("New shopping list")
                setPositiveButton("Add") {
                    dialog, which ->
                    if (listTitleInput.text.toString().isNotEmpty() && listDateInput.text.toString().isNotEmpty()) {
                        dbHandler.addList(listTitleInput.text.toString(), Date.valueOf(listDateInput.text.toString()))
                    }
                }
                setNegativeButton("Cancel") {
                    dialog, which -> Log.d("Main", "Canceled")
                }
                setView(dialogLayout)
                show()
            }
            Rv.adapter?.notifyItemInserted(dbHandler.getLists().size)
        }
    }
}