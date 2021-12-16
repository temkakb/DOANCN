package com.example.doancn.Fragments.MyClass.more

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.ClassViewModel
import com.example.doancn.IClassActivity
import com.example.doancn.R
import com.example.doancn.databinding.FragmentItemListDialogListDialogBinding
import com.example.doancn.databinding.FragmentItemListDialogListDialogItemBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ItemListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentItemListDialogListDialogBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var iClassActivity: IClassActivity
    private val classViewModel: ClassViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false)
        recyclerView = binding.list
        if(classViewModel.role == "TEACHER")
            adapter = ItemAdapter(BottomSheetItem.getItemTeacher())
        else
            adapter = ItemAdapter(BottomSheetItem.getItemStudent())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        return binding.root

    }

    private inner class ViewHolder(binding: FragmentItemListDialogListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val text: TextView = binding.text
        val icon: ImageView = binding.imageView

    }


    private inner class ItemAdapter(private val list: List<BottomSheetItem>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            Log.d("ItemAdapter", "onCreateViewHolder : $list")
            return ViewHolder(
                FragmentItemListDialogListDialogItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            iClassActivity = requireActivity() as IClassActivity
            super.onAttachedToRecyclerView(recyclerView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (list[position].type == 1 || list[position].type == 9 )
                holder.text.setTextColor(getColor(requireContext(), R.color.red))
            holder.text.text = list[position].name
            holder.icon.setImageResource(list[position].icon)
            holder.itemView.setOnClickListener {
                iClassActivity.handleBottomSheetItem(list[position])
            }

        }

        override fun getItemCount(): Int {
            return list.size
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

class BottomSheetItem(val type: Int, val icon: Int, val name: String) {
    companion object {
        fun getItemTeacher(): List<BottomSheetItem> {
            return arrayListOf(
                BottomSheetItem(1, R.drawable.delete, "Hủy lớp học"),
                BottomSheetItem(2, R.drawable.ic_baseline_update_24, "Cập nhật thông tin"),
                BottomSheetItem(3, R.drawable.ic_baseline_info_24, "Tình trạng buổi học")

            )
        }
        fun getItemStudent(): List<BottomSheetItem> {
            return arrayListOf(
                BottomSheetItem(9, R.drawable.ic_baseline_exit_to_app_24, "Thoát lớp học")

            )
        }
    }
}