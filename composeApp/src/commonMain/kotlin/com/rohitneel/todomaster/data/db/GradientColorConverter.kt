package com.rohitneel.todomaster.data.db

import androidx.room.TypeConverter

class GradientColorConverter {

    @TypeConverter
    fun fromGradientColor(gradientColor: List<Int>?): String {
        return gradientColor?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toGradientColor(gradientColorString: String): List<Int> {
        return if (gradientColorString.isNotEmpty()) {
            gradientColorString.split(",").mapNotNull { it.trim().toIntOrNull() }
        } else {
            emptyList()
        }
    }
}
