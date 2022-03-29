package com.example.hw16_maktab67_part1.ui.todo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.hw16_maktab67_part1.R
import com.example.hw16_maktab67_part1.database.AppDataSource
import com.example.hw16_maktab67_part1.database.CurrentUser
import com.example.hw16_maktab67_part1.database.Task
import com.example.hw16_maktab67_part1.database.TaskStatus
import com.example.hw16_maktab67_part1.ui.ViewModelFactory
import com.example.hw16_maktab67_part1.ui.model.*

class TodoFragment : Fragment(R.layout.fragment_todo), RecyclerViewListener,
    EditTaskInterface {

    private val appDataSource: AppDataSource by lazy {
        AppDataSource.getProfileDatabase(
            requireContext()
        )
    }

    private lateinit var imageView: ImageView

    private lateinit var todoFragmentViewModel: TodoFragmentViewModel

    private lateinit var recyclerView: RecyclerView

    private lateinit var galleryResultLauncher: ActivityResultLauncher<String>

    private lateinit var cameraResultLauncher: ActivityResultLauncher<Void>

    private var position = -1


//////////////////////////////////////EditAlertDialog///////////////////////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelInit()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.todo_recycler_view)
        recyclerView.adapter = RecycleViewAdapter(emptyList(), this)
        imageView = view.findViewById(R.id.todo_layout_image_view)


        currentUserInit()

        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                todoFragmentViewModel.imageUri = it.toString()
            }

        cameraResultLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                todoFragmentViewModel.uriFromCamera(requireContext(), bitmap)
            }

        todoFragmentViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it is CurrentUser) recyclerViewInit()
        }

        todoFragmentViewModel.todoTaskList.observe(viewLifecycleOwner) { TaskList ->
            if (TaskList.isEmpty()) imageView.visibility = View.VISIBLE
            else imageView.visibility = View.GONE
            recyclerView.adapter = RecycleViewAdapter(TaskList, this)
        }

    }

    override fun share() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "title: ${todoFragmentViewModel.task.title} \n" +
                        "description: ${todoFragmentViewModel.task.description} \n" +
                        "state: ${todoFragmentViewModel.task.taskStatus} \n" +
                        "time: ${todoFragmentViewModel.task.time} \n" +
                        "date: ${todoFragmentViewModel.task.date} \n"
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun viewModelInit() {
        todoFragmentViewModel = ViewModelProvider(
            this,
            ViewModelFactory(appDataSource)
        ).get(TodoFragmentViewModel::class.java)
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun currentUserInit() {
        todoFragmentViewModel.getCurrentUser()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun recyclerViewInit() {
        todoFragmentViewModel.getTodoTaskList()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    @SuppressLint("InflateParams")
    override fun recyclerViewClick(task: Task, position: Int) {
        todoFragmentViewModel.task = task
        todoFragmentViewModel.position = position

        val taskLayout = layoutInflater.inflate(R.layout.task_layout_edit_task, null)
        val editAlertDialog = EditAlertDialog(task, requireContext(), taskLayout, this)
        editAlertDialog.showTaskDialog()
    }


//////////////////////////////////////EditAlertInterface////////////////////////////////////////////

    @SuppressLint("NotifyDataSetChanged")
    override fun deleteTask() {
        todoFragmentViewModel.removeTask()
        todoFragmentViewModel.getTodoTaskList()
        recyclerView.adapter?.notifyItemRemoved(todoFragmentViewModel.position)
    }

    override fun datePicker() {
        todoFragmentViewModel.datePicker(requireContext())
    }

    override fun timePicker() {
        todoFragmentViewModel.timePicker(requireContext())
    }

    override fun pictureFromGallery() {
        galleryResultLauncher.launch("image/*")
    }

    override fun pictureFromCamera() {
        cameraResultLauncher.launch(null)
    }

    override fun save(description: String, taskStatus: TaskStatus) {
        todoFragmentViewModel.description = description
        todoFragmentViewModel.taskStatus = taskStatus
        todoFragmentViewModel.updateTask()
        recyclerView.adapter?.notifyItemChanged(position)
    }


    //////////////////////////////////////EditAlertInterface////////////////////////////////////////////
///////////////////////////Notify Data Set Changed by Add New Task//////////////////////////////////
    @SuppressLint("NotifyDataSetChanged")
    override fun newTaskAdded() {
        if (::todoFragmentViewModel.isInitialized) {
            todoFragmentViewModel.getTodoTaskList()
            recyclerView.adapter?.notifyItemInserted(
                todoFragmentViewModel.todoTaskList.value?.size.toString().toInt()
            )
        }
    }

    override fun dataSetChanged() {
        todoFragmentViewModel.getTodoTaskList()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

}