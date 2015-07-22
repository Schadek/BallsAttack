class State_Background
{
  /**************************************************************************************************************************************************************/
  private ArrayList<AmigaBall> bgBalls;          //The balls bouncing in the background. Limited to maxBalls
  private float gravity;                         //The gravity acting in the background space. Has a probability to inverse
  private int maxBalls = 5;                          //The maximum amount of balls on the grid
  /**************************************************************************************************************************************************************/
  
  
  State_Background()
  {
    bgBalls = new ArrayList<AmigaBall>();
    gravity = 0.1;
    addBall();
  }

  void update()
  {
    background(0);

    if (background_anim)
    {
      if ((int)random(0, 1000) == 42)
      {
        gravity *= (-1);
        for (AmigaBall balls : bgBalls)
        {
          balls.setGravity(gravity);
        }
      }

      if ((int)random(0, 100) == 42)
      {
        if (bgBalls.size() < maxBalls)
        {
          addBall();
        }
      }

      for (int i=0; i < bgBalls.size (); i++)
      {
        bgBalls.get(i).update();
        bgBalls.get(i).show();

        for (int k=0; k < bgBalls.size (); k++)
        {
          if (k != i)
          {
            bgBalls.get(i).checkBallCollision(bgBalls.get(k), true);
          }
        }
      }
    }
  }

  void addBall()
  {
    boolean isColliding = true;
    while (isColliding)
    {
      bgBalls.add(new AmigaBall((int)(width*0.02), (int)(width*0.05), height*0.5, gravity));

      if (bgBalls.size() == 1)
      {
        isColliding = false;
      }

      if (isColliding)
      {
        for (int k=0; k < bgBalls.size (); k++)
        {
          if (k != bgBalls.size()-1)
          {
            if ((bgBalls.get(bgBalls.size()-1).getX() - bgBalls.get(k).getX())*(bgBalls.get(bgBalls.size()-1).getX() - bgBalls.get(k).getX()) + (bgBalls.get(k).getY() - bgBalls.get(bgBalls.size()-1).getY())*(bgBalls.get(k).getY() - bgBalls.get(bgBalls.size()-1).getY()) <= (bgBalls.get(bgBalls.size()-1).getRad()+bgBalls.get(k).getRad())*(bgBalls.get(bgBalls.size()-1).getRad()+bgBalls.get(k).getRad()))
            {
              removeBall(bgBalls.size()-1);
            } else
            {
              isColliding = false;
            }
          }
        }
      }
    }
  }

  void removeBall(int other)
  {
    if (bgBalls.size() > 0)
    {
      bgBalls.remove(other);
    }
  }

  void resetBalls()
  {
    bgBalls.clear();
  }
}

