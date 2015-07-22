class AmigaBall 
{
  /*************************************************************************************************************************************************************/
  private PVector position;      //Saves the x/y position of the ball
  private PVector movement;      //Saves the velocity of the ball
  private int radius;            //The radius of the ball
  private color ballColor;       //The color of the ball
  private float minSpawnHeight;  //The minimum height at which a ball can spawn
  private float gravity;         //The gravity of the playfield. Has to be saved for each ball so the balls can be used in the background of the menus
  private int playerEnergy;      //The energy of the player character. Has to be saved for each ball so the balls can be used in the background of the menus
  /*************************************************************************************************************************************************************/


  AmigaBall(PVector tempPos, PVector tempMovement, int tempRadius, color tempcol, float tempGrav, float tempminSpawnHeight)
  {
    position = tempPos;
    movement = tempMovement;
    radius = tempRadius;
    ballColor = tempcol;
    gravity = tempGrav;
    playerEnergy = 100;

    if (minSpawnHeight <= radius)
    {
      minSpawnHeight = radius+1;
    } else 
    {
      minSpawnHeight = tempminSpawnHeight;
    }
  }

  AmigaBall(int tempMinRad, int tempMaxRad, float tempminSpawnHeight, float tempGrav)
  {
    radius = (int) random(tempMinRad, tempMaxRad);
    ballColor = color(random(0, 255), random(0, 255), random(0, 255));
    gravity = tempGrav;
    playerEnergy = 100;
    minSpawnHeight = tempminSpawnHeight;

    position = new PVector(random(radius, width - radius), random(radius, height - minSpawnHeight));

    if (random(0, 1) == 0)
    {
      movement = new PVector(random((-5), (-1)), 0);
    } else
    {
      movement = new PVector(random(1, 5), 0);
    }
    movement.mult(3);
  }

  void update()
  {
    if (position.y < height - radius)
    {
      movement.y += gravity;
    }

    position.add(movement);
    checkEdgeCollision();
  }

  void checkEdgeCollision()
  {
    if (position.x > width - radius)
    {
      position.x = width - radius;
      movement.x *= (-1);
    } else if (position.x < radius)
    {
      position.x = radius;
      movement.x *= (-1);
    }
    if (position.y > height - radius)
    {
      position.y = height - radius;
      movement.y *= (-1);
    } else if (position.y < radius)
    {
      position.y = radius;
      movement.y *= (-1);
    }
  }

  void spawnBalls()
  {
    if (radius > machine.field.getSchwarzschild())
    {
      machine.field.enemyBalls.add(new AmigaBall(new PVector((int)(position.x+(radius/2)), (int)position.y), movement, radius/2, ballColor, gravity, (int)minSpawnHeight));
      machine.field.enemyBalls.add(new AmigaBall(new PVector((int)(position.x-(radius/2)), (int)position.y), new PVector(movement.x*(-1), movement.y), radius/2, ballColor, gravity, (int)minSpawnHeight));
    }
  }

  void checkBallCollision(AmigaBall collider, boolean isElastic)
  {
    if (isElastic)
    {
      PVector bVect = PVector.sub(collider.position, position);

      float bVectMag = bVect.mag();

      if (bVectMag < radius + collider.radius) 
      {
        float theta  = bVect.heading();
        float sine = sin(theta);
        float cosine = cos(theta);

        PVector[] bTemp = {
          new PVector(), new PVector()
          };

          bTemp[1].x  = cosine * bVect.x + sine * bVect.y;
        bTemp[1].y  = cosine * bVect.y - sine * bVect.x;

        PVector[] vTemp = {
          new PVector(), new PVector()
          };

          vTemp[0].x  = cosine * movement.x + sine * movement.y;
        vTemp[0].y  = cosine * movement.y - sine * movement.x;
        vTemp[1].x  = cosine * collider.movement.x + sine * collider.movement.y;
        vTemp[1].y  = cosine * collider.movement.y - sine * collider.movement.x;

        PVector[] vFinal = {  
          new PVector(), new PVector()
          };

          vFinal[0].x = ((radius*0.1 - collider.radius*0.1) * vTemp[0].x + 2 * collider.radius*0.1 * vTemp[1].x) / (radius*0.1 + collider.radius*0.1);
        vFinal[0].y = vTemp[0].y;

        vFinal[1].x = ((collider.radius*0.1 - radius*0.1) * vTemp[1].x + 2 * radius*0.1 * vTemp[0].x) / (radius*0.1 + collider.radius*0.1);
        vFinal[1].y = vTemp[1].y;

        bTemp[0].x += vFinal[0].x;
        bTemp[1].x += vFinal[1].x;

        PVector[] bFinal = { 
          new PVector(), new PVector()
          };

          bFinal[0].x = cosine * bTemp[0].x - sine * bTemp[0].y;
        bFinal[0].y = cosine * bTemp[0].y + sine * bTemp[0].x;
        bFinal[1].x = cosine * bTemp[1].x - sine * bTemp[1].y;
        bFinal[1].y = cosine * bTemp[1].y + sine * bTemp[1].x;

        collider.position.x = position.x + bFinal[1].x;
        collider.position.y = position.y + bFinal[1].y;

        position.add(bFinal[0]);

        movement.x = cosine * vFinal[0].x - sine * vFinal[0].y;
        movement.y = cosine * vFinal[0].y + sine * vFinal[0].x;
        collider.movement.x = cosine * vFinal[1].x - sine * vFinal[1].y;
        collider.movement.y = cosine * vFinal[1].y + sine * vFinal[1].x;
      }
    } else
    {
      float dx = collider.position.x - position.x;
      float dy = collider.position.y - position.y;
      float distance = sqrt(dx*dx + dy*dy);
      float minDist = collider.radius + radius;
      if (distance < minDist) { 
        float angle = atan2(dy, dx);
        float targetX = position.x + cos(angle) * minDist;
        float targetY = position.y + sin(angle) * minDist;
        float ax = (targetX - collider.position.x);
        float ay = (targetY - collider.position.y);
        movement.x -= ax;
        movement.y -= ay;
        collider.movement.x += ax;
        collider.movement.y += ay;
      }
    }
  }

  void show() 
  {
    noStroke();

    if (radius >= playerEnergy && ball_deadly && machine.state == GAME_STATES.INGAME)
    {
      fill(255, 0, 0);
    } else if (ball_deadly && machine.state == GAME_STATES.INGAME)
    {
      fill(0, 255, 0);
    } else
    {
      fill(ballColor);
    }
    ellipse(position.x, position.y, radius*2, radius*2);
  }

  void addX(int tempX)
  {
    position.x += tempX;
  }

  void setColor(color other)
  {
    ballColor = other;
  }

  void setEnergy(int other)
  {
    playerEnergy = other;
  }

  void setGravity(float other)
  {
    gravity = other;
  }

  int getX()
  {
    return (int) position.x;
  }

  int getY()
  {
    return (int) position.y;
  }

  int getRad()
  {
    return radius;
  }
}

