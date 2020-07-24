package client

import com.soywiz.klock.timesPerSecond
import com.soywiz.korge.view.FixedSizeContainer
import com.soywiz.korge.view.addFixedUpdater
import com.soywiz.korge.view.addUpdater

class Statistics(_w : Double, _h : Double) : FixedSizeContainer(_w, _h) {
    init {

        addFixedUpdater(60.timesPerSecond) {

        }
    }
}