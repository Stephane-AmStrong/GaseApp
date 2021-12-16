package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Employee(
//        val id: String,
//        val name: String,
//        val email: String?,
//        val phone_number: String,
//        val stores: List<String>,
//        val is_owner: Boolean,
//        val created_at: Date?,
//        val updated_at: Date,
//        val deleted_at: Date?,
//        val discriminator: String?,
//
//        val workstation_id: String?,
//
//        val workstation: Workstation?

        val created_at: Date,
        var deleted_at: Date?,
        val discriminator: String,
        var email: String,
        var open_id: String,
        val id: String,
        val is_owner: Boolean,
        var name: String,
        var phone_number: String,
        var stores: List<String>,
        var updated_at: Date?,
        var disabled_at: Date?,
        var workstation_id: String,
        var workstation: Workstation,
        var isSelected: Boolean = false,
)