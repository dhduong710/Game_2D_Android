package com.hust.gamecombat.entities

import com.badlogic.gdx.Gdx
import kotlin.math.abs

import kotlin.random.Random

class Enemy(
    characterID: CharacterID,
    startX: Float,
    startY: Float
) : BaseCharacter(characterID) {

    private var aiActionTimer = 0f
    private val aiActionCooldown = 1.0f

    override var speed = 2000f

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

            if (distanceX < 250f) {

                isFacingRight = (player.x > this.x)

                val attackChoice = Random.nextInt(4)

                when (attackChoice) {
                    0 -> attackNormal()
                    1 -> useSkill1()
                    2 -> useSkill2()
                    3 -> useSkill3()
                }


            } else {

                if (player.x > this.x) {
                    moveRight(delta)
                } else {
                    moveLeft(delta)
                }

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
