package com.hust.demonslayer.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Player {
    var x = 100f
    var y = 100f
    private val speed = 200f
    private val jumpForce = 400f
    private val gravity = -900f
    private var velocityY = 0f
    private var isOnGround = true

    // Animation
    private val idleTexture = Texture("player_idle.png")
    private val attack1 = Texture("player_attack1.png")
    private val attack2 = Texture("player_attack2.png")

    private val idleRegion = TextureRegion(idleTexture)
    private val attackAnimation = Animation(0.15f, TextureRegion(attack1), TextureRegion(attack2))  // 0.15f is the frame duration
    private var stateTime = 0f          // Tracks the elapsed time of the animation

    var isAttacking = false
    private var attackTime = 0f
    private val attackDuration = 0.3f

    fun moveLeft(delta: Float) {
        if (!isAttacking) x -= speed * delta
    }

    fun moveRight(delta: Float) {
        if (!isAttacking) x += speed * delta
    }

    fun jump() {
        if (isOnGround && !isAttacking) {
            velocityY = jumpForce
            isOnGround = false
        }
    }

    fun attack() {
        if (!isAttacking) {
            isAttacking = true
            attackTime = 0f
            stateTime = 0f
            Gdx.app.log("Player", "Attack start!")
        }
    }

    fun update(delta: Float, groundY: Float) {
        velocityY += gravity * delta                // v = v0 + a * t
        y += velocityY * delta                      // y = y0 + v * t

        if (y <= groundY) {
            y = groundY
            velocityY = 0f
            isOnGround = true
        }

        if (isAttacking){
            attackTime += delta
            stateTime += delta
            if (attackTime >= attackDuration) {
                isAttacking = false
                stateTime = 0f
                Gdx.app.log("Player", "Attack end.")
            }
        }
    }

    fun getCurrentFrame(): TextureRegion {
        return if (isAttacking) {
            attackAnimation.getKeyFrame(stateTime, false)
        } else {
            idleRegion
        }
    }

    fun dispose() {
        idleTexture.dispose()
        attack1.dispose()
        attack2.dispose()
    }
}
