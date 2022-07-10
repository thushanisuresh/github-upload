package com.example.buunytoys.utility

import com.example.buunytoys.model.ModelUser

class Tools {
    companion object{
        fun get_signed_in_user(): ModelUser? {
            return try {
                val allCustomer: List<ModelUser> = ModelUser.listAll(ModelUser::class.java)
                    ?: return null

                if (allCustomer.isEmpty()) {
                    null
                }else allCustomer[0]
            }catch (e: Exception){
                null
            }
        }
    }
}