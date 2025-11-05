package com.hust.gamecombat.entities

import com.badlogic.gdx.Gdx

class Player(characterID: CharacterID) : BaseCharacter(characterID) {

    private val attackNormalCooldown = 1.6f
    private var attackNormalTimer = 0f

    override fun update(delta: Float, groundY: Float) {
        super.update(delta, groundY)

        if (attackNormalTimer > 0) {
            attackNormalTimer -= delta
        }
    }
    override fun attackNormal() {
        if (attackNormalTimer > 0) return

        if (isOnGround && !isBusy() && state != State.BLOCK) {
            state = State.ATTACK_NORMAL
            actionTimer = attackDuration

            attackNormalTimer = attackNormalCooldown
        }
    }

    override fun takeDamage(damage: Int) {
        val hpBefore = currentHp
        super.takeDamage(damage)

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
