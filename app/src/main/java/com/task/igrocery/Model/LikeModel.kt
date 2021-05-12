package com.task.igrocery.Model

class LikeModel {
    var id = 0
    var image: String
    var name: String
    var price: String
    var status: String

    internal constructor(
        image: String,
        name: String,
        price: String,
        status: String
    ) {
        this.image = image
        this.name = name
        this.price = price
        this.status = status
    }

    internal constructor(
        id: Int,
        image: String,
        name: String,
        price: String,
        status: String
    ) {
        this.id = id
        this.image = image
        this.name = name
        this.price = price
        this.status = status
    }
}