package com.snakegame.ag.engine

data class Point(val x: Int, val y: Int)

class GameState(val cols: Int, val rows: Int) {
    var snake = mutableListOf(Point(cols / 2, rows / 2))
    var direction = Direction.RIGHT
    var nextDirection = Direction.RIGHT
    var food = Point(0, 0)
    var score = 0
    var isOver = false
    var isPaused = false
    var baseDelay = 150L

    val delay: Long get() = (baseDelay - (score / 5) * 5L).coerceAtLeast(60L)

    init { spawnFood() }

    fun spawnFood() {
        val free = mutableListOf<Point>()
        for (x in 0 until cols) for (y in 0 until rows)
            if (Point(x, y) !in snake) free.add(Point(x, y))
        if (free.isNotEmpty()) food = free.random()
    }

    fun update(): Boolean {
        if (isOver || isPaused) return false
        direction = nextDirection
        val head = snake.first()
        val newHead = when (direction) {
            Direction.UP -> Point(head.x, head.y - 1)
            Direction.DOWN -> Point(head.x, head.y + 1)
            Direction.LEFT -> Point(head.x - 1, head.y)
            Direction.RIGHT -> Point(head.x + 1, head.y)
        }

        if (newHead.x < 0 || newHead.x >= cols || newHead.y < 0 || newHead.y >= rows || newHead in snake) {
            isOver = true; return true
        }

        snake.add(0, newHead)
        return if (newHead == food) {
            score++; spawnFood(); false
        } else {
            snake.removeAt(snake.lastIndex); false
        }
    }

    fun setDir(d: Direction) {
        val opposite = when (d) {
            Direction.UP -> Direction.DOWN; Direction.DOWN -> Direction.UP
            Direction.LEFT -> Direction.RIGHT; Direction.RIGHT -> Direction.LEFT
        }
        if (direction != opposite) nextDirection = d
    }

    fun reset() {
        snake = mutableListOf(Point(cols / 2, rows / 2))
        direction = Direction.RIGHT; nextDirection = Direction.RIGHT
        score = 0; isOver = false; isPaused = false; spawnFood()
    }
}
