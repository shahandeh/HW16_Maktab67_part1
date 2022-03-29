package com.example.hw16_maktab67_part1.ui.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.hw16_maktab67_part1.R
import com.example.hw16_maktab67_part1.database.AppDataSource
import com.example.hw16_maktab67_part1.database.Profile
import com.example.hw16_maktab67_part1.database.TaskStatus
import com.example.hw16_maktab67_part1.ui.ViewModelFactory
import com.example.hw16_maktab67_part1.ui.doing.DoingFragment
import com.example.hw16_maktab67_part1.ui.done.DoneFragment
import com.example.hw16_maktab67_part1.ui.model.*
import com.example.hw16_maktab67_part1.ui.todo.TodoFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File
import java.io.IOException
import java.util.*

class MainFragment : Fragment(R.layout.fragment_main), LoginInterface, CreateTaskInterface, RemoveAllTaskAlertDialogInterface {

    private lateinit var mainFragmentViewModel: MainFragmentViewModel

    private val appDataSource by lazy { AppDataSource.getProfileDatabase(requireContext()) }

    private val fragmentListListener : MutableList<RecyclerViewListener> = mutableListOf(TodoFragment(), DoingFragment(), DoneFragment())
    private val fragmentList : MutableList<Fragment> = mutableListOf(TodoFragment(), DoingFragment(), DoneFragment())
    private val viewPagerTabList = mutableListOf("TODO", "DOING", "DONE")

    private lateinit var loginAlertDialog: LogInAlertDialog

    // Add Task
    private lateinit var time: String
    private lateinit var date: String
    private var photoUri: String? = null
    lateinit var taskStatus: TaskStatus
    private var calendar: Calendar = Calendar.getInstance()

    private lateinit var createTaskAlertDialog : CreateTaskAlertDialog


    // View Pager
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private lateinit var takeImageFromCamera: ActivityResultLauncher<Void>
    private lateinit var takeImageFromGallery: ActivityResultLauncher<String>

    private lateinit var removeAllTaskAlertDialog: RemoveAllTaskAlertDialog

    private lateinit var tabLayoutMediator: TabLayoutMediator

    @SuppressLint("SimpleDateFormat", "InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelInit()

        takeImageFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                photoUri = uri.toString()
            }

        takeImageFromCamera =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                photoUri = savePhotoFromCamera(System.currentTimeMillis().toString(), bitmap)
            }

        view.findViewById<FloatingActionButton>(R.id.floating_action_button).setOnClickListener {
            floatingActionButton()
        }

        loginAlertDialog()
    }

    private fun loginAlertDialog() {
        val loginLayout = layoutInflater.inflate(R.layout.login_layout, null)
        loginAlertDialog = LogInAlertDialog(requireContext(), loginLayout, this)
        loginAlertDialog.loginDialog()
    }

    private fun viewModelInit() {
        mainFragmentViewModel =
            ViewModelProvider(this, ViewModelFactory(appDataSource))
                .get(MainFragmentViewModel::class.java)
    }

    @SuppressLint("InflateParams")
    private fun floatingActionButton() {
        val createTaskLayout = layoutInflater.inflate(R.layout.task_layout_add_task, null)
        val pictureDialogLayout = layoutInflater.inflate(R.layout.pick_image, null)
        createTaskAlertDialog =
            CreateTaskAlertDialog(createTaskLayout, requireContext(), pictureDialogLayout, this)
        createTaskAlertDialog.createDialog()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_remove_all_task -> {
                val layout = layoutInflater.inflate(R.layout.remove_all_task_dialog, null)
                removeAllTaskAlertDialog = RemoveAllTaskAlertDialog(requireContext(), layout, this)
                removeAllTaskAlertDialog.create()
            }
            R.id.menu_log_out -> {
                tabLayoutMediator.detach()
                mainFragmentViewModel.removeCurrentUser()
                this.onDestroy()
            }
            R.id.menu_logIn -> {
                if (tabLayoutMediator.isAttached) Toast.makeText(
                    requireContext(),
                    "Please log out first!",
                    Toast.LENGTH_SHORT
                ).show()
                else loginAlertDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun removeAllTask() {
        mainFragmentViewModel.removeAllTask(mainFragmentViewModel.username)
        removeAllTaskAlertDialog.alertDialogDismiss()
        fragmentListListener.forEach {
            it.dataSetChanged()
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun viewPager() {
        viewPager = requireView().findViewById(R.id.view_pager)
        tabLayout = requireView().findViewById(R.id.tab_layout)
        viewPager.adapter = FragmentAdapter(requireActivity())
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = viewPagerTabList[position]
        }
        tabLayoutMediator.attach()
    }

    inner class FragmentAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = fragmentList.size
        override fun createFragment(position: Int): Fragment = fragmentList[position]
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    override fun login(username: String, password: String) {
        mainFragmentViewModel.username = username
        mainFragmentViewModel.password = password
        mainFragmentViewModel.getUser()
        mainFragmentViewModel.profile.observe(this) {
            if (it is Profile) {
                mainFragmentViewModel.userValidate()
            }
        }
        mainFragmentViewModel.profileIsValid.observe(viewLifecycleOwner){
            if (it){
                mainFragmentViewModel.setCurrentUser()
                setHasOptionsMenu(true)
                viewPager()
                loginAlertDialog.loginDismiss()
            }
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    override fun signUp(name: String, username: String, email: String, password: String) {
        mainFragmentViewModel.name = name
        mainFragmentViewModel.username = username
        mainFragmentViewModel.email = email
        mainFragmentViewModel.password = password
        mainFragmentViewModel.register()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    override fun datePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, Year, Month, Day ->
                date = "$Year/$Month/$Day"
            }, year, month, day)
        datePickerDialog.show()
    }


    override fun timePicker() {
        val timeSetListener =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                time = "$hour:$minute"
            }
        TimePickerDialog(
            context,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }


    override fun pictureFromGallery() {
        takeImageFromGallery.launch("image/*")
    }


    override fun pictureFromCamera() {
        takeImageFromCamera.launch(null)
    }


    override fun save(title: String, description: String, taskStatus: TaskStatus) {
        mainFragmentViewModel.createTaskUsername = mainFragmentViewModel.username
        mainFragmentViewModel.createTaskTitle = title
        mainFragmentViewModel.createTaskDescription = description
        mainFragmentViewModel.createTaskStatus = taskStatus
        Log.d("majid", "save: $taskStatus")
        mainFragmentViewModel.createTaskTime = time
        mainFragmentViewModel.createTaskDate = date
        mainFragmentViewModel.createTaskPhotoUri = photoUri
        mainFragmentViewModel.taskList.observe(viewLifecycleOwner) {
            fragmentListListener.forEach {
                it.newTaskAdded()
            }
        }
        mainFragmentViewModel.addTask()
        mainFragmentViewModel.getTaskList()
        createTaskAlertDialog.alertDialogDismiss()
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun savePhotoFromCamera(fileName: String, bitmap: Bitmap): String {
        return try {
            requireContext().openFileOutput("$fileName.jpg", MODE_PRIVATE).use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap")
                }
                Uri.fromFile(File("${requireContext().filesDir}/$fileName.jpg")).toString()
            }
        } catch (e: IOException) {
            e.printStackTrace().toString()
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


}