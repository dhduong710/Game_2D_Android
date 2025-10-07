package com.hust.demonslayer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.hust.demonslayer.entities.Player

class DemonSlayerGame: ApplicationAdapter() {
    private lateinit var  batch: SpriteBatch        //combines multiple textures into a single command to the gpu
    private lateinit var playerTexture: Texture
    private lateinit var player: Player

    private lateinit var stage: Stage               //contains UI objects or other actors
    private lateinit var btnLeft: ImageButton
    private lateinit var btnRight: ImageButton
    private lateinit var btnJump: ImageButton

    private val groundLevel = 100f

    private lateinit var shapeRenderer: ShapeRenderer



    override fun create() {
        batch = SpriteBatch()
        playerTexture = Texture("player.png")
        player = Player()

        stage = Stage(ScreenViewport(), batch)
        Gdx.input.inputProcessor = stage

        btnLeft = ImageButton(TextureRegionDrawable(Texture("ui/btn_left.png")))
        btnRight = ImageButton(TextureRegionDrawable(Texture("ui/btn_right.png")))
        btnJump = ImageButton(TextureRegionDrawable(Texture("ui/btn_jump.png")))

        btnLeft.setSize(100f, 100f)
        btnRight.setSize(100f, 100f)
        btnJump.setSize(100f, 100f)

        btnLeft.setPosition(50f, 200f)
        btnRight.setPosition(170f, 200f)
        btnJump.setPosition(Gdx.graphics.width -150f, 200f)


        stage.addActor(btnLeft)
        stage.addActor(btnRight)
        stage.addActor(btnJump)

        shapeRenderer = ShapeRenderer()
    }

    override fun render() {

        // calculate new coordinates for player based on input
        val delta = Gdx.graphics.deltaTime          // time since last frame

        if(btnLeft.isPressed) player.moveLeft(delta)
        if(btnRight.isPressed) player.moveRight(delta)
        if(btnJump.isPressed) player.jump()

        player.update(delta, groundLevel)

        // clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f ,1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // draw player
        batch.begin()
        batch.draw(playerTexture, player.x, player.y)
        batch.end()

        // update and draw UI elements
        stage.act(delta)
        stage.draw()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0f, 0f, 0.5f, 1f)        // a : alpha (transparency)
        shapeRenderer.rect(0f, 0f, Gdx.graphics.width.toFloat(), groundLevel)
        shapeRenderer.end()
    }

    override fun dispose() {
        batch.dispose()
        playerTexture.dispose()
        stage.dispose()
        shapeRenderer.dispose()
    }
}
