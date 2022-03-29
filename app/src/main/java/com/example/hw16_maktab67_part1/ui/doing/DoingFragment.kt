package com.example.hw16_maktab67_part1.ui.doing

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

class DoingFragment : Fragment(R.layout.fragment_doing), RecyclerViewListener,
    EditTaskInterface {

    private val appDataSource: AppDataSource by lazy {
        AppDataSource.getProfileDatabase(
            requireContext()
        )
    }

    private lateinit var imageView: ImageView

    private lateinit var doingFragmentViewModel: DoingFragmentViewModel

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

        recyclerView = view.findViewById(R.id.fragment_doing_layout_recycler_view)
        recyclerView.adapter = RecycleViewAdapter(emptyList(), this)
        imageView = view.findViewById(R.id.fragment_doing_layout_image_view)

        currentUserInit()

        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                doingFragmentViewModel.imageUri = it.toString()
            }

        cameraResultLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                doingFragmentViewModel.uriFromCamera(requireContext(), bitmap)
            }

        doingFragmentViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it is CurrentUser) recyclerViewInit()
        }

        doingFragmentViewModel.todoTaskList.observe(viewLifecycleOwner) { TaskList ->
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
                "title: ${doingFragmentViewModel.task.title} \n" +
                        "description: ${doingFragmentViewModel.task.description} \n" +
                        "state: ${doingFragmentViewModel.task.taskStatus} \n" +
                        "time: ${doingFragmentViewModel.task.time} \n" +
                        "date: ${doingFragmentViewModel.task.date} \n"
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun viewModelInit() {
        doingFragmentViewModel = ViewModelProvider(
            this,
            ViewModelFactory(appDataSource)
        ).get(doingFragmentViewModel::class.java)
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun currentUserInit() {
        doingFragmentViewModel.getCurrentUser()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun recyclerViewInit() {
        doingFragmentViewModel.getTodoTaskList()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    @SuppressLint("InflateParams")
    override fun recyclerViewClick(task: Task, position: Int) {
        doingFragmentViewModel.task = task
        doingFragmentViewModel.position = position

        val taskLayout = layoutInflater.inflate(R.layout.task_layout_edit_task, null)
        val editAlertDialog = EditAlertDialog(task, requireContext(), taskLayout, this)
        editAlertDialog.showTaskDialog()
    }


//////////////////////////////////////EditAlertInterface////////////////////////////////////////////

    @SuppressLint("NotifyDataSetChanged")
    override fun deleteTask() {
        doingFragmentViewModel.removeTask()
        doingFragmentViewModel.getTodoTaskList()
        recyclerView.adapter?.notifyItemRemoved(doingFragmentViewModel.position)
    }

    override fun datePicker() {
        doingFragmentViewModel.datePicker(requireContext())
    }

    override fun timePicker() {
        doingFragmentViewModel.timePicker(requireContext())
    }

    override fun pictureFromGallery() {
        galleryResultLauncher.launch("image/*")
    }

    override fun pictureFromCamera() {
        cameraResultLauncher.launch(null)
    }

    override fun save(description: String, taskStatus: TaskStatus) {
        doingFragmentViewModel.description = description
        doingFragmentViewModel.taskStatus = taskStatus
        doingFragmentViewModel.updateTask()
        recyclerView.adapter?.notifyItemChanged(position)
    }


///////////////////////////Notify Data Set Changed by Add New Task//////////////////////////////////
    @SuppressLint("NotifyDataSetChanged")
    override fun newTaskAdded() {
        if (::doingFragmentViewModel.isInitialized) {
            doingFragmentViewModel.getTodoTaskList()
            recyclerView.adapter?.notifyItemInserted(
                doingFragmentViewModel.todoTaskList.value?.size.toString().toInt()
            )
        }
    }

    override fun dataSetChanged() {
        doingFragmentViewModel.getTodoTaskList()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

}