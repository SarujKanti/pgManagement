package com.skd.pgmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class GenericAdapter<T , VB: ViewBinding>(

    private var data: MutableList<T>,
    private val bind: (VB, T, Int) -> Unit,
    private val inflater: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<GenericAdapter.GenericViewHolder<VB>>() {

    class GenericViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<VB> {
        val binding = inflater(LayoutInflater.from(parent.context), parent, false)
        return GenericViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder<VB>, position: Int) {
        bind(holder.binding, data[position], position)
    }

    override fun getItemCount(): Int = data.size

    // Method to update a particular item
    fun updateItem(position: Int, newItem: T) {
        if (position in data.indices) {
            data[position] = newItem
            notifyItemChanged(position)
        }
    }

    fun updateData(newData: List<T>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun getUpdatedData(): List<T> {
        return data.toList() // Returns a copy of the current data
    }

    fun setData(newData: List<T>) {
        updateData(newData)
    }

    fun setDataFromSingleItem(item: T) {
        data.clear()
        data.add(item)
        notifyDataSetChanged()
    }

    fun updateItemSilently(position: Int, newItem: T) {
        if (position in data.indices) {
            data[position] = newItem
            // Do NOT call notifyItemChanged(position)
        }
    }

}
