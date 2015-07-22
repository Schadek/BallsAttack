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
    livesPosition = new PVector((int)(width*0.065), (int)(height*0.0125));

    myTexts = new Text[4];
    myTexts[0] = new Text(width*0.5, height*0.5, width*0.04, "Live lost", font, 0, color(255), 1);
    myTexts[1] = new Text(width*0.008, height*0.032, width*0.02, "Lives:", fontSmall, 255, color(255), 0);     
    myTexts[2]= new Text(width*0.25, height*0.035, width*0.02, "(PLAYERLIVES)", fontSmall, 0, color(255), 1);
    myTexts[3]= new Text(width*0.5, height*0.035, width*0.025, "" + time, fontSmall, 255, color(255), 1);
  }

  void update()
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
      rect(livesPosition.x+i*width*0.02, livesPosition.y, machine.field.myPlayer.getSizeX(), machine.field.myPlayer.getSizeY());
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

  void liveLost()
  {
  }

  void addX(int tempX)
  {
    livesPosition.x += tempX;
    healthbarPosition.x += tempX;
    for (Text texts : myTexts)
    {
      texts.setX(tempX);
    }
  }
  
  int getTimeSurvived()
  {
    return time/1000;
  }
}

