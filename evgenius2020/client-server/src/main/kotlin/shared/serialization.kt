package shared

import com.google.gson.Gson

val gson = Gson()

fun serialize(obj: Any) = gson.toJson(obj)
inline fun <reified T> deserialize(json: String): T {
    return gson.fromJson(json, T::class.java)
}