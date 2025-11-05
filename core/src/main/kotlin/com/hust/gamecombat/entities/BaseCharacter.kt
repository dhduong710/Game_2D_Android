package com.hust.gamecombat.entities

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

abstract class BaseCharacter(characterID: CharacterID) {
    enum class State {
        IDLE, RUN, JUMP, BLOCK, HIT,
        ATTACK_NORMAL, SKILL_1, SKILL_2, SKILL_3
    }
    internal var state = State.IDLE

    var isFacingRight = true
    var isAlive = true

    var x = 0f
    var y = 0f
    var velocityY = 0f
    val gravity = -900f
    var jumpForce = 400f
    open var speed = 200f
    var isOnGround = true

    var maxHp = 100
    var currentHp = 100

    var maxMp = 100
    var currentMp = 100
    var mpRegenRate = 5f

    private var mpRegenAccumulator = 0f

    protected val skill1Cost = 15
    protected val skill2Cost = 25
    protected val skill3Cost = 40

    protected var actionTimer = 0f
    private val hitStunDuration = 0.3f
    protected val attackDuration = 0.4f

    protected val portraitTex: Texture
    protected val idleTex: Texture
    protected val runTex: Texture
    protected val jumpTex: Texture
    protected val blockTex: Texture
    protected val hitTex: Texture
    protected val attackNormalTex: Texture
    protected val skill1Tex: Texture
    protected val skill2Tex: Texture
    protected val skill3Tex: Texture

    internal val portraitRegion: TextureRegion
    protected val idleRegion: TextureRegion
    protected val runRegion: TextureRegion
    protected val jumpRegion: TextureRegion
    protected val blockRegion: TextureRegion
    protected val hitRegion: TextureRegion
    protected val attackNormalRegion: TextureRegion
    protected val skill1Region: TextureRegion
    protected val skill2Region: TextureRegion
    protected val skill3Region: TextureRegion

    init {
        val path = characterID.assetPath

        portraitTex = Texture("$path/portrait.png")
        idleTex = Texture("$path/idle.png")
        runTex = Texture("$path/run.png")
        jumpTex = Texture("$path/jump.png")
        blockTex = Texture("$path/block.png")
        hitTex = Texture("$path/hit.png")
        attackNormalTex = Texture("$path/attack_normal.png")
        skill1Tex = Texture("$path/skill_1.png")
        skill2Tex = Texture("$path/skill_2.png")
        skill3Tex = Texture("$path/skill_3.png")

        portraitRegion = TextureRegion(portraitTex)
        idleRegion = TextureRegion(idleTex)
        runRegion = TextureRegion(runTex)
        jumpRegion = TextureRegion(jumpTex)
        blockRegion = TextureRegion(blockTex)
        hitRegion = TextureRegion(hitTex)
        attackNormalRegion = TextureRegion(attackNormalTex)
        skill1Region = TextureRegion(skill1Tex)
        skill2Region = TextureRegion(skill2Tex)
        skill3Region = TextureRegion(skill3Tex)

        currentHp = maxHp
    }

    open fun getCurrentFrame(): TextureRegion {
        return when (state) {
            State.HIT -> hitRegion
            State.BLOCK -> blockRegion
            State.ATTACK_NORMAL -> attackNormalRegion
            State.SKILL_1 -> skill1Region
            State.SKILL_2 -> skill2Region
            State.SKILL_3 -> skill3Region
            State.JUMP -> jumpRegion
            State.RUN -> runRegion
            else -> idleRegion
        }
    }

    open fun update(delta: Float, groundY: Float) {
        if (!isAlive) return

        velocityY += gravity * delta
        y += velocityY * delta

        if (y <= groundY) {
            y = groundY
            velocityY = 0f
            if (!isOnGround) {
                isOnGround = true
                if (state == State.JUMP || state == State.HIT) {
                    state = State.IDLE
                }
            }
        } else {
            isOnGround = false
        }

        if (currentMp < maxMp) {
            mpRegenAccumulator += mpRegenRate * delta

            if (mpRegenAccumulator >= 1f) {
                val amountToRegen = mpRegenAccumulator.toInt()
                currentMp += amountToRegen
                mpRegenAccumulator -= amountToRegen

                if (currentMp > maxMp) currentMp = maxMp
            }
        }

        if (actionTimer > 0) {
            actionTimer -= delta
            if (actionTimer <= 0) {
                state = State.IDLE
            }
        }
    }

    open fun draw(batch: SpriteBatch, scale: Float) {
        if (!isAlive) return

        val frame = getCurrentFrame()
        val width = frame.regionWidth * scale
        val height = frame.regionHeight * scale

        if (isFacingRight) {
            batch.draw(frame, x, y, width, height)
        } else {
            batch.draw(frame, x + width, y, -width, height)     // Flip horizontally when facing left
        }
    }

    open fun dispose() {
        portraitTex.dispose()
        idleTex.dispose()
        runTex.dispose()
        jumpTex.dispose()
        blockTex.dispose()
        hitTex.dispose()
        attackNormalTex.dispose()
        skill1Tex.dispose()
        skill2Tex.dispose()
        skill3Tex.dispose()
    }

    fun isBusy(): Boolean {
        return state == State.HIT ||
            state == State.ATTACK_NORMAL ||
            state == State.SKILL_1 ||
            state == State.SKILL_2 ||
            state == State.SKILL_3
    }

    open fun moveLeft(delta: Float) {
        if (isBusy() || state == State.BLOCK) return
        x -= speed * delta
        isFacingRight = false
        if (isOnGround) state = State.RUN
    }

    open fun moveRight(delta: Float) {
        if (isBusy() || state == State.BLOCK) return
        x += speed * delta
        isFacingRight = true
        if (isOnGround) state = State.RUN
    }

    open fun stopMoving() {
        if (state == State.RUN) {
            state = State.IDLE
        }
    }

    open fun jump() {
        if (isOnGround && !isBusy() && state != State.BLOCK) {
            velocityY = jumpForce
            isOnGround = false
            state = State.JUMP
        }
    }


    open fun block() {

        if (state == State.BLOCK) return

        if (isOnGround && !isBusy()) {

            if (currentMp >= 50) {

                state = State.BLOCK

                currentMp = 0
            }

        }
    }

    open fun stopBlocking() {
        if (state == State.BLOCK) {
            state = State.IDLE
        }
    }

    open fun attackNormal() {
        if (isOnGround && !isBusy() && state != State.BLOCK) {
            state = State.ATTACK_NORMAL
            actionTimer = attackDuration
        }
    }

    open fun useSkill1() {
        if (isOnGround && !isBusy() && state != State.BLOCK) {
            if (currentMp >= skill1Cost) {
                currentMp -= skill1Cost
                state = State.SKILL_1
                actionTimer = 0.5f
            }
        }
    }

    open fun useSkill2() {
        if (isOnGround && !isBusy() && state != State.BLOCK) {
            if (currentMp >= skill2Cost) {
                currentMp -= skill2Cost
                state = State.SKILL_2
                actionTimer = 0.7f
            }
        }
    }

    open fun useSkill3() {
        if (isOnGround && !isBusy() && state != State.BLOCK) {
            if (currentMp >= skill3Cost) {
                currentMp -= skill3Cost
                state = State.SKILL_3
                actionTimer = 1.2f
            }
        }
    }

    open fun takeDamage(damage: Int) {
        if (!isAlive) return

        if (state == State.BLOCK) {
            return
        }

        currentHp -= damage
        if (currentHp <= 0) {
            currentHp = 0
            isAlive = false
        } else {
            state = State.HIT
            actionTimer = hitStunDuration
            velocityY = 0f          // Stop falling
        }
    }
}
