package com.master.mybase.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gyf.immersionbar.ktx.immersionBar
import com.master.lib.base.BaseVBActivity
import com.master.lib.dialog.BottomSheetDialog
import com.master.lib.enums.SheetState
import com.master.lib.ext.Log
import com.master.lib.ext.inflater
import com.master.lib.widget.RecyclerViewDivider
import com.master.lib.widget.ViewHolder
import com.master.mybase.R
import com.master.mybase.databinding.ActivitySheetLayoutBinding

class SheetLayoutActivity : BaseVBActivity<ActivitySheetLayoutBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar {
            transparentBar()
        }
        binding.sheetLayout.setState(SheetState.DISPLAY, false)
        binding.sheetLayout.addOnStateChangedListener {
            Log.d("state = $it")
        }
        binding.sheetLayout.addOnScrollListener {
            binding.btnSheetDialog.height = (binding.btnSheetDialog.height + it).toInt()
        }
        binding.btnSheetDialog.setOnClickListener {
            BottomSheetDialog(this, R.layout.dialog_bottom_sheet).setAnimatorSpeed(4f)
                .setExpandHeightRatio(0.8f)
                .setState(SheetState.EXPAND)
                .setAmount(0.7f)
                .show()
        }
        binding.recyclerView.addItemDecoration(RecyclerViewDivider())
        binding.recyclerView.adapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(parent.inflater(android.R.layout.simple_list_item_1))
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.setText(android.R.id.text1, position.toString())
            }

            override fun getItemCount(): Int {
                return 100
            }
        }
        binding.btnFold.setOnClickListener {
            binding.sheetLayout.setState(SheetState.DISPLAY, true)
        }
    }

}