package com.amstrong.gaseapp.data.db.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class LineModifier(
        val id: String?,
        val receipt_number: String?,
        var line_number_fk: Long = 0,
        val modifier_id: String,
        val line_modifier_number: Int,
        val modifier_option_id: String?,
        val name: String,
        val option: String,
        val price: Int,
        val money_amount: Int,
        val pushed: Boolean,

        @Ignore
        val LineItem: LineItem?,
        @Ignore
        val Modifier: Modifier?,

        @PrimaryKey(autoGenerate = true)
        var modifier_number: Long = 0,
)