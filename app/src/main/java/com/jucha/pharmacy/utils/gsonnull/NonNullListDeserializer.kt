package com.jucha.pharmacy.utils.gsonnull

import com.google.gson.*
import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.Type

class NonNullListDeserializer<T> : JsonDeserializer<ArrayList<T>> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ArrayList<T> {
        if (json is JsonArray) {
            val array = json
            val size = array.size()
            if (size == 0) {
                return arrayListOf()
            }
            val list: ArrayList<T> = ArrayList(size)
            for (i in 0 until size) {
                val elementType: Type = `$Gson$Types`.getCollectionElementType(typeOfT, ArrayList::class.java)
                val value: T = context.deserialize(array[i], elementType)
                if (value != null) {
                    list.add(value)
                }
            }
            return list
        }
        return arrayListOf()
    }
}