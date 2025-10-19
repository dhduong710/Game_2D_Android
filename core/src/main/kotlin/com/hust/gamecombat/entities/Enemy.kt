package com.hust.gamecombat.entities

import com.badlogic.gdx.Gdx
import kotlin.math.abs

class Enemy(
    characterID: CharacterID,
    startX: Float,
    startY: Float
) : BaseCharacter(characterID) {

    private var aiActionTimer = 0f
    private val aiActionCooldown = 1.0f

    init {
        this.x = startX
        this.y = startY

        isFacingRight = false
    }

    fun updateAI(delta: Float, player: Player) {
        if (!isAlive || !player.isAlive || isBusy()) {
            if (state == State.RUN) stopMoving()
            return
        }

        val distanceX = abs(player.x - this.x)

        aiActionTimer -= delta
        if (aiActionTimer <= 0) {
            aiActionTimer = aiActionCooldown

            if (distanceX < 100f) {

                isFacingRight = (player.x > this.x)
                attackNormal()
            } else if (distanceX < 400f) {

                if (player.x > this.x) {
                    moveRight(delta * 10)           // 10 is speed of enemy
                } else {
                    moveLeft(delta * 10)
                }
            } else {

                stopMoving()
            }
        }
    }

    override fun takeDamage(damage: Int) {
        val hpBefore = currentHp
        super.takeDamage(damage)

        if (state == State.BLOCK) {
            Gdx.app.log("Enemy", "Enemy blocked attack!")
        } else if (hpBefore > currentHp) {
            Gdx.app.log("Enemy", "Enemy took $damage damage, HP: $currentHp/$maxHp")
        }

        if (!isAlive) {
            Gdx.app.log("Enemy", "Enemy was defeated!")
        }
    }
}
