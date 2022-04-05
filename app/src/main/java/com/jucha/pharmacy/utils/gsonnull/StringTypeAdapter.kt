package com.jucha.pharmacy.utils.gsonnull

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class StringTypeAdapter : TypeAdapter<String>() {
    override fun write(out: JsonWriter, value: String?) {
    }

    override fun read(`in`: JsonReader): String {
        if (`in`.peek() === JsonToken.NULL) {
            `in`.nextNull()
            return ""
        }
        return `in`.nextString()
    }

}
