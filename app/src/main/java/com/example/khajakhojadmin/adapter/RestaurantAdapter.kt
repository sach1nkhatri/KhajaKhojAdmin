package com.example.khajakhojadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.khajakhojadmin.R
import com.example.khajakhojadmin.model.Restaurant
import com.google.firebase.database.FirebaseDatabase

class RestaurantAdapter(private var restaurantList: MutableList<Restaurant>) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_sample_item, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.bind(restaurant)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    fun deleteRestaurant(position: Int) {
        val restaurant = restaurantList[position]
        restaurantList.removeAt(position)
        notifyItemRemoved(position)
        deleteRestaurantFromFirebase(restaurant.id)
    }

    private fun deleteRestaurantFromFirebase(id: String?) {
        id?.let {
            val database = FirebaseDatabase.getInstance().reference.child("restaurants").child(it)
            database.removeValue()
        }
    }

    fun updateRestaurantList(newList: List<Restaurant>) {
        restaurantList = newList.toMutableList()
        notifyDataSetChanged()
    }

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val restaurantName: TextView = itemView.findViewById(R.id.RestaurantName)
        private val restaurantCuisine: TextView = itemView.findViewById(R.id.ResturantCuisine)
        private val restaurantDistance: TextView = itemView.findViewById(R.id.ResturantDistance)
        private val restaurantTime: TextView = itemView.findViewById(R.id.ResturantTime)
        private val restaurantLocation: TextView = itemView.findViewById(R.id.restaurantAddress)
        private val restaurantRating: TextView = itemView.findViewById(R.id.restaurantRating)

        fun bind(restaurant: Restaurant) {
            restaurantName.text = restaurant.name
            restaurantCuisine.text = restaurant.cuisineType
            restaurantDistance.text = "0.5 km"
            restaurantTime.text = "${restaurant.openTime} - ${restaurant.closeTime}"
            restaurantLocation.text = restaurant.address
            restaurantRating.text = restaurant.rating.toString()
        }
    }
}

