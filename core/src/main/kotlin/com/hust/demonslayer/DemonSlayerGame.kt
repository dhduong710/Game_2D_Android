package com.hust.demonslayer

import com.badlogic.gdx.Game

class DemonSlayerGame : Game() {
    override fun create() {

        setScreen(MenuScreen(this))
    }
}
