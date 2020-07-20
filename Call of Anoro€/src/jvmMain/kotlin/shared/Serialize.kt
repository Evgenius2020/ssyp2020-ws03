package shared

import java.io.*
import java.util.*

fun deserialize(s: String): Any? {
    val data = Base64.getDecoder().decode(s)
    val ois = ObjectInputStream(
        ByteArrayInputStream(data)
    )
    val o = ois.readObject()
    ois.close()
    return o
}

fun serialize(o: Any?): String {
    val baos = ByteArrayOutputStream()
    val oos = ObjectOutputStream(baos)
    oos.writeObject(o)
    oos.close()
    return Base64.getEncoder().encodeToString(baos.toByteArray())
}