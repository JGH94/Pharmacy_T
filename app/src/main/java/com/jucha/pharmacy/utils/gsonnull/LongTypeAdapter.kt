package com.jucha.pharmacy.utils.gsonnull

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class LongTypeAdapter: TypeAdapter<Long>() {
    override fun write(out: JsonWriter, value: Long?) {
    }

    override fun read(`in`: JsonReader): Long {
        if (`in`.peek() === JsonToken.NULL) {
            `in`.nextNull()
            return 0
        }
        return `in`.nextLong()
    }

}