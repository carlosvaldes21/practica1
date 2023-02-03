package com.carlosvaldes.practica1.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.carlosvaldes.practica1.R
import com.carlosvaldes.practica1.databinding.ActivityMainBinding
import com.carlosvaldes.practica1.models.Car
import com.carlosvaldes.practica1.network.CarsApi
import com.carlosvaldes.practica1.network.RetrofitService
import com.carlosvaldes.practica1.views.adapaters.CarsAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)


        //Utilizamos corutinas para no bloquear el hilo principal
        CoroutineScope(Dispatchers.IO).launch {

            //Debemos crear una instancia de retrofit y mandarle nuestra API
            //Al mismo tiempo llamamos a nuestro método
            val call = RetrofitService.getRetrofit().create(CarsApi::class.java)
                .getCars("cars")

            //Lo metemos a la cola de las llamadas en espera
            call.enqueue(object : Callback<ArrayList<Car>>{

                //Aqui debemos implementar dos métodos, el onResponse
                // y onFailure, para saber si fue correcto o no
                override fun onResponse(
                    call: Call<ArrayList<Car>>,
                    response: Response<ArrayList<Car>>
                ) {
                    Log.d("CORRECTO", "${response.body()!!}")
                    //Si fue correcto, asignamos layoutManager al recycler view
                    binding.rvCars.layoutManager = LinearLayoutManager(this@MainActivity)
                    //Asignamos el adaptador
                    binding.rvCars.adapter = CarsAdapter(this@MainActivity, response.body()!!)
                }

                override fun onFailure(call: Call<ArrayList<Car>>, t: Throwable) {
                    Log.d("ERRORES", "Error: ${t.message}")
                    Toast.makeText(this@MainActivity, "No pudimos cargar la información ${t.message}", Toast.LENGTH_LONG)
                        .show()
                }

            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut();
                showAuth()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun selectedCar(car: Car) {
        val params = Bundle().apply {
            putString("id", car.id)
        }

        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtras(params)
        }

        startActivity(intent)
    }

    private fun showAuth()
    {
        val authIntent = Intent(this, AuthActivity::class.java).apply {}

        startActivity(authIntent)
        finish()
    }
}