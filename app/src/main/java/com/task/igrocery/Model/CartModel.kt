package com.task.igrocery.Model

class CartModel {
    var id = 0
    var image: String
    var name: String
    var price: String
    var quantity: String
    var total: String

    internal constructor(image: String, name: String, price: String, quantity: String,total: String) {
        this.image = image
        this.name = name
        this.price = price
        this.quantity = quantity
        this.total = total
    }

    internal constructor(id: Int, image: String, name: String, price: String, quantity: String, total: String) {
        this.id = id
        this.image = image
        this.name = name
        this.price = price
        this.quantity = quantity
        this.total = total
    }
}