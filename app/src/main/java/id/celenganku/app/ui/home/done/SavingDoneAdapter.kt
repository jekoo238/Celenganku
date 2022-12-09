package id.celenganku.app.ui.home.done

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.celenganku.app.databinding.ItemSavingDoneBinding
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.ui.home.HomeFragmentDirections
import id.celenganku.app.utils.formatNumber
import java.util.concurrent.TimeUnit

class SavingDoneAdapter: ListAdapter<SavingsEntity, SavingDoneAdapter.HistoryViewHolder>(
        object : DiffUtil.ItemCallback<SavingsEntity>(){
            override fun areItemsTheSame(oldItem: SavingsEntity, newItem: SavingsEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SavingsEntity, newItem: SavingsEntity): Boolean {
                return oldItem == newItem
            }
        }
) {

    inner class HistoryViewHolder(private val binding: ItemSavingDoneBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: SavingsEntity){
            with(binding){
                title.text = item.title
                if (item.image != null){
                    image.setImageURI(item.image.toUri())
                }

                target.text = formatNumber(item.target)
                item.dateFinished?.let { finished ->
                    val day = TimeUnit.MILLISECONDS.toDays(finished-item.dateCreated)
                    completeDay.text = "Tercapai Dalam Waktu $day Hari"
                }
                card.setOnClickListener {
                    val direction = HomeFragmentDirections.actionMainFeatureFragmentToHistoryDetailFragment(item.id!!, item.title)
                    it.findNavController().navigate(direction)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemSavingDoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}