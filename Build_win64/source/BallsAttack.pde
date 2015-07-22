private STATE_MACHINE machine; 

void setup() 
{
  if (resolutionX <= 0 || resolutionY <= 0)
  {
    resolutionX = displayWidth; 
    resolutionY = displayHeight;
  }

  size(resolutionX, resolutionY);
  machine = new STATE_MACHINE();
}

void draw()
{
  machine.update();
}

void keyPressed()
{
  machine.keyPressed();
}

void keyReleased()
{
  machine.keyReleased();
}


/*
TODO: 
 - United menus to one class, possibly implementing
 - Actual highscore list
 - Level selection with individual highscore lists
 - Sound
 - Power-Ups maybe
 
 BUGS:
 - Sometimes the balls 'merge' and collide with each other, while rapidly increasing their speed. Looks like a problem in the collision code
 - The playerlist is not yet usable. It will display the correct players and their achievements but won't be able to scroll in the list (also: '-' symbols everywhere)
 */
