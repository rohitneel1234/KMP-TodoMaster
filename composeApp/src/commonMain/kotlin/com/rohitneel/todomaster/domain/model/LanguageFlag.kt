package com.rohitneel.todomaster.domain.model

enum class LanguageFlag(var code: String, val flag: String) {
    CHINESE("zh", "\uD83C\uDDE8\uD83C\uDDF3"),
    DUTCH("nl", "\uD83C\uDDF3\uD83C\uDDF1"),
    ENGLISH("en", "\uD83C\uDDEC\uD83C\uDDE7"),
    FRENCH("fr", "\uD83C\uDDEB\uD83C\uDDF7"),
    GERMAN("de", "\uD83C\uDDE9\uD83C\uDDEA"),
    HINDI("hi", "\uD83C\uDDEE\uD83C\uDDF3"),
    ITALIAN("it", "\uD83C\uDDEE\uD83C\uDDF9"),
    JAPANESE("ja", "\uD83C\uDDEF\uD83C\uDDF5"),
    PORTUGUESE("pt", "\uD83C\uDDF5\uD83C\uDDF9"),
    RUSSIAN("ru", "\uD83C\uDDF7\uD83C\uDDFA"),
    SPANISH("es", "\uD83C\uDDEA\uD83C\uDDF8"),
    TURKISH("tr", "\uD83C\uDDF9\uD83C\uDDF7"),
    VIETNAMESE("vi", "\uD83C\uDDFB\uD83C\uDDF3");

    companion object {
        private val flagMap = entries.associateBy { it.code }
        fun getFlag(code: String) = flagMap[code]?.flag ?: ""
    }
}
