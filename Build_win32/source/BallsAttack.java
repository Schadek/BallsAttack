import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BallsAttack extends PApplet {

private STATE_MACHINE machine; 

public void setup() 
{
  if (resolutionX <= 0 || resolutionY <= 0)
  {
    resolutionX = displayWidth; 
    resolutionY = displayHeight;
  }

  size(resolutionX, resolutionY);
  machine = new STATE_MACHINE();
}

public void draw()
{
  machine.update();
}

public void keyPressed()
{
  machine.keyPressed();
}

public void keyReleased()
{
  machine.keyReleased();
}


/*
TODO: 
 - United menus to one class, possibly implementing
 - Actual highscore list
 - Level selection with individual highscore lists
 - Sound
 - Power-Ups maybe
 
 BUGS:
 - Sometimes the balls 'merge' and collide with each other, while rapidly increasing their speed. Looks like a problem in the collision code
 - The playerlist is not yet usable. It will display the correct players and their achievements but won't be able to scroll in the list (also: '-' symbols everywhere)
 */
class AmigaBall 
{
  /*************************************************************************************************************************************************************/
  private PVector position;      //Saves the x/y position of the ball
  private PVector movement;      //Saves the velocity of the ball
  private int radius;            //The radius of the ball
  private int ballColor;       //The color of the ball
  private float minSpawnHeight;  //The minimum height at which a ball can spawn
  private float gravity;         //The gravity of the playfield. Has to be saved for each ball so the balls can be used in the background of the menus
  private int playerEnergy;      //The energy of the player character. Has to be saved for each ball so the balls can be used in the background of the menus
  /*************************************************************************************************************************************************************/


  AmigaBall(PVector tempPos, PVector tempMovement, int tempRadius, int tempcol, float tempGrav, float tempminSpawnHeight)
  {
    position = tempPos;
    movement = tempMovement;
    radius = tempRadius;
    ballColor = tempcol;
    gravity = tempGrav;
    playerEnergy = 100;

    if (minSpawnHeight <= radius)
    {
      minSpawnHeight = radius+1;
    } else 
    {
      minSpawnHeight = tempminSpawnHeight;
    }
  }

  AmigaBall(int tempMinRad, int tempMaxRad, float tempminSpawnHeight, float tempGrav)
  {
    radius = (int) random(tempMinRad, tempMaxRad);
    ballColor = color(random(0, 255), random(0, 255), random(0, 255));
    gravity = tempGrav;
    playerEnergy = 100;
    minSpawnHeight = tempminSpawnHeight;

    position = new PVector(random(radius, width - radius), random(radius, height - minSpawnHeight));

    if (random(0, 1) == 0)
    {
      movement = new PVector(random((-5), (-1)), 0);
    } else
    {
      movement = new PVector(random(1, 5), 0);
    }
    movement.mult(3);
  }

  public void update()
  {
    if (position.y < height - radius)
    {
      movement.y += gravity;
    }

    position.add(movement);
    checkEdgeCollision();
  }

  public void checkEdgeCollision()
  {
    if (position.x > width - radius)
    {
      position.x = width - radius;
      movement.x *= (-1);
    } else if (position.x < radius)
    {
      position.x = radius;
      movement.x *= (-1);
    }
    if (position.y > height - radius)
    {
      position.y = height - radius;
      movement.y *= (-1);
    } else if (position.y < radius)
    {
      position.y = radius;
      movement.y *= (-1);
    }
  }

  public void spawnBalls()
  {
    if (radius > machine.field.getSchwarzschild())
    {
      machine.field.enemyBalls.add(new AmigaBall(new PVector((int)(position.x+(radius/2)), (int)position.y), movement, radius/2, ballColor, gravity, (int)minSpawnHeight));
      machine.field.enemyBalls.add(new AmigaBall(new PVector((int)(position.x-(radius/2)), (int)position.y), new PVector(movement.x*(-1), movement.y), radius/2, ballColor, gravity, (int)minSpawnHeight));
    }
  }

  public void checkBallCollision(AmigaBall collider, boolean isElastic)
  {
    if (isElastic)
    {
      PVector bVect = PVector.sub(collider.position, position);

      float bVectMag = bVect.mag();

      if (bVectMag < radius + collider.radius) 
      {
        float theta  = bVect.heading();
        float sine = sin(theta);
        float cosine = cos(theta);

        PVector[] bTemp = {
          new PVector(), new PVector()
          };

          bTemp[1].x  = cosine * bVect.x + sine * bVect.y;
        bTemp[1].y  = cosine * bVect.y - sine * bVect.x;

        PVector[] vTemp = {
          new PVector(), new PVector()
          };

          vTemp[0].x  = cosine * movement.x + sine * movement.y;
        vTemp[0].y  = cosine * movement.y - sine * movement.x;
        vTemp[1].x  = cosine * collider.movement.x + sine * collider.movement.y;
        vTemp[1].y  = cosine * collider.movement.y - sine * collider.movement.x;

        PVector[] vFinal = {  
          new PVector(), new PVector()
          };

          vFinal[0].x = ((radius*0.1f - collider.radius*0.1f) * vTemp[0].x + 2 * collider.radius*0.1f * vTemp[1].x) / (radius*0.1f + collider.radius*0.1f);
        vFinal[0].y = vTemp[0].y;

        vFinal[1].x = ((collider.radius*0.1f - radius*0.1f) * vTemp[1].x + 2 * radius*0.1f * vTemp[0].x) / (radius*0.1f + collider.radius*0.1f);
        vFinal[1].y = vTemp[1].y;

        bTemp[0].x += vFinal[0].x;
        bTemp[1].x += vFinal[1].x;

        PVector[] bFinal = { 
          new PVector(), new PVector()
          };

          bFinal[0].x = cosine * bTemp[0].x - sine * bTemp[0].y;
        bFinal[0].y = cosine * bTemp[0].y + sine * bTemp[0].x;
        bFinal[1].x = cosine * bTemp[1].x - sine * bTemp[1].y;
        bFinal[1].y = cosine * bTemp[1].y + sine * bTemp[1].x;

        collider.position.x = position.x + bFinal[1].x;
        collider.position.y = position.y + bFinal[1].y;

        position.add(bFinal[0]);

        movement.x = cosine * vFinal[0].x - sine * vFinal[0].y;
        movement.y = cosine * vFinal[0].y + sine * vFinal[0].x;
        collider.movement.x = cosine * vFinal[1].x - sine * vFinal[1].y;
        collider.movement.y = cosine * vFinal[1].y + sine * vFinal[1].x;
      }
    } else
    {
      float dx = collider.position.x - position.x;
      float dy = collider.position.y - position.y;
      float distance = sqrt(dx*dx + dy*dy);
      float minDist = collider.radius + radius;
      if (distance < minDist) { 
        float angle = atan2(dy, dx);
        float targetX = position.x + cos(angle) * minDist;
        float targetY = position.y + sin(angle) * minDist;
        float ax = (targetX - collider.position.x);
        float ay = (targetY - collider.position.y);
        movement.x -= ax;
        movement.y -= ay;
        collider.movement.x += ax;
        collider.movement.y += ay;
      }
    }
  }

  public void show() 
  {
    noStroke();

    if (radius >= playerEnergy && ball_deadly && machine.state == GAME_STATES.INGAME)
    {
      fill(255, 0, 0);
    } else if (ball_deadly && machine.state == GAME_STATES.INGAME)
    {
      fill(0, 255, 0);
    } else
    {
      fill(ballColor);
    }
    ellipse(position.x, position.y, radius*2, radius*2);
  }

  public void addX(int tempX)
  {
    position.x += tempX;
  }

  public void setColor(int other)
  {
    ballColor = other;
  }

  public void setEnergy(int other)
  {
    playerEnergy = other;
  }

  public void setGravity(float other)
  {
    gravity = other;
  }

  public int getX()
  {
    return (int) position.x;
  }

  public int getY()
  {
    return (int) position.y;
  }

  public int getRad()
  {
    return radius;
  }
}

class Bullet
{
  /*******************************************************************************************************/
  private PVector position;       //Saves the position of the bullet 
  private int speed;              //The speed in pixel the bullet travels each frame
  private int radius;             //The radius of the projectile
  /*******************************************************************************************************/


  Bullet(int tempXPos, int tempYPos, int tempRadius, int tempSpeed)
  {
    position = new PVector(tempXPos, tempYPos);
    speed = tempSpeed;
    radius = tempRadius;
  }

  public void update(int currentBullet)
  {
    updateBullet(currentBullet);
    drawBullet();
  }

  private void updateBullet(int currentBullet)
  {
    position.y -= speed;

    if (position.y < 0)
    {
      machine.field.myBullets.remove(currentBullet);
    }
  }

  private void drawBullet()
  {
    stroke(255);
    strokeWeight(1);
    fill(machine.field.myPlayer.getColor());
    ellipse((float) position.x, (float) position.y, (float) radius, (float) radius);
  }

  public int getX()
  {
    return (int)position.x;
  }

  public int getY()
  {
    return (int)position.y;
  }

  public int getRad()
  {
    return radius;
  }
}

class GUI
{
  /************************************************************************************************************************************/
  private GUI_STATES state;             //The states of the GUI (see STATES.java)

  private Text[] myTexts;               //The texts used by the GUI
  private PVector healthbarPosition;    //Position of the healthbar
  private PVector healthbarSize;        //Size of the healthbar
  private PVector livesPosition;        //Position of the lives (only the player sprites, not the text)

  private int frameCounter;             //Framecounter used for the "LIVE LOST" fade
  private int timeAtStart;              //The time the program has run for when the game begins
  private int time;                     //The current time (time - timeAtStart = time the game has lasted for)
  /************************************************************************************************************************************/


  GUI(int tempHPX, int tempHPY)
  {
    state = GUI_STATES.STANDARD;
    healthbarSize = new PVector(tempHPX, tempHPY);
    healthbarPosition = new PVector(width-healthbarSize.x-11, 9);
    livesPosition = new PVector((int)(width*0.065f), (int)(height*0.0125f));

    myTexts = new Text[4];
    myTexts[0] = new Text(width*0.5f, height*0.5f, width*0.04f, "Live lost", font, 0, color(255), 1);
    myTexts[1] = new Text(width*0.008f, height*0.032f, width*0.02f, "Lives:", fontSmall, 255, color(255), 0);     
    myTexts[2]= new Text(width*0.25f, height*0.035f, width*0.02f, "(PLAYERLIVES)", fontSmall, 0, color(255), 1);
    myTexts[3]= new Text(width*0.5f, height*0.035f, width*0.025f, "" + time, fontSmall, 255, color(255), 1);
  }

  public void update()
  {
    updateTimer();
    updateLives();
    updateHealth();
    updateTexts();
  }

  private void updateTimer()
  {
    if (machine.field.state == INGAME_STATES.STANDARD && timeAtStart == 0 || machine.field.state == INGAME_STATES.HIT && timeAtStart == 0)
    {
      timeAtStart = millis();
    }

    if (machine.field.state == INGAME_STATES.STANDARD || machine.field.state == INGAME_STATES.HIT)
    {
      time = millis() - timeAtStart;
      myTexts[3].setText("" + (int)time/1000);
    }
  }

  private void updateTexts()
  {
    myTexts[2].setText("(" + machine.field.myPlayer.getLives() + ")");
    for (int i=0; i < myTexts.length; i++)
    {
      myTexts[i].update();
    }
  }

  private void updateLives()
  { 
    for (int i=0; i < machine.field.myPlayer.getLives (); i++)
    {
      stroke(255);
      strokeWeight(1);
      fill(0, 120, 255);
      rect(livesPosition.x+i*width*0.02f, livesPosition.y, machine.field.myPlayer.getSizeX(), machine.field.myPlayer.getSizeY());
      if (i >= 7)
      {
        myTexts[2].setOpacity(255);
        i = machine.field.myPlayer.getLives();
      } else
      {
        myTexts[2].setOpacity(0);
      }
    }

    if (state == GUI_STATES.LIVE_LOST_SHOW)
    {
      frameCounter++;
      myTexts[0].setOpacity(frameCounter*8);
      if (frameCounter >= 95)
      {
        frameCounter = 0;
        state = GUI_STATES.LIVE_LOST_FADE;
      }
    } else if (state == GUI_STATES.LIVE_LOST_FADE)
    {
      frameCounter++;
      myTexts[0].setOpacity(255-frameCounter*8);
      if (frameCounter >= 35)
      {
        frameCounter = 0;
        state = GUI_STATES.STANDARD;
      }
    }
  }

  private void updateHealth()
  {
    stroke(255);
    strokeWeight(1);
    noFill();
    rect(healthbarPosition.x, healthbarPosition.y, healthbarSize.x+1, healthbarSize.y+1);

    if (machine.field.myPlayer.energy == 100)
    {
      fill(0, 145, 34);
    } else if (machine.field.myPlayer.energy > 80)
    {
      fill(35, 219, 41);
    } else if (machine.field.myPlayer.energy > 40)
    {
      fill(245, 245, 39);
    } else if (machine.field.myPlayer.energy > 20)
    {
      fill(245, 207, 39);
    } else if (machine.field.myPlayer.energy > 0)
    {
      fill(250, 96, 30);
    } else 
    {
      fill(255, 0, 0);
    }

    noStroke();
    if (machine.field.myPlayer.getEnergy() > 0)
    {
      rect(healthbarPosition.x+1, healthbarPosition.y+1, (healthbarSize.x/100)*machine.field.myPlayer.getEnergy(), healthbarSize.y);
    } else
    {
      rect(healthbarPosition.x+1, healthbarPosition.y+1, healthbarSize.x, healthbarSize.y);
    }
  }

  public void liveLost()
  {
  }

  public void addX(int tempX)
  {
    livesPosition.x += tempX;
    healthbarPosition.x += tempX;
    for (Text texts : myTexts)
    {
      texts.setX(tempX);
    }
  }
  
  public int getTimeSurvived()
  {
    return time/1000;
  }
}

class Grid
{
  /*******************************************************************************************************/
  private int cells;                    //The number of cells the grid inherits
  private int margin;                   //The space between the edges of the window and the grid
  private int gridColor;              //The color of the actual grid
  private int backgroundColor;        //The color of the background
  /*******************************************************************************************************/


  Grid(int tempCells, int tempMargin, int tempGridColor, int tempBackgroundColor)
  {
    cells = tempCells; 
    margin = tempMargin;
    gridColor = tempGridColor;
    backgroundColor = tempBackgroundColor;
  }

  public void update()
  {
    background(backgroundColor);
    stroke(gridColor);
    strokeWeight(3);
    size(width, height);
    for (int i=0; i<=cells; i++) 
    {
      line(margin, margin+i*(height-2*margin)/cells, width-margin, margin+i*(height-2*margin)/cells);
      line(margin+i*(width-2*margin)/cells, margin, margin+i*(width-2*margin)/cells, height-margin);
    }
  }

  public void setColor(int tempBG, int tempCol)
  {
    gridColor = tempCol;
    backgroundColor = tempBG;
  }
  
  public int getBackgroundColor()
  {
    return backgroundColor;
  }
}

class Player
{
  /*****************************************************************************************************************************************************/
  PLAYER_STATES state;              //State the player is in (Vulnerable or invincible)

  private PVector position;         //The position of the player
  private PVector size;             //The size of the player
  private int speed;                //The speed the player travels with
  private int fillCol;            //The inner color of the player. Is used for the GUI, too. The outline is colored inverted to the background
  private boolean mov_right;        //A switch to check whether the player is pressing the 'd' key. Enables fluid motion. 
  private boolean mov_left;         //A switch to check whether the player is pressing the 'a' key. Enables fluid motion. 

  private int frameCounter;         //Counter used to mark invincibility frames
  private int lives;                //The lives the player has left
  private int energy;               //The energy the current life has left. (max. 100)
  /*****************************************************************************************************************************************************/


  Player(int tempsizeX, int tempsizeY, int tempSpeed, int tempFillColor, int tempLives)
  {
    size = new PVector(tempsizeX, tempsizeY);
    position = new PVector(width/2, height-size.y-1);
    speed = tempSpeed;
    fillCol = tempFillColor;
    lives = tempLives;
    energy = 100;
    state = PLAYER_STATES.INVINCIBLE;
  }

  public void movLeft()
  {
    mov_left = true;
  }

  public void movRight()
  {
    mov_right = true;
  }

  public void movLeftStop()
  {
    mov_left = false;
  }

  public void movRightStop()
  {
    mov_right = false;
  }

  public void shoot()
  {
    if (machine.field.myBullets.size() < machine.field.maxBullets)
    {
      machine.field.myBullets.add(new Bullet((int)(position.x + size.x/2), (int)(position.y), machine.field.bulletSize, machine.field.bulletSpeed));
    }
  }

  public void update()
  {
    calculatePosition();
    checkEdgeCollision();
    checkBallCollision();
    calculateInvincibility();
    drawPlayer();
  }

  private void drawPlayer()
  {
    if (state == PLAYER_STATES.VULNERABLE)
    {
      fill(fillCol);
    } else 
    {
      fill(color(255, 255, 0));
    }
    stroke(color(255-red(machine.field.myGrid.getBackgroundColor()), 255-green(machine.field.myGrid.getBackgroundColor()), 255-blue(machine.field.myGrid.getBackgroundColor())));
    strokeWeight(1);
    rect(position.x, position.y, size.x, size.y);
  }

  private void calculatePosition()
  {
    if (mov_left)
    {
      position.x -= speed;
    } else if (mov_right)
    {
      position.x += speed;
    }
  }

  private void checkEdgeCollision()
  {
    if (position.x > width - size.x)
    {
      position.x = width - size.x - 1;
    } else if (position.x < 0)
    {
      position.x = 0;
    }
  }

  private void checkBallCollision()
  {
    for (int i=0; i < machine.field.enemyBalls.size (); i++)
    {
      if (intersects(machine.field.enemyBalls.get(i)))
      {

        machine.field.enemyBalls.get(i).movement.y *= (-1);
        machine.field.enemyBalls.get(i).movement.x *= (-1);

        while (intersects (machine.field.enemyBalls.get (i)))
        {
          machine.field.enemyBalls.get(i).position.x += machine.field.enemyBalls.get(i).movement.x*0.01f;
          machine.field.enemyBalls.get(i).position.y += machine.field.enemyBalls.get(i).movement.y*0.01f;
        }

        if (state == PLAYER_STATES.VULNERABLE)
        {
          machine.field.state = INGAME_STATES.HIT;
          energy -= machine.field.enemyBalls.get(i).radius;
          state = PLAYER_STATES.INVINCIBLE;
          frameCounter = 60;
        }
      }
    }
  }

  public boolean intersects(AmigaBall other)
  {
    float distX = abs(other.position.x - (position.x + size.x*0.5f));
    float distY = abs(other.position.y - (position.y + size.y*0.5f));
    float cornerDistance_sq = (distX - size.x*0.5f)*(distX - size.x*0.5f) + (distY - size.y*0.5f)*(distY - size.y*0.5f);

    if (distX > (size.x*0.5f + other.radius))
    { 
      return false;
    }
    if (distY > (size.y*0.5f + other.radius)) 
    { 
      return false;
    }
    if (distX <= size.x*0.5f) 
    { 
      return true;
    } 
    if (distY <= size.y*0.5f) 
    { 
      return true;
    }
    if (cornerDistance_sq <= other.radius*other.radius)
    {
      return true;
    }
    return false;
  }

  public void calculateInvincibility()
  {
    if (frameCounter > 0)
    {
      frameCounter--;
    } else
    {
      state = PLAYER_STATES.VULNERABLE;
    }
  }

  public void addX(int tempX)
  {
    position.x += tempX;
  }

  public void setLives(int tempLives)
  {
    lives = tempLives;
  }

  public int getSizeX()
  {
    return (int)size.x;
  }

  public int getSizeY()
  {
    return (int)size.y;
  }

  public int getColor()
  {
    return fillCol;
  }

  public int getLives()
  {
    return lives;
  }

  public int getEnergy()
  {
    return energy;
  }

  public boolean isDead()
  {
    if (energy <= 0)
    {
      return true;
    }
    return false;
  }
}

class STATE_MACHINE
{
  /****************************************************************************************************************************/
  private GAME_STATES state;                  //The current state the game runs in

  private State_Playfield field;              //The ingame state where the gameplay takes place
  private State_Background background;        //The background seen in the main menu and its sub-menus
  private State_Main menu;                    //The main menu visible when starting the game
  private State_Options options;              //The options menu
  private State_Custom custom_game;           //The menu to adjust custom games
  private State_Pause game_halt;              //The pause menu (ESC ingame)
  private State_GameOver gameover;            //The game over menu with the results of the game
  private State_Playerlist playerlist;        //The playerlist which includes every player that has ever successfully finished a game
  private Text title;                         //The title used by the menus
  /****************************************************************************************************************************/


  STATE_MACHINE()
  {
    font = loadFont("Impact-100.vlw");
    fontSmall = loadFont("Impact-48.vlw");

    background = new State_Background();
    menu = new State_Main();
    options = new State_Options();
    game_halt = new State_Pause();
    custom_game = new State_Custom();
    gameover = new State_GameOver();
    playerlist = new State_Playerlist();
    title = new Text(width*0.5f, height*0.15f, width*0.06f, gameTitle, font, 255, color(255), 1);

    state = GAME_STATES.MAIN_MENU;
  }

  public void update()
  {
    switch (state)
    {
    case MAIN_MENU:
      background.update();
      menu.update();
      title.update(); 
      break;
    case INGAME:
      field.update(); 
      break;
    case INGAME_PAUSE:
      game_halt.update(); 
      break;
    case GAMEOVER:
      gameover.update();
      break;
    case PLAYERLIST:
      background.update();
      title.update();
      playerlist.update();
      break;
    case OPTIONS:
      background.update();
      options.update();
      title.update();
      break;
    case CUSTOM_GAME:
      background.update();
      title.update();
      custom_game.update();
      break;
    }
  }

  public void keyReleased()
  {
    switch (state)
    {
    case INGAME:
      field.keyReleased(); 
      break;
    }
  }

  public void keyPressed()
  {
    switch (state)
    {
    case MAIN_MENU:
      menu.keyPressed(); 
      break;
    case INGAME:
      field.keyPressed(); 
      break;
    case INGAME_PAUSE:
      game_halt.keyPressed(); 
      break;
    case GAMEOVER:
      gameover.keyPressed();
      break;
    case PLAYERLIST:
      playerlist.keyPressed();
      break;
    case OPTIONS:
      options.keyPressed();
      break;
    case CUSTOM_GAME:
      custom_game.keyPressed();
      break;
    }
  }
}

/**************************************************************************************************************************************************************/
private String gameTitle = "Balls Attack";        //Title of the game. Will adjust the title in the menus

private int resolutionX = displayWidth;                     //Screen resolution along the x-axis
private int resolutionY = displayHeight;                      //Screen resolution along the y-axis

public boolean ball_deadly = false;                 //Whether the balls are displayed in green/red or in their actual color              
public boolean ball_grid = false;                   //Whether a grid is displayed in the background
public boolean background_anim = true;              //Whether some bouncing balls are displayed in the menu backgrounds

public PFont font;                                  //The standard font used by most texts
public PFont fontSmall;                             //The lower resolution of the standard font. (Needed for correct display of texts smaller than ~20)
/**************************************************************************************************************************************************************/
class State_Background
{
  /**************************************************************************************************************************************************************/
  private ArrayList<AmigaBall> bgBalls;          //The balls bouncing in the background. Limited to maxBalls
  private float gravity;                         //The gravity acting in the background space. Has a probability to inverse
  private int maxBalls = 5;                          //The maximum amount of balls on the grid
  /**************************************************************************************************************************************************************/
  
  
  State_Background()
  {
    bgBalls = new ArrayList<AmigaBall>();
    gravity = 0.1f;
    addBall();
  }

  public void update()
  {
    background(0);

    if (background_anim)
    {
      if ((int)random(0, 1000) == 42)
      {
        gravity *= (-1);
        for (AmigaBall balls : bgBalls)
        {
          balls.setGravity(gravity);
        }
      }

      if ((int)random(0, 100) == 42)
      {
        if (bgBalls.size() < maxBalls)
        {
          addBall();
        }
      }

      for (int i=0; i < bgBalls.size (); i++)
      {
        bgBalls.get(i).update();
        bgBalls.get(i).show();

        for (int k=0; k < bgBalls.size (); k++)
        {
          if (k != i)
          {
            bgBalls.get(i).checkBallCollision(bgBalls.get(k), true);
          }
        }
      }
    }
  }

  public void addBall()
  {
    boolean isColliding = true;
    while (isColliding)
    {
      bgBalls.add(new AmigaBall((int)(width*0.02f), (int)(width*0.05f), height*0.5f, gravity));

      if (bgBalls.size() == 1)
      {
        isColliding = false;
      }

      if (isColliding)
      {
        for (int k=0; k < bgBalls.size (); k++)
        {
          if (k != bgBalls.size()-1)
          {
            if ((bgBalls.get(bgBalls.size()-1).getX() - bgBalls.get(k).getX())*(bgBalls.get(bgBalls.size()-1).getX() - bgBalls.get(k).getX()) + (bgBalls.get(k).getY() - bgBalls.get(bgBalls.size()-1).getY())*(bgBalls.get(k).getY() - bgBalls.get(bgBalls.size()-1).getY()) <= (bgBalls.get(bgBalls.size()-1).getRad()+bgBalls.get(k).getRad())*(bgBalls.get(bgBalls.size()-1).getRad()+bgBalls.get(k).getRad()))
            {
              removeBall(bgBalls.size()-1);
            } else
            {
              isColliding = false;
            }
          }
        }
      }
    }
  }

  public void removeBall(int other)
  {
    if (bgBalls.size() > 0)
    {
      bgBalls.remove(other);
    }
  }

  public void resetBalls()
  {
    bgBalls.clear();
  }
}

class State_Custom
{
  /**************************************************************************************************************************************************************/
  CUSTOM_STATES state;                        //The current state of the custom game menu

  private int frameCounter;                   //The counter used for the fadeout after starting the game

  private Text[] options;                     //The various options the player can choose from        
  private Text[] option_status;               //The matching values for the options (ON/OFF, numbers etc.)

  private boolean changeSetting;              //Whether the player is currently changing an attribute
  private int[][] board;                      //The 2D field used for the options
  private int row;                            //Used to determine the cursor's position
  private int col;                            //Used to determine the cursor's position
  private int cursorPos;                      //The current position of the cursor

  private int minRad;                         //The minimum radius for the balls
  private int maxRad;                         //The maximum radius for the balls
  private int ballInterval;                   //The interval (in seconds) at which balls do spawn
  private int startBalls;                     //The amount of balls the game begins with
  private int gravity;                        //The gravity of the playfield
  private int schwarzschild;                  //The radius at which a ball is destroyed instead of being cut in two
  private int maxBullets;                     //The maximum amount of bullets the player can have on screen
  private int bulletSize;                     //The diameter of these bullets
  private int bulletSpeed;                    //The speed of these bullets
  private int livesAmount;                    //The amount of lives the player has in the beginning
  private boolean elasticCollision;           //Whether the balls bounce in an elastic or inelastic way
  private boolean ball_ballCollision;         //Whether the balls collide with each other
  /**************************************************************************************************************************************************************/


  State_Custom()
  {
    board = new int[4][4];

    options = new Text[16];
    options[0] = new Text(width*0.2f, height*0.35f, width*0.017f, "Min. radius", fontSmall, 255, color(255), 1); 
    options[1] = new Text(width*0.4f, height*0.35f, width*0.017f, "Max. radius", fontSmall, 255, color(255), 1);
    options[2] = new Text(width*0.6f, height*0.35f, width*0.017f, "Ball interval (sec.)", fontSmall, 255, color(255), 1);
    options[3] = new Text(width*0.8f, height*0.35f, width*0.017f, "Start balls", fontSmall, 255, color(255), 1);
    options[4] = new Text(width*0.2f, height*0.5f, width*0.017f, "Gravity", fontSmall, 255, color(255), 1);
    options[5] = new Text(width*0.4f, height*0.5f, width*0.017f, "Destruction rad.", fontSmall, 255, color(255), 1);
    options[6] = new Text(width*0.6f, height*0.5f, width*0.017f, "Max. bullets", fontSmall, 255, color(255), 1);
    options[7] = new Text(width*0.8f, height*0.5f, width*0.017f, "Bullet size", fontSmall, 255, color(255), 1);
    options[8] = new Text(width*0.2f, height*0.65f, width*0.017f, "Bullets speed", fontSmall, 255, color(255), 1);
    options[9] = new Text(width*0.4f, height*0.65f, width*0.017f, "Lives", fontSmall, 255, color(255), 1);
    options[10] = new Text(width*0.6f, height*0.65f, width*0.017f, "Ball-Ball collision", fontSmall, 255, color(255), 1);
    options[11] = new Text(width*0.8f, height*0.65f, width*0.017f, "Elastic collision", fontSmall, 255, color(255), 1);
    options[12] = new Text(width*0.2f, height*0.8f, width*0.025f, "Start game", fontSmall, 255, color(255), 1);
    options[13] = new Text(width*0.4f, height*0.8f, width*0.025f, "Back to main menu", fontSmall, 255, color(255), 1);
    options[14] = new Text(width*0.6f, height*0.8f, width*0.025f, "Reset", fontSmall, 255, color(255), 1);
    options[15] = new Text(width*0.8f, height*0.8f, width*0.025f, "Set to minimum", fontSmall, 255, color(255), 1);

    option_status = new Text[12];
    option_status[0] = new Text(width*0.2f, height*0.4f, width*0.017f, "<  " + minRad + "  >", fontSmall, 255, color(255), 1); 
    option_status[1] = new Text(width*0.4f, height*0.4f, width*0.017f, "<  " + maxRad + "  >", fontSmall, 255, color(255), 1);
    option_status[2] = new Text(width*0.6f, height*0.4f, width*0.017f, "<  " + ballInterval + "  >", fontSmall, 255, color(255), 1);
    option_status[3] = new Text(width*0.8f, height*0.4f, width*0.017f, "<  " + startBalls + "  >", fontSmall, 255, color(255), 1);
    option_status[4] = new Text(width*0.2f, height*0.55f, width*0.017f, "<  " + gravity + "  >", fontSmall, 255, color(255), 1);
    option_status[5] = new Text(width*0.4f, height*0.55f, width*0.017f, "<  " + schwarzschild + "  >", fontSmall, 255, color(255), 1);
    option_status[6] = new Text(width*0.6f, height*0.55f, width*0.017f, "<  " + maxBullets + "  >", fontSmall, 255, color(255), 1);
    option_status[7] = new Text(width*0.8f, height*0.55f, width*0.017f, "<  " + bulletSize + "  >", fontSmall, 255, color(255), 1);
    option_status[8] = new Text(width*0.2f, height*0.7f, width*0.017f, "<  " + bulletSpeed + "  >", fontSmall, 255, color(255), 1);
    option_status[9] = new Text(width*0.4f, height*0.7f, width*0.017f, "<  " + livesAmount + "  >", fontSmall, 255, color(255), 1);
    option_status[10] = new Text(width*0.6f, height*0.7f, width*0.017f, "<  " + "ON" + "  >", fontSmall, 255, color(255), 1);
    option_status[11] = new Text(width*0.8f, height*0.7f, width*0.017f, "<  " + "ON" + "  >", fontSmall, 255, color(255), 1);
    resetSettings(); 

    state = CUSTOM_STATES.STANDARD;
  }

  public void update()
  {
    frameCounter++;

    switch (state)
    {
    case STANDARD:
      /**********************************************************************************/
      calculateCursor();
      for (int i=0; i < options.length; i++) {
        options[i].update();
      }
      for (int i=0; i < option_status.length; i++) {
        option_status[i].update();
      }
      break;
      /**********************************************************************************/
    case FADE:
      /**********************************************************************************/
      calculateCursor();
      for (int i=0; i < options.length; i++) {
        options[i].update();
      }
      for (int i=0; i < option_status.length; i++) {
        option_status[i].update();
      }

      fill(0, 0, 0, frameCounter*8);
      rect(0, 0, width, height);

      if (frameCounter == 120)
      {
        machine.background.resetBalls();
        frameCounter = 0;
        state = CUSTOM_STATES.STANDARD;
        machine.state = GAME_STATES.INGAME;
      }
      break;
      /**********************************************************************************/
    }
  }

  public void changeSetting()
  {
    if (key == 'a' || key == 'A')
    {
      switch (cursorPos)
      {
      case 0:
        minRad--;
        break;
      case 1:
        maxRad--;
        break;
      case 2:
        ballInterval--;
        break;
      case 3:
        startBalls--;
        break;
      case 4:
        gravity--;
        break;
      case 5:
        schwarzschild--;
        break;
      case 6:
        maxBullets--;
        break;
      case 7:
        bulletSize--;
        break;
      case 8:
        bulletSpeed--;
        break;
      case 9:
        livesAmount--;
        break;
      case 10:
        ball_ballCollision = !ball_ballCollision;
        break;
      case 11:
        elasticCollision = !elasticCollision;
        break;
      }
    } else
    {
      switch (cursorPos)
      {
      case 0:
        minRad++;
        break;
      case 1:
        maxRad++;
        break;
      case 2:
        ballInterval++;
        break;
      case 3:
        startBalls++;
        break;
      case 4:
        gravity++;
        break;
      case 5:
        schwarzschild++;
        break;
      case 6:
        maxBullets++;
        break;
      case 7:
        bulletSize++;
        break;
      case 8:
        bulletSpeed++;
        break;
      case 9:
        livesAmount++;
        break;
      case 10:
        ball_ballCollision = !ball_ballCollision;
        break;
      case 11:
        elasticCollision = !elasticCollision;
        break;
      }
    }
    updateSettings();
  }

  public void updateSettings()
  {
    if (minRad < 1)
    {
      minRad = 1;
    }
    if (maxRad < 1)
    {
      maxRad = 1;
    }
    if (minRad > maxRad)
    {
      maxRad++;
    }
    if (ballInterval < 1)
    {
      ballInterval = 1;
    }
    if (startBalls < 1)
    {
      startBalls = 1;
    }
    if (schwarzschild < 1)
    {
      schwarzschild = 1;
    }
    if (maxBullets < 0)
    {
      maxBullets = 0;
    }
    if (bulletSize < 1)
    {
      bulletSize = 1;
    }
    if (bulletSpeed < 1)
    {
      bulletSpeed = 1;
    }
    if (livesAmount < 1)
    {
      livesAmount = 1;
    }
    if (maxBullets < 1)
    {
      maxBullets = 1;
    }

    option_status[0].setText("<  " + minRad + "  >");
    option_status[1].setText("<  " + maxRad + "  >");
    option_status[2].setText("<  " + ballInterval + "  >");
    option_status[3].setText("<  " + startBalls + "  >");
    option_status[4].setText("<  " + gravity + "  >");
    option_status[5].setText("<  " + schwarzschild + "  >");
    option_status[6].setText("<  " + maxBullets + "  >");
    option_status[7].setText("<  " + bulletSize + "  >");
    option_status[8].setText("<  " + bulletSpeed + "  >");
    option_status[9].setText("<  " + livesAmount + "  >");

    if (ball_ballCollision)
    {
      option_status[10].setText("<  ON  >");
    } else
    {
      option_status[10].setText("<  OFF  >");
    }
    if (elasticCollision)
    {
      option_status[11].setText("<  ON  >");
    } else
    {
      option_status[11].setText("<  OFF  >");
    }
  }

  public void calculateCursor()
  {
    if (row < 0)
    {
      row = board.length-1;
    } else if (row > 3)
    {
      row = 0;
    }

    if (col < 0)
    {
      col = board.length-1;
    } else if (col > 3)
    {
      col = 0;
    }

    switch (row)
    {
    case 0: 
      switch (col)
      {
      case 0: 
        cursorPos = 0;
        break;
      case 1: 
        cursorPos = 1;
        break;
      case 2: 
        cursorPos = 2;
        break;
      case 3:
        cursorPos = 3;
        break;
      }
      break;
    case 1: 
      switch (col)
      {
      case 0: 
        cursorPos = 4;
        break;
      case 1: 
        cursorPos = 5;
        break;
      case 2: 
        cursorPos = 6;
        break;
      case 3:
        cursorPos = 7;
        break;
      }
      break;
    case 2:
      switch (col)
      {
      case 0: 
        cursorPos = 8;
        break;
      case 1: 
        cursorPos = 9;
        break;
      case 2: 
        cursorPos = 10;
        break;
      case 3:
        cursorPos = 11;
        break;
      } 
      break;
    case 3:
      switch (col)
      {
      case 0: 
        cursorPos = 12;
        break;
      case 1: 
        cursorPos = 13;
        break;
      case 2: 
        cursorPos = 14;
        break;
      case 3:
        cursorPos = 15;
        break;
      } 
      break;
    }

    for (int i=0; i < options.length; i++)
    {
      options[i].setCol(color(255));
    }

    for (int i=0; i < option_status.length; i++)
    {
      option_status[i].setCol(color(255));
    }

    options[cursorPos].setCol(color(0, 120, 255));

    if (changeSetting)
    {
      option_status[cursorPos].setCol(color(0, 120, 255));
    }
  }

  public void resetSettings()
  {
    //These are the standard settings for custom games
    minRad = 30;
    maxRad = 45;
    ballInterval = 8;
    startBalls = 2;
    gravity = 1;
    schwarzschild = 20;
    maxBullets = 3;
    bulletSize = 8;
    bulletSpeed = 15;
    livesAmount = 3;
    ball_ballCollision = true;
    elasticCollision = true;
    updateSettings();
  }

  public void minimizeSettings()
  {
    minRad = 1;
    maxRad = 1;
    ballInterval = 1;
    startBalls = 1;
    gravity = 1;
    schwarzschild = 1;
    maxBullets = 1;
    bulletSize = 1;
    bulletSpeed = 1;
    livesAmount = 1;
    updateSettings();
  }

  private void enterFunction()
  {
    switch (cursorPos)
    {
    case 12:
      machine.field = new State_Playfield(gravity*0.1f, maxBullets, schwarzschild, minRad, maxRad, height*0.4f, bulletSize, bulletSpeed, startBalls, ballInterval, elasticCollision, ball_ballCollision);
      if (ball_grid)
      {
        machine.field.myGrid.setColor(color(0), color(80));
      } 
      machine.field.myPlayer.setLives(livesAmount-1);
      frameCounter = 0;
      state = CUSTOM_STATES.FADE;
      break;
    case 13:
      machine.state = GAME_STATES.MAIN_MENU;
      break;
    case 14:
      resetSettings();
      break;
    case 15:
      minimizeSettings();
      break;
    default:
      changeSetting = true;
    }
  }

  public void keyPressed()
  {
    switch (state)
    {
    case STANDARD:
      if (!changeSetting)
      {
        switch (key)
        {
        case 'w': 
          row--;
          break;
        case 'a': 
          col--; 
          break;
        case 's': 
          row++; 
          break;
        case 'd': 
          col++; 
          break;
        case ENTER: 
          enterFunction(); 
          break;
        case ESC:
          key = 0;
          machine.state = GAME_STATES.MAIN_MENU;
          break;
        }
      } else
      {
        switch (key)
        {
        case 'a': 
        case 'd': 
          changeSetting(); 
          break;
        case ENTER:
        case ESC:
          key = 0;
          changeSetting = false;
          break;
        }
      }
      break;
    case FADE:
    default:
      key = 0;
      break;
    }
  }
}

class State_GameOver
{
  /******************************************************************************************************************/
  private GAMEOVER_STATES state;       //Current state of the game over screen

  private Text gameover;               //The gameover text (an exact copy of the game over text from the ingame state)
  private Text[] results;              //Time survived and balls destroyed

  private int frameCounter;            //Counter used for the results transparency
  private String typing;               //The string that is displayed on screen while still entering one's name
  private ArrayList<String> players;   //The players including the amount of balls they shot and the time they survived. (Player, Time, Balls)
  /******************************************************************************************************************/


  State_GameOver()
  {
    gameover = new Text(width*0.5f, height*0.5f, width*0.08f, "GAME OVER", font, 255, color(200, 0, 0), 1);
    players = new ArrayList<String>();

    results = new Text[6];
    results[0] = new Text(width*0.5f, height*0.4f, width*0.04f, "Seconds survived:", font, 0, color(255), 2);
    results[1] = new Text(width*0.6f, height*0.4f, width*0.04f, "", font, 0, color(255), 0);
    results[2] = new Text(width*0.5f, height*0.5f, width*0.04f, "Balls destroyed:", font, 0, color(255), 2);
    results[3] = new Text(width*0.6f, height*0.5f, width*0.04f, "", font, 0, color(255), 0);
    results[4] = new Text(width*0.5f, height*0.6f, width*0.04f, "Please enter your name:", font, 0, color(255), 2);
    results[5] = new Text(width*0.6f, height*0.6f, width*0.04f, "", font, 0, color(255), 0);

    state = GAMEOVER_STATES.FADE;
  }

  public void update()
  {
    background(0);

    switch (state)
    {
    case FADE:
      results[1].setText("" + machine.field.gui.getTimeSurvived());
      results[3].setText("" + machine.field.getBallsDestroyed());

      if (gameover.getY() > height*0.15f)
      {
        gameover.setY(-2);
        if (gameover.getY() < height*0.25f)
        {
          frameCounter++;
          for (int i=0; i < results.length; i++)
          {
            results[i].setOpacity(frameCounter*3);
          }
        }
      } else
      {
        for (int i=0; i < results.length; i++)
        {
          results[i].setOpacity(255);
        }
        state = GAMEOVER_STATES.NAME_INPUT;
      }
      updateTexts();
      break;
    case NAME_INPUT:
      updateTexts();
      break;
    }
  }

  private void updateTexts()
  {
    gameover.update();
    for (int i=0; i < results.length; i++)
    {
      results[i].update();
    }
  }

  private void savePlayer()
  {
    String[] tempArray = loadStrings("playerlist.txt");
    for (int i=0; i < tempArray.length; i++)
    {
      players.add(tempArray[i]);
    }

    players.add(results[5].getText());
    players.add("" + machine.field.gui.getTimeSurvived());
    players.add("" + machine.field.getBallsDestroyed());

    tempArray = new String[players.size()];
    for (int i=0; i < players.size (); i++)
    {
      tempArray[i] = players.get(i);
    }
    saveStrings("playerlist.txt", tempArray);
  }

  public void keyPressed()
  {
    switch (state)
    {
    case FADE:
      switch (key)
      {
      case ESC:
        key = 0;
        break;
      }
      break;
    case NAME_INPUT:
      switch (key)
      {
      case '\n':
        savePlayer();
        results[5].setText("");
        machine.playerlist.updateContent();
        machine.playerlist.updateTexts();
        machine.state = GAME_STATES.PLAYERLIST;
        break;
      case BACKSPACE:
        if (results[5].getText().length() > 0 ) {
          results[5].setText(results[5].getText().substring(0, results[5].getText().length()-1));
        }
        break;
      default:
        results[5].setText(results[5].getText() + key);
        break;
      }
    }
  }
}

class State_Playfield
{
  /************************************************************************************************************************************************/
  private INGAME_STATES state;                      //The state of the game
  private int frameCounter;                         //The counter used for the fades

  private ArrayList<AmigaBall> enemyBalls;          //The balls on the playfield
  private ArrayList<Bullet> myBullets;              //The bullets currently flying

  private Grid myGrid;                              //The grid in the background. If not defined displayed completely black
  private Player myPlayer;                          //The player character
  private GUI gui;                                  //The GUI including the healthbars
  private Text countdown;                           //The countdown displayed in the beginning 
  private Text gameover;                            //The text displayed at the end of the game over transition

  private float gravity;                            //The gravity of the playfield
  private int minRad;                               //The minimum radius for the balls
  private int maxRad;                               //The maximum radius for the balls
  private int minHeight;                            //The minimum height at which a ball is able to spawn (height - minHeight)
  private int maxBullets;                           //The maximum amount of bullets that can be on the field at the same time
  private int bulletSize;                           //The diameter of these bullets
  private int bulletSpeed;                          //The speed of these bullets
  private int schwarzschild_r;                      //The radius at which a ball is destroyed instead of being cut in two
  private int ballInterval;                         //The rate (in seconds) at which a new ball spawns
  private int intervalCounter;                      //The counter used to determine the frame at which a new ball spawns

  private boolean elasticCollision;                 //Whether the balls bounce in an elastic or inelastic way
  private boolean ball_ballCollision;               //Whether the balls collide with each other

  private int ballsDestroyed;                       //The total amount of balls the player hit (Including smaller children of bigger balls)
  /************************************************************************************************************************************************/


  State_Playfield()
  {
    enemyBalls = new ArrayList<AmigaBall>();
    myBullets = new ArrayList<Bullet>();
    state = INGAME_STATES.STANDARD;
    gui = new GUI(310, 25);
  }

  State_Playfield(float tempGrav, int tempMaxBullets, int tempSchw, int tempMinRad, int tempMaxRad, float tempMinHeight, int tempBulletSize, int tempBulletSpeed, int tempStartBalls, int tempInterval, boolean tempElast, boolean tempBallBall)
  {
    enemyBalls = new ArrayList<AmigaBall>();
    myBullets = new ArrayList<Bullet>();
    gravity = tempGrav;
    minRad = tempMinRad;
    maxRad = tempMaxRad;
    minHeight = (int)tempMinHeight;
    bulletSize = tempBulletSize;
    bulletSpeed = tempBulletSpeed;
    maxBullets = tempMaxBullets;
    schwarzschild_r = tempSchw;
    ballInterval = tempInterval;
    intervalCounter = ballInterval*60;
    elasticCollision = tempElast;
    ball_ballCollision = tempBallBall;

    for (int i=0; i < tempStartBalls; i++)
    {
      addBall();
    }

    countdown = new Text(width*0.5f, height*0.5f, width*0.04f, "", font, 0, color(255), 1);
    gameover = new Text(width*0.5f, height*0.5f, width*0.08f, "GAME OVER", font, 0, color(200, 0, 0), 1);
    myPlayer = new Player((int)(width*0.0125f), (int)(width*0.025f), width/80, color(0, 120, 255), 5);
    gui = new GUI((int)(width*0.25f), (int)(height*0.03f));
    myGrid = new Grid(15, 40, color(0), color(0));

    state = INGAME_STATES.INITIALIZE_FADE;
  }

  public void update()
  {
    frameCounter++;

    switch (state)
    {
    case INITIALIZE_FADE:
      myGrid.update();
      myPlayer.update();
      for (AmigaBall balls : enemyBalls) {
        balls.show();
      }
      gui.update();
      fill(0, 0, 0, 255-frameCounter*2);
      rect(0, 0, width, height);
      if (frameCounter == 150)
      {
        frameCounter = 0;
        state = INGAME_STATES.COUNTDOWN;
      }
      break;
    case COUNTDOWN:
      myGrid.update();
      myPlayer.update();
      for (AmigaBall balls : enemyBalls) {
        balls.show();
      }
      gui.update();
      if (frameCounter < 90)
      {
        countdown.setOpacity(255);
        countdown.setText("3");
        countdown.update();
      } else if (frameCounter < 180)
      {
        countdown.setOpacity(255);
        countdown.setText("2");
        countdown.update();
      } else if (frameCounter < 270)
      {
        countdown.setOpacity(255);
        countdown.setText("1");
        countdown.update();
      } else 
      {
        countdown.setOpacity(0);
        frameCounter = 0;
        state = INGAME_STATES.STANDARD;
      }
      break;
    case HIT:
    case STANDARD:
      myGrid.update();
      myPlayer.update();
      checkBallSpawn();
      updateBalls();
      updateBullets();
      gui.update();
      partyEnd();

      if (state == INGAME_STATES.HIT)
      {
        background(255, 0, 0);
        if (frameCounter >= 2)
        {
          frameCounter = 0;
          state = INGAME_STATES.STANDARD;
        }
      }
      break;
    case GAMEOVER_TRANSITION1:
      if (frameCounter == 25)
      {
        frameCounter = 0;
        state = INGAME_STATES.GAMEOVER_TRANSITION2;
      }
      break;
    case GAMEOVER_TRANSITION2:
      myGrid.update();
      if (frameCounter == 0)
      {
        gui.addX(-3);
        myPlayer.addX(-3);
        for (AmigaBall balls : enemyBalls)
        {
          balls.addX(-3);
        }
      } else if (frameCounter%4 == 0)
      {
        gui.addX(-6);
        myPlayer.addX(-6);
        for (AmigaBall balls : enemyBalls)
        {
          balls.addX(-6);
        }
      } else if (frameCounter%2 == 0)
      {
        gui.addX(6);
        myPlayer.addX(6);
        for (AmigaBall balls : enemyBalls)
        {
          balls.addX(6);
        }
      }
      myPlayer.update();
      for (AmigaBall balls : enemyBalls)
      {
        balls.show();
      }
      gui.update();
      fill(200, 0, 0, frameCounter);
      rect(0, 0, width, height);
      if (frameCounter == 330)
      {
        frameCounter = 0;
        gameover.setOpacity(255);
        state = INGAME_STATES.GAMEOVER_TRANSITION3;
      }
      break;
    case GAMEOVER_TRANSITION3:
      fill(0, 0, 0, frameCounter/5);
      rect(0, 0, width, height);
      gameover.update();
      if (frameCounter == 150)
      {
        frameCounter = 0;
        state = INGAME_STATES.STANDARD;
        machine.gameover.state = GAMEOVER_STATES.FADE;
        machine.state = GAME_STATES.GAMEOVER;
      }
    }
  }

  public void checkBallSpawn()
  {
    if (ballInterval != 0)
    {
      if (intervalCounter == 0)
      {
        addBall();
        intervalCounter = ballInterval*60;
      } else
      {
        intervalCounter--;
      }
    }
  }

  public void addBall()
  {
    boolean isColliding = true;
    while (isColliding)
    {
      enemyBalls.add(new AmigaBall(minRad, maxRad, minHeight, gravity));

      if (enemyBalls.size() == 1)
      {
        isColliding = false;
      }

      for (int k=0; k < enemyBalls.size (); k++)
      {
        if (k != enemyBalls.size()-1)
        {
          if ((enemyBalls.get(enemyBalls.size()-1).getX() - enemyBalls.get(k).getX())*(enemyBalls.get(enemyBalls.size()-1).getX() - enemyBalls.get(k).getX()) + (enemyBalls.get(k).getY() - enemyBalls.get(enemyBalls.size()-1).getY())*(enemyBalls.get(k).getY() - enemyBalls.get(enemyBalls.size()-1).getY()) <= (enemyBalls.get(enemyBalls.size()-1).getRad()+enemyBalls.get(k).getRad())*(enemyBalls.get(enemyBalls.size()-1).getRad()+enemyBalls.get(k).getRad()))
          {
            removeBall(enemyBalls.size()-1);
          } else
          {
            isColliding = false;
          }
        }
      }
    }
  }

  public void removeBall(int other)
  {
    if (enemyBalls.size() > 0)
    {
      enemyBalls.remove(other);
    }
  }

  public void updateBalls()
  {
    for (int i=0; i < enemyBalls.size (); i++)             
    {
      for (AmigaBall balls : enemyBalls)
      {
        balls.setEnergy(machine.field.myPlayer.getEnergy());
      }
      enemyBalls.get(i).update();
      enemyBalls.get(i).show();

      if (ball_ballCollision)
      {
        for (int k=0; k < enemyBalls.size (); k++)
        {
          if (k != i)
          {
            enemyBalls.get(i).checkBallCollision(enemyBalls.get(k), elasticCollision);
          }
        }
      }
    }
  }

  public void updateBullets()
  {
    for (int i=0; i < myBullets.size (); i++)
    {
      myBullets.get(i).update(i);
      for (int k=0; k < enemyBalls.size (); k++)
      {
        if (myBullets.size() > 0)
        {
          if ((myBullets.get(i).getX() - enemyBalls.get(k).getX())*(myBullets.get(i).getX() - enemyBalls.get(k).getX()) + (enemyBalls.get(k).getY() - myBullets.get(i).getY())*(enemyBalls.get(k).getY() - myBullets.get(i).getY()) <= (myBullets.get(i).getRad()+enemyBalls.get(k).getRad())*(myBullets.get(i).getRad()+enemyBalls.get(k).getRad()))
          {
            enemyBalls.get(k).spawnBalls();
            myBullets.remove(i);
            enemyBalls.remove(k);
            ballsDestroyed++;
            break;
          }
        }
      }
    }
  }

  public void partyEnd()
  {
    if (myPlayer.isDead())
    {
      if (myPlayer.lives > 0)
      {
        myPlayer.lives--;
        gui.state = GUI_STATES.LIVE_LOST_SHOW;
        myPlayer.energy = 100;
      } else
      {
        frameCounter = 0;
        state = INGAME_STATES.GAMEOVER_TRANSITION1;
      }
    }
  }

  public int getBallsDestroyed()
  {
    return ballsDestroyed;
  }
  
  public int getSchwarzschild()
  {
    return schwarzschild_r;
  }

  public void keyPressed()
  {
    switch (state)
    {
    case HIT:
    case STANDARD:
      switch (key)
      {
      case   'a':
      case  LEFT: 
        myPlayer.movLeft(); 
        break;
      case   'd':
      case RIGHT: 
        myPlayer.movRight(); 
        break;
      case ENTER: 
        myPlayer.shoot(); 
        break;
      case ESC:
        key = 0; 
        machine.state = GAME_STATES.INGAME_PAUSE;
      }
      break;
    case INITIALIZE_FADE:
    case COUNTDOWN:
      switch (key)
      {
      case ESC:
        key = 0; 
        machine.state = GAME_STATES.INGAME_PAUSE;
        break;
      }
      break;
    default:
      key = 0;
      break;
    }
  }

  public void keyReleased()
  {
    switch(key)
    {
    case   'a':
    case  LEFT: 
      myPlayer.movLeftStop(); 
      break;
    case   'd':
    case RIGHT: 
      myPlayer.movRightStop(); 
      break;
    }
  }
}

class State_Main
{
  /*****************************************************************************************************************/
  private Text[] options;          //The texts for the different sub-menus
  private int cursorPos;           //The current position of the cursor
  /*****************************************************************************************************************/


  State_Main()
  {
    options = new Text[4];

    options[0] = new Text(width*0.5f, height*0.4f, width*0.03f, "Custom game", fontSmall, 255, color(255), 1);
    options[1] = new Text(width*0.5f, height*0.5f, width*0.03f, "Player list", fontSmall, 255, color(255), 1);
    options[2] = new Text(width*0.5f, height*0.6f, width*0.03f, "Options", fontSmall, 255, color(255), 1);
    options[3] = new Text(width*0.5f, height*0.7f, width*0.03f, "Quit", fontSmall, 255, color(255), 1);
  }

  public void update()
  {
    calculateCursor();


    for (int i=0; i < options.length; i++)
    {
      options[i].update();
    }
  }

  public void calculateCursor()
  {
    if (cursorPos < 0)
    {
      cursorPos = options.length-1;
    } else if (cursorPos > options.length-1)
    {
      cursorPos = 0;
    }

    for (int i=0; i < options.length; i++)
    {
      options[i].setCol(color(255));
    }

    for (int i=0; i < options.length; i++)
    {
      options[i].setCol(color(255));
    }
    options[cursorPos].setCol(color(0, 120, 255));
  }

  private void enterPressed()
  {
    switch (cursorPos)
    {
    case 0:
      machine.state = GAME_STATES.CUSTOM_GAME;
      break;
    case 1:
      machine.playerlist.updateContent();
      machine.playerlist.updateTexts();
      machine.state = GAME_STATES.PLAYERLIST;
      break;
    case 2:
      machine.options.state = OPTIONS_STATES.FROM_MAIN_MENU;
      machine.state = GAME_STATES.OPTIONS;
      break;
    case 3:
      exit();
      break;
    }
  }

  public void keyPressed()
  {
    switch (key)
    {
    case 'w':
    case 'W': 
      cursorPos--; 
      break;
    case 's':
    case 'S': 
      cursorPos++; 
      break;
    case ENTER: 
      enterPressed(); 
      break;
    case ESC:
      key = 0;
      break;
    }
  }
}

class State_Options
{
  /************************************************************************************************************************************************/
  OPTIONS_STATES state;                 //The current state of the options. (Mainly whether the player opened them from the main menu or the ingame pause menu)
  private Text info;                    //The info text at the bottom of the screen
  private Text[] options;               //The options available
  private Text[] option_status;         //The matching stati (ON/OFF, values) for these options
  int cursorPos;                        //The current cursor position
  /************************************************************************************************************************************************/
  

  State_Options()
  {
    info = new Text(width*0.5f, height*0.85f, width*0.02f, "Info:", fontSmall, 255, color(0, 120, 255), 1);

    options = new Text[4]; 
    options[0] = new Text(width*0.45f, height*0.4f, width*0.02f, "Death indicator", fontSmall, 255, color(255), 2);
    options[1] = new Text(width*0.45f, height*0.5f, width*0.02f, "Background grid", fontSmall, 255, color(255), 2);
    options[2] = new Text(width*0.45f, height*0.6f, width*0.02f, "Menu animation", fontSmall, 255, color(255), 2);
    options[3] = new Text(width*0.5f, height*0.75f, width*0.025f, "", fontSmall, 255, color(255), 1);

    option_status = new Text[3];
    option_status[0] = new Text(width*0.6f, height*0.4f, width*0.02f, "", fontSmall, 255, color(255), 1);
    option_status[1] = new Text(width*0.6f, height*0.5f, width*0.02f, "", fontSmall, 255, color(255), 1);
    option_status[2] = new Text(width*0.6f, height*0.6f, width*0.02f, "", fontSmall, 255, color(255), 1);
  }

  public void update()
  {
    calculateCursor();
    updateTexts();
  }

  public void updateTexts()
  {
    for (int i=0; i < options.length; i++)
    {
      options[i].update();
    }

    if (state == OPTIONS_STATES.FROM_MAIN_MENU)
    {
      options[3].setText("Back to main menu");
    } else
    {
      options[3].setText("Resume game");
    }

    if (ball_deadly)
    {
      option_status[0].setText("<  ON   >");
    } else
    {
      option_status[0].setText("<  OFF  >");
    }

    if (ball_grid)
    {
      option_status[1].setText("<  ON   >");
    } else
    {
      option_status[1].setText("<  OFF  >");
    }

    if (background_anim)
    {
      option_status[2].setText("<  ON   >");
    } else
    {
      option_status[2].setText("<  OFF  >");
    }

    for (int i=0; i < option_status.length; i++)
    {
      option_status[i].align(0);
      option_status[i].update();
    }

    info.update();
  }

  public void calculateCursor()
  {
    if (cursorPos < 0)
    {
      cursorPos = options.length-1;
    } else if (cursorPos > options.length-1)
    {
      cursorPos = 0;
    }

    for (int i=0; i < options.length; i++)
    {
      options[i].setCol(color(255));
    }

    for (int i=0; i < option_status.length; i++)
    {
      option_status[i].setCol(color(255));
    }

    options[cursorPos].setCol(color(0, 120, 255));
    if (cursorPos < option_status.length)
    {
      option_status[cursorPos].setCol(color(0, 120, 255));
    }

    switch (cursorPos)
    {
    case 0: 
      info.setText("Info: Balls which would be deadly to your ship are colored red."); 
      break;
    case 1:
      info.setText("Info: Shows a grid in the background.");
      break;
    case 2:
      info.setText("Info: Displays bouncing balls in the menus.");
      break;
    default: 
      info.setText(""); 
      break;
    }
  }

  private void enterFunction()
  {
    switch (cursorPos)
    {
    case 0:
      changeSetting();
      break;
    case 1:
      changeSetting();
      break;
    case 2:
      changeSetting();
      break;
    case 3:
      leaveMenu();
      break;
    }
  }

  public void leaveMenu()
  {
    if (state == OPTIONS_STATES.FROM_MAIN_MENU)
    {
      cursorPos = 0;
      machine.state = GAME_STATES.MAIN_MENU;
    } else
    {
      if (ball_grid)
      {
        machine.field.myGrid = new Grid(15, 40, color(80), color(0));
      } else
      {
        machine.field.myGrid = new Grid(15, 40, color(0), color(0));
      }
      cursorPos = 0;
      machine.state = GAME_STATES.INGAME;
    }
  }

  public void changeSetting()
  {
    switch (cursorPos)
    {
    case 0: 
      ball_deadly = !ball_deadly; 
      break;
    case 1: 
      ball_grid = !ball_grid; 
      break;
      case 2: 
      background_anim = !background_anim; 
      break;
    }
  }

  public void keyPressed()
  {
    switch (key)
    {
    case 'w': 
    case 'W':
      cursorPos--; 
      break;
    case 's': 
    case 'S':
      cursorPos++; 
      break;
    case 'a':
    case 'A':
      changeSetting();
      break;
    case 'd':
    case 'D':
      changeSetting();
      break;
    case ENTER: 
      enterFunction(); 
      break;
    case ESC:
      key = 0;
      leaveMenu();
      break;
    }
  }
}

class State_Pause
{
  /*****************************************************************************/
  private Text[] options;          //The available options (Continue, Options, Leave)
  private int cursorPos;           //The current position of the cursor
  /*****************************************************************************/


  State_Pause() {
    options = new Text[4];

    options[0] = new Text(width*0.5f, height*0.45f, width*0.035f, "Paused", loadFont("Impact-48.vlw"), 255, color(255), 1);
    options[1] = new Text(width*0.38f, height*0.55f, width*0.022f, "Continue", loadFont("Impact-48.vlw"), 255, color(255), 1);
    options[2] = new Text(width*0.5f, height*0.55f, width*0.022f, "Options", loadFont("Impact-48.vlw"), 255, color(255), 1);
    options[3] = new Text(width*0.62f, height*0.55f, width*0.022f, "Leave", loadFont("Impact-48.vlw"), 255, color(255), 1);
  }

  public void update()
  {
    calculateCursor();
    rectMode(CENTER);
    stroke(255);
    fill(0);
    rect(width*0.5f, height*0.5f, width*0.4f, height*0.25f);
    rectMode(CORNER);

    for (int i=0; i < options.length; i++)
    {
      options[i].update();
    }
  }

  public void calculateCursor()
  {
    for (int i=0; i < options.length; i++)
    {
      options[i].setCol(color(255));
    }

    if (cursorPos < 0)
    {
      cursorPos = options.length-2;
    } else if (cursorPos > options.length-2)
    {
      cursorPos = 0;
    }

    switch (cursorPos)
    {
    case 0:
      options[1].setCol(color(0, 120, 255));
      break;
    case 1:
      options[2].setCol(color(0, 120, 255));
      break;
    case 2:
      options[3].setCol(color(0, 120, 255));
      break;
    }
  }

  private void enterPressed()
  {
    switch (cursorPos)
    {
    case 0:
      cursorPos = 0;
      machine.state = GAME_STATES.INGAME;
      break;
    case 1:
      cursorPos = 0;
      machine.options.state = OPTIONS_STATES.FROM_INGAME;
      machine.state = GAME_STATES.OPTIONS;
      break;
    case 2:
      cursorPos = 0;
      machine.state = GAME_STATES.MAIN_MENU;
      break;
    }
  }


  public void keyPressed()
  {
    switch (key)
    {
    case 'a':
      cursorPos--;
      break;
    case 'd':
      cursorPos++;
      break;
    case ESC:
      key = 0;
      cursorPos = 0;
      machine.state = GAME_STATES.INGAME;
      break;
    case ENTER:
      enterPressed();
      break;
    }
  }
}

class State_Playerlist
{
  /*******************************************************************************************************************/
  private Text[] options;                  //The three values a player can have. Name, time survived and balls destroyed
  private String[] playerString;           //The string in which the data of playerlist.txt is loaded into
  private ArrayList<Text> players;         //The list used in displaying this data
  private int firstPlayerPos;              //The position of the first player currently visible. (Not used yet)
  /*******************************************************************************************************************/


  State_Playerlist()
  {
    options = new Text[3];
    playerString = loadStrings("playerlist.txt");

    options[0] = new Text(width*0.3f, height*0.3f, width*0.02f, "Name", fontSmall, 255, color(0, 120, 255), 2);
    options[1] = new Text(width*0.5f, height*0.3f, width*0.02f, "Seconds", fontSmall, 255, color(0, 120, 255), 1);
    options[2] = new Text(width*0.7f, height*0.3f, width*0.02f, "Balls", fontSmall, 255, color(0, 120, 255), 1);
  }

  public void update()
  {
    for (int i=0; i < options.length; i++)
    {
      options[i].update();
    }

    for (Text texts : players)
    {
      texts.update();
    }
  }

  public void updateContent()
  {
    playerString = loadStrings("playerlist.txt");

    if (playerString.length < 18)
    {
      int tempLength = playerString.length;
      players = new ArrayList<Text>();
      for (int i=0; i < playerString.length; i+=3)
      {
        players.add(new Text(width*0.3f, height*0.5f, width*0.02f, playerString[i], fontSmall, 255, color(255), 2));
        players.add(new Text(width*0.5f, height*0.5f, width*0.02f, playerString[i+1], fontSmall, 255, color(255), 1));
        players.add(new Text(width*0.7f, height*0.5f, width*0.02f, playerString[i+2], fontSmall, 255, color(255), 1));
      }
      for (int i=tempLength; i < 18; i++)
      {
        players.add(new Text(width*0.7f, height*0.5f, width*0.02f, "", fontSmall, 0, color(255), 1));
      }
    } else
    {
      players = new ArrayList<Text>();
      for (int i=0; i < playerString.length; i+=3)
      {
        players.add(new Text(width*0.3f, height*0.5f, width*0.02f, playerString[i], fontSmall, 255, color(255), 2));
        players.add(new Text(width*0.5f, height*0.5f, width*0.02f, playerString[i+1], fontSmall, 255, color(255), 1));
        players.add(new Text(width*0.7f, height*0.5f, width*0.02f, playerString[i+2], fontSmall, 255, color(255), 1));
      }
    }
  }

  private void updateTexts()
  {
    if (firstPlayerPos < 0)
    {
      firstPlayerPos = 0;
    }

    for (Text texts : players)
    {
      texts.setOpacity(0);
    }

    for (int i=0; i < 18; i+=3)
    {
      players.get(firstPlayerPos + i).setOpacity(255);
      players.get(firstPlayerPos + i + 1).setOpacity(255);
      players.get(firstPlayerPos + i + 2).setOpacity(255);
      players.get(firstPlayerPos + i).setYabs(height*0.35f+i*height*0.014f);
      players.get(firstPlayerPos + i + 1).setYabs(height*0.35f+i*height*0.014f);
      players.get(firstPlayerPos + i + 2).setYabs(height*0.35f+i*height*0.014f);
    }
  }

  public void keyPressed()
  {
    switch (key)
    {
    case ESC:
      key = 0;
      machine.state = GAME_STATES.MAIN_MENU;
      break;
    case ENTER:
      machine.state = GAME_STATES.MAIN_MENU;
      break;
    case 'w':
      //firstPlayerPos--;
      //updateTexts();
      break;
    case 's':
      //firstPlayerPos++;
      //updateTexts();
      break;
    }
  }
}

class Text
{
  /*********************************************************************************************************************/
  private float xPos;              //
  private float yPos;              //
  private int fontSize;            //
  private float opacity;           //
  private String content;          //
  private PFont font;              //
  private int col;               //
  private int align;               //
  /*********************************************************************************************************************/


  Text(float tempXPos, float tempYPos, float tempSize, String tempContent, PFont tempFont, int tempOpacity, int tempCol, int tempAlign)
  {
    xPos = tempXPos;
    yPos = tempYPos;
    fontSize = (int)tempSize;
    content = tempContent;
    this.font = tempFont;
    opacity = tempOpacity;
    align = tempAlign;

    col = color(red(tempCol), green(tempCol), blue(tempCol), opacity);
  }

  public void update()
  {
    drawText();
  }

  public void drawText()
  {
    switch (align)
    {
    case 0: 
      textAlign(LEFT, CENTER); 
      break;
    case 1: 
      textAlign(CENTER, CENTER); 
      break;
    case 2: 
      textAlign(RIGHT, CENTER); 
      break;
    default: 
      textAlign(CENTER, CENTER); 
      break;
    }
    fill(col);
    textFont(font);
    textSize(fontSize);
    text(content, xPos, yPos);
  }

  public void setCol(int tempCol)
  {
    col = color(red(tempCol), green(tempCol), blue(tempCol), opacity);
  }

  public void setX(float tempX)
  {
    xPos += (int)tempX;
  }

  public void setY(float tempY)
  {
    yPos += (int)tempY;
  }
  
  public void setYabs(float tempY)
  {
    yPos = tempY;
  }

  public void setOpacity(int tempOp)
  {
    col = color(red(col), green(col), blue(col), tempOp);
  }

  public void align(int tempAlign)
  {
    align = tempAlign;
  }

  public void setText(String tempContent)
  {
    content = tempContent;
  }

  public float getY()
  {
    return yPos;
  }
  
  public String getText()
  {
    return content;
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--hide-stop", "BallsAttack" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
