import engine.Dot
import engine.Engine
import engine.Player
import engine.Vector
import kotlinx.coroutines.*
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*

class ClientTester {

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @Test
    @DisplayName("ServerTest")
    fun serverTest(){
        runBlocking {
            val server = serverActor()
            withContext(Dispatchers.Default){
                coroutineScope {
                    //REGISTER TEST
                    var responsePlayer = CompletableDeferred<Player>()
                    server.send(Register(responsePlayer))

                    val p1 = responsePlayer.await()

                    var responsePlayers = CompletableDeferred<HashMap<Long, Player>>()
                    server.send(GetPlayers(responsePlayers))

                    var players = responsePlayers.await()
                    assertEquals(1, players.size)
                    assert(p1 === players[p1.id])

                    responsePlayer = CompletableDeferred()
                    server.send(Register(responsePlayer))

                    val p2 = responsePlayer.await()

                    responsePlayers = CompletableDeferred()
                    server.send(GetPlayers(responsePlayers))

                    players = responsePlayers.await()
                    assertEquals(2, players.size)
                    assert(p2 === players[p2.id])

                    //SET TARGET TEST
                    assertEquals(p1.id, p2.targetId)
                    assertNotEquals(p2.id, p1.targetId)

                    //UPDATE CHECK
                    p1.pos = Dot(0.0, 0.0)
                    p2.pos = Dot(0.0, 0.0)

                    p1.dir = Vector(1.0, 0.0)
                    p2.dir = Vector(0.0, 1.0)
                    val dR = Engine.speed * Engine.dt

                    server.send(Update)

                    responsePlayers = CompletableDeferred()
                    server.send(GetPlayers(responsePlayers))
                    players = responsePlayers.await()

                    assert(players[p1.id] === p1)
                    assert(players[p2.id] === p2)

                    assertEquals(Dot(dR, 0.0), p1.pos)
                    assertEquals(Dot(0.0, dR), p2.pos)

                    //CHANGE DIRECTION CHECK
                    server.send(ChangeDirection(p1.id, Vector(0.0, 1.0)))
                    server.send(ChangeDirection(p2.id, Vector(1.0, 0.0)))

                    server.send(Update)

                    responsePlayers = CompletableDeferred()
                    server.send(GetPlayers(responsePlayers))
                    players = responsePlayers.await()

                    assertEquals(Vector(0.0, 1.0), p1.dir.makeLenOne())
                    assertNotEquals(Vector(1.0, 0.0), p2.dir) // HIT

                    assert(players[p1.id] === p1)
                    assert(players[p2.id] === p2)

                    assertEquals(Dot(dR, dR), p1.pos)
                    assertNotEquals(Dot(dR, dR), p2.pos) // HIT
                }
            }
            server.close()
        }
    }
}