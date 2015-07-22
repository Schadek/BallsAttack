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

    options[0] = new Text(width*0.3, height*0.3, width*0.02, "Name", fontSmall, 255, color(0, 120, 255), 2);
    options[1] = new Text(width*0.5, height*0.3, width*0.02, "Seconds", fontSmall, 255, color(0, 120, 255), 1);
    options[2] = new Text(width*0.7, height*0.3, width*0.02, "Balls", fontSmall, 255, color(0, 120, 255), 1);
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
        players.add(new Text(width*0.3, height*0.5, width*0.02, playerString[i], fontSmall, 255, color(255), 2));
        players.add(new Text(width*0.5, height*0.5, width*0.02, playerString[i+1], fontSmall, 255, color(255), 1));
        players.add(new Text(width*0.7, height*0.5, width*0.02, playerString[i+2], fontSmall, 255, color(255), 1));
      }
      for (int i=tempLength; i < 18; i++)
      {
        players.add(new Text(width*0.7, height*0.5, width*0.02, "", fontSmall, 0, color(255), 1));
      }
    } else
    {
      players = new ArrayList<Text>();
      for (int i=0; i < playerString.length; i+=3)
      {
        players.add(new Text(width*0.3, height*0.5, width*0.02, playerString[i], fontSmall, 255, color(255), 2));
        players.add(new Text(width*0.5, height*0.5, width*0.02, playerString[i+1], fontSmall, 255, color(255), 1));
        players.add(new Text(width*0.7, height*0.5, width*0.02, playerString[i+2], fontSmall, 255, color(255), 1));
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
      players.get(firstPlayerPos + i).setYabs(height*0.35+i*height*0.014);
      players.get(firstPlayerPos + i + 1).setYabs(height*0.35+i*height*0.014);
      players.get(firstPlayerPos + i + 2).setYabs(height*0.35+i*height*0.014);
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

