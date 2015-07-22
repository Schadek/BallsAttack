class Text
{
  /*********************************************************************************************************************/
  private float xPos;              //
  private float yPos;              //
  private int fontSize;            //
  private float opacity;           //
  private String content;          //
  private PFont font;              //
  private color col;               //
  private int align;               //
  /*********************************************************************************************************************/


  Text(float tempXPos, float tempYPos, float tempSize, String tempContent, PFont tempFont, int tempOpacity, color tempCol, int tempAlign)
  {
    xPos = tempXPos;
    yPos = tempYPos;
    fontSize = (int)tempSize;
    content = tempContent;
    this.font = tempFont;
    opacity = tempOpacity;
    align = tempAlign;

    col = color(red(tempCol), green(tempCol), blue(tempCol), opacity);
  }

  void update()
  {
    drawText();
  }

  void drawText()
  {
    switch (align)
    {
    case 0: 
      textAlign(LEFT, CENTER); 
      break;
    case 1: 
      textAlign(CENTER, CENTER); 
      break;
    case 2: 
      textAlign(RIGHT, CENTER); 
      break;
    default: 
      textAlign(CENTER, CENTER); 
      break;
    }
    fill(col);
    textFont(font);
    textSize(fontSize);
    text(content, xPos, yPos);
  }

  void setCol(color tempCol)
  {
    col = color(red(tempCol), green(tempCol), blue(tempCol), opacity);
  }

  void setX(float tempX)
  {
    xPos += (int)tempX;
  }

  void setY(float tempY)
  {
    yPos += (int)tempY;
  }
  
  void setYabs(float tempY)
  {
    yPos = tempY;
  }

  void setOpacity(int tempOp)
  {
    col = color(red(col), green(col), blue(col), tempOp);
  }

  void align(int tempAlign)
  {
    align = tempAlign;
  }

  void setText(String tempContent)
  {
    content = tempContent;
  }

  float getY()
  {
    return yPos;
  }
  
  String getText()
  {
    return content;
  }
}

