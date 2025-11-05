package com.hust.gamecombat

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.hust.gamecombat.entities.BaseCharacter
import com.hust.gamecombat.entities.CharacterID
import com.hust.gamecombat.entities.Enemy
import com.hust.gamecombat.entities.Player
import kotlin.math.abs

class GameScreen(
    private val game: Game,
    private val p1ID: CharacterID,
    private val p2ID: CharacterID,
    private val arenaID: ArenaID
) : Screen {

    private val batch: SpriteBatch = SpriteBatch()
    private val stage: Stage = Stage(ScreenViewport(), batch)
    private lateinit var player: Player
    private lateinit var enemy: Enemy

    private var playerLastState: BaseCharacter.State = BaseCharacter.State.IDLE

    private var enemyLastState: BaseCharacter.State = BaseCharacter.State.IDLE

    private val groundY = 120f
    private val characterScale = 0.5f
    private lateinit var arenaTexture: Texture
    private enum class GameState { RUNNING, PLAYER_WIN, PLAYER_LOSE }
    private var gameState = GameState.RUNNING
    private var gameTimer = 60f
    private var gameOverTimer = 0f
    private val gameOverDelay = 3f

    private lateinit var btnUp: ImageButton
    private lateinit var btnDown: ImageButton
    private lateinit var btnLeft: ImageButton
    private lateinit var btnRight: ImageButton
    private lateinit var btnAttack: ImageButton
    private lateinit var btnSkill1: ImageButton
    private lateinit var btnSkill2: ImageButton
    private lateinit var btnSkill3: ImageButton

    private lateinit var font: BitmapFont
    private lateinit var timerLabel: Label
    private lateinit var hpBarFrame: Texture
    private lateinit var hpBarFill: Texture
    private lateinit var mpBarFill: Texture
    private lateinit var avatarFrame: Texture
    private lateinit var timerFrame: Texture
    private lateinit var winBanner: Texture
    private lateinit var loseBanner: Texture

    private var playerCanDealDamage = true
    private var enemyCanDealDamage = true

    override fun show() {

        Gdx.input.inputProcessor = stage
        arenaTexture = Texture(arenaID.assetPath)
        font = BitmapFont(Gdx.files.internal("fonts/main_font.fnt"))
        hpBarFrame = Texture("ui/hud/hp_bar_frame.png")
        hpBarFill = Texture("ui/hud/hp_bar_fill.png")
        mpBarFill = Texture("ui/hud/mp_bar_fill.png")
        avatarFrame = Texture("ui/hud/avatar_frame.png")
        timerFrame = Texture("ui/hud/timer_frame.png")
        winBanner = Texture("ui/results/banner_win.png")
        loseBanner = Texture("ui/results/banner_lose.png")
        player = Player(p1ID)
        player.x = Gdx.graphics.width / 4f
        player.y = groundY
        enemy = Enemy(p2ID, Gdx.graphics.width * 0.75f, groundY)

        setupControls()
        setupHud()
    }

    private fun setupControls() {

        val upTex = Texture("ui/controls/btn_up.png")
        val downTex = Texture("ui/controls/btn_down.png")
        val leftTex = Texture("ui/controls/btn_left.png")
        val rightTex = Texture("ui/controls/btn_right.png")
        val attackTex = Texture("ui/controls/btn_attack.png")
        val skill1Tex = Texture("ui/controls/btn_skill1.png")
        val skill2Tex = Texture("ui/controls/btn_skill2.png")
        val skill3Tex = Texture("ui/controls/btn_skill3.png")


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
        val skillBtnSize = 192f
        val spacing = 20f
        val actionBaseX = Gdx.graphics.width - mainBtnSize - 50f
        val actionBaseY = 50f

        btnAttack = ImageButton(TextureRegionDrawable(attackTex))
        btnAttack.setSize(mainBtnSize, mainBtnSize)
        btnAttack.setPosition(actionBaseX, actionBaseY)
        stage.addActor(btnAttack)


        btnSkill1 = ImageButton(TextureRegionDrawable(skill3Tex))
        btnSkill1.setSize(skillBtnSize, skillBtnSize)
        btnSkill1.setPosition(actionBaseX + (mainBtnSize - skillBtnSize) / 2f, actionBaseY + mainBtnSize + spacing)
        stage.addActor(btnSkill1)


        btnSkill3 = ImageButton(TextureRegionDrawable(skill1Tex))
        btnSkill3.setSize(skillBtnSize, skillBtnSize)
        btnSkill3.setPosition(actionBaseX - skillBtnSize - spacing, actionBaseY + (mainBtnSize - skillBtnSize) / 2f)
        stage.addActor(btnSkill3)


        btnSkill2 = ImageButton(TextureRegionDrawable(skill2Tex))
        btnSkill2.setSize(skillBtnSize, skillBtnSize)
        val midX = (btnSkill1.x + btnSkill3.x) / 2f
        val midY = (btnSkill1.y + btnSkill3.y) / 2f
        btnSkill2.setPosition(midX, midY)
        stage.addActor(btnSkill2)
    }


    private fun setupHud() {
        font.region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)

        timerLabel = Label(gameTimer.toInt().toString(), Label.LabelStyle(font, Color.WHITE))
        timerLabel.setFontScale(4.0f)
        timerLabel.setSize(200f, 100f)
        timerLabel.setAlignment(Align.center)
        timerLabel.setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height - 75f, Align.center)
        stage.addActor(timerLabel)
    }


    override fun render(delta: Float) {

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (gameState == GameState.RUNNING) {
            gameTimer -= delta
            if (gameTimer < 0) gameTimer = 0f
            timerLabel.setText(gameTimer.toInt().toString())

            if (gameTimer <= 0f) {
                checkWinLoseByTime()
            }

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

        batch.begin()
        drawWorld()
        drawHud()
        drawResultScreen()
        batch.end()

        stage.act(delta)
        stage.draw()
    }


    private fun handleInput(delta: Float) {
        if (btnLeft.isPressed) player.moveLeft(delta)
        else if (btnRight.isPressed) player.moveRight(delta)
        else player.stopMoving()

        if (btnDown.isPressed) player.block()
        else player.stopBlocking()

        if (btnUp.isPressed) player.jump()

        if (btnAttack.isPressed) player.attackNormal()


        if (btnSkill3.isPressed) player.useSkill1()
        if (btnSkill2.isPressed) player.useSkill2()
        if (btnSkill1.isPressed) player.useSkill3()
    }

    private fun clampCharacterPositions() {
        val screenWidth = Gdx.graphics.width.toFloat()

        val playerWidth = player.getCurrentFrame().regionWidth * characterScale

        player.x = player.x.coerceIn(0f, screenWidth - playerWidth)

        val enemyWidth = enemy.getCurrentFrame().regionWidth * characterScale
        enemy.x = enemy.x.coerceIn(0f, screenWidth - enemyWidth)
    }

    private fun updateGame(delta: Float) {

        player.update(delta, groundY)
        enemy.update(delta, groundY)
        enemy.updateAI(delta, player)


        if (player.state != playerLastState) {

            if (player.state == BaseCharacter.State.ATTACK_NORMAL ||
                player.state == BaseCharacter.State.SKILL_1 ||
                player.state == BaseCharacter.State.SKILL_2 ||
                player.state == BaseCharacter.State.SKILL_3)
            {
                playerCanDealDamage = true
            }
        }
        playerLastState = player.state


        if (enemy.state != enemyLastState) {

            if (enemy.state == BaseCharacter.State.ATTACK_NORMAL ||
                enemy.state == BaseCharacter.State.SKILL_1 ||
                enemy.state == BaseCharacter.State.SKILL_2 ||
                enemy.state == BaseCharacter.State.SKILL_3)
            {
                enemyCanDealDamage = true
            }
        }
        enemyLastState = enemy.state

        clampCharacterPositions()
    }

    private fun checkCombat() {
        val distance = abs(player.x - enemy.x)

        val hitRange = 250f
        val skillRange = 270f

        val damage = 10
        val skill1Dmg = 15
        val skill2Dmg = 20
        val skill3Dmg = 25

        if (playerCanDealDamage &&
            player.isFacingRight == (enemy.x > player.x))
        {
            var dealtDamage = false

            when (player.state) {
                BaseCharacter.State.ATTACK_NORMAL -> {
                    if (distance < hitRange) {
                        enemy.takeDamage(damage)
                        dealtDamage = true
                    }
                }
                BaseCharacter.State.SKILL_1 -> {
                    if (distance < skillRange) {
                        enemy.takeDamage(skill1Dmg)
                        dealtDamage = true
                    }
                }
                BaseCharacter.State.SKILL_2 -> {
                    if (distance < skillRange) {
                        enemy.takeDamage(skill2Dmg)
                        dealtDamage = true
                    }
                }
                BaseCharacter.State.SKILL_3 -> {
                    if (distance < skillRange) {
                        enemy.takeDamage(skill3Dmg)
                        dealtDamage = true
                    }
                }
                else -> {}
            }


            if (dealtDamage) {
                playerCanDealDamage = false
            }
        }


        if (enemyCanDealDamage &&
            enemy.isFacingRight == (player.x > enemy.x))
        {
            var dealtDamage = false

            when (enemy.state) {
                BaseCharacter.State.ATTACK_NORMAL -> {
                    if (distance < hitRange) {
                        player.takeDamage(damage)
                        dealtDamage = true
                    }
                }
                BaseCharacter.State.SKILL_1 -> {
                    if (distance < skillRange) {
                        player.takeDamage(skill1Dmg)
                        dealtDamage = true
                    }
                }
                BaseCharacter.State.SKILL_2 -> {
                    if (distance < skillRange) {
                        player.takeDamage(skill2Dmg)
                        dealtDamage = true
                    }
                }
                BaseCharacter.State.SKILL_3 -> {
                    if (distance < skillRange) {
                        player.takeDamage(skill3Dmg)
                        dealtDamage = true
                    }
                }
                else -> {}
            }

            if (dealtDamage) {
                enemyCanDealDamage = false
            }
        }
    }

    private fun checkWinLose() {
        if (gameState != GameState.RUNNING) return
        if (!player.isAlive) gameState = GameState.PLAYER_LOSE
        else if (!enemy.isAlive) gameState = GameState.PLAYER_WIN
    }

    private fun checkWinLoseByTime() {

        if (gameState != GameState.RUNNING) return

        if (player.currentHp > enemy.currentHp) {
            gameState = GameState.PLAYER_WIN
            enemy.isAlive = false
        } else {
            gameState = GameState.PLAYER_LOSE
            player.isAlive = false
        }
    }
    private fun drawWorld() {
        batch.draw(arenaTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        player.draw(batch, characterScale)
        enemy.draw(batch, characterScale)
    }


    private fun drawHud() {
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        val barWidth = screenWidth / 2f - 160f
        val barHeight = 60f
        val barY = screenHeight - 90f

        val avatarSize = 160f
        val portraitSize = 140f
        val avatarY = screenHeight - 180f

        drawHealthBar(player, 80f, barY, barWidth, barHeight)
        batch.draw(avatarFrame, 10f, avatarY, avatarSize, avatarSize)
        batch.draw(player.portraitRegion, 20f, avatarY + 10f, portraitSize, portraitSize)

        drawHealthBar(enemy, screenWidth - 80f - barWidth, barY, barWidth, barHeight)
        batch.draw(avatarFrame, screenWidth - 10f - avatarSize, avatarY, avatarSize, avatarSize)
        batch.draw(enemy.portraitRegion, screenWidth - 20f - portraitSize, avatarY + 10f, portraitSize, portraitSize)

        val timerFrameWidth = 220f
        val timerFrameHeight = 120f
        batch.draw(timerFrame,
            screenWidth / 2f - timerFrameWidth / 2f,
            screenHeight - 130f,
            timerFrameWidth,
            timerFrameHeight
        )
    }

    private fun drawHealthBar(character: BaseCharacter, x: Float, y: Float, width: Float, height: Float) {
        val hpRatio = character.currentHp.toFloat() / character.maxHp.toFloat()
        batch.draw(hpBarFill, x, y, width * hpRatio, height)
        batch.draw(hpBarFrame, x, y, width, height)

        val mpRatio = character.currentMp.toFloat() / character.maxMp.toFloat()
        batch.draw(mpBarFill, x, y - 30f, width * mpRatio, 70f)
    }

    private fun drawResultScreen() {
        if (gameState == GameState.RUNNING) return
        val banner = if (gameState == GameState.PLAYER_WIN) winBanner else loseBanner
        val x = Gdx.graphics.width / 2f - banner.width / 2f
        val y = Gdx.graphics.height / 2f - banner.height / 2f
        batch.draw(banner, x, y)
    }

    override fun dispose() {
        batch.dispose()
        stage.dispose()
        font.dispose()
        player.dispose()
        enemy.dispose()
        arenaTexture.dispose()
        hpBarFrame.dispose()
        hpBarFill.dispose()
        mpBarFill.dispose()
        avatarFrame.dispose()
        timerFrame.dispose()
        winBanner.dispose()
        loseBanner.dispose()

    }
    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
}
