package com.example.khajakhojadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import android.widget.Filter
import android.widget.Filterable
import java.util.Locale

class RestaurantAdapter(private var restaurantList: List<Restaurant>) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>(), Filterable {

    private var filteredRestaurantList: List<Restaurant> = restaurantList

    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantImage: ShapeableImageView = view.findViewById(R.id.restuarantImage)
        val restaurantName: TextView = view.findViewById(R.id.RestaurantName)
        val restaurantCuisine: TextView = view.findViewById(R.id.ResturantCuisine)
        val restaurantDistance: TextView = view.findViewById(R.id.ResturantDistance)
        val restaurantTime: TextView = view.findViewById(R.id.ResturantTime)
        val restaurantAddress: TextView = view.findViewById(R.id.restaurantAddress)
        val restaurantRating: TextView = view.findViewById(R.id.restaurantRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_list_view, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = filteredRestaurantList[position]
//        holder.restaurantImage.setImageResource(restaurant.imageResId)
        holder.restaurantName.text = restaurant.name
//        holder.restaurantCuisine.text = restaurant.cuisine
//        holder.restaurantTime.text = restaurant.time
        holder.restaurantAddress.text = restaurant.address
    }

    override fun getItemCount() = filteredRestaurantList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase(Locale.ROOT)
                val results = FilterResults()
                results.values = if (query.isNullOrEmpty()) {
                    restaurantList
                } else {
                    restaurantList.filter {
                        it.name.lowercase(Locale.ROOT).contains(query) ||
//                                it.cuisine.lowercase(Locale.ROOT).contains(query) ||
                                it.address.lowercase(Locale.ROOT).contains(query)
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredRestaurantList = results?.values as List<Restaurant>
                notifyDataSetChanged()
            }
        }
    }
}

