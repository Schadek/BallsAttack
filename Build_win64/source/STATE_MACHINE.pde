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
    title = new Text(width*0.5, height*0.15, width*0.06, gameTitle, font, 255, color(255), 1);

    state = GAME_STATES.MAIN_MENU;
  }

  void update()
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

  void keyReleased()
  {
    switch (state)
    {
    case INGAME:
      field.keyReleased(); 
      break;
    }
  }

  void keyPressed()
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

