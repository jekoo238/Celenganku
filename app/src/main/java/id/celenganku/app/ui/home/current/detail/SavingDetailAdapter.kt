package id.celenganku.app.ui.home.current.detail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.celenganku.app.databinding.ItemSavingDetailLogBinding
import id.celenganku.app.model.SavingsLogEntity
import id.celenganku.app.utils.format
import id.celenganku.app.utils.formatNumberTok

class SavingDetailAdapter: ListAdapter<SavingsLogEntity, SavingDetailAdapter.SavingDetailViewHolder>(
        object : DiffUtil.ItemCallback<SavingsLogEntity>(){
            override fun areItemsTheSame(oldItem: SavingsLogEntity, newItem: SavingsLogEntity): Boolean {
                return oldItem.logId == newItem.logId
            }

            override fun areContentsTheSame(oldItem: SavingsLogEntity, newItem: SavingsLogEntity): Boolean {
                return oldItem == newItem
            }
        }
) {

    inner class SavingDetailViewHolder(private val binding: ItemSavingDetailLogBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: SavingsLogEntity){
            with(binding){
                if (item.notes.isNullOrEmpty().not()){
                    note.text = item.notes
                    note.visibility = View.VISIBLE
                }
                date.text = item.timestamp.format("dd MMMM yyyy ● HH:mm")
                if (item.increase) {
                    nominal.setTextColor(Color.parseColor("#4caf50"))
                    nominal.text = "+ ${formatNumberTok(item.nominal)}"
                } else {
                    nominal.setTextColor(Color.parseColor("#d32f2f"))
                    nominal.text = "- ${formatNumberTok(item.nominal)}"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavingDetailViewHolder {
        val binding = ItemSavingDetailLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavingDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavingDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}