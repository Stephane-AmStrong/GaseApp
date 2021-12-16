package com.amstrong.gaseapp.data.response

import java.util.*

data class UserCreateDto(
        val name: String,
        val workstation_id: String,
        val email: String,
        val phone_number: String?,
        var open_id: String?,
        var updated_at: Date?,
        val disabled_at: Date?,
)