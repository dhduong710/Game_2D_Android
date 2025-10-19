package com.hust.gamecombat

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport

class MenuScreen(private val game: Game) : Screen {

    private val stage = Stage(ScreenViewport())     // manage actors (buttons, images, etc.)
    private val backgroundTexture = Texture("backgrounds/menu_bg.png")
    private val logoTexture = Texture("ui/screens/logo.png")
    private val startTex = Texture("ui/screens/btn_start.png")
    private val exitTex = Texture("ui/screens/btn_exit.png")

    init {
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        val logoWidth = 1100f
        val logoHeight = 600f

        val btnWidth = 500f
        val btnHeight = 250f


        val spacing = 0.5f


        val logoCenterY = screenHeight / 2f + 250f

        val startCenterY = logoCenterY - (logoHeight / 2f) - spacing - (btnHeight / 2f)

        val exitCenterY = startCenterY - (btnHeight / 2f) - spacing - (btnHeight / 2f)

        val logo = Image(logoTexture)
        logo.setSize(logoWidth, logoHeight)
        logo.setPosition(screenWidth / 2f, logoCenterY, Align.center) // // position by its center point
        stage.addActor(logo)

        val btnStart = ImageButton(TextureRegionDrawable(startTex))
        btnStart.setSize(btnWidth, btnHeight)
        btnStart.setPosition(screenWidth / 2f, startCenterY, Align.center)

        btnStart.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.log("MenuScreen", "To CharacterSelectScreen...")
                game.screen = CharacterSelectScreen(game)
                dispose()
            }
        })
        stage.addActor(btnStart)

        val btnExit = ImageButton(TextureRegionDrawable(exitTex))
        btnExit.setSize(btnWidth, btnHeight)
        btnExit.setPosition(screenWidth / 2f, exitCenterY, Align.center)

        btnExit.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })
        stage.addActor(btnExit)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f)
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

    override fun dispose() {
        stage.dispose()
        backgroundTexture.dispose()
        logoTexture.dispose()
        startTex.dispose()
        exitTex.dispose()
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
}
