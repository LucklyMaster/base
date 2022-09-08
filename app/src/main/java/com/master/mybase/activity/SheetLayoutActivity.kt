package com.master.mybase.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gyf.immersionbar.ktx.immersionBar
import com.master.lib.base.BaseVBActivity
import com.master.lib.dialog.BottomSheetDialog
import com.master.lib.ext.Log
import com.master.lib.ext.inflater
import com.master.lib.view.SheetLayout
import com.master.lib.widget.ViewHolder
import com.master.mybase.databinding.ActivitySheetLayoutBinding

class SheetLayoutActivity : BaseVBActivity<ActivitySheetLayoutBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar {
            transparentBar()
        }
        binding.sheetLayout.setState(SheetLayout.STATE_EXPAND_HALF, false)
        binding.sheetLayout.setOnStateChangedListener {
            Log.d("state = $it")
        }
        binding.btnSheet.setOnClickListener {
            BottomSheetDialog(this).show()
        }
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
            binding.sheetLayout.setState(SheetLayout.STATE_EXPAND_HALF, true)
        }
    }

}