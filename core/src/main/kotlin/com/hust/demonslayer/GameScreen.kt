package com.hust.demonslayer

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.hust.demonslayer.entities.Enemy
import com.hust.demonslayer.entities.Player
import kotlin.math.abs

class GameScreen(private val game: Game) : Screen {
    private lateinit var batch: SpriteBatch         // for drawing textures
    private lateinit var stage: Stage               // for handling user input and UI elements
    private lateinit var player: Player
    private lateinit var enemy: Enemy

    private lateinit var btnLeft: ImageButton
    private lateinit var btnRight: ImageButton
    private lateinit var btnJump: ImageButton
    private lateinit var btnAttack: ImageButton

    private lateinit var hpBarFrame: Texture
    private lateinit var hpBarFill: Texture

    private lateinit var winTexture: Texture
    private lateinit var loseTexture: Texture

    private val groundLevel = 100f
    private lateinit var bgTexture: Texture

    private var enemyAttackTimer = 0f
    private val enemyAttackCooldown = 1.5f
    private val characterScale = 0.5f

    private var gameState: GameState = GameState.RUNNING
    private var gameOverTimer = 0f
    private val gameOverDelay = 3f

    enum class GameState {
        RUNNING, PLAYER_WIN, PLAYER_LOSE
    }

    override fun show() {
        batch = SpriteBatch()
        stage = Stage(ScreenViewport(), batch)
        Gdx.input.inputProcessor = stage

        bgTexture = Texture("background.png")
        hpBarFrame = Texture("ui/hp_bar_frame.png")
        hpBarFill = Texture("ui/hp_bar_fill.png")

        winTexture = Texture("ui/win.png")
        loseTexture = Texture("ui/lose.png")

        setupButtons()

        player = Player()
        player.x = 50f
        player.y = groundLevel
        enemy = Enemy(Gdx.graphics.width / 2f, groundLevel)
    }

    private fun setupButtons() {

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
    }

    override fun render(delta: Float) {
        if (gameState == GameState.RUNNING) {
            handleInput(delta)
            updateGame(delta)
            checkCombat()
            checkWinLose()
        } else {
            gameOverTimer += delta
            if (gameOverTimer >= gameOverDelay) {
                game.screen = MenuScreen(game)
                dispose()
                return
            }
        }

        clearScreen()
        drawWorld()
        drawHud()

        drawResultScreen()

        stage.act(delta)
        stage.draw()
    }

    private fun handleInput(delta: Float) {

        if (btnLeft.isPressed) player.moveLeft(delta)
        if (btnRight.isPressed) player.moveRight(delta)
        if (btnJump.isPressed) player.jump()
        if (btnAttack.isPressed) player.attack()
    }

    private fun updateGame(delta: Float) {

        player.update(delta, groundLevel)
        enemy.update(delta, groundLevel, player.x)
    }

    private fun checkCombat() {

        if (player.isAttacking && !player.hasDealtDamageThisAttack && enemy.isAlive &&
            abs(player.x - enemy.x) < 80f) {
            enemy.takeDamage(20)
            player.hasDealtDamageThisAttack = true
        }

        if (enemy.isAlive && abs(player.x - enemy.x) < 50f) {
            enemyAttackTimer += Gdx.graphics.deltaTime
            if (enemyAttackTimer >= enemyAttackCooldown) {
                player.takeDamage(15)
                enemyAttackTimer = 0f
            }
        } else {
            enemyAttackTimer = 0f
        }
    }

    private fun checkWinLose() {
        if (player.currentHp <= 0) {
            gameState = GameState.PLAYER_LOSE
        } else if (!enemy.isAlive) {
            gameState = GameState.PLAYER_WIN
        }
    }

    private fun clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    private fun drawWorld() {
        batch.begin()
        batch.draw(bgTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        val frame = player.getCurrentFrame()
        batch.draw(frame, player.x, player.y, frame.regionWidth * characterScale, frame.regionHeight * characterScale)
        if (enemy.isAlive) {
            enemy.draw(batch, characterScale)
        }
        batch.end()
    }

    private fun drawHud() {
        batch.begin()

        val barWidth = 200f
        val barHeight = 25f

        drawHealthBar(player.x, player.y, player.getCurrentFrame().regionWidth, player.getCurrentFrame().regionHeight, player.currentHp, player.maxHp, barWidth, barHeight)

        if(enemy.isAlive) {
            drawHealthBar(enemy.x, enemy.y, enemy.textureRegion.regionWidth, enemy.textureRegion.regionHeight, enemy.currentHp, enemy.maxHp, barWidth, barHeight)
        }
        batch.end()
    }

    private fun drawHealthBar(charX: Float, charY: Float, charWidth: Int, charHeight: Int, currentHp: Int, maxHp: Int, barWidth: Float, barHeight: Float) {
        val charCenterX = charX + (charWidth * characterScale / 2f)
        val hpBarX = charCenterX - (barWidth / 2f)
        val hpBarY = charY + (charHeight * characterScale) + 15f

        batch.draw(hpBarFrame, hpBarX, hpBarY, barWidth, barHeight)

        val hpRatio = currentHp.toFloat() / maxHp.toFloat()
        val currentFillWidth = barWidth * hpRatio
        batch.draw(hpBarFill, hpBarX, hpBarY, if (currentFillWidth > 0) currentFillWidth else 0f, barHeight)
    }

    private fun drawResultScreen() {
        if (gameState == GameState.RUNNING) return

        batch.begin()
        val resultTexture = if (gameState == GameState.PLAYER_WIN) winTexture else loseTexture
        val x = Gdx.graphics.width / 2f - resultTexture.width / 2f
        val y = Gdx.graphics.height / 2f - resultTexture.height / 2f
        batch.draw(resultTexture, x, y)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        stage.dispose()
        player.dispose()
        enemy.dispose()
        bgTexture.dispose()
        hpBarFrame.dispose()
        hpBarFill.dispose()
        winTexture.dispose()
        loseTexture.dispose()
    }

    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
}
