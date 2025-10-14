package com.hust.demonslayer

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport

class MenuScreen(private val game: Game) : Screen {

    private val stage = Stage(ScreenViewport())
    private val backgroundTexture = Texture("background.png")

    init {
        val startTex = Texture("ui/btn_start.png")
        val exitTex = Texture("ui/btn_exit.png")

        val btnStart = ImageButton(TextureRegionDrawable(startTex))
        val btnExit = ImageButton(TextureRegionDrawable(exitTex))

        val btnWidth = 300f
        val btnHeight = 150f

        btnStart.setSize(btnWidth, btnHeight)
        btnExit.setSize(btnWidth, btnHeight)

        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        btnStart.setPosition(screenWidth / 2 - btnWidth / 2, screenHeight / 2)
        btnExit.setPosition(screenWidth / 2 - btnWidth / 2, screenHeight / 2 - 200f)

        btnStart.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = GameScreen(game)
                dispose()
            }
        })

        btnExit.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })

        stage.addActor(btnStart)
        stage.addActor(btnExit)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.batch.begin()
        stage.batch.draw(backgroundTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        stage.batch.end()

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    override fun dispose() {
        stage.dispose()
        backgroundTexture.dispose()
    }
}
