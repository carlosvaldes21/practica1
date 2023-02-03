package com.carlosvaldes.practica1.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.carlosvaldes.practica1.R
import com.carlosvaldes.practica1.databinding.ActivityDetailBinding
import com.carlosvaldes.practica1.models.Car
import com.carlosvaldes.practica1.network.CarsApi
import com.carlosvaldes.practica1.network.RetrofitService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDetailBinding

    private lateinit var map: GoogleMap

    private var title = "Réntame"
    private var description = "Puedes rentar este carro en esta sucursal"
    private lateinit var car: Car

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras

        val id = bundle?.getString("id", "0")

        CoroutineScope(Dispatchers.IO).launch {
            var call = RetrofitService.getRetrofit().create(CarsApi::class.java)
                .getCar(id)

            call.enqueue(object: Callback<Car> {
                override fun onResponse(call: Call<Car>, response: Response<Car>) {
                    car = response.body()!!

                    Glide.with(this@DetailActivity).load(response.body()!!.image).into(binding.ivCar)
                    binding.tvCarName.text = response.body()!!.name
                    binding.tvPrice.text = "$" + response.body()!!.price
                    var colors = ""
                    for ( color in response.body()!!.colors ) {
                        colors += "${color}, "
                    }
                    binding.tvColors.text = colors
                    binding.tvWheels.text = "Rin " + response.body()!!.wheels
                    if ( response.body()!!.seating.contains("Piel") ) {
                        Glide.with(this@DetailActivity).load("https://http2.mlstatic.com/D_NQ_NP_2X_696021-MLM48889172170_012022-F.webp").into(binding.ivPiel)
                    }

                    if ( response.body()!!.seating.contains("Tela") ) {
                        Glide.with(this@DetailActivity).load("https://images.milanuncios.com/api/v1/ma-ad-media-pro/images/fccb53b0-61df-40e7-ba64-75a885d77421?rule=hw396_70").into(binding.ivTela)
                    }

                    val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMaps) as SupportMapFragment
                    mapFragment.getMapAsync(this@DetailActivity)
                }

                override fun onFailure(call: Call<Car>, t: Throwable) {
                    Log.d("ERRORES", "No se pudo: ${t.message}")
                }

            })
        }


    }

    /**
     * Implement members to onMapReadyCallback
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker(car.coordinates.latitude, car.coordinates.longitude)
    }

    /**
     * Function to create a marker
     * @param String latitude
     * @param String longitude
     */
    fun createMarker(latitude: String, longitude: String){
        val coordinates = LatLng(latitude.toDouble(), longitude.toDouble())
        val marker = MarkerOptions()
            .position(coordinates)
            .title("Rentar ${car.name}")
            .snippet("Puedes rentar tu próximo ${car.name} en esta sucursal")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))


        map.addMarker(marker)

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            4000,
            null
        )
    }
}