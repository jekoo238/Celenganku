package id.celenganku.app.ui.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import id.celenganku.app.databinding.ItemSavingBinding
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.ui.main.MainFeatureFragmentDirections
import id.celenganku.app.utils.formatNumber
import java.util.*

class CurrentAdapter: ListAdapter<SavingsEntity, CurrentAdapter.CurrentViewHolder>(
    object : DiffUtil.ItemCallback<SavingsEntity>(){
        override fun areItemsTheSame(oldItem: SavingsEntity, newItem: SavingsEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SavingsEntity, newItem: SavingsEntity): Boolean {
            return oldItem == newItem
        }
    }
) {

    inner class CurrentViewHolder(private val binding: ItemSavingBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: SavingsEntity){
            with(binding){
                root.transitionName = "transition_item_${item.id}"
                title.text = item.title
                if (item.image != null){
                    Picasso.get().load(Uri.parse(item.image)).into(image)
                }

                target.text = formatNumber(item.target)

                collected.text = formatNumber(item.collected)
                progressBar.apply {
                    max = item.target
                    progress = item.collected
                }
                targerPerDay.text = "${formatNumber(item.targetPerDay)} / Hari"
                val estimationDay = ((item.target-item.collected)/item.targetPerDay)
                estimation.text = "$estimationDay Hari Lagi"
                root.setOnClickListener {
                    val direction = MainFeatureFragmentDirections.actionMainFeatureFragmentToSavingDetailFragment(item.id!!, item.title)
                    val extra = FragmentNavigatorExtras(it to "transition_detail_savings")
                    it.findNavController().navigate(direction, extra)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentViewHolder {
        val binding = ItemSavingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}