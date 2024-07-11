package com.example.khajakhojadmin.activity

import android.os.Bundle
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khajakhojadmin.R
import com.example.khajakhojadmin.adapter.RestaurantAdapter
import com.example.khajakhojadmin.model.Restaurant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeleteRestaurant : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var database: DatabaseReference
    private lateinit var restaurantList: MutableList<Restaurant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete_restaurant)

        val searchView: SearchView = findViewById(R.id.searchView)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        restaurantAdapter = RestaurantAdapter(mutableListOf()) // Initialize with empty list

        ////////

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                restaurantAdapter.deleteRestaurant(position)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)


        ////////

        recyclerView.adapter = restaurantAdapter

        database = FirebaseDatabase.getInstance().reference.child("restaurants")
        fetchRestaurantsFromFirebase()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                filterRestaurants(newText)
                return true
            }
        })
    }

    private fun fetchRestaurantsFromFirebase() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    restaurantList = mutableListOf()
                    for (restaurantSnapshot in snapshot.children) {
                        val restaurant = restaurantSnapshot.getValue(Restaurant::class.java)
                        restaurant?.id = restaurantSnapshot.key.toString()
                        restaurant?.let {
                            restaurantList.add(it)
                        }
                    }
                    restaurantAdapter.updateRestaurantList(restaurantList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


//    private fun filterRestaurants(query: String?) {
//        val filteredList = if (query.isNullOrEmpty()) {
//            restaurantList
//        } else {
//            restaurantList.filter {
//                it.name.contains(query, ignoreCase = true) ||
//                        it.cuisineType.contains(query, ignoreCase = true)
//            }
//        }
//        restaurantAdapter.updateRestaurantList(filteredList)
//    }
}
