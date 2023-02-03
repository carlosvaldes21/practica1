package com.carlosvaldes.practica1.models

data class Car(
    var id: String,
    var name:String,
    var image:String,
    var colors:ArrayList<String>,
    var price: String,
    var seating : ArrayList<String>,
    var wheels: String,
    var coordinates: Coordinates
)

data class Coordinates(
    var latitude: String,
    var longitude:String
)

