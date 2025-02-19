package com.example.app.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Events(
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null,
    val year:Int,
    val month:Int,
    val day:Int,
    val hour:Int,
    val minute:Int,
    val title:String,
    val description:String
)