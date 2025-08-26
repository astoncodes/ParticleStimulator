import java.awt.Color;

public class ParticleSimulator {

    /**
     * Enum for material types of the particles
     */
    public enum ParticleType {


    EMPTY, METAL, SAND, WATER, OIL, ACID, FIRE, STEAM, GLASS
}

    /** grid of particles of various materials*/
    private ParticleType[][] grid;

    /** The display window */
    private ParticleDisplay display;

    /**
     * Create a new SandSimulator of given size.
     * @param numRows number of rows
     * @param numCols number of columns
     */
    public ParticleSimulator(int numRows, int numCols) {

        String[] names = {"Empty", "Metal", "Sand", "Water", "Oil", "Acid", "Fire", "Steam"};
        display = new ParticleDisplay("Particle Simulation", names, numRows, numCols);


        grid = new ParticleType[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                grid[row][col] = ParticleType.EMPTY;
            }
        }

    }

    /**
     * Called after the user clicks on a location using the given tool
     * @param row Row of location
     * @param col Column of location
     * @param tool Name of selected tool
     */
    public void updateFromUser(int row, int col, String tool) {
        ParticleType selected = switch (tool) {
            case "Metal" -> ParticleType.METAL;
            case "Sand" -> ParticleType.SAND;
            case "Water" -> ParticleType.WATER;
            case "Oil" -> ParticleType.OIL;
            case "Acid" -> ParticleType.ACID;
            case "Fire" -> ParticleType.FIRE;
            case "Steam" -> ParticleType.STEAM;

            default -> ParticleType.EMPTY;
        };
        grid[row][col] = selected;
    }

    /**
     * copies each element of grid into the display
     */
    public void setDisplayColors() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                Color color = switch (grid[row][col]) {
                    case METAL -> Color.GRAY;
                    case SAND -> Color.YELLOW;
                    case WATER -> Color.BLUE;
                    case OIL -> new Color(255, 165, 0); // Orange
                    case ACID -> new Color(0, 255, 0); // Green
                    case FIRE -> Color.RED;
                    case STEAM -> Color.LIGHT_GRAY;
                    case GLASS -> new Color(192, 192, 255); // Light bluish-gray for glass
                    default -> Color.BLACK;
                };
                display.setColor(color, row, col);
            }
        }
    }

    /**
     * Update the simulation by one step.
     * Called repeatedly.
     * Causes one random particle to maybe do something
     */
    public void updateRandomLocation() {

            int numRows = grid.length;
            int numCols = grid[0].length;

            int row = (int) (Math.random() * numRows);
            int col = (int) (Math.random() * numCols);

            switch (grid[row][col]) {
                case SAND -> handleSandMovement(row, col);
                case WATER -> handleWaterMovement(row, col);
                case OIL -> handleOilMovement(row, col);
                case ACID -> handleAcidMovement(row, col);
                case FIRE -> handleFireMovement(row, col);
                case STEAM -> handleSteamMovement(row, col);
                default -> {
            } // Do nothing for other types
        }
    }

    /**
     * Handles the movement of acid particles.
     * Acid can dissolve metal particles and move in a random direction
     * @param row Row of the acid particle
     * @param col Column of the acid particle
     */
    private void handleAcidMovement(int row, int col) {
        int numCols = grid[0].length;
        int direction = (int) (Math.random() * 3); // 0: down, 1: left, 2: right

        if (row > 0 && grid[row - 1][col] == ParticleType.METAL) {
            grid[row - 1][col] = ParticleType.EMPTY; // Dissolve metal
        } else if (direction == 0 && row > 0 && grid[row - 1][col] == ParticleType.EMPTY) {
            swap(row, col, row - 1, col); // Move down
        } else if (direction == 1 && col > 0 && grid[row][col - 1] == ParticleType.EMPTY) {
            swap(row, col, row, col - 1); // Move left
        } else if (direction == 2 && col < numCols - 1 && grid[row][col + 1] == ParticleType.EMPTY) {
            swap(row, col, row, col + 1); // Move right
        }
    }

    /**
     * Handles the behavior of fire particles.
     * Fire spreads randomly to empty adjacent cells,
     * or interact with other materials like sand (turns into glass)
     * or water (turns into steam).
     * @param row Row of the fire particle
     * @param col Column of the fire particle
     */
    private void handleFireMovement(int row, int col) {
        if (Math.random() < 0.1) {
            grid[row][col] = ParticleType.EMPTY; // Dissipate fire randomly
        } else {
            int direction = (int) (Math.random() * 4); // 0: up, 1: left, 2: right, 3: down
            int numRows = grid.length;
            int numCols = grid[0].length;

            int newRow = row + (direction == 0 ? 1 : (direction == 3 ? -1 : 0));
            int newCol = col + (direction == 1 ? -1 : (direction == 2 ? 1 : 0));

            if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols) {
                if (grid[newRow][newCol] == ParticleType.SAND) {
                    grid[newRow][newCol] = ParticleType.GLASS; // Turn sand into glass
                } else if (grid[newRow][newCol] == ParticleType.WATER) {
                    grid[newRow][newCol] = ParticleType.STEAM; // Turn water into steam
                } else if (grid[newRow][newCol] == ParticleType.GLASS) {
                    // Fire interacts with glass but does not disappear
                    grid[newRow][newCol] = ParticleType.GLASS;

                } else if (grid[newRow][newCol] == ParticleType.METAL) {
                    // Fire interacts with metal but does not disappear

                }else if (grid[newRow][newCol] == ParticleType.EMPTY) {
                    swap(row, col, newRow, newCol); // Spread fire
                }
            }
        }
    }

    /**
     * Handles the movement of steam particles.
     * Steam rises upwards if the cell above is empty
     * and can dissipate randomly.
     * @param row Row of the steam particle
     * @param col Column of the steam particle
     */

    private void handleSteamMovement(int row, int col) {
        if (row < grid.length - 1 && grid[row + 1][col] == ParticleType.EMPTY) {
            swap(row, col, row + 1, col); // Rise up
        } else if (Math.random() < 0.1) {
            grid[row][col] = ParticleType.EMPTY; // Dissipate randomly
        }
    }


    /**
     * Handles the movement of sand particles.
     * Sand falls down if the cell below is empty or contains water,
     * and can slide left or right if adjacent cells are empty or contain water.
     * @param row Row of the sand particle
     * @param col Column of the sand particle
     */
    private void handleSandMovement(int row, int col) {
        int numRows = grid.length;
        if (row > 0) {
            if (grid[row - 1][col] == ParticleType.EMPTY || grid[row - 1][col] == ParticleType.WATER) {
                swap(row, col, row - 1, col); // Fall down
            } else {
                // Slide left or right
                int direction = Math.random() < 0.5 ? -1 : 1;
                int newCol = col + direction;
                if (newCol >= 0 && newCol < grid[0].length &&
                        (grid[row - 1][newCol] == ParticleType.EMPTY || grid[row - 1][newCol] == ParticleType.WATER)) {
                    swap(row, col, row - 1, newCol);
                }
            }
        }
    }


    /**
     * Handles the movement of water particles.
     * Water moves down if the cell below is empty
     * or spreads left and right if the adjacent cells are empty.
     * @param row Row of the water particle
     * @param col Column of the water particle
     */

    private void handleWaterMovement(int row, int col) {
        int numRows = grid.length;
        int numCols = grid[0].length;
        if (row > 0 && grid[row - 1][col] == ParticleType.EMPTY) {
            swap(row, col, row - 1, col); // Move down
        } else {
            // Move left or right
            int direction = Math.random() < 0.5 ? -1 : 1;
            int newCol = col + direction;
            if (newCol >= 0 && newCol < numCols && grid[row][newCol] == ParticleType.EMPTY) {
                swap(row, col, row, newCol);
            }
        }
    }

    /**
     * Handles the movement of oil particles.
     * Oil floats above water or falls down if the cell below is empty,
     * and can move left or right if adjacent cells are empty or contain water.
     * @param row Row of the oil particle
     * @param col Column of the oil particle
     */
    private void handleOilMovement(int row, int col) {
        int numRows = grid.length;
        int numCols = grid[0].length;
        if (row > 0 && (grid[row - 1][col] == ParticleType.EMPTY || grid[row - 1][col] == ParticleType.WATER)) {
            swap(row, col, row - 1, col); // Float above water or move down
        } else {
            // Move left or right
            int direction = Math.random() < 0.5 ? -1 : 1;
            int newCol = col + direction;
            if (newCol >= 0 && newCol < numCols &&
                    (grid[row][newCol] == ParticleType.EMPTY || grid[row][newCol] == ParticleType.WATER)) {
                swap(row, col, row, newCol);
            }
        }
    }

    /**
     * Swaps the particles between two specified locations in the grid.
     * Used for moving particles within the simulation.
     * @param row1 Row of the first particle
     * @param col1 Column of the first particle
     * @param row2 Row of the second particle
     * @param col2 Column of the second particle
     */
    private void swap(int row1, int col1, int row2, int col2) {
        ParticleType temp = grid[row1][col1];
        grid[row1][col1] = grid[row2][col2];
        grid[row2][col2] = temp;
    }

    /**
     * Run the SandSimulator particle simulation.
     *
     */
    public void run() {
        // keep updating as long as the program is running
        while (true) {
            // update some number of particles, as determined by the speed slider
            for (int i = 0; i < display.getSpeed(); i++) {
                updateRandomLocation();
            }
            // Update the display object's colors
            setDisplayColors();
            // wait for redrawing and for mouse events
            display.refresh();

            ParticleDisplay.Location mouseLoc = display.getMouseLocation();
            //test if mouse clicked
            if (mouseLoc != null) {
                updateFromUser(mouseLoc.row(), mouseLoc.col(), display.getToolString());
            }
        }
    }

    /** Creates a new SandSimulator and sets it running */
    public static void main(String[] args) {
        ParticleSimulator sim = new ParticleSimulator(120, 80);
        sim.run();
    }
}