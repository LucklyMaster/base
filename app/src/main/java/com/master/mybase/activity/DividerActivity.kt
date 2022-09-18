package com.master.mybase.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.master.lib.base.BaseVBActivity
import com.master.lib.ext.dp2pxi
import com.master.lib.ext.setSize
import com.master.lib.widget.RecyclerViewDivider
import com.master.lib.widget.ViewHolder
import com.master.mybase.databinding.ActivityDividerBinding

class DividerActivity : BaseVBActivity<ActivityDividerBinding>(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnViewClickListeners(this) { arrayOf(btnHorizontal, btnVertical, btnGrid, btnGrid2) }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnHorizontal -> {
                binding.recyclerView.layoutManager = LinearLayoutManager(
                    this, RecyclerView.HORIZONTAL, false
                )
                binding.recyclerView.addItemDecoration(
                    RecyclerViewDivider(RecyclerView.VERTICAL, dp2pxi(10), Color.RED)
                )
            }
            binding.btnVertical -> {
                binding.recyclerView.layoutManager = LinearLayoutManager(
                    this, RecyclerView.VERTICAL, false
                )
                binding.recyclerView.addItemDecoration(
                    RecyclerViewDivider(RecyclerViewDivider.HORIZONTAL, dp2pxi(10), Color.RED)
                )
            }
            binding.btnGrid -> {
                binding.recyclerView.layoutManager = GridLayoutManager(
                    this, 4, RecyclerView.VERTICAL, false
                )
                binding.recyclerView.addItemDecoration(
                    RecyclerViewDivider(RecyclerViewDivider.BOTH, dp2pxi(10), Color.RED)
                )
            }
            binding.btnGrid2 -> {
                binding.recyclerView.layoutManager = GridLayoutManager(
                    this, 4, RecyclerView.HORIZONTAL, false
                )
                binding.recyclerView.addItemDecoration(
                    RecyclerViewDivider(RecyclerViewDivider.BOTH, dp2pxi(10), Color.RED)
                )
            }
        }
        binding.recyclerView.adapter = Adapter()
    }

    private class Adapter : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(TextView(parent.context).apply {
                setSize(dp2pxi(40), dp2pxi(40))
                setBackgroundColor(Color.BLACK)
                gravity = Gravity.CENTER
                textSize = 18f
                setTextColor(Color.WHITE)
            })
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val textView = holder.itemView as TextView
            textView.text = position.toString()
        }

        override fun getItemCount(): Int {
            return 120
        }
    }
}