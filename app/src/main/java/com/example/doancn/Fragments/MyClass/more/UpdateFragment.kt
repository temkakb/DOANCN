package com.example.doancn.Fragments.MyClass.more

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.example.doancn.ClassViewModel
import com.example.doancn.ClassViewModel.ClassEvent.*
import com.example.doancn.Fragments.CreateClass.CreateClassViewModel
import com.example.doancn.Fragments.CreateClass.MapsActivity
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.classModel.ClassQuest
import com.example.doancn.R
import com.example.doancn.Utilities.StringUtils
import com.example.doancn.databinding.FragmentUpdateBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@ExperimentalCoroutinesApi
class UpdateFragment : Fragment() {
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1

    private val viewModel: ClassViewModel by activityViewModels()

    private lateinit var subjects: Array<String>
    private val dowSet = HashSet<ClassQuest.DayOfWeeks>()
    private var paymentOption: ClassQuest.PaymentOption? = null
    private var location: ClassQuest.Location? = null
    private var dateStart: LocalDate? = null
    private var subject: ClassQuest.Subject? = null
    private lateinit var shifts: ArrayList<ClassQuest.Shift>
    private lateinit var paymentOptions: Array<String>
    private val createClassViewModel: CreateClassViewModel by activityViewModels()
    private var startAt: Double = 0.0
    private var duration: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subjects = resources.getStringArray(R.array.Subjects)
        paymentOptions = resources.getStringArray(R.array.option)
        navController = findNavController()


    }

    @SuppressLint("SimpleDateFormat")
    private fun showData(classroom: Classroom) {
        binding.createCLassName.setText(classroom.name)
        binding.createCLassShortDescription.setText(classroom.shortDescription)
        binding.subjectAutoCompleteTextView.setText(
            subjects[classroom.subject.subjectId.toInt() - 1],
            false
        )
        subject = ClassQuest.Subject(classroom.subject.name, classroom.subject.subjectId.toString())

        dateStart = LocalDate.parse(classroom.startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        binding.createCLassDateStart.setText(classroom.startDate)
        val dow = resources.getStringArray(R.array.Dow)
        classroom.shifts.forEach {
            when (it.dayOfWeek.dowId) {
                1 -> binding.toggleButton.check(R.id.buttonMonday)
                2 -> binding.toggleButton.check(R.id.buttonTuesday)
                3 -> binding.toggleButton.check(R.id.buttonWednesday)
                4 -> binding.toggleButton.check(R.id.buttonThursday)
                5 -> binding.toggleButton.check(R.id.buttonFriday)
                6 -> binding.toggleButton.check(R.id.buttonSaturday)
                7 -> binding.toggleButton.check(R.id.buttonSunday)
            }
            dowSet.add(ClassQuest.DayOfWeeks(dow[it.dayOfWeek.dowId - 1]))
        }

        startAt = classroom.shifts.first().startAt.toDouble()
        duration = classroom.shifts.first().duration.toDouble() / 60000
        Log.d("duration", duration.toString())
        val hours = startAt / 3600000
        val minute = (startAt % 3600000) / 60000
        binding.createCLassTimeStart.setText(
            String.format(
                "%dh:%dph ",
                hours.toInt(),
                minute.toInt()
            )
        )
        binding.createCLassDuration.setText(String.format("%d", duration.toInt()))
        binding.autoCompleteTextViewPaymentOption.setText(
            paymentOptions[classroom.option.paymentOptionId.toInt() - 1],
            false
        )
        paymentOption = ClassQuest.PaymentOption(
            classroom.option.name,
            classroom.option.paymentOptionId.toString()
        )

        binding.createCLassFee.setText(String.format("%.2f", classroom.fee))
        binding.createCLassAddress.setText(classroom.location.address)
        location = ClassQuest.Location("", "", 0.0, 0.0).apply {
            this.address = classroom.location.address
            this.city = classroom.location.city
            this.coordinateX = classroom.location.coordinateX.toDouble()
            this.coordinateY = classroom.location.coordinateY.toDouble()
        }

    }

    override fun onResume() {
        super.onResume()
        binding.updateClassBtn.setOnClickListener {
            if (checkModel()) {
                shifts = ArrayList()
                Log.d("createCLassDuration", binding.createCLassDuration.text.toString())
                dowSet.forEach {
                    shifts.add(
                        ClassQuest.Shift(
                            it.name.uppercase(),
                            (binding.createCLassDuration.text.toString()
                                .toDouble() * 60000).toString(),
                            startAt.toString()
                        )
                    )
                }
                location?.address = binding.createCLassAddress.text.toString()
                val classRoom = ClassQuest(
                    name = binding.createCLassName.text.toString(),
                    about = "",
                    currentAttendanceNumber = 0,
                    fee = binding.createCLassFee.text.toString().toDouble(),
                    location = location!!,
                    paymentOption = paymentOption!!,
                    shortDescription = binding.createCLassShortDescription.text.toString(),
                    startDate = dateStart.toString(),
                    subject = subject!!,
                    shifts = shifts
                )
                Log.d("updateClassroom", classRoom.toString())
                updateClassroom(classRoom = classRoom)
            }
        }

    }

    private fun updateClassroom(classRoom: ClassQuest) {
        viewModel.updateClassroom(classRoom)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        viewModel.selectedItem.observe(viewLifecycleOwner, {
            location = it
            Log.d("change_location", location.toString())

        })


        collectData()
        setupSubjectType()
        setupPaymentOptionType()
        setupDatePicker()
        setupShiftPicker()
        setupLocationPicker()
        setupValidation()
        val classroom =
            arguments?.getSerializable("targetUpdateClass") as Classroom
        showData(classroom)
        return binding.root
    }

    private fun collectData() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.updateState.collect { event ->
                when (event) {
                    is Success -> {
                        MaterialDialog(requireContext()).show {
                            title(text = "Thông báo")
                            message(text = "Cập nhật thành công")
                            positiveButton(R.string.backToClass) { _ ->
                                Log.d("event_data", event.data.toString())
                                navController.navigate(
                                    R.id.action_updateFragment_to_navigation_home,
                                    Bundle().apply {
                                        putSerializable("classRoomHome", event.data)
                                    })
                                viewModel.resetUpdateState()
                            }
                            negativeButton(R.string.disagree) { dialog ->

                            }
                        }
                    }
                    is Error -> {
                        MaterialDialog(requireContext()).show {
                            title(text = "Lỗi")
                            message(text = event.data)
                            positiveButton(R.string.confirm) { dialog ->

                            }
                            negativeButton(R.string.cancel) { dialog ->
                                navController.popBackStack()
                            }
                        }
                        Log.d("Error", event.data.toString())

                    }
                    is Loading -> {

                    }
                }
            }
        }
    }

    private fun setupLocationPicker() {
        val locationImage = binding.createclassLocation
        locationImage.setOnClickListener {
            val REQUEST_CODE = 300
            if (getLocationPermission()) {
                val intent = Intent(requireContext(), MapsActivity::class.java)
                activity?.startActivityForResult(intent, REQUEST_CODE)
            }


        }
    }

    private fun setupShiftPicker() {
        val materialButtonToggleGroup = binding.toggleButton
        materialButtonToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->

            when (checkedId) {
                R.id.buttonMonday -> setUpPickDow("MONDAY", isChecked)
                R.id.buttonTuesday -> setUpPickDow("TUESDAY", isChecked)
                R.id.buttonWednesday -> setUpPickDow("WEDNESDAY", isChecked)
                R.id.buttonThursday -> setUpPickDow("THURSDAY", isChecked)
                R.id.buttonFriday -> setUpPickDow("FRIDAY", isChecked)
                R.id.buttonSaturday -> setUpPickDow("SATURDAY", isChecked)
                R.id.buttonSunday -> setUpPickDow("SUNDAY", isChecked)
            }
        }
        binding.createCLassTimeStartTextInputLayout.setEndIconOnClickListener {
            val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
            val clockFormat =
                if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build()
            picker.show(requireFragmentManager(), "tag")
            picker.addOnPositiveButtonClickListener {
                binding.createCLassTimeStart.setText("${picker.hour}h:${picker.minute}ph")
                startAt = picker.hour * 3600000 + (picker.minute.toDouble() * 60000)
            }
        }
    }

    private fun setUpPickDow(name: String, checked: Boolean) {
        if (checked)
            dowSet.add(ClassQuest.DayOfWeeks(name))
        else
            dowSet.removeIf {
                it.name == name
            }
    }

    private fun setupDatePicker() {
        binding.createCLassDateStartTextInputLayout.setEndIconOnClickListener {
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(constraintsBuilder.build())
                    .setTitleText("Chọn ngày bắt đầu")
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                dateStart =
                    Instant.ofEpochMilli(datePicker.selection!!).atZone(ZoneId.systemDefault())
                        .toLocalDate()
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formattedString: String = dateStart!!.format(formatter)
                binding.createCLassDateStart.setText(formattedString)
            }

            datePicker.show(requireFragmentManager(), "date picker")
        }
    }

    private fun setupPaymentOptionType() {
        val adapter = ArrayAdapter(requireContext(), R.layout.subject_list_item, paymentOptions)
        val autoCompleteTextView: AutoCompleteTextView? =
            (binding.createCLassPaymentOptionMenu.editText as? AutoCompleteTextView)
        autoCompleteTextView?.setAdapter(adapter)
        autoCompleteTextView!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, rowId ->
                paymentOption = ClassQuest.PaymentOption(
                    name = parent.getItemAtPosition(position) as String,
                    paymentOptionId = (rowId + 1).toString()
                )
            }

    }

    private fun setupSubjectType() {
        val adapter = ArrayAdapter(requireContext(), R.layout.subject_list_item, subjects)
        val autoCompleteTextView: AutoCompleteTextView? =
            (binding.createCLassSubjectMenu.editText as? AutoCompleteTextView)
        autoCompleteTextView?.setAdapter(adapter)
        autoCompleteTextView!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, rowId ->
                subject = ClassQuest.Subject(
                    name = parent.getItemAtPosition(position) as String,
                    subjectId = (rowId + 1).toString()
                )
            }
    }

    private fun getLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        )
            true
        else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
            false
        }

    }

    private fun setupValidation() {
        binding.createCLassName.doOnTextChanged { text, start, before, count ->
            when (StringUtils.checkFormat(text)) {
                0 -> binding.createCLassName.error = "Vui lòng nhập thông tin"
                1 -> binding.createCLassName.error = "Không thể chứa kí tự đặc biệt"
                else -> binding.createCLassName.error = null
            }
        }
        binding.createCLassShortDescription.doOnTextChanged { text, start, before, count ->
            when (StringUtils.checkFormat(text)) {
                0 -> binding.createCLassShortDescription.error = "Vui lòng nhập thông tin"
            }
        }
        binding.createCLassAddress.doOnTextChanged { text, start, before, count ->
            when (StringUtils.checkFormat(text)) {
                0 -> binding.createCLassAddress.error = "Vui lòng nhập thông tin"
            }
        }
        binding.createCLassDateStart.doAfterTextChanged {
            binding.createCLassDateStart.error = null
        }
        binding.createCLassTimeStart.doAfterTextChanged {
            binding.createCLassTimeStart.error = null
        }
        binding.createCLassFee.doAfterTextChanged {
            if (it?.length == 0)
                binding.createCLassFee.error = "Vui lòng nhập thông tin"
            else
                binding.createCLassFee.error = null
        }
        binding.createCLassDuration.doAfterTextChanged {
            if (it?.length == 0)
                binding.createCLassDuration.error = "Vui lòng nhập thông tin"
            else
                binding.createCLassDuration.error = null
        }

    }


    private fun checkModel(): Boolean {
        var valid = true
        if (binding.createCLassName.text.isNullOrBlank()) {
            binding.createCLassName.error = "Vui lòng nhập thông tin"
            valid = false
        }
        if (binding.createCLassShortDescription.text.isNullOrBlank()) {
            binding.createCLassShortDescription.error = "Vui lòng nhập thông tin"
            valid = false
        }
        if (binding.createCLassDateStart.text.isNullOrBlank()) {
            binding.createCLassDateStart.error = "Vui lòng nhập thông tin"
            valid = false
        }
        if (binding.createCLassDuration.text.isNullOrBlank()) {
            binding.createCLassDuration.error = "Vui lòng nhập thông tin"
            valid = false
        }
        if (binding.createCLassTimeStart.text.isNullOrBlank()) {
            binding.createCLassTimeStart.error = "Vui lòng nhập thông tin"
            valid = false
        }
        if (binding.createCLassFee.text.isNullOrBlank()) {
            binding.createCLassFee.error = "Vui lòng nhập thông tin"
            valid = false
        }
        if (binding.createCLassAddress.text.isNullOrBlank()) {
            binding.createCLassAddress.error = "Vui lòng nhập thông tin"
            valid = false
        }
        if (location == null) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Lỗi")
                .setMessage(resources.getString(R.string.supporting_text))
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                }

                .setPositiveButton(resources.getString(R.string.go_to_map)) { dialog, which ->
                    binding.createclassLocation.performClick()
                }
                .show()
            valid = false
        }
        var errorMessage = ""
        Log.d("errorMessage", errorMessage)
        if (paymentOption == null) {
            errorMessage += "\nKiểu thanh toán"
            valid = false
        }
        if (subject == null) {
            errorMessage += "\nMôn học"
            valid = false
        }
        if (dowSet.isEmpty()) {
            errorMessage += "\nBuổi học"
            valid = false
        }
        if (errorMessage.isNotBlank()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Lỗi")
                .setMessage(resources.getString(R.string.supporting_text2) + errorMessage)
                .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->
                }
                .show()
        } else
            if (!valid) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Lỗi")
                    .setMessage(resources.getString(R.string.supporting_text3))
                    .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->
                    }
                    .show()
            }
        return valid

    }
}