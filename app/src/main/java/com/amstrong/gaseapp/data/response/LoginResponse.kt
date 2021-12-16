package com.amstrong.gaseapp.data.response

import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.data.db.entities.Workstation
import java.util.*

data class LoginResponse(
        val error_message: String?,
        val error_details: List<String>,
        val expire_date: Date,
        val is_success: Boolean,
        val employee: Employee,
        val token: String?
)