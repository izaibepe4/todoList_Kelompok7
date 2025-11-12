package com.example.todoapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.viewmodel.TodoFilter
import com.example.todoapp.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class) // Diperlukan untuk Scaffold, TopAppBar, dll.
@Composable
fun TodoScreen(vm: TodoViewModel = viewModel()) {
    val todos by vm.todos.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }
    val currentFilter by vm.filter.collectAsState()

    //Logika untuk memfilter daftar
    val filteredTodos = remember(todos, currentFilter) {
        when (currentFilter) {
            TodoFilter.ALL -> todos
            TodoFilter.ACTIVE -> todos.filter { !it.isDone }
            TodoFilter.COMPLETED -> todos.filter { it.isDone }
        }
    }

    //Menggunakan Scaffold & TopAppBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo List Reaktif") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding -> // innerPadding dari Scaffold
        Column(
            modifier = Modifier
                .padding(innerPadding) // Terapkan padding dari Scaffold
                .padding(horizontal = 16.dp) // Beri padding kita sendiri
                .fillMaxSize()
        ) {

            //Input dengan trailingIcon
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Tambah tugas...") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        if (text.isNotBlank()) {
                            vm.addTask(text.trim())
                            text = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Tugas")
                    }
                }
            )
            // UI Filter
            FilterControls(
                currentFilter = currentFilter,
                onFilterChange = { newFilter -> vm.setFilter(newFilter) }
            )

            HorizontalDivider() // Menggunakan HorizontalDivider

            LazyColumn {
                items(filteredTodos) { todo ->
                    TodoItem(
                        todo = todo,
                        onToggle = { vm.toggleTask(todo.id) },
                        onDelete = { vm.deleteTask(todo.id) }
                    )
                }
            }
        }
    }
}


// Composable FilterControls
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterControls(
    currentFilter: TodoFilter,
    onFilterChange: (TodoFilter) -> Unit
) {
    val filters = TodoFilter.values()

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        filters.forEach { filter ->
            SegmentedButton(
                selected = (filter == currentFilter),
                onClick = { onFilterChange(filter) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = filters.indexOf(filter),
                    count = filters.size
                )
            ) {
                Text(
                    text = when (filter) {
                        TodoFilter.ALL -> "Semua"
                        TodoFilter.ACTIVE -> "Aktif"
                        TodoFilter.COMPLETED -> "Selesai"
                    }
                )
            }
        }
    }
}