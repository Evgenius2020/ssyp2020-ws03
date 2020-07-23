package engine.managers

import engine.Configuration
import shared.Entity
import shared.Player

data class TeamInfo(
        val nicknames: MutableList<String>,
        var score: Double = 0.0
)

class TeamsManager : BaseManager<Unit>() {
    private val teams = List<TeamInfo>(Configuration.teamCount) { TeamInfo(mutableListOf<String>(), 0.0) }

    fun register(player: Player) {
        super.register(player, Unit)
        player.team = nextTeam()
        addPlayer(player.team, player.nick)
    }
    fun removePlayer(player: Player) {
        removePlayer(player.team, player.nick)
        super.delete(player)
    }

    private fun nextTeam() = teams.withIndex().minBy { (_, f) -> f.nicknames.size }?.index ?: 0
    private fun addPlayer(team: Int, nickname: String) = teams[team].nicknames.add(nickname)
    private fun removePlayer(team: Int, nickname: String) = teams[team].nicknames.remove(nickname)

    fun addScore(team: Int, score: Double) {
        teams[team].score += score
    }

    fun getNames(team: Int) = teams[team].nicknames.toTypedArray()
    fun getScore(team: Int) = teams[team].score
}