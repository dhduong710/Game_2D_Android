package com.hust.demonslayer.entities

class Player {
    var x = 100f
    var y = 100f
    private val speed = 200f
    private val jumpForce = 400f
    private val gravity = -900f
    private var velocityY = 0f
    private var isOnGround = true

    fun moveLeft(delta: Float) {
        x -= speed * delta
    }

    fun moveRight(delta: Float) {
        x += speed * delta
    }

    fun jump() {
        if (isOnGround) {
            velocityY = jumpForce
            isOnGround = false
        }
    }

    fun update(delta: Float) {
        velocityY += gravity * delta    // v = v0 + a * t
        y += velocityY * delta          // y = y0 + v * t

        if(y <= 100f) {
            y = 100f
            velocityY = 0f
            isOnGround = true
        }
    }
}
