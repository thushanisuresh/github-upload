package com.example.buunytoys.model

class ModelOrder {

        var order_id = ""
        var customer = ModelUser()
        var cart: List<ModelCart> = ArrayList<ModelCart>()

    }
