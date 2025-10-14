package com.hust.demonslayer.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Enemy(startX: Float, startY: Float) {
    var x = startX
    var y = startY

    var maxHp = 50
    var currentHp = 50
    var isAlive = true

    private val texture = Texture("enemy.png")
    val textureRegion = TextureRegion(texture)

    private val speed = 100f

    fun update(delta: Float, groundY: Float, playerX: Float) {
        if (!isAlive) return

        if (x < playerX - 50f) {
            x += speed * delta
        } else if (x > playerX + 50f) {
            x -= speed * delta
        }

        y = groundY
    }

    fun takeDamage(damage: Int) {
        if (!isAlive) return

        currentHp -= damage
        Gdx.app.log("Enemy", "Enemy took $damage damage, current HP: $currentHp")

        if (currentHp <= 0) {
            currentHp = 0
            isAlive = false
            Gdx.app.log("Enemy", "Enemy defeated!")
        }
    }

    fun draw(batch: SpriteBatch, scale: Float) {
        if (isAlive) {
            batch.draw(textureRegion, x, y, textureRegion.regionWidth * scale, textureRegion.regionHeight * scale)
        }
    }

    fun dispose() {
        texture.dispose()
    }
}
