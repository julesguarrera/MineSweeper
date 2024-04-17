import java.util.ArrayList;
import java.util.Arrays;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;

//creates MineWorld class, extending World
class MineWorld extends World {
  int cellSize = 25; // cell size constant
  ArrayList<ArrayList<Cell>> allCells; // 2D grid of the cells in an ArrayList
  int rows; // num of rows
  int cols; // num of cols
  int numMine; // number of mines on the board
  Random seed; // random seed
  boolean gameIsOver; // determines if a game is over, starts false
  int flagsLeft; // number of flags that are left to be placed, shown on screen
  int minesLeft; // number of mines that remain to be flagged, not shown on screen
  int timePass; // indiciates how much time has passed in ticks for the current world
  boolean gameWon; // returns true when the game is won
  int clock; // indiciates the seconds passed that will be displayed on the scree
  boolean startingScreen; // determines if the game is currently on the starting screen or not

  // constructor for MineWorld class to play
  MineWorld(int rows, int cols, int numMine) {
    this.rows = rows;
    this.cols = cols;
    this.numMine = numMine;
    this.seed = new Random();
    this.flagsLeft = this.numMine;
    this.minesLeft = this.numMine;
    this.startingScreen = true;
    this.allCells = new ArrayList<ArrayList<Cell>>();
    this.gameIsOver = false;
    for (int i = 0; i < rows; ++i) {
      ArrayList<Cell> row = new ArrayList<Cell>();
      for (int j = 0; j < cols; ++j) {
        row.add(new Cell(0));
      }
      this.allCells.add(row);

    }
    // adds bombs to the cells
    this.bombCreation(rows, cols, numMine, this.seed);
    // adds all of the cells neighbors
    this.addNeighbors();
  }

  // constructor for MineWorld class to test
  MineWorld(int rows, int cols, int numMine, Random seed) {
    this.rows = rows;
    this.cols = cols;
    this.numMine = numMine;
    this.flagsLeft = this.numMine;
    this.minesLeft = this.numMine;
    this.startingScreen = false;
    this.allCells = new ArrayList<ArrayList<Cell>>();
    this.seed = seed;
    this.gameIsOver = false;
    for (int i = 0; i < rows; ++i) {
      ArrayList<Cell> row = new ArrayList<Cell>();
      for (int j = 0; j < cols; ++j) {
        row.add(new Cell(0));
      }
      this.allCells.add(row);

    }
    // adds bombs to the cells
    this.bombCreation(rows, cols, numMine, this.seed);
    // adds all of the cells neighbors
    this.addNeighbors();

  }

  // creates the placement of the numMine
  void bombCreation(int row, int col, int count, Random seed) {
    ArrayList<ArrayList<Cell>> order = this.allCells;
    while (count > 0) {
      int spotX = seed.nextInt(col - 1);
      int spotY = seed.nextInt(row - 1);
      // System.out.println(count);
      if (0 == order.get(spotY).get(spotX).represent) {
        order.get(spotY).set(spotX, new Cell(-1));

        count--;
      }
    }
    this.allCells = order;
  }

  // links the cells together with their neighbors
  void addNeighbors() {
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.cols; j++) {

        Cell current = this.allCells.get(i).get(j);
        if (j < this.cols - 1) {
          current.addCell(this.allCells.get(i).get(j + 1));

          if (i < this.rows - 1) {
            current.addCell(this.allCells.get(i + 1).get(j + 1));
          }

          if (i > 0) {
            current.addCell(this.allCells.get(i - 1).get(j + 1));
          }
        }

        if (i > 0) {
          current.addCell(this.allCells.get(i - 1).get(j));
        }

        if (i < this.rows - 1) {
          current.addCell(this.allCells.get(i + 1).get(j));
        }

        if (j > 0) {
          current.addCell(this.allCells.get(i).get(j - 1));

          if (i > 0) {

            current.addCell(this.allCells.get(i - 1).get(j - 1));
          }

          if (i < this.rows - 1) {

            current.addCell(this.allCells.get(i + 1).get(j - 1));
          }
        }
      }
    }
  }

  // creates WorldScene to actually draw scene
  public WorldScene makeScene() {
    if (startingScreen) {
      WorldScene drawStart = new WorldScene(1000, 1000);

      drawStart.placeImageXY(new TextImage("Minesweeper", cellSize * 2, Color.BLACK), cellSize * 8,
          60);
      drawStart.placeImageXY(new TextImage("Difficulty", cellSize, Color.BLACK), cellSize * 8,
          cellSize * 5);
      drawStart.placeImageXY(
          new OverlayOffsetImage(new TextImage("Easy", 15, Color.BLACK), 1, 0,
              new RectangleImage(80, 20, OutlineMode.SOLID, Color.GREEN)),
          cellSize * 8, cellSize * 7);
      drawStart.placeImageXY(
          new OverlayOffsetImage(new TextImage("Medium", 15, Color.BLACK), 1, 0,
              new RectangleImage(80, 20, OutlineMode.SOLID, Color.GREEN)),
          cellSize * 8, cellSize * 9);
      drawStart.placeImageXY(
          new OverlayOffsetImage(new TextImage("Hard", 15, Color.BLACK), 1, 0,
              new RectangleImage(80, 20, OutlineMode.SOLID, Color.GREEN)),
          cellSize * 8, cellSize * 11);

      return drawStart;
    }
    else {

      WorldScene finalDraw = new WorldScene(cellSize * this.cols, cellSize * this.rows);
      for (int j = 0; j < this.rows; ++j) {
        for (int i = 0; i < this.cols; ++i) {

          finalDraw.placeImageXY(allCells.get(j).get(i).draw(this.cellSize, i, j),
              (this.cellSize / 2) + (i * this.cellSize), (this.cellSize / 2) + (j * this.cellSize));
        }
      }
      finalDraw.placeImageXY(
          new RectangleImage(this.cols * cellSize, this.cellSize + (this.cellSize / 5 * 4),
              OutlineMode.SOLID, new Color(0, 0, 190)),
          (this.cellSize * this.cols) / 2,
          (this.cellSize * this.rows) + ((this.cellSize + 20) / 2) + 1);
      finalDraw.placeImageXY(
          new BesideImage(new EquilateralTriangleImage(15, OutlineMode.SOLID, Color.RED),
              new TextImage(" = " + this.flagsLeft, this.cellSize / 5 * 3, FontStyle.BOLD,
                  Color.WHITE)),
          this.cellSize * (this.cols / 8),
          (this.cellSize * this.rows) + ((this.cellSize + 20) / 2) + 1);
      finalDraw.placeImageXY(
          new TextImage(String.valueOf(this.clock), this.cellSize / 5 * 3, Color.WHITE),
          this.cellSize * this.cols - ((this.cols - (this.cols - (this.cols / 8))) * this.cellSize),
          (this.cellSize * this.rows) + ((this.cellSize + 20) / 2) + 1);
      finalDraw.placeImageXY(
          new OverlayImage(new TextImage("Restart", 15, Color.WHITE),
              new RectangleImage(80, 20, OutlineMode.SOLID, Color.BLACK)),
          this.cellSize * this.cols / 2 + (this.cols / 2) + 3,
          (this.cellSize * this.rows) + ((this.cellSize + 20) / 2) + 1);

      if (this.gameIsOver) {
        if (this.gameWon) {
          finalDraw.placeImageXY(new TextImage("You Win", this.rows * 2.25, Color.BLACK),
              this.cellSize * cols / 2 - this.cellSize / 5, this.cellSize * rows / 2);
        }
        else {
          finalDraw.placeImageXY(new TextImage("Game Over", this.rows * 2.25, Color.RED),
              this.cellSize * cols / 2 - this.cellSize / 5, this.cellSize * rows / 2);
        }
      }
      return finalDraw;

    }
  }

  public void onTick() {
    if (!this.gameIsOver) {
      if (this.timePass % 10 == 0) {
        this.clock++;
      }
      this.timePass++;
    }
  }

  public void onMousePressed(Posn pos, String buttonName) {
    if (startingScreen) {
      if (pos.x < cellSize * 9.6 && pos.x > cellSize * 6.4) {
        if (pos.y < cellSize * 7.4 && pos.y > cellSize * 6.6) {

          this.rows = 12;
          this.cols = 8;
          this.numMine = 10;
          this.seed = new Random();
          this.flagsLeft = this.numMine;
          this.minesLeft = this.numMine;
          this.startingScreen = false;
          this.clock = 0;
          this.timePass = -1;
          this.allCells = new ArrayList<ArrayList<Cell>>();
          this.gameIsOver = false;
          for (int i = 0; i < rows; ++i) {
            ArrayList<Cell> row = new ArrayList<Cell>();
            for (int j = 0; j < cols; ++j) {
              row.add(new Cell(0));
            }
            this.allCells.add(row);

          }
          // adds bombs to the cells
          this.bombCreation(this.rows, this.cols, this.numMine, this.seed);
          // adds all of the cells neighbors
          this.addNeighbors();
        }

        else if (pos.y < cellSize * 9.4 && pos.y > cellSize * 8.6) {

          this.rows = 21;
          this.cols = 12;
          this.numMine = 40;
          this.seed = new Random();
          this.flagsLeft = this.numMine;
          this.minesLeft = this.numMine;
          this.startingScreen = false;
          this.clock = 0;
          this.timePass = -1;
          this.allCells = new ArrayList<ArrayList<Cell>>();
          this.gameIsOver = false;
          for (int i = 0; i < rows; ++i) {
            ArrayList<Cell> row = new ArrayList<Cell>();
            for (int j = 0; j < cols; ++j) {
              row.add(new Cell(0));
            }
            this.allCells.add(row);

          }
          // adds bombs to the cells
          this.bombCreation(this.rows, this.cols, this.numMine, this.seed);
          // adds all of the cells neighbors
          this.addNeighbors();
        }

        else if (pos.y < cellSize * 11.4 && pos.y > cellSize * 10.6) {

          this.rows = 30;
          this.cols = 16;
          this.numMine = 99;
          this.seed = new Random();
          this.flagsLeft = this.numMine;
          this.minesLeft = this.numMine;
          this.startingScreen = false;
          this.clock = 0;
          this.timePass = -1;
          this.allCells = new ArrayList<ArrayList<Cell>>();
          this.gameIsOver = false;
          for (int i = 0; i < rows; ++i) {
            ArrayList<Cell> row = new ArrayList<Cell>();
            for (int j = 0; j < cols; ++j) {
              row.add(new Cell(0));
            }
            this.allCells.add(row);

          }
          // adds bombs to the cells
          this.bombCreation(this.rows, this.cols, this.numMine, this.seed);
          // adds all of the cells neighbors
          this.addNeighbors();
        }

      }
    }
    else if (!this.gameIsOver) {
      if (pos.x < this.cellSize * this.cols / 2 + (this.cols / 2) + 40
          && pos.x > this.cellSize * this.cols / 2 + (this.cols / 2) - 40
          && pos.y < ((this.cellSize * this.rows) + ((this.cellSize + 20) / 2) + 1) + 10
          && pos.y > ((this.cellSize * this.rows) + ((this.cellSize + 20) / 2) + 1) - 10) {

        this.startingScreen = true;
      }
      if (buttonName.equals("LeftButton")) {
        for (int i = 0; i < this.allCells.size(); i++) {
          if (i * this.cellSize <= pos.y && pos.y < (i + 1) * this.cellSize) {
            for (int j = 0; j < this.allCells.get(i).size(); j++) {
              if (j * this.cellSize <= pos.x && pos.x < (j + 1) * this.cellSize) {
                Cell currCell = this.allCells.get(i).get(j);
                if (currCell.represent != -1) {
                  currCell.neighborR();
                }
                else {
                  this.gameIsOver = true;
                  for (int k = 0; k < this.allCells.size(); k++) {
                    for (int z = 0; z < this.allCells.get(i).size(); z++) {
                      Cell curr = this.allCells.get(k).get(z);

                      if (curr.represent == -1) {
                        curr.clicked = true;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      else if (buttonName.equals("RightButton")) {
        for (int i = 0; i < this.allCells.size(); i++) {
          if (i * this.cellSize <= pos.y && pos.y < (i + 1) * this.cellSize) {
            for (int j = 0; j < this.allCells.get(i).size(); j++) {
              if (j * this.cellSize <= pos.x && pos.x < (j + 1) * this.cellSize) {
                Cell currCell = this.allCells.get(i).get(j);

                if (!currCell.clicked) {
                  currCell.flag = !currCell.flag;
                  if (!currCell.flag) {
                    this.flagsLeft += 2;
                  }

                  if (currCell.represent == -1) {
                    this.flagsLeft--;
                    this.minesLeft--;

                    if (!currCell.flag) {
                      this.minesLeft += 2;
                    }

                    if (this.minesLeft == 0 && this.flagsLeft == 0) {
                      this.gameWon = true;
                      this.gameIsOver = true;
                    }
                  }
                  else {
                    this.flagsLeft--;
                  }

                }

              }
            }
          }
        }
      }
    }
    else if (this.gameIsOver) {
      if (pos.x < this.cellSize * this.cols / 2 + (this.cols / 2) + 40
          && pos.x > this.cellSize * this.cols / 2 + (this.cols / 2) - 40
          && pos.y < ((this.cellSize * this.rows) + ((this.cellSize + 20) / 2) + 1) + 10
          && pos.y > ((this.cellSize * this.rows) + ((this.cellSize + 20) / 2) + 1) - 10) {

        this.startingScreen = true;
      }
    }

  }

}

// Represents a cell in the WorldScene
class Cell {
  boolean flag; // returns true if a flag is on a cell
  int represent; // will be -1 if a bomb, 0 if not
  boolean clicked; // will return true if a cell has been clicked
  ArrayList<Cell> neighbor = new ArrayList<Cell>(); // list of a cells neighbors

  // constructor for Cell
  Cell(int represent) {
    this.represent = represent;
    this.flag = false;
    this.clicked = false;
  }

  // overridden constructor for Cell
  Cell(boolean flag, int represent, boolean clicked) {
    this.flag = flag;
    this.represent = represent;
    this.clicked = clicked;

  }

  // Adds a cell to the list of this cells neighbors
  void addCell(Cell add) {
    if (add.represent == -1 && this.represent != -1) {
      this.represent = 0;
    }
    neighbor.add(add);
  }

  // counts number of mines that are touching a singular cell
  int countMines() {
    int count = 0;

    for (Cell c : this.neighbor) {
      if (c.represent == -1) {
        count += 1;
      }
    }
    return count;
  }

  // draws the grid
  WorldImage draw(int size, int row, int col) {

    WorldImage blank = new RectangleImage(size, size, OutlineMode.OUTLINE, Color.BLACK);
    if (flag) {
      return new OverlayImage(
          new EquilateralTriangleImage(size / 1.56, OutlineMode.SOLID, Color.RED), new OverlayImage(
              blank, new RectangleImage(size, size, OutlineMode.SOLID, new Color(131, 181, 255))));
    }
    if (clicked) {
      if (this.represent != -1) {
        Color shade = Color.WHITE;

        if (this.countMines() == 1) {
          shade = Color.BLUE;
        }
        else if (this.countMines() == 2) {
          shade = new Color(0, 153, 0);
        }
        else if (this.countMines() == 3) {
          shade = Color.RED;
        }
        else if (this.countMines() == 4) {
          shade = new Color(102, 0, 153);
        }
        else if (this.countMines() == 5) {
          shade = new Color(153, 0, 0);
        }
        else if (this.countMines() == 5) {
          shade = new Color(51, 204, 255);
        }
        else if (this.countMines() > 5) {
          shade = Color.BLACK;
        }
        return new OverlayImage(
            new TextImage(String.valueOf(this.countMines()), size - 5, FontStyle.BOLD, shade),
            new OverlayImage(blank,
                new RectangleImage(size, size, OutlineMode.SOLID, Color.WHITE)));
      }

      return new OverlayImage(new CircleImage(size / 4, OutlineMode.SOLID, Color.BLACK),
          new OverlayImage(blank, new RectangleImage(size, size, OutlineMode.SOLID, Color.WHITE)));

    }
    return new OverlayImage(blank,
        new RectangleImage(size, size, OutlineMode.SOLID, new Color(131, 181, 255)));
  }

  void neighborR() {
    if (!this.clicked && this.represent == 0) {
      this.clicked = true;
      boolean counterT = true;
      for (Cell c : this.neighbor) {
        if (c.represent == -1) {
          counterT = false;
        }
      }
      if (counterT) {
        for (int k = 0; k < this.neighbor.size(); k++) {
          this.neighbor.get(k).neighborR();
        }
      }
    }
  }
}

class ExamplesMines {
  MineWorld world1;
  MineWorld world2;
  MineWorld world3;
  MineWorld world4;
  Cell c1;
  Cell c2;
  Cell c3;
  Cell c4;
  Cell c5;
  Cell c6;
  Cell c7;
  Cell c8;
  Cell c9;

  // for testing to draw

  Cell flagC; // cell with flag on it
  Cell bombC; // cell with bomb on it
  Cell mtC; // cell with nothing on it clicked
  Cell fillC; // cell with nothing on it unclicked
  Cell numC; // cell with a number on it

  // for testing add cell

  Cell ct1;
  Cell ct2;
  Cell ct3;
  Cell ct4;
  Cell ct5;

  // for testing an intermediate game board

  Cell ci1;
  Cell ci2;
  Cell ci3;
  Cell ci4;
  Cell ci5;
  Cell ci6;
  Cell ci7;
  Cell ci8;
  Cell ci9;

  void init() {

    // use in real game
    this.world1 = new MineWorld(30, 16, 99);
    this.world4 = new MineWorld(30, 16, 99, new Random(3));
    // use in testing
    this.world2 = new MineWorld(3, 3, 3, new Random(5));

    c1 = new Cell(false, -1, false);
    c2 = new Cell(false, -1, false);
    c3 = new Cell(false, 0, false);
    c4 = new Cell(false, -1, false);
    c5 = new Cell(false, 0, false);
    c6 = new Cell(false, 0, false);
    c7 = new Cell(false, 0, false);
    c8 = new Cell(false, 0, false);
    c9 = new Cell(false, 0, false);

    // for testing to draw

    flagC = new Cell(true, 0, false);
    bombC = new Cell(false, -1, true);
    mtC = new Cell(false, 0, true);
    fillC = new Cell(false, 0, false);
    numC = new Cell(false, 0, true);

    this.numC.neighbor = new ArrayList<Cell>(Arrays.asList(this.bombC));

    // for testing add cell and add cell back

    ct1 = new Cell(0);
    ct2 = new Cell(0);
    ct3 = new Cell(0);
    ct4 = new Cell(-1);
    ct5 = new Cell(-1);

    // to test an intermediate game board

    ci1 = new Cell(true, -1, false);
    ci2 = new Cell(false, -1, false);
    ci3 = new Cell(false, 0, true);
    ci4 = new Cell(false, 0, true);
    ci5 = new Cell(true, -1, false);
    ci6 = new Cell(false, 0, true);
    ci7 = new Cell(false, 0, true);
    ci8 = new Cell(false, 0, false);
    ci9 = new Cell(false, 0, true);

    this.c1.neighbor = new ArrayList<Cell>(Arrays.asList(this.c2, this.c5, this.c4));
    this.c2.neighbor = new ArrayList<Cell>(
        Arrays.asList(this.c3, this.c6, this.c5, this.c1, this.c4));
    this.c3.neighbor = new ArrayList<Cell>(Arrays.asList(this.c6, this.c2, this.c5));
    this.c4.neighbor = new ArrayList<Cell>(
        Arrays.asList(this.c5, this.c8, this.c2, this.c1, this.c7));
    this.c5.neighbor = new ArrayList<Cell>(
        Arrays.asList(this.c6, this.c9, this.c3, this.c2, this.c8, this.c4, this.c1, this.c7));
    this.c6.neighbor = new ArrayList<Cell>(
        Arrays.asList(this.c3, this.c9, this.c5, this.c2, this.c8));
    this.c7.neighbor = new ArrayList<Cell>(Arrays.asList(this.c8, this.c5, this.c4));
    this.c8.neighbor = new ArrayList<Cell>(
        Arrays.asList(this.c9, this.c6, this.c5, this.c7, this.c4));
    this.c9.neighbor = new ArrayList<Cell>(Arrays.asList(this.c6, this.c8, this.c5));

    // to test intermediate game board
    this.world3 = new MineWorld(3, 3, 3, new Random(5));

    world3.allCells = new ArrayList<ArrayList<Cell>>(
        Arrays.asList(new ArrayList<Cell>(Arrays.asList(this.ci1, this.ci2, this.ci3)),
            new ArrayList<Cell>(Arrays.asList(this.ci4, this.ci5, this.ci6)),
            new ArrayList<Cell>(Arrays.asList(this.ci7, this.ci8, this.ci9))));

    this.ci1.neighbor = new ArrayList<Cell>(Arrays.asList(this.ci2, this.ci5, this.ci4));
    this.ci2.neighbor = new ArrayList<Cell>(
        Arrays.asList(this.ci3, this.ci6, this.ci5, this.ci1, this.ci4));
    this.ci3.neighbor = new ArrayList<Cell>(Arrays.asList(this.ci6, this.ci2, this.ci5));
    this.ci4.neighbor = new ArrayList<Cell>(
        Arrays.asList(this.ci5, this.ci8, this.ci2, this.ci1, this.ci7));
    this.ci5.neighbor = new ArrayList<Cell>(Arrays.asList(this.ci6, this.ci9, this.ci3, this.ci2,
        this.ci8, this.ci4, this.ci1, this.ci7));
    this.ci6.neighbor = new ArrayList<Cell>(
        Arrays.asList(this.ci3, this.ci9, this.ci5, this.ci2, this.ci8));
    this.ci7.neighbor = new ArrayList<Cell>(Arrays.asList(this.ci8, this.ci5, this.ci4));
    this.ci8.neighbor = new ArrayList<Cell>(
        Arrays.asList(this.ci9, this.ci6, this.ci5, this.ci7, this.ci4));
    this.ci9.neighbor = new ArrayList<Cell>(Arrays.asList(this.ci6, this.ci8, this.ci5));

  }

  // testing the game constructor
  void testConstruct(Tester t) {
    this.init();

    t.checkExpect(this.world1.rows, 30);
    t.checkExpect(this.world1.cols, 16);
    t.checkExpect(this.world1.numMine, 99);
    t.checkExpect(this.world2.rows, 3);
    t.checkExpect(this.world2.cols, 3);
    t.checkExpect(this.world2.numMine, 3);
    t.checkExpect(this.world3.rows, 3);
    t.checkExpect(this.world3.cols, 3);
    t.checkExpect(this.world3.numMine, 3);

  }

  // testing bomb creation
  void testBombCreation(Tester t) {
    this.init();
    // bomb creation is initialized in the constructor and thus does not need to be
    // called again;
    // bombs should be placed in their respective cells at this point.
    // -1 is a bomb, 0 is not a bomb.
    ArrayList<ArrayList<Cell>> order = world2.allCells;
    t.checkExpect(order.size(), 3);
    t.checkExpect(order.get(0).get(0).represent, -1);
    t.checkExpect(order.get(0).get(1).represent, -1);
    t.checkExpect(order.get(0).get(2).represent, 0);
    t.checkExpect(order.get(1).get(0).represent, -1);
    t.checkExpect(order.get(1).get(1).represent, 0);
    t.checkExpect(order.get(1).get(2).represent, 0);
    t.checkExpect(order.get(2).get(0).represent, 0);
    t.checkExpect(order.get(2).get(1).represent, 0);
    t.checkExpect(order.get(2).get(2).represent, 0);
  }

  // testing add neighbors
  void testAddneighbors(Tester t) {
    this.init();

    t.checkExpect(this.world2.allCells.get(0).get(0).neighbor.get(0), this.c2);
    t.checkExpect(this.world2.allCells.get(2).get(1).neighbor.get(2), this.c5);
    t.checkExpect(this.world2.allCells.get(1).get(1).neighbor.get(1), this.c9);

  }

  // testing make scene, real test is an intermediate game board
  void testMakeScene(Tester t) {
    this.init();

    // testing intermediate game board
    WorldScene ws = new WorldScene(75, 75);
    ws.placeImageXY(this.ci1.draw(25, world3.rows, world3.cols), 12, 12);
    ws.placeImageXY(this.ci2.draw(25, world3.rows, world3.cols), 37, 12);
    ws.placeImageXY(this.ci3.draw(25, world3.rows, world3.cols), 62, 12);
    ws.placeImageXY(this.ci4.draw(25, world3.rows, world3.cols), 12, 37);
    ws.placeImageXY(this.ci5.draw(25, world3.rows, world3.cols), 37, 37);
    ws.placeImageXY(this.ci6.draw(25, world3.rows, world3.cols), 62, 37);
    ws.placeImageXY(this.ci7.draw(25, world3.rows, world3.cols), 12, 62);
    ws.placeImageXY(this.ci8.draw(25, world3.rows, world3.cols), 37, 62);
    ws.placeImageXY(this.ci9.draw(25, world3.rows, world3.cols), 62, 62);
    t.checkExpect(this.world3.makeScene(), ws);

    // testing intermediate game board
    WorldScene ws2 = new WorldScene(75, 75);
    ws2.placeImageXY(this.c1.draw(25, world2.rows, world2.cols), 12, 12);
    ws2.placeImageXY(this.c2.draw(25, world2.rows, world2.cols), 37, 12);
    ws2.placeImageXY(this.c3.draw(25, world2.rows, world2.cols), 62, 12);
    ws2.placeImageXY(this.c4.draw(25, world2.rows, world2.cols), 12, 37);
    ws2.placeImageXY(this.c5.draw(25, world2.rows, world2.cols), 37, 37);
    ws2.placeImageXY(this.c6.draw(25, world2.rows, world2.cols), 62, 37);
    ws2.placeImageXY(this.c7.draw(25, world2.rows, world2.cols), 12, 62);
    ws2.placeImageXY(this.c8.draw(25, world2.rows, world2.cols), 37, 62);
    ws2.placeImageXY(this.c9.draw(25, world2.rows, world2.cols), 62, 62);
    t.checkExpect(this.world2.makeScene(), ws2);

    // testing when game is won
    this.init();

    world2.gameWon = true;
    world2.gameIsOver = true;
    WorldScene ws3 = new WorldScene(75, 75);
    ws3.placeImageXY(this.c1.draw(25, world2.rows, world2.cols), 12, 12);
    ws3.placeImageXY(this.c2.draw(25, world2.rows, world2.cols), 37, 12);
    ws3.placeImageXY(this.c3.draw(25, world2.rows, world2.cols), 62, 12);
    ws3.placeImageXY(this.c4.draw(25, world2.rows, world2.cols), 12, 37);
    ws3.placeImageXY(this.c5.draw(25, world2.rows, world2.cols), 37, 37);
    ws3.placeImageXY(this.c6.draw(25, world2.rows, world2.cols), 62, 37);
    ws3.placeImageXY(this.c7.draw(25, world2.rows, world2.cols), 12, 62);
    ws3.placeImageXY(this.c8.draw(25, world2.rows, world2.cols), 37, 62);
    ws3.placeImageXY(new OverlayImage(new RectangleImage(25, 25, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(25, 25, OutlineMode.SOLID, new Color(131, 181, 255))), 62, 62);
    ws3.placeImageXY(new RectangleImage(75, 45, OutlineMode.SOLID, new Color(0, 0, 190)), 37, 98);
    ws3.placeImageXY(new BesideImage(new EquilateralTriangleImage(15, OutlineMode.SOLID, Color.RED),
        new TextImage(" = 3", 15, FontStyle.BOLD, Color.WHITE)), 0, 98);
    ws3.placeImageXY(new TextImage("0", 15, Color.WHITE), 75, 98);
    ws3.placeImageXY(new OverlayImage(new TextImage("Restart", 15, Color.WHITE),
        new RectangleImage(80, 20, OutlineMode.SOLID, Color.BLACK)), 41, 98);
    ws3.placeImageXY(new TextImage("You Win", 6.75, Color.BLACK), 32, 37);
    t.checkExpect(world2.makeScene(), ws3);

    // testing when game is lost
    this.init();

    world2.gameWon = false;
    world2.gameIsOver = true;
    WorldScene ws4 = new WorldScene(75, 75);
    ws4.placeImageXY(this.c1.draw(25, world2.rows, world2.cols), 12, 12);
    ws4.placeImageXY(this.c2.draw(25, world2.rows, world2.cols), 37, 12);
    ws4.placeImageXY(this.c3.draw(25, world2.rows, world2.cols), 62, 12);
    ws4.placeImageXY(this.c4.draw(25, world2.rows, world2.cols), 12, 37);
    ws4.placeImageXY(this.c5.draw(25, world2.rows, world2.cols), 37, 37);
    ws4.placeImageXY(this.c6.draw(25, world2.rows, world2.cols), 62, 37);
    ws4.placeImageXY(this.c7.draw(25, world2.rows, world2.cols), 12, 62);
    ws4.placeImageXY(this.c8.draw(25, world2.rows, world2.cols), 37, 62);
    ws4.placeImageXY(new OverlayImage(new RectangleImage(25, 25, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(25, 25, OutlineMode.SOLID, new Color(131, 181, 255))), 62, 62);
    ws4.placeImageXY(new RectangleImage(75, 45, OutlineMode.SOLID, new Color(0, 0, 190)), 37, 98);
    ws4.placeImageXY(new BesideImage(new EquilateralTriangleImage(15, OutlineMode.SOLID, Color.RED),
        new TextImage(" = 3", 15, FontStyle.BOLD, Color.WHITE)), 0, 98);
    ws4.placeImageXY(new TextImage("0", 15, Color.WHITE), 75, 98);
    ws4.placeImageXY(new OverlayImage(new TextImage("Restart", 15, Color.WHITE),
        new RectangleImage(80, 20, OutlineMode.SOLID, Color.BLACK)), 41, 98);
    ws4.placeImageXY(new TextImage("Game Over", 6.75, Color.RED), 32, 37);

    t.checkExpect(world2.makeScene(), ws4);

    // testing starting screen
    this.init();
    world2.startingScreen = true;
    WorldScene drawStart = new WorldScene(1000, 1000);

    drawStart.placeImageXY(new TextImage("Minesweeper", 50, Color.BLACK), 200, 60);
    drawStart.placeImageXY(new TextImage("Difficulty", 25, Color.BLACK), 200, 125);
    drawStart.placeImageXY(new OverlayOffsetImage(new TextImage("Easy", 15, Color.BLACK), 1, 0,
        new RectangleImage(80, 20, OutlineMode.SOLID, Color.GREEN)), 200, 175);
    drawStart.placeImageXY(new OverlayOffsetImage(new TextImage("Medium", 15, Color.BLACK), 1, 0,
        new RectangleImage(80, 20, OutlineMode.SOLID, Color.GREEN)), 200, 225);
    drawStart.placeImageXY(new OverlayOffsetImage(new TextImage("Hard", 15, Color.BLACK), 1, 0,
        new RectangleImage(80, 20, OutlineMode.SOLID, Color.GREEN)), 200, 275);

    t.checkExpect(this.world2.makeScene(), drawStart);
  }

  // testing count mines
  void testCountMines(Tester t) {
    this.init();
    Cell testCell = new Cell(false, -1, true);
    testCell.neighbor.add(new Cell(false, -1, true));
    testCell.neighbor.add(new Cell(false, 5, true));
    testCell.neighbor.add(new Cell(false, -1, true));
    t.checkExpect(testCell.countMines(), 2);
    t.checkExpect(this.c8.countMines(), 1);
    t.checkExpect(this.c7.countMines(), 1);
    // testing when a cell is a mine itself
    t.checkExpect(this.c1.countMines(), 2);
  }

  // testing draw
  void testDraw(Tester t) {
    this.init();

    // testing drawing flag
    t.checkExpect(this.bombC.draw(20, 0, 0),
        new OverlayImage(new CircleImage(20 / 4, OutlineMode.SOLID, Color.BLACK),
            new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
                new RectangleImage(20, 20, OutlineMode.SOLID, Color.WHITE))));
    // testing drawing bomb
    t.checkExpect(this.flagC.draw(20, 0, 0),
        new OverlayImage(new EquilateralTriangleImage(20 / 1.56, OutlineMode.SOLID, Color.RED),
            new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
                new RectangleImage(20, 20, OutlineMode.SOLID, new Color(131, 181, 255)))));
    // testing drawing num on cell
    t.checkExpect(this.numC.draw(20, 0, 0),
        new OverlayImage(new TextImage(String.valueOf(1), 15, FontStyle.BOLD, Color.BLUE),
            new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
                new RectangleImage(20, 20, OutlineMode.SOLID, new Color(255, 255, 255)))));
    // testing drawing empty clicked cell
    t.checkExpect(this.mtC.draw(20, 0, 0),
        new OverlayImage(new TextImage(String.valueOf(0), 15, FontStyle.BOLD, Color.WHITE),
            new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
                new RectangleImage(20, 20, OutlineMode.SOLID, new Color(255, 255, 255)))));
    // testing drawing an empty unclicked cell
    t.checkExpect(this.fillC.draw(20, 0, 0),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new RectangleImage(20, 20, OutlineMode.SOLID, new Color(131, 181, 255))));

  }

  // testing add cell
  void testAddCell(Tester t) {
    this.init();

    this.ct1.addCell(this.ct2);
    this.ct1.addCell(this.ct3);
    this.ct1.addCell(this.ct4);

    t.checkExpect(this.ct1.neighbor.contains(this.ct2), true);
    t.checkExpect(this.ct1.neighbor.contains(this.ct4), true);
    t.checkExpect(this.ct1.neighbor.contains(this.ct3), true);
    t.checkExpect(this.ct1.neighbor.contains(this.ct5), false);
    t.checkExpect(this.ct2.neighbor.contains(this.ct1), false);
    t.checkExpect(this.ct4.neighbor.contains(this.ct1), false);
    t.checkExpect(this.ct3.neighbor.contains(this.ct1), false);
    t.checkExpect(this.ct3.neighbor.contains(this.ct4), false);
    t.checkExpect(this.c4.neighbor.contains(this.ct2), false);

  }

  //Testing onTick method
  void testOnTick(Tester t) {
    this.init();
    world2.onTick();
    t.checkExpect(this.world2.timePass, 1);
    world2.onTick();
    world2.onTick();
    world2.onTick();
    world2.onTick();
    world2.onTick();
    world2.onTick();
    world2.onTick();
    world2.onTick();
    world2.onTick();
    t.checkExpect(this.world2.timePass, 10);
    t.checkExpect(world2.clock, 1);

  }

  // Testting on click
  void testOnClick(Tester t) {
    this.init();

    // testing clicking a mine
    this.world2.onMousePressed(new Posn(0, 0), "LeftButton");

    t.checkExpect(world2.gameIsOver, true);
    t.checkExpect(world2.allCells.get(0).get(0).clicked, true);

    this.init();

    // testing clicking a flag
    this.world2.onMousePressed(new Posn(0, 0), "RightButton");

    t.checkExpect(world2.allCells.get(0).get(0).clicked, false);
    t.checkExpect(world2.allCells.get(0).get(0).flag, true);
    t.checkExpect(world2.flagsLeft, 2);
    t.checkExpect(world2.minesLeft, 2);

    // clicking again to take off flag
    this.world2.onMousePressed(new Posn(0, 0), "RightButton");
    t.checkExpect(world2.allCells.get(0).get(0).clicked, false);
    t.checkExpect(world2.allCells.get(0).get(0).flag, false);
    t.checkExpect(world2.flagsLeft, 3);
    t.checkExpect(world2.minesLeft, 3);

    this.init();

    // testing clicking a cell and revealing a number
    this.world2.onMousePressed(new Posn(60, 0), "LeftButton");

    t.checkExpect(world2.allCells.get(0).get(2).clicked, true);
    t.checkExpect(world2.allCells.get(0).get(2).flag, false);

  }

  void testNeighborR(Tester t) {
    this.init();

    c9.neighborR();
    // testing when none of the neighbors are mines (floods)
    t.checkExpect(this.c8.clicked, true);
    t.checkExpect(this.c6.clicked, true);
    t.checkExpect(this.c5.clicked, true);

    this.init();
    c3.neighborR();
    // testing when one of the neighbors is a mine (does not flood)
    t.checkExpect(this.c6.clicked, false);
    t.checkExpect(this.c2.clicked, false);
    t.checkExpect(this.c5.clicked, false);

  }

  // to run a seeded version
//  void testBigBang2(Tester t) {
//    this.init();
//    int worldWidth = world4.cellSize * world4.cols;
//    int worldHeight = world4.cellSize * world4.rows + (world4.cellSize + 20);;
//    double tickRate = 0.1;
//    this.world4.bigBang(worldWidth, worldHeight, tickRate); }
  void testBigBang2(Tester t) {
    this.init();
    int worldWidth = world1.cellSize * world1.cols;
    int worldHeight = world1.cellSize * world1.rows + (world1.cellSize + 20);
    double tickRate = 0.1;
    this.world1.bigBang(worldWidth, worldHeight, tickRate);
  }
}
