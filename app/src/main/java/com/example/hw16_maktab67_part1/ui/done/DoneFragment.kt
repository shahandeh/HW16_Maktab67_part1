package com.example.hw16_maktab67_part1.ui.done

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
import com.example.hw16_maktab67_part1.ui.model.EditAlertDialog
import com.example.hw16_maktab67_part1.ui.model.EditTaskInterface
import com.example.hw16_maktab67_part1.ui.model.RecycleViewAdapter
import com.example.hw16_maktab67_part1.ui.model.RecyclerViewListener

class DoneFragment : Fragment(R.layout.fragment_done), RecyclerViewListener,
    EditTaskInterface {

    private val appDataSource: AppDataSource by lazy {
        AppDataSource.getProfileDatabase(
            requireContext()
        )
    }

    private lateinit var imageView: ImageView

    private lateinit var doneFragmentViewModel: DoneFragmentViewModel

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

        recyclerView = view.findViewById(R.id.fragment_done_layout_recycler_view)
        recyclerView.adapter = RecycleViewAdapter(emptyList(), this)
        imageView = view.findViewById(R.id.fragment_done_layout_image_view)


        currentUserInit()

        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                doneFragmentViewModel.imageUri = it.toString()
            }

        cameraResultLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                doneFragmentViewModel.uriFromCamera(requireContext(), bitmap)
            }

        doneFragmentViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it is CurrentUser) recyclerViewInit()
        }

        doneFragmentViewModel.todoTaskList.observe(viewLifecycleOwner) { TaskList ->
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
                "title: ${doneFragmentViewModel.task.title} \n" +
                        "description: ${doneFragmentViewModel.task.description} \n" +
                        "state: ${doneFragmentViewModel.task.taskStatus} \n" +
                        "time: ${doneFragmentViewModel.task.time} \n" +
                        "date: ${doneFragmentViewModel.task.date} \n"
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun viewModelInit() {
        doneFragmentViewModel = ViewModelProvider(
            this,
            ViewModelFactory(appDataSource)
        ).get(DoneFragmentViewModel::class.java)
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun currentUserInit() {
        doneFragmentViewModel.getCurrentUser()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun recyclerViewInit() {
        doneFragmentViewModel.getTodoTaskList()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    @SuppressLint("InflateParams")
    override fun recyclerViewClick(task: Task, position: Int) {
        doneFragmentViewModel.task = task
        doneFragmentViewModel.position = position

        val taskLayout = layoutInflater.inflate(R.layout.task_layout_edit_task, null)
        val editAlertDialog = EditAlertDialog(task, requireContext(), taskLayout, this)
        editAlertDialog.showTaskDialog()
    }


//////////////////////////////////////EditAlertInterface////////////////////////////////////////////

    @SuppressLint("NotifyDataSetChanged")
    override fun deleteTask() {
        doneFragmentViewModel.removeTask()
        doneFragmentViewModel.getTodoTaskList()
        recyclerView.adapter?.notifyItemRemoved(doneFragmentViewModel.position)
    }

    override fun datePicker() {
        doneFragmentViewModel.datePicker(requireContext())
    }

    override fun timePicker() {
        doneFragmentViewModel.timePicker(requireContext())
    }

    override fun pictureFromGallery() {
        galleryResultLauncher.launch("image/*")
    }

    override fun pictureFromCamera() {
        cameraResultLauncher.launch(null)
    }

    override fun save(description: String, taskStatus: TaskStatus) {
        doneFragmentViewModel.description = description
        doneFragmentViewModel.taskStatus = taskStatus
        doneFragmentViewModel.updateTask()
        recyclerView.adapter?.notifyItemChanged(position)
    }


    //////////////////////////////////////EditAlertInterface////////////////////////////////////////////
///////////////////////////Notify Data Set Changed by Add New Task//////////////////////////////////
    @SuppressLint("NotifyDataSetChanged")
    override fun newTaskAdded() {
        if (::doneFragmentViewModel.isInitialized) {
            doneFragmentViewModel.getTodoTaskList()
            recyclerView.adapter?.notifyItemInserted(
                doneFragmentViewModel.todoTaskList.value?.size.toString().toInt()
            )
        }
    }

    override fun dataSetChanged() {
        doneFragmentViewModel.getTodoTaskList()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

}