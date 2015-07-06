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
    options[0] = new Text(width*0.2, height*0.35, width*0.017, "Min. radius", fontSmall, 255, color(255), 1); 
    options[1] = new Text(width*0.4, height*0.35, width*0.017, "Max. radius", fontSmall, 255, color(255), 1);
    options[2] = new Text(width*0.6, height*0.35, width*0.017, "Ball interval (sec.)", fontSmall, 255, color(255), 1);
    options[3] = new Text(width*0.8, height*0.35, width*0.017, "Start balls", fontSmall, 255, color(255), 1);
    options[4] = new Text(width*0.2, height*0.5, width*0.017, "Gravity", fontSmall, 255, color(255), 1);
    options[5] = new Text(width*0.4, height*0.5, width*0.017, "Destruction rad.", fontSmall, 255, color(255), 1);
    options[6] = new Text(width*0.6, height*0.5, width*0.017, "Max. bullets", fontSmall, 255, color(255), 1);
    options[7] = new Text(width*0.8, height*0.5, width*0.017, "Bullet size", fontSmall, 255, color(255), 1);
    options[8] = new Text(width*0.2, height*0.65, width*0.017, "Bullets speed", fontSmall, 255, color(255), 1);
    options[9] = new Text(width*0.4, height*0.65, width*0.017, "Lives", fontSmall, 255, color(255), 1);
    options[10] = new Text(width*0.6, height*0.65, width*0.017, "Ball-Ball collision", fontSmall, 255, color(255), 1);
    options[11] = new Text(width*0.8, height*0.65, width*0.017, "Elastic collision", fontSmall, 255, color(255), 1);
    options[12] = new Text(width*0.2, height*0.8, width*0.025, "Start game", fontSmall, 255, color(255), 1);
    options[13] = new Text(width*0.4, height*0.8, width*0.025, "Back to main menu", fontSmall, 255, color(255), 1);
    options[14] = new Text(width*0.6, height*0.8, width*0.025, "Reset", fontSmall, 255, color(255), 1);
    options[15] = new Text(width*0.8, height*0.8, width*0.025, "Set to minimum", fontSmall, 255, color(255), 1);

    option_status = new Text[12];
    option_status[0] = new Text(width*0.2, height*0.4, width*0.017, "<  " + minRad + "  >", fontSmall, 255, color(255), 1); 
    option_status[1] = new Text(width*0.4, height*0.4, width*0.017, "<  " + maxRad + "  >", fontSmall, 255, color(255), 1);
    option_status[2] = new Text(width*0.6, height*0.4, width*0.017, "<  " + ballInterval + "  >", fontSmall, 255, color(255), 1);
    option_status[3] = new Text(width*0.8, height*0.4, width*0.017, "<  " + startBalls + "  >", fontSmall, 255, color(255), 1);
    option_status[4] = new Text(width*0.2, height*0.55, width*0.017, "<  " + gravity + "  >", fontSmall, 255, color(255), 1);
    option_status[5] = new Text(width*0.4, height*0.55, width*0.017, "<  " + schwarzschild + "  >", fontSmall, 255, color(255), 1);
    option_status[6] = new Text(width*0.6, height*0.55, width*0.017, "<  " + maxBullets + "  >", fontSmall, 255, color(255), 1);
    option_status[7] = new Text(width*0.8, height*0.55, width*0.017, "<  " + bulletSize + "  >", fontSmall, 255, color(255), 1);
    option_status[8] = new Text(width*0.2, height*0.7, width*0.017, "<  " + bulletSpeed + "  >", fontSmall, 255, color(255), 1);
    option_status[9] = new Text(width*0.4, height*0.7, width*0.017, "<  " + livesAmount + "  >", fontSmall, 255, color(255), 1);
    option_status[10] = new Text(width*0.6, height*0.7, width*0.017, "<  " + "ON" + "  >", fontSmall, 255, color(255), 1);
    option_status[11] = new Text(width*0.8, height*0.7, width*0.017, "<  " + "ON" + "  >", fontSmall, 255, color(255), 1);
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

  void changeSetting()
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

  void updateSettings()
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

  void calculateCursor()
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

  void resetSettings()
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

  void minimizeSettings()
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
      machine.field = new State_Playfield(gravity*0.1, maxBullets, schwarzschild, minRad, maxRad, height*0.4, bulletSize, bulletSpeed, startBalls, ballInterval, elasticCollision, ball_ballCollision);
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

  void keyPressed()
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

