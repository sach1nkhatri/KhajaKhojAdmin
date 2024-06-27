package com.example.khajakhojadmin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge

class DeleteRestaurant : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete_restaurant)

        val restaurantList = listOf(
            Restaurant("Chiya Hub", "Multi Cuisine", "0.5km", "10am to 10pm", "Ganeshowr, Marga", "4.5*", R.drawable.chiyahub),
            Restaurant("Chiya Hub", "Multi Cuisine", "0.5km", "10am to 10pm", "Ganeshowr, Marga", "4.5*", R.drawable.chiyahub),
            Restaurant("Pasta Palace", "Italian", "1.2km", "11am to 11pm", "Main Street", "4.2*", R.drawable.chiyahub),
            Restaurant("Burger Barn", "American", "0.8km", "9am to 9pm", "Central Avenue", "4.7*", R.drawable.chiyahub),
            Restaurant("Sushi Spot", "Japanese", "2.0km", "12pm to 12am", "Tokyo Lane", "4.6*", R.drawable.chiyahub),
            Restaurant("Taco Town", "Mexican", "1.5km", "10am to 10pm", "Fiesta Road", "4.3*",R.drawable.chiyahub),
            Restaurant("Curry Corner", "Indian", "1.0km", "11am to 11pm", "Spice Street", "4.8*", R.drawable.chiyahub),
            Restaurant("Pizza Planet", "Italian", "2.5km", "10am to 10pm", "Cheese Avenue", "4.4*", R.drawable.chiyahub),
            Restaurant("BBQ Bliss", "American", "3.0km", "12pm to 12am", "Grill Road", "4.1*", R.drawable.chiyahub),
            Restaurant("Noodle Nest", "Chinese", "0.7km", "11am to 11pm", "Dragon Street", "4.9*", R.drawable.chiyahub),
            Restaurant("Vegan Vibes", "Vegan", "1.3km", "10am to 10pm", "Greenway", "4.5*", R.drawable.chiyahub),
            Restaurant("Seafood Shack", "Seafood", "2.2km", "12pm to 12am", "Ocean Drive", "4.0*", R.drawable.chiyahub),
            Restaurant("Bagel Bakery", "Bakery", "0.9km", "7am to 7pm", "Bagel Boulevard", "4.6*", R.drawable.chiyahub),
            Restaurant("Steak House", "Steakhouse", "3.5km", "5pm to 11pm", "Meat Street", "4.3*", R.drawable.chiyahub),
            Restaurant("Salad Stop", "Healthy", "1.1km", "10am to 10pm", "Fresh Lane", "4.7*", R.drawable.chiyahub),
            Restaurant("Dim Sum Delight", "Chinese", "2.3km", "11am to 11pm", "Dumpling Avenue", "4.8*", R.drawable.chiyahub),
            Restaurant("Soup Shack", "Healthy", "1.2km", "10am to 10pm", "Soup Lane", "4.5*", R.drawable.chiyahub),
            Restaurant("Burger Barn", "American", "1.0km", "11am to 11pm", "Burger Avenue", "4.7*", R.drawable.chiyahub),
            Restaurant("Sushi Spot", "Japanese", "0.8km", "12pm to 12am", "Sushi Boulevard", "4.6*", R.drawable.chiyahub),
            Restaurant("Taco Town", "Mexican", "1.5km", "10am to 10pm", "Taco Avenue", "4.3*", R.drawable.chiyahub)
            // Add more Restaurant objects here
        )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = RestaurantAdapter(restaurantList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }
}
