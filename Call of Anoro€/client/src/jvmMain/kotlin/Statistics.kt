package client

import com.soywiz.korge.view.*
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.file.std.tmpdir

class Statistics(_w: Double, _h: Double) : FixedSizeContainer(_w, _h) {
    private var blackboard: View
    private var centerLine: View
    private var team1score: View
    private var team2score: View
    private var team1 = mutableMapOf<String, View>()
    private var team2 = mutableMapOf<String, View>()

    init {
        blackboard = solidRect(width, height, RGBA(0, 0, 0, 150)).xy(0, 0)

        centerLine = solidRect(1, 1, RGBA(0, 0, 0, 0)) {
            position(_w / 2 - 0.5, _h / 2 - 0.5)
        }

        team1score = text("0", 20.0, RGBA(9, 132, 227, 255)) {
            filtering = false
            this.y = height / 8
            addUpdater {
                alignRightToLeftOf(centerLine)
                this.x -= 20
            }
        }

        team2score = text("0", 20.0, RGBA(214, 48, 49, 255)) {
            filtering = false
            this.y = height / 8
            addUpdater {
                alignLeftToRightOf(centerLine)
                this.x += 20
            }
        }


    }

    fun updateTeamStats(teamMembers: HashMap<Int, Array<String>>, nickToKills: HashMap<String, Int>, nickToDeaths: HashMap<String, Int>) {
        val team1Members = teamMembers[0]!!
        val team2Members = teamMembers[1]!!

        val team1Iterator = team1.iterator()
        while (team1Iterator.hasNext()) {
            val item = team1Iterator.next()
            if (item.key !in team1Members) {
                removeChild(team1[item.key])
                team1Iterator.remove()
            }
        }

        val team2Iterator = team2.iterator()
        while (team2Iterator.hasNext()) {
            val item = team2Iterator.next()
            if (item.key !in team1Members) {
                removeChild(team2[item.key])
                team2Iterator.remove()
            }
        }

        var tmpIterator = 1

        for (i in team1Members)
        {
            if (i in team1)
            {
                team1[i].setText("$i: ${nickToKills[i]}/${nickToDeaths[i]}")
                team1[i]!!.y = height * (tmpIterator + 1) / 8
                tmpIterator++
            }
            else
            {
                val playerText = text("$i: ${nickToKills[i]}/${nickToDeaths[i]}", 14.0, RGBA(9, 132, 227, 255)) {
                    filtering = false
                    this.y = height * ((tmpIterator + 1) / 8)
                    addUpdater {
                        alignRightToLeftOf(centerLine)
                        this.x -= 20
                    }
                }
                team1[i] = playerText
                tmpIterator++
            }
        }

        tmpIterator = 1

        for (i in team2Members)
        {
            if (i in team2)
            {
                team2[i].setText("$i: ${nickToKills[i]}/${nickToDeaths[i]}")
                team2[i]!!.y = height * (tmpIterator + 1) / 8
                tmpIterator++
            }
            else
            {
                val playerText = text("$i: ${nickToKills[i]}/${nickToDeaths[i]}", 14.0, RGBA(214, 48, 49, 255)) {
                    filtering = false
                    this.y = height * ((tmpIterator + 1) / 8)
                    addUpdater {
                        alignLeftToRightOf(centerLine)
                        this.x += 20
                    }
                }
                team2[i] = playerText
                tmpIterator++
            }
        }
    }

    fun setTeamScores(t1: Int, t2: Int) {
        team1score.setText(t1.toString())
        team2score.setText(t2.toString())
    }

}