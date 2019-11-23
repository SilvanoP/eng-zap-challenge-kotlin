package br.com.desafio.grupozap.features.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.desafio.grupozap.R
import br.com.desafio.grupozap.features.common.RealStateView
import br.com.desafio.grupozap.utils.BusinessType
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_realstates.view.*

class RealStatesListAdapter(private val realStatesList: MutableList<RealStateView>,
                            private val listener: AdapterClickListener):
    RecyclerView.Adapter<RealStatesListAdapter.RealStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealStateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_realstates, parent, false)
        return RealStateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return realStatesList.size
    }

    override fun onBindViewHolder(holder: RealStateViewHolder, position: Int) {
        val realState = realStatesList[position]
        holder.bind(realState)
    }

    fun addItems(newList:List<RealStateView>) {
        val pos = realStatesList.size
        realStatesList.addAll(newList)
        notifyItemRangeChanged(pos, newList.size)
    }

    inner class RealStateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(realState: RealStateView) {
            //TODO USE PLACEHOLDER WHEN NOT AVAILABLE
            val imageUri = realState.images[0]
            Picasso.get().load(imageUri)
                .into(itemView.itemPhotoImage)

            var price = realState.price
            var descPrice = itemView.context.resources.getString(R.string.price_desc_sale).format(realState.price, realState.yearlyIptu)
            if (realState.businessType == BusinessType.RENTAL.toString()) {
                val monthlyCondoFee = realState.monthlyCondoFee?.toInt()?:0
                price = realState.rentalTotalPrice + monthlyCondoFee
                descPrice = itemView.context.resources.getString(R.string.price_desc_rental)
                    .format(realState.rentalTotalPrice, realState.monthlyCondoFee, realState.yearlyIptu)
            }
            val priceFull = itemView.context.resources.getString(R.string.price_full).format(price)
            itemView.itemFullPriceText.text = priceFull
            itemView.itemDescPriceText.text = descPrice

            val description = itemView.context.resources.getString(R.string.real_state_details)
                .format(realState.usableAreas, realState.bedrooms)
            itemView.itemDescriptionText.text = description

            itemView.itemLocationText.text = "%s - %s".format(realState.neighborhood, realState.city)
        }

        override fun onClick(v: View?) {
            listener.onItemSelected(realStatesList[adapterPosition])
        }
    }

    interface AdapterClickListener {
        fun onItemSelected(realState: RealStateView)
    }
}