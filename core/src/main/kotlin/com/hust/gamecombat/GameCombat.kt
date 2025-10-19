package com.hust.gamecombat

import com.badlogic.gdx.Game

class GameCombat : Game() {
    override fun create() {

        setScreen(MenuScreen(this))
    }
}
