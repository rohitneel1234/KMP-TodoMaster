package com.rohitneel.todomaster.data.db

import androidx.room.TypeConverter
import com.rohitneel.todomaster.domain.model.FontStyleModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FontStyleModelConverter {

    @TypeConverter
    fun fromFontStyleModel(fontStyleModel: FontStyleModel): String {
        return Json.encodeToString(fontStyleModel)
    }

    @TypeConverter
    fun toFontStyleModel(json: String): FontStyleModel {
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            FontStyleModel()
        }
    }
}
