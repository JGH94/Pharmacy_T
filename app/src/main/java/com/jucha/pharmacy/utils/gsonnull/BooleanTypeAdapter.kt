package com.jucha.pharmacy.utils.gsonnull

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class BooleanTypeAdapter: TypeAdapter<Boolean>() {
    override fun write(out: JsonWriter, value: Boolean?) {
    }

    override fun read(`in`: JsonReader): Boolean {
        if (`in`.peek() === JsonToken.NULL) {
            `in`.nextNull()
            return false
        }
        return `in`.nextBoolean()
    }

}