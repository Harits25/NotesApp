package com.idn.notesapp.fragment.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.idn.notesapp.R
import com.idn.notesapp.data.model.NoteData
import com.idn.notesapp.data.ViewModelData.NotesViewModel
import com.idn.notesapp.databinding.FragmentListBinding
import com.idn.notesapp.fragment.SharedViewModels
import com.idn.notesapp.fragment.list.adapter.ListAdapter
import com.idn.notesapp.utils.hideKeyboard
import jp.wasabeef.recyclerview.animators.LandingAnimator

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val mNotesViewModel : NotesViewModel by viewModels()
    private val adapter: ListAdapter by lazy { ListAdapter() }
    private val mSharedViewModel : SharedViewModels by viewModels()
    private var _listBinding : FragmentListBinding? = null
    private val listBinding: FragmentListBinding
        get() = _listBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _listBinding = FragmentListBinding.inflate(inflater, container, false)
        listBinding.lifecycleOwner = this
        listBinding.mSharedViewModel = mSharedViewModel

        setUpRecyclerView()

        mNotesViewModel.getAllData.observe(viewLifecycleOwner) { data ->
            mSharedViewModel.checkIfDataBaseEmpty(data)
            adapter.setData(data)
        }

        setHasOptionsMenu(true)
        hideKeyboard(requireActivity())

        return listBinding.root
    }

    private fun setUpRecyclerView() {
        listBinding.rvList.adapter = adapter
        listBinding.rvList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        listBinding.rvList.itemAnimator = LandingAnimator().apply {
            addDuration = 300
        }
        swipeToDelete(listBinding.rvList)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object: SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deleteItem = adapter.datalist[viewHolder.adapterPosition]
                mNotesViewModel.deleteData(deleteItem)
                restoreDeleteData(viewHolder.itemView, deleteItem)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeleteData(view: View, deleteItem: NoteData) {
        val snackbar = Snackbar.make(
            view, "Delete '${deleteItem.title}'",
            Snackbar.LENGTH_LONG
        )

        snackbar.setAction("Undo") {
            mNotesViewModel.insertData(deleteItem)
        }
        snackbar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmRemoveAll()
            R.id.menu_priority_high -> mNotesViewModel.sortByHighPriority.observe(this) {
                adapter.setData(it)
            }
            R.id.menu_priority_low -> mNotesViewModel.sortByLowPriority.observe(this) {
                adapter.setData(it)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemoveAll() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All?")
            .setMessage("Are you sure you want to Remove All?")
            .setPositiveButton("Yes"){_,_ ->
                mNotesViewModel.deleteAllData()
                Toast.makeText(requireContext(), "Successfully Removed All", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    override fun onQueryTextSubmit(query : String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String?) {
        val searchQuery = "%$query"


        mNotesViewModel.searchDatabase(searchQuery).observe(this) {list ->
            list.let { adapter.setData(it) }
        }
    }

    override fun onDestroy() {
        _listBinding = null
        super.onDestroy()
    }

}