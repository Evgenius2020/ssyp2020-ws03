import com.soywiz.korio.async.launch
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.dump
import kotlinx.serialization.load

class Client(s: Socket) {
    private val inputChannel = Channel<Request>(Channel.Factory.UNLIMITED)
    private val outputChannel = Channel<Request>(Channel.Factory.UNLIMITED)

    private val inStream = s.openReadChannel()
    private val outStream = s.openWriteChannel()

    val cbor = Cbor()


    fun run(){
        launch(Dispatchers.IO) {
            while (true){
                val req = outputChannel.receive()
                val ser = cbor.dump(Request.serializer(), req)
                outStream.writeInt(ser.size)
                outStream.writeFully(ser, 0, ser.size)
            }
        }

        launch(Dispatchers.IO){
            val size = inStream.readInt()
            val ser = ByteArray(size)
            inStream.readFully(ser, 0, size)
            inputChannel.send(cbor.load(Request.serializer(), ser))
        }
    }


    suspend fun sendRequest(r: Request){
        outputChannel.send(r)
    }

    fun getRequests():ReceiveChannel<Request>{
        return inputChannel
    }


}