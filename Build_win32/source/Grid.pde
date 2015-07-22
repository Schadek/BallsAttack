class Grid
{
  /*******************************************************************************************************/
  private int cells;                    //The number of cells the grid inherits
  private int margin;                   //The space between the edges of the window and the grid
  private color gridColor;              //The color of the actual grid
  private color backgroundColor;        //The color of the background
  /*******************************************************************************************************/


  Grid(int tempCells, int tempMargin, color tempGridColor, color tempBackgroundColor)
  {
    cells = tempCells; 
    margin = tempMargin;
    gridColor = tempGridColor;
    backgroundColor = tempBackgroundColor;
  }

  void update()
  {
    background(backgroundColor);
    stroke(gridColor);
    strokeWeight(3);
    size(width, height);
    for (int i=0; i<=cells; i++) 
    {
      line(margin, margin+i*(height-2*margin)/cells, width-margin, margin+i*(height-2*margin)/cells);
      line(margin+i*(width-2*margin)/cells, margin, margin+i*(width-2*margin)/cells, height-margin);
    }
  }

  void setColor(color tempBG, color tempCol)
  {
    gridColor = tempCol;
    backgroundColor = tempBG;
  }
  
  color getBackgroundColor()
  {
    return backgroundColor;
  }
}

