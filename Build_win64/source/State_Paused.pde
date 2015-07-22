class State_Pause
{
  /*****************************************************************************/
  private Text[] options;          //The available options (Continue, Options, Leave)
  private int cursorPos;           //The current position of the cursor
  /*****************************************************************************/


  State_Pause() {
    options = new Text[4];

    options[0] = new Text(width*0.5, height*0.45, width*0.035, "Paused", loadFont("Impact-48.vlw"), 255, color(255), 1);
    options[1] = new Text(width*0.38, height*0.55, width*0.022, "Continue", loadFont("Impact-48.vlw"), 255, color(255), 1);
    options[2] = new Text(width*0.5, height*0.55, width*0.022, "Options", loadFont("Impact-48.vlw"), 255, color(255), 1);
    options[3] = new Text(width*0.62, height*0.55, width*0.022, "Leave", loadFont("Impact-48.vlw"), 255, color(255), 1);
  }

  void update()
  {
    calculateCursor();
    rectMode(CENTER);
    stroke(255);
    fill(0);
    rect(width*0.5, height*0.5, width*0.4, height*0.25);
    rectMode(CORNER);

    for (int i=0; i < options.length; i++)
    {
      options[i].update();
    }
  }

  void calculateCursor()
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


  void keyPressed()
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

