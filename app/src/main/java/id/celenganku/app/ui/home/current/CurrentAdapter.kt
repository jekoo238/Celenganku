package id.celenganku.app.ui.home.current

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import id.celenganku.app.databinding.ItemSavingBinding
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.ui.home.HomeFragmentDirections
import id.celenganku.app.utils.formatNumber
import kotlin.math.roundToInt

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
                title.text = item.title
                if (item.image != null){
                    Picasso.get().load(Uri.parse(item.image)).into(savingImage)
                }

                target.text = formatNumber(item.target)

                targetProgressBar.apply {
                    max = item.target
                    progress = item.collected
                }
                val percent = ((item.collected.toFloat()/item.target.toFloat())*100F).roundToInt()
                percentage.text = "$percent%"

                val suffix = when (item.fillingType) {
                    0L -> "Perhari"
                    1L -> "Perminggu"
                    else -> "Perbulan"
                }
                targetPerDay.text = "${formatNumber(item.targetPerDay)} $suffix"

                val estimationDay = ((item.target-item.collected)/item.targetPerDay)

                estimation.text = "Estimasi : $estimationDay ${item.fillingTypeText} Lagi"

                root.setOnClickListener {
                    val direction = HomeFragmentDirections.actionMainFeatureFragmentToSavingDetailFragment(item.id!!, item.title)
                    it.findNavController().navigate(direction)
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