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
    info = new Text(width*0.5, height*0.85, width*0.02, "Info:", fontSmall, 255, color(0, 120, 255), 1);

    options = new Text[4]; 
    options[0] = new Text(width*0.45, height*0.4, width*0.02, "Death indicator", fontSmall, 255, color(255), 2);
    options[1] = new Text(width*0.45, height*0.5, width*0.02, "Background grid", fontSmall, 255, color(255), 2);
    options[2] = new Text(width*0.45, height*0.6, width*0.02, "Menu animation", fontSmall, 255, color(255), 2);
    options[3] = new Text(width*0.5, height*0.75, width*0.025, "", fontSmall, 255, color(255), 1);

    option_status = new Text[3];
    option_status[0] = new Text(width*0.6, height*0.4, width*0.02, "", fontSmall, 255, color(255), 1);
    option_status[1] = new Text(width*0.6, height*0.5, width*0.02, "", fontSmall, 255, color(255), 1);
    option_status[2] = new Text(width*0.6, height*0.6, width*0.02, "", fontSmall, 255, color(255), 1);
  }

  void update()
  {
    calculateCursor();
    updateTexts();
  }

  void updateTexts()
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

  void leaveMenu()
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

  void changeSetting()
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

