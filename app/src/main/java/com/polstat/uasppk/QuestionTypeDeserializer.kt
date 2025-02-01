package com.polstat.uasppk

import com.google.gson.*
import java.lang.reflect.Type

class QuestionTypeDeserializer : JsonDeserializer<QuestionType> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): QuestionType {
        val typeString = json?.asString ?: ""
        return when (typeString) {
            "OPEN_ENDED" -> QuestionType.OPEN_ENDED
            "MULTIPLE_CHOICE_SINGLE" -> QuestionType.MULTIPLE_CHOICE_SINGLE
            "MULTIPLE_CHOICE_MULTIPLE" -> QuestionType.MULTIPLE_CHOICE_MULTIPLE
            else -> throw JsonParseException("Unknown question type: $typeString")
        }
    }
}