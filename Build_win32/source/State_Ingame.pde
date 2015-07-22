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

    countdown = new Text(width*0.5, height*0.5, width*0.04, "", font, 0, color(255), 1);
    gameover = new Text(width*0.5, height*0.5, width*0.08, "GAME OVER", font, 0, color(200, 0, 0), 1);
    myPlayer = new Player((int)(width*0.0125), (int)(width*0.025), width/80, color(0, 120, 255), 5);
    gui = new GUI((int)(width*0.25), (int)(height*0.03));
    myGrid = new Grid(15, 40, color(0), color(0));

    state = INGAME_STATES.INITIALIZE_FADE;
  }

  void update()
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

  void checkBallSpawn()
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

  void addBall()
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

  void removeBall(int other)
  {
    if (enemyBalls.size() > 0)
    {
      enemyBalls.remove(other);
    }
  }

  void updateBalls()
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

  void updateBullets()
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

  void partyEnd()
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

  int getBallsDestroyed()
  {
    return ballsDestroyed;
  }
  
  int getSchwarzschild()
  {
    return schwarzschild_r;
  }

  void keyPressed()
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

  void keyReleased()
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

