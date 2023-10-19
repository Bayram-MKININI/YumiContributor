package net.noliaware.yumi_contributor.commun.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import net.noliaware.yumi_contributor.commun.domain.model.ActionParam

class ActionParamAdapter : JsonAdapter<List<ActionParam>>() {

    @ToJson
    override fun toJson(jsonWriter: JsonWriter, actionParam: List<ActionParam>?) = Unit

    @FromJson
    override fun fromJson(reader: JsonReader): List<ActionParam> {
        val list = mutableListOf<ActionParam>()
        with(reader) {
            if (peek() == JsonReader.Token.NULL) {
                nextNull()
            } else {
                beginObject()
                while (hasNext()) {
                    list.add(ActionParam(nextName(), readJsonValue().toString()))
                }
                endObject()
            }
        }
        return list
    }
}