package se.materka.conflux.ui.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_list_item.view.*
import se.materka.conflux.R
import se.materka.conflux.databinding.FragmentListItemBinding
import se.materka.conflux.db.entity.Station


/**
 * Copyright 2017 Mattias Karlsson

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class ListAdapter(val onItemClicked: (station: Station) -> Unit?,
                  val onItemLongClicked: (station: Station) -> Unit?) : RecyclerView.Adapter<ViewHolder>() {
    companion object {
        private val FOOTER_VIEW = 1
    }

    private var selected: Station? = null
    private var parent: RecyclerView? = null

    private var stations: List<Station> = mutableListOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        parent = recyclerView
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        try {
            if (holder is StationViewHolder) {
                stations[position].let { station ->
                    holder.bind(station)
                    holder.itemView.selected?.visibility = if (station.id == selected?.id) View.VISIBLE else View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return if (stations.isEmpty()) 1 else stations.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == stations.size) FOOTER_VIEW else R.layout.fragment_list_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == FOOTER_VIEW) {
            val view = inflater.inflate(R.layout.fragment_list_footer, parent, false)
            FooterViewHolder(view)
        } else {
            val binding: FragmentListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
            StationViewHolder(binding)
        }
    }

    fun updateDataSet(items: List<Station>) {
        stations = items
        notifyDataSetChanged()
    }

    private fun select(position: Int) {
        for (i in 0 until itemCount) {
            parent?.findViewHolderForAdapterPosition(i)?.itemView?.selected?.visibility = View.GONE
        }
        selected = stations[position]
        notifyDataSetChanged()
    }

    inner class FooterViewHolder(itemView: View) : ViewHolder(itemView)

    inner class StationViewHolder(private val listItemBinding: FragmentListItemBinding) : ViewHolder(listItemBinding.root) {

        init {
            listItemBinding.root.setOnClickListener {
                onItemClicked(stations[adapterPosition])
                select(adapterPosition)
            }
            listItemBinding.root.setOnLongClickListener {
                onItemLongClicked(stations[adapterPosition])
                true
            }
        }

        fun bind(station: Station) {
            listItemBinding.station = station
            listItemBinding.executePendingBindings()
        }
    }

}