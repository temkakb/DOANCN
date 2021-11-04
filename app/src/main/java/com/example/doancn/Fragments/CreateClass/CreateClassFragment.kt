package com.example.doancn.Fragments.CreateClass

import android.Manifest
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
import androidx.lifecycle.Observer
import com.example.doancn.DI.DataState
import com.example.doancn.Models.classModel.ClassQuest
import com.example.doancn.R
import com.example.doancn.Utilities.StringUtils
import com.example.doancn.databinding.CreateClassFragmentBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CreateClassFragment : Fragment() {

    companion object {
        fun newInstance() = CreateClassFragment()
    }

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1

    @ExperimentalCoroutinesApi
    private val viewModel: CreateClassViewModel by activityViewModels()
    private var _binding: CreateClassFragmentBinding? = null
    private val binding get() = _binding!!
    private val dowSet = HashSet<ClassQuest.DayOfWeeks>()

    //
    private var paymentOption: ClassQuest.PaymentOption? = null
    private var location: ClassQuest.Location? = null
    private var dateStart: LocalDate? = null
    private var subject: ClassQuest.Subject? = null
    private lateinit var shifts: ArrayList<ClassQuest.Shift>
    private var startAt: Double = 0.0
    private var duration: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateClassFragmentBinding.inflate(inflater, container, false)
        viewModel.selectedItem.observe(viewLifecycleOwner, Observer {
            location = it

        })
        viewModel.createClassResponse.observe(viewLifecycleOwner, Observer {

            when (it) {
                is DataState.Success -> {
                    showDialog("Thành công", it.data);
                }
                is DataState.Error -> {
                    if (it.data.isNotBlank()) showDialog("Lịch học bị trùng khớp", it.data)
                }
                is DataState.Loading -> TODO()
            }

        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSubjectType()
        setupPaymentOptionType()
        setupDatePicker()
        setupShiftPicker()
        setupLocationPicker()
        setupValidation()
        //viewModel.getSubject()

    }

    override fun onResume() {
        super.onResume()

        binding.createClassBtn.setOnClickListener {

            if (checkModel()) {
                shifts = ArrayList()
                dowSet.forEach {
                    shifts?.add(
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
                    shifts = shifts!!
                )
                Log.d("createClassroom", classRoom.toString())
                createClassroom(classRoom)

            }


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


    private fun showDialog(title: String, data: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(data)
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
            }
            .setPositiveButton("Xác nhận") { dialog, which ->
            }
            .show()
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

    @ExperimentalCoroutinesApi
    private fun createClassroom(classRoom: ClassQuest) {
        viewModel.createClassroom(classRoom)
    }

    private fun setupLocationPicker() {
        val locationImage = binding.createclassLocation
        locationImage.setOnClickListener {
            val REQUEST_CODE = 300
            getLocationPermission()
            val intent = Intent(requireContext(), MapsActivity::class.java)
            activity?.startActivityForResult(intent, REQUEST_CODE)

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
        val pickTimeButton = binding.createCLassPickTimeBtn
        pickTimeButton.setOnClickListener {
            val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
            val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build()
            picker.show(requireFragmentManager(), "tag")
            picker.addOnPositiveButtonClickListener {
                binding.createCLassTimeStart.setText("${picker.hour}h:${picker.minute}m")
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
        val button = binding.createCLassPickDateBtn
        button.setOnClickListener {
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
                        .toLocalDate();
                binding.createCLassDateStart.setText(dateStart.toString())
            }

            datePicker.show(requireFragmentManager(), "date picker")
        }
    }

    private fun setupPaymentOptionType() {
        val items = resources.getStringArray(R.array.option);
        val adapter = ArrayAdapter(requireContext(), R.layout.subject_list_item, items)
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
//        var items = ArrayList<String>()
//        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
//            when (dataState) {
//                is DataState.Success -> dataState.data.forEach {
//                    items.add(StringUtils.subjectMapper(it.name))
//                }
//            }
//        })
        val items = resources.getStringArray(R.array.Subjects);
        Log.d("setupSubjectType", items.toString())
        val adapter = ArrayAdapter(requireContext(), R.layout.subject_list_item, items)
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

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        )
            return
        else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}