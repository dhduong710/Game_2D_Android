package com.hust.gamecombat.entities

import com.badlogic.gdx.Gdx

class Player(characterID: CharacterID) : BaseCharacter(characterID) {
    override fun takeDamage(damage: Int) {
        val hpBefore = currentHp
        super.takeDamage(damage) /

        if (state == State.BLOCK) {
            Gdx.app.log("Player", "Player blocked attack!")
        } else if (hpBefore > currentHp) {
            Gdx.app.log("Player", "Player took $damage damage, HP: $currentHp/$maxHp")
        }

        if (!isAlive) {
            Gdx.app.log("Player", "Player was defeated!")
        }
    }
}
