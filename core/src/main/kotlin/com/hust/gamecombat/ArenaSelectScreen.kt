package com.hust.gamecombat

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.hust.gamecombat.entities.CharacterID
import kotlin.math.max


enum class ArenaID(val assetPath: String, val displayName: String) {
    KONOHA("arenas/konoha.png", "Konoha"),
    THOUSANDSUNNY("arenas/thousandsunny.png", "Thousand Sunny");
}



class ArenaSelectScreen(
    private val game: Game,
    private val p1Choice: CharacterID,
    private val p2Choice: CharacterID
) : Screen {

    private val stage = Stage(ScreenViewport())
    private val batch = SpriteBatch()

    private enum class SelectionState { SELECTING_P1, SELECTING_P2, READY }
    private var currentState = SelectionState.SELECTING_P1

    private val backgroundTexture = Texture("backgrounds/char_select_bg.png")
    private val font = BitmapFont(Gdx.files.internal("fonts/main_font.fnt"))
    private val fightButtonTexture = Texture("ui/screens/btn_fight.png")
    private val selectorTexture = Texture("ui/hud/avatar_frame.png")

    private lateinit var btnUp: ImageButton
    private lateinit var btnDown: ImageButton
    private lateinit var btnLeft: ImageButton
    private lateinit var btnRight: ImageButton
    private lateinit var btnAttack: ImageButton
    private lateinit var btnFight: ImageButton

    private val buttonTextures = mutableListOf<Texture>()

    private var arenaChoice: ArenaID? = null
    private val availableArenas = ArenaID.values()
    private val portraitTextures = mutableMapOf<ArenaID, Texture>()

    private val cols = availableArenas.size
    private val rows = 1
    private val portraitSize = 200f
    private val portraitSpacing = 50f
    private var cursorX = 0
    private val gridStartX: Float
    private val gridStartY: Float

    private val titleLabel: Label
    private val arenaNameLabel: Label

    private val emptyTexture: Texture


    init {
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        availableArenas.forEach { arenaID ->
            val tex = Texture(Gdx.files.internal(arenaID.assetPath))
            portraitTextures[arenaID] = tex
        }

        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(0f, 0f, 0f, 0f)
        pixmap.fill()
        emptyTexture = Texture(pixmap)
        pixmap.dispose()

        font.region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)

        titleLabel = Label("ARENA SELECTION", Label.LabelStyle(font, Color.WHITE))
        titleLabel.setFontScale(4.0f)
        titleLabel.setSize(screenWidth, 50f)
        titleLabel.setPosition(0f, screenHeight - 100f)
        titleLabel.setAlignment(Align.center)
        stage.addActor(titleLabel)


        val gridTotalWidth = (cols * portraitSize) + (max(0, cols - 1) * portraitSpacing)
        gridStartX = (screenWidth - gridTotalWidth) / 2f
        gridStartY = screenHeight / 2f

        arenaNameLabel = Label(availableArenas[0].displayName, Label.LabelStyle(font, Color.WHITE))
        arenaNameLabel.setFontScale(2.5f)
        arenaNameLabel.setAlignment(Align.center)
        arenaNameLabel.setPosition(screenWidth / 2f, gridStartY - 50f, Align.center)
        stage.addActor(arenaNameLabel)

        btnFight = ImageButton(TextureRegionDrawable(fightButtonTexture))
        btnFight.setSize(800f, 300f)
        btnFight.setPosition(screenWidth / 2f, 180f, Align.center)
        btnFight.isVisible = false
        btnFight.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                startGame()
            }
        })
        stage.addActor(btnFight)

        setupControls()

        Gdx.input.inputProcessor = stage
    }

    private fun setupControls() {

        val upTex = Texture("ui/controls/btn_up.png")
        val downTex = Texture("ui/controls/btn_down.png")
        val leftTex = Texture("ui/controls/btn_left.png")
        val rightTex = Texture("ui/controls/btn_right.png")
        val attackTex = Texture("ui/controls/btn_attack.png")

        buttonTextures.add(upTex)
        buttonTextures.add(downTex)
        buttonTextures.add(leftTex)
        buttonTextures.add(rightTex)
        buttonTextures.add(attackTex)

        val dpadSize = 200f
        val dpadBaseX = 50f
        val dpadBaseY = 50f

        btnDown = ImageButton(TextureRegionDrawable(downTex))
        btnDown.setSize(dpadSize, dpadSize)
        btnDown.setPosition(dpadBaseX + dpadSize, dpadBaseY)
        stage.addActor(btnDown)

        btnUp = ImageButton(TextureRegionDrawable(upTex))
        btnUp.setSize(dpadSize, dpadSize)
        btnUp.setPosition(dpadBaseX + dpadSize, dpadBaseY + dpadSize)
        stage.addActor(btnUp)

        btnLeft = ImageButton(TextureRegionDrawable(leftTex))
        btnLeft.setSize(dpadSize, dpadSize)
        btnLeft.setPosition(dpadBaseX + 50f, dpadBaseY + dpadSize/2)
        stage.addActor(btnLeft)

        btnRight = ImageButton(TextureRegionDrawable(rightTex))
        btnRight.setSize(dpadSize, dpadSize)
        btnRight.setPosition(dpadBaseX + dpadSize * 2 - 50f, dpadBaseY + dpadSize/2)
        stage.addActor(btnRight)

        val mainBtnSize = 240f
        val actionBaseX = Gdx.graphics.width - mainBtnSize - 50f
        val actionBaseY = 50f

        btnAttack = ImageButton(TextureRegionDrawable(attackTex))
        btnAttack.setSize(mainBtnSize, mainBtnSize)
        btnAttack.setPosition(actionBaseX, actionBaseY)
        stage.addActor(btnAttack)

        addControlListeners()
    }

    private fun addControlListeners() {

        val leftDownListener = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                handleInput(Direction.LEFT); return true
            }
        }
        btnLeft.addListener(leftDownListener)

        val rightUpListener = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                handleInput(Direction.RIGHT); return true
            }
        }
        btnRight.addListener(rightUpListener)


        btnAttack.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                handleInput(Direction.SELECT); return true
            }
        })
    }

    private fun handleInput(direction: Direction) {
        if (currentState == SelectionState.READY) {
            if (direction == Direction.SELECT) startGame()
            return
        }

        when (direction) {
            Direction.LEFT -> cursorX = (cursorX - 1 + cols) % cols
            Direction.RIGHT -> cursorX = (cursorX + 1) % cols

            Direction.SELECT -> {

                val index = cursorX
                if (index >= availableArenas.size) return
                arenaChoice = availableArenas[index]

                currentState = SelectionState.READY

                btnUp.isVisible = false
                btnDown.isVisible = false
                btnLeft.isVisible = false
                btnRight.isVisible = false
                btnAttack.isVisible = false
                btnFight.isVisible = true
            }
            else -> {}
        }

        if (currentState != SelectionState.READY) {
            arenaNameLabel.setText(availableArenas[cursorX].displayName)
        }
    }

    private fun startGame() {
        if (arenaChoice == null) return

        Gdx.app.log("Select", "Start Game! P1: $p1Choice vs P2: $p2Choice on Arena: $arenaChoice")

        game.screen = GameScreen(game, p1Choice, p2Choice, arenaChoice!!)

        dispose()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = stage.camera.combined
        batch.begin()

        batch.draw(backgroundTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        val mapToDraw: ArenaID? = if (currentState == SelectionState.READY) {
            arenaChoice
        } else {
            availableArenas[cursorX]
        }

        if (mapToDraw != null) {
            val tex = portraitTextures[mapToDraw]!!

            val displayWidth = Gdx.graphics.width * 0.5f
            val displayHeight = displayWidth * (tex.height.toFloat() / tex.width.toFloat())
            val posX = (Gdx.graphics.width - displayWidth) / 2f
            val posY = (Gdx.graphics.height - displayHeight) / 2f + 50f

            batch.draw(tex, posX, posY, displayWidth, displayHeight)
        }


        batch.end()

        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
        batch.dispose()
        backgroundTexture.dispose()
        fightButtonTexture.dispose()
        selectorTexture.dispose()
        font.dispose()
        emptyTexture.dispose()
        buttonTextures.forEach { it.dispose() }
        portraitTextures.values.forEach { it.dispose() }
    }

    override fun show() { Gdx.input.inputProcessor = stage }
    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    private enum class Direction { UP, DOWN, LEFT, RIGHT, SELECT }
}
