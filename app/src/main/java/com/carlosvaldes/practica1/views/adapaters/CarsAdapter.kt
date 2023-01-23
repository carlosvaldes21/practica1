package com.carlosvaldes.practica1.views.adapaters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.carlosvaldes.practica1.databinding.CarElementBinding
import com.carlosvaldes.practica1.models.Car
import com.carlosvaldes.practica1.views.activities.MainActivity

class CarsAdapter(private var context: Context, private var cars: ArrayList<Car>): RecyclerView.Adapter<CarsAdapter.ViewHolder>() {
    class ViewHolder( view: CarElementBinding ) : RecyclerView.ViewHolder(view.root) {
        //PASO 1: Primero debemos asignar las variables para que el onBindviewHolder las pueda encontrar y usar
        val ivCar = view.ivCar
        val tvCarName = view.tvCarName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //PASO 3: Aqui ya inflamos la vista
        val binding = CarElementBinding.inflate(LayoutInflater.from(context))

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //PASO 2:
        //Aqui es donde se asigna cada elemento del car_element
        Glide.with(context).load(cars[position].image).into(holder.ivCar)

        holder.tvCarName.text = cars[position].name

        holder.itemView.setOnClickListener{
            (context as? MainActivity)?.selectedCar(cars[position])
        }
    }
}