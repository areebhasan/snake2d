package com.snakegame;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


import java.util.LinkedList;

public class PrimaryController {

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    private LinkedList<int[]> snake;
    private int[] food;
    private int gridSize = 20;
    private int gridWidth = 20;
    private int gridHeight = 20;
    private Direction direction = Direction.RIGHT;
    private boolean running = false;
    private long lastUpdate = 0;
    private long speed = 200_000_000; // Speed in nanoseconds (initial speed)
    private boolean gameOver = false;


    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private void startGame() {
        snake.clear();
        snake.add(new int[]{gridWidth / 2, gridHeight / 2}); // Initial position of the snake
        direction = Direction.RIGHT; // Initial direction
        spawnFood(); // Place the first food
        running = true;
        gameOver = false; // Reset the game over flag
    }
    
    
    
    public void initialize() {
        snake = new LinkedList<>();
        startGame();
    
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running && now - lastUpdate >= speed) {
                    updateGame();
                    drawGame(gc);
                    lastUpdate = now;
                }
            }
        }.start();
        
    
        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(this::handleKeyPress);
    }
    

    private void updateGame() {
        // Move the snake
        int headX = snake.getFirst()[0];
        int headY = snake.getFirst()[1];
    
        switch (direction) {
            case UP:    headY--; break;
            case DOWN:  headY++; break;
            case LEFT:  headX--; break;
            case RIGHT: headX++; break;
        }
    
        // Wrap the snake position if it goes off the screen
        if (headX < 0) {
            headX = gridWidth - 1;
        } else if (headX >= gridWidth) {
            headX = 0;
        }
        if (headY < 0) {
            headY = gridHeight - 1;
        } else if (headY >= gridHeight) {
            headY = 0;
        }
    
        // Check if the snake eats itself
        for (int i = 1; i < snake.size(); i++) {
            int[] segment = snake.get(i);
            if (segment[0] == headX && segment[1] == headY) {
                running = false; // Stop the game if the snake collides with itself
                gameOver = true;
                return;
            }
        }
    
        // Move the snake forward
        snake.addFirst(new int[]{headX, headY});
    
        // Check if the snake eats the food
        if (headX == food[0] && headY == food[1]) {
            spawnFood(); // Respawn food
        } else if (snake.size() > 1) {
            snake.removeLast(); // Remove the tail segment if food is not eaten
        }
    }
    
    

    private void drawGame(GraphicsContext gc) {
        // Clear the canvas
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    
        // Draw the snake's body
        gc.setFill(Color.GREEN);
        for (int i = 1; i < snake.size(); i++) { // Start from 1 to skip the head
            int[] segment = snake.get(i);
            gc.fillRect(segment[0] * gridSize, segment[1] * gridSize, gridSize, gridSize);
        }
    
        // Draw the snake's head
        int[] head = snake.getFirst();
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(head[0] * gridSize, head[1] * gridSize, gridSize, gridSize);
    
        // Draw the food
        gc.setFill(Color.RED);
        gc.fillRect(food[0] * gridSize, food[1] * gridSize, gridSize, gridSize);
    
        // Update the score label
        scoreLabel.setText("Score: " + (snake.size() - 1));

        //Game Over display
        if (gameOver) {
                gc.setFill(Color.RED);
                gc.setTextAlign(TextAlignment.CENTER); // Align text to center
                gc.setFont(new Font("Arial", 20)); // Set font size for visibility
                gc.fillText("Game Over", gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2);
            }
    }
    

    private void spawnFood() {
        int x = (int) (Math.random() * gridWidth);
        int y = (int) (Math.random() * gridHeight);
        food = new int[]{x, y};
    }
    
    @FXML
    private void handleRestart() {
        startGame(); // Call startGame method to restart the game
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.R) {
            startGame(); // Restart the game
        } else if (event.getCode() == KeyCode.UP && direction != Direction.DOWN) {
            direction = Direction.UP;
        } else if (event.getCode() == KeyCode.DOWN && direction != Direction.UP) {
            direction = Direction.DOWN;
        } else if (event.getCode() == KeyCode.LEFT && direction != Direction.RIGHT) {
            direction = Direction.LEFT;
        } else if (event.getCode() == KeyCode.RIGHT && direction != Direction.LEFT) {
            direction = Direction.RIGHT;
        }
    }
}

