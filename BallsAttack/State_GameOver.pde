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
    gameover = new Text(width*0.5, height*0.5, width*0.08, "GAME OVER", font, 255, color(200, 0, 0), 1);
    players = new ArrayList<String>();

    results = new Text[6];
    results[0] = new Text(width*0.5, height*0.4, width*0.04, "Seconds survived:", font, 0, color(255), 2);
    results[1] = new Text(width*0.6, height*0.4, width*0.04, "", font, 0, color(255), 0);
    results[2] = new Text(width*0.5, height*0.5, width*0.04, "Balls destroyed:", font, 0, color(255), 2);
    results[3] = new Text(width*0.6, height*0.5, width*0.04, "", font, 0, color(255), 0);
    results[4] = new Text(width*0.5, height*0.6, width*0.04, "Please enter your name:", font, 0, color(255), 2);
    results[5] = new Text(width*0.6, height*0.6, width*0.04, "", font, 0, color(255), 0);

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

      if (gameover.getY() > height*0.15)
      {
        gameover.setY(-2);
        if (gameover.getY() < height*0.25)
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

  void keyPressed()
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

