class State_Main
{
  /*****************************************************************************************************************/
  private Text[] options;          //The texts for the different sub-menus
  private int cursorPos;           //The current position of the cursor
  /*****************************************************************************************************************/


  State_Main()
  {
    options = new Text[4];

    options[0] = new Text(width*0.5, height*0.4, width*0.03, "Custom game", fontSmall, 255, color(255), 1);
    options[1] = new Text(width*0.5, height*0.5, width*0.03, "Player list", fontSmall, 255, color(255), 1);
    options[2] = new Text(width*0.5, height*0.6, width*0.03, "Options", fontSmall, 255, color(255), 1);
    options[3] = new Text(width*0.5, height*0.7, width*0.03, "Quit", fontSmall, 255, color(255), 1);
  }

  void update()
  {
    calculateCursor();


    for (int i=0; i < options.length; i++)
    {
      options[i].update();
    }
  }

  void calculateCursor()
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

  void keyPressed()
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

