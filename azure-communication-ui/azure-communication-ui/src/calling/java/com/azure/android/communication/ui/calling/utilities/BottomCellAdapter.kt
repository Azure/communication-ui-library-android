// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R

internal class BottomCellAdapter(context: Context) : RecyclerView.Adapter<BottomCellViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private var bottomCellItems: List<BottomCellItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomCellViewHolder {
        val view: View =
            mInflater.inflate(R.layout.azure_communication_ui_bottom_drawer_cell, parent, false)
        return BottomCellViewHolder(view)
    }

    override fun onBindViewHolder(holder: BottomCellViewHolder, position: Int) {
        val item: BottomCellItem = bottomCellItems[position]
        holder.setCellData(item)
    }

    override fun getItemCount(): Int {
        return bottomCellItems.size
    }

    fun setBottomCellItems(bottomCellItems: List<BottomCellItem>) {
        this.bottomCellItems = bottomCellItems
    }

    fun enableBottomCellItem(bottomCellItemName: String) {
        for (bottomCellItem in bottomCellItems) {
            bottomCellItem.enabled = bottomCellItem.title == bottomCellItemName
        }
        super.notifyDataSetChanged()
    }
}
