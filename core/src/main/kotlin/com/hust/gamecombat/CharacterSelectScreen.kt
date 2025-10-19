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
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.hust.gamecombat.entities.CharacterID
import kotlin.math.max
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

abstract class ClickListener : InputListener() {
    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        return true
    }
    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        clicked(event, x, y)
    }
    abstract fun clicked(event: InputEvent?, x: Float, y: Float)
}

class CharacterSelectScreen(private val game: Game) : Screen {

    private val stage = Stage(ScreenViewport())
    private val batch = SpriteBatch()       // draw texture, image, etc.

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

    private val availableCharacters = CharacterID.values()
    private var p1Choice: CharacterID? = null
    private var p2Choice: CharacterID? = null

    private val cols = 2
    private val rows = 1
    private val portraitSize = 200f
    private val portraitSpacing = 50f
    private var cursorX = 0
    private val gridStartX: Float
    private val gridStartY: Float

    private val titleLabel: Label
    private val p1Label: Label
    private val p2Label: Label
    private val p1NameLabel: Label
    private val p2NameLabel: Label

    private val portraitTextures = mutableMapOf<CharacterID, Texture>()

    private val emptyTexture: Texture
    private lateinit var p1PortraitImage: Image
    private lateinit var p2PortraitImage: Image


    init {
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        // Pre-load all character portraits into a map for quick access.
        availableCharacters.forEach { charID ->
            val tex = Texture(Gdx.files.internal("${charID.assetPath}/portrait.png"))
            portraitTextures[charID] = tex
        }

        // Create a 1x1 transparent texture to use as a placeholder for portraits.
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(0f, 0f, 0f, 0f)
        pixmap.fill()
        emptyTexture = Texture(pixmap)
        pixmap.dispose()

        // Set the texture filter to prevent the font from looking blurry.
        font.region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)

        titleLabel = Label("CHARACTER SELECTION", Label.LabelStyle(font, Color.WHITE))
        titleLabel.setFontScale(4.0f)
        titleLabel.setSize(screenWidth, 50f)
        titleLabel.setPosition(0f, screenHeight - 100f)
        titleLabel.setAlignment(Align.center)
        stage.addActor(titleLabel)


        val gridTotalWidth = (cols * portraitSize) + (max(0, cols - 1) * portraitSpacing)
        gridStartX = (screenWidth - gridTotalWidth) / 2f
        gridStartY = screenHeight / 2f

        val p1NameX = gridStartX + portraitSize / 2f
        val p2NameX = gridStartX + portraitSize + portraitSpacing + portraitSize / 2f
        val nameY = gridStartY - 50f

        p1NameLabel = Label(availableCharacters[0].displayName, Label.LabelStyle(font, Color.WHITE))
        p1NameLabel.setFontScale(2.5f)
        p1NameLabel.setAlignment(Align.center)
        p1NameLabel.setPosition(p1NameX, nameY, Align.center)
        stage.addActor(p1NameLabel)

        p2NameLabel = Label(availableCharacters[1].displayName, Label.LabelStyle(font, Color.WHITE))
        p2NameLabel.setFontScale(2.5f)
        p2NameLabel.setAlignment(Align.center)
        p2NameLabel.setPosition(p2NameX, nameY, Align.center)
        stage.addActor(p2NameLabel)

        val selectedPortraitSize = 100f
        val selectedY = 120f
        val p1X = screenWidth * 0.3f
        val p2X = screenWidth * 0.7f

        p1Label = Label("PLAYER 1", Label.LabelStyle(font, Color.CYAN))
        p1Label.setFontScale(2.0f)
        p1Label.setSize(selectedPortraitSize, 50f)
        p1Label.setAlignment(Align.center)
        p1Label.setPosition(p1X, selectedY + selectedPortraitSize + 20f, Align.center)
        stage.addActor(p1Label)

        // A Stack is used to place a colored frame on top of the character image.
        val p1Stack = Stack()
        p1Stack.setSize(selectedPortraitSize, selectedPortraitSize)
        p1Stack.setPosition(p1X, selectedY, Align.center)

        p1PortraitImage = Image(emptyTexture)
        p1PortraitImage.setScaling(Scaling.fit)

        val p1ImageContainer = Container(p1PortraitImage)
        p1ImageContainer.pad(20f)
        p1ImageContainer.fill(true)
        p1Stack.add(p1ImageContainer)

        val p1Frame = Image(selectorTexture)
        p1Frame.color = Color.CYAN
        p1Stack.add(p1Frame)

        stage.addActor(p1Stack)

        p2Label = Label("PLAYER 2", Label.LabelStyle(font, Color.RED))
        p2Label.setFontScale(2.0f)
        p2Label.setSize(selectedPortraitSize, 50f)
        p2Label.setAlignment(Align.center)
        p2Label.setPosition(p2X, selectedY + selectedPortraitSize + 20f, Align.center)
        stage.addActor(p2Label)

        val p2Stack = Stack()
        p2Stack.setSize(selectedPortraitSize, selectedPortraitSize)
        p2Stack.setPosition(p2X, selectedY, Align.center)

        p2PortraitImage = Image(emptyTexture)
        p2PortraitImage.setScaling(Scaling.fit)

        val p2ImageContainer = Container(p2PortraitImage)
        p2ImageContainer.pad(20f)
        p2ImageContainer.fill(true)
        p2Stack.add(p2ImageContainer)

        val p2Frame = Image(selectorTexture)
        p2Frame.color = Color.RED
        p2Stack.add(p2Frame)

        stage.addActor(p2Stack)

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

        val dpadSize = 150f
        val attackBtnSize = 180f
        val screenWidth = Gdx.graphics.width.toFloat()


        val bottomMargin = 20f
        val sideMargin = 20f

        val dpadCenterX = sideMargin + dpadSize // 20 + 150 = 170f
        val dpadCenterY = bottomMargin + dpadSize // 20 + 150 = 170f


        buttonTextures.add(Texture("ui/controls/btn_up.png"))
        btnUp = ImageButton(TextureRegionDrawable(buttonTextures.last())) // take last element
        btnUp.setSize(dpadSize, dpadSize)
        btnUp.setPosition(dpadCenterX - dpadSize / 2f, dpadCenterY) // (95, 170)
        stage.addActor(btnUp)

        buttonTextures.add(Texture("ui/controls/btn_down.png"))
        btnDown = ImageButton(TextureRegionDrawable(buttonTextures.last()))
        btnDown.setSize(dpadSize, dpadSize)
        btnDown.setPosition(dpadCenterX - dpadSize / 2f, bottomMargin) // (95, 20)
        stage.addActor(btnDown)

        buttonTextures.add(Texture("ui/controls/btn_left.png"))
        btnLeft = ImageButton(TextureRegionDrawable(buttonTextures.last()))
        btnLeft.setSize(dpadSize, dpadSize)
        btnLeft.setPosition(sideMargin, dpadCenterY - dpadSize / 2f) // (20, 95)
        stage.addActor(btnLeft)

        buttonTextures.add(Texture("ui/controls/btn_right.png"))
        btnRight = ImageButton(TextureRegionDrawable(buttonTextures.last()))
        btnRight.setSize(dpadSize, dpadSize)
        btnRight.setPosition(dpadCenterX, dpadCenterY - dpadSize / 2f) // (170, 95)
        stage.addActor(btnRight)



        buttonTextures.add(Texture("ui/controls/btn_attack.png"))
        btnAttack = ImageButton(TextureRegionDrawable(buttonTextures.last()))
        btnAttack.setSize(attackBtnSize, attackBtnSize)

        btnAttack.setPosition(screenWidth - attackBtnSize - sideMargin, bottomMargin)
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
        btnDown.addListener(leftDownListener)

        val rightUpListener = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                handleInput(Direction.RIGHT); return true
            }
        }
        btnRight.addListener(rightUpListener)
        btnUp.addListener(rightUpListener)


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
            Direction.LEFT -> cursorX = (cursorX - 1 + cols) % cols     // (0 - 1 + 5) % 5 = 4
            Direction.RIGHT -> cursorX = (cursorX + 1) % cols           // (4 + 1) % 5 = 0
            Direction.SELECT -> {

                val index = cursorX
                if (index >= availableCharacters.size) return

                val selectedChar = availableCharacters[index]

                if (currentState == SelectionState.SELECTING_P1) {
                    p1Choice = selectedChar

                    p1PortraitImage.drawable = TextureRegionDrawable(portraitTextures[selectedChar])
                    currentState = SelectionState.SELECTING_P2
                    cursorX = (cursorX + 1) % cols
                } else {
                    p2Choice = selectedChar

                    p2PortraitImage.drawable = TextureRegionDrawable(portraitTextures[selectedChar])
                    currentState = SelectionState.READY

                    btnUp.isVisible = false
                    btnDown.isVisible = false
                    btnLeft.isVisible = false
                    btnRight.isVisible = false
                    btnAttack.isVisible = false
                    btnFight.isVisible = true
                }
            }

            else -> { }
        }
    }

    private fun startGame() {
        if (p1Choice == null || p2Choice == null) return

        Gdx.app.log("Select", "Start Game! P1: $p1Choice vs P2: $p2Choice")
        game.screen = GameScreen(game, p1Choice!!, p2Choice!!)
        dispose()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = stage.camera.combined  // set camera for batch
        batch.begin()

        batch.draw(backgroundTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        var charIndex = 0
        for (x in 0 until cols) {
            if (charIndex < availableCharacters.size) {
                val charID = availableCharacters[charIndex]
                val tex = portraitTextures[charID]!!

                val posX = gridStartX + x * (portraitSize + portraitSpacing)

                batch.draw(tex, posX, gridStartY, portraitSize, portraitSize)
                batch.color = Color.WHITE
            }
            charIndex++
        }

        if (currentState != SelectionState.READY) {
            val selectorPadding = 15f
            val extraZoom = 50f
            val selectorSize = portraitSize + (selectorPadding * 2) + extraZoom
            val totalPadding = selectorPadding + (extraZoom / 2f)
            val posX = gridStartX + cursorX * (portraitSize + portraitSpacing) - totalPadding
            val posY = gridStartY - totalPadding

            batch.color = if (currentState == SelectionState.SELECTING_P1) Color.CYAN else Color.RED
            batch.draw(selectorTexture, posX, posY, selectorSize, selectorSize)
            batch.color = Color.WHITE
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
