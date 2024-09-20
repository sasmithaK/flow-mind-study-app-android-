package com.example.flowmind.ui.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flowmind.adapter.TodoAdapter
import com.example.flowmind.databinding.FragmentTodoBinding
import com.example.flowmind.model.TodoItem

class TodoFragment : Fragment() {

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sample data for testing
        val sampleTodos = listOf(
            TodoItem(id = 1, title = "Study for Exam", description = "Math exam next week", isCompleted = false),
            TodoItem(id = 2, title = "Finish Assignment", description = "Complete the coding assignment", isCompleted = true),
            TodoItem(id = 3, title = "Group Meeting", description = "Discuss project ideas", isCompleted = false)
        )

        // Set up RecyclerView with adapter
        val todoAdapter = TodoAdapter(sampleTodos)
        binding.recyclerViewTodo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
