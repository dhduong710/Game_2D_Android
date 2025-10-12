package com.hust.demonslayer

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.hust.demonslayer.entities.Player
import kotlin.math.abs
import com.badlogic.gdx.graphics.g2d.TextureRegion

class DemonSlayerGame : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch     // for drawing textures
    private lateinit var stage: Stage           // for handling input and UI elements
    private lateinit var player: Player

    private lateinit var btnLeft: ImageButton
    private lateinit var btnRight: ImageButton
    private lateinit var btnJump: ImageButton
    private lateinit var btnAttack: ImageButton

    private lateinit var enemyTexture: Texture
    private var enemyX = 0f
    private var enemyY = 100f

    private val groundLevel = 100f
    private lateinit var bgTexture: Texture

    override fun create() {
        batch = SpriteBatch()
        stage = Stage(ScreenViewport(), batch)
        Gdx.input.inputProcessor = stage

        player = Player()
        bgTexture = Texture("background.png")
        enemyTexture = Texture("enemy.png")

        val leftTex = Texture("ui/btn_left.png")
        val rightTex = Texture("ui/btn_right.png")
        val jumpTex = Texture("ui/btn_jump.png")
        val attackTex = Texture("ui/btn_attack.png")

        btnLeft = ImageButton(TextureRegionDrawable(leftTex))
        btnRight = ImageButton(TextureRegionDrawable(rightTex))
        btnJump = ImageButton(TextureRegionDrawable(jumpTex))
        btnAttack = ImageButton(TextureRegionDrawable(attackTex))

        val smallBtnSize = 130f
        val bigBtnSize = 160f

        btnLeft.setSize(smallBtnSize, smallBtnSize)
        btnRight.setSize(smallBtnSize, smallBtnSize)
        btnJump.setSize(smallBtnSize, smallBtnSize)
        btnAttack.setSize(bigBtnSize, bigBtnSize)

        val baseY = 80f
        val baseX = 80f

        btnLeft.setPosition(baseX, baseY)
        btnRight.setPosition(baseX + 200f, baseY)
        btnJump.setPosition(baseX + 100f, baseY + 180f)

        btnAttack.setPosition(Gdx.graphics.width - bigBtnSize - 100f, baseY + 50f)

        stage.addActor(btnLeft)
        stage.addActor(btnRight)
        stage.addActor(btnJump)
        stage.addActor(btnAttack)

        player.x = (Gdx.graphics.width / 2f) - 100f
        player.y = groundLevel

        enemyX = player.x + 300f
        enemyY = groundLevel
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime

        if (btnLeft.isPressed) player.moveLeft(delta)
        if (btnRight.isPressed) player.moveRight(delta)
        if (btnJump.isPressed) player.jump()
        if (btnAttack.isPressed) player.attack()

        player.update(delta, groundLevel)

        if (player.isAttacking && abs(player.x - enemyX) < 60f) {
            Gdx.app.log("Combat", "hit!")
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)         // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()

        batch.draw(bgTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        val scale = 0.5f
        val frame = player.getCurrentFrame()

        batch.draw(frame, player.x, player.y, frame.regionWidth * scale, frame.regionHeight * scale)

        val enemyRegion = TextureRegion(enemyTexture)
        batch.draw(enemyRegion, enemyX, enemyY, enemyRegion.regionWidth * scale, enemyRegion.regionHeight * scale)

        batch.end()

        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        batch.dispose()
        stage.dispose()
        player.dispose()
        enemyTexture.dispose()
        bgTexture.dispose()
    }
}
