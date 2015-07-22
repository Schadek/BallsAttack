/**************************************************************************************************************************************************************/
private String gameTitle = "Balls Attack";        //Title of the game. Will adjust the title in the menus

private int resolutionX = displayWidth;                     //Screen resolution along the x-axis
private int resolutionY = displayHeight;                      //Screen resolution along the y-axis

public boolean ball_deadly = false;                 //Whether the balls are displayed in green/red or in their actual color              
public boolean ball_grid = false;                   //Whether a grid is displayed in the background
public boolean background_anim = true;              //Whether some bouncing balls are displayed in the menu backgrounds

public PFont font;                                  //The standard font used by most texts
public PFont fontSmall;                             //The lower resolution of the standard font. (Needed for correct display of texts smaller than ~20)
/**************************************************************************************************************************************************************/
