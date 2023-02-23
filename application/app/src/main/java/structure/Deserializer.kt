package structure

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import kotlin.reflect.KClass

class Deserializer<T : Any>(private val c: KClass<T>) : ResponseDeserializable<T> {
    override fun deserialize(content: String): T {
        return Gson().fromJson(content, c.java)
    }
}