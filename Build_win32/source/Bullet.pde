class Bullet
{
  /*******************************************************************************************************/
  private PVector position;       //Saves the position of the bullet 
  private int speed;              //The speed in pixel the bullet travels each frame
  private int radius;             //The radius of the projectile
  /*******************************************************************************************************/


  Bullet(int tempXPos, int tempYPos, int tempRadius, int tempSpeed)
  {
    position = new PVector(tempXPos, tempYPos);
    speed = tempSpeed;
    radius = tempRadius;
  }

  void update(int currentBullet)
  {
    updateBullet(currentBullet);
    drawBullet();
  }

  private void updateBullet(int currentBullet)
  {
    position.y -= speed;

    if (position.y < 0)
    {
      machine.field.myBullets.remove(currentBullet);
    }
  }

  private void drawBullet()
  {
    stroke(255);
    strokeWeight(1);
    fill(machine.field.myPlayer.getColor());
    ellipse((float) position.x, (float) position.y, (float) radius, (float) radius);
  }

  int getX()
  {
    return (int)position.x;
  }

  int getY()
  {
    return (int)position.y;
  }

  int getRad()
  {
    return radius;
  }
}

