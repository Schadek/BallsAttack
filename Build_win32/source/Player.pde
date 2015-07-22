class Player
{
  /*****************************************************************************************************************************************************/
  PLAYER_STATES state;              //State the player is in (Vulnerable or invincible)

  private PVector position;         //The position of the player
  private PVector size;             //The size of the player
  private int speed;                //The speed the player travels with
  private color fillCol;            //The inner color of the player. Is used for the GUI, too. The outline is colored inverted to the background
  private boolean mov_right;        //A switch to check whether the player is pressing the 'd' key. Enables fluid motion. 
  private boolean mov_left;         //A switch to check whether the player is pressing the 'a' key. Enables fluid motion. 

  private int frameCounter;         //Counter used to mark invincibility frames
  private int lives;                //The lives the player has left
  private int energy;               //The energy the current life has left. (max. 100)
  /*****************************************************************************************************************************************************/


  Player(int tempsizeX, int tempsizeY, int tempSpeed, color tempFillColor, int tempLives)
  {
    size = new PVector(tempsizeX, tempsizeY);
    position = new PVector(width/2, height-size.y-1);
    speed = tempSpeed;
    fillCol = tempFillColor;
    lives = tempLives;
    energy = 100;
    state = PLAYER_STATES.INVINCIBLE;
  }

  void movLeft()
  {
    mov_left = true;
  }

  void movRight()
  {
    mov_right = true;
  }

  void movLeftStop()
  {
    mov_left = false;
  }

  void movRightStop()
  {
    mov_right = false;
  }

  void shoot()
  {
    if (machine.field.myBullets.size() < machine.field.maxBullets)
    {
      machine.field.myBullets.add(new Bullet((int)(position.x + size.x/2), (int)(position.y), machine.field.bulletSize, machine.field.bulletSpeed));
    }
  }

  void update()
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
          machine.field.enemyBalls.get(i).position.x += machine.field.enemyBalls.get(i).movement.x*0.01;
          machine.field.enemyBalls.get(i).position.y += machine.field.enemyBalls.get(i).movement.y*0.01;
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

  boolean intersects(AmigaBall other)
  {
    float distX = abs(other.position.x - (position.x + size.x*0.5));
    float distY = abs(other.position.y - (position.y + size.y*0.5));
    float cornerDistance_sq = (distX - size.x*0.5)*(distX - size.x*0.5) + (distY - size.y*0.5)*(distY - size.y*0.5);

    if (distX > (size.x*0.5 + other.radius))
    { 
      return false;
    }
    if (distY > (size.y*0.5 + other.radius)) 
    { 
      return false;
    }
    if (distX <= size.x*0.5) 
    { 
      return true;
    } 
    if (distY <= size.y*0.5) 
    { 
      return true;
    }
    if (cornerDistance_sq <= other.radius*other.radius)
    {
      return true;
    }
    return false;
  }

  void calculateInvincibility()
  {
    if (frameCounter > 0)
    {
      frameCounter--;
    } else
    {
      state = PLAYER_STATES.VULNERABLE;
    }
  }

  void addX(int tempX)
  {
    position.x += tempX;
  }

  void setLives(int tempLives)
  {
    lives = tempLives;
  }

  int getSizeX()
  {
    return (int)size.x;
  }

  int getSizeY()
  {
    return (int)size.y;
  }

  color getColor()
  {
    return fillCol;
  }

  int getLives()
  {
    return lives;
  }

  int getEnergy()
  {
    return energy;
  }

  boolean isDead()
  {
    if (energy <= 0)
    {
      return true;
    }
    return false;
  }
}

