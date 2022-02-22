package com.idn.notesapp.fragment.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.idn.notesapp.R
import com.idn.notesapp.data.model.NoteData
import com.idn.notesapp.data.ViewModelData.NotesViewModel
import com.idn.notesapp.databinding.FragmentUpdateBinding
import com.idn.notesapp.fragment.SharedViewModels

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private val mSharedViewModels : SharedViewModels by viewModels()
    private val mNotesViewModel : NotesViewModel by viewModels()

    private var _binding : FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        // For Update from color spinner
        binding.spUpdate.onItemSelectedListener = mSharedViewModels.listener

        // For set Menu
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemRemoval()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        val title = binding.etUpTitle.text.toString()
        val description = binding.etDescUpdate.text.toString()
        val getPriority = binding.spUpdate.selectedItem.toString()

        val validation = mSharedViewModels.verifyDataFromUser(title, description)
        if (validation) {
            val updateItem = NoteData(
                args.currentItem.id, title, mSharedViewModels.parsePriority(getPriority), description
            )
            mNotesViewModel.updateData(updateItem)
            Toast.makeText(requireContext(), "Successfully Update", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment2)
        } else {
            Toast.makeText(requireContext(), "Please Fill All Requirements", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmItemRemoval() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete '${args.currentItem.title}'?")
            .setMessage("Are You Sure About Want To Remove '${args.currentItem.title}'?")
            .setPositiveButton("Yes"){ _, _->
                mNotesViewModel.deleteData(args.currentItem)
                Toast.makeText(requireContext(), "Successfully Removed : ${args.currentItem.title}",
                    Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_updateFragment_to_listFragment2)
            }
            .setNegativeButton("No"){_, _->}
            .create()
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}