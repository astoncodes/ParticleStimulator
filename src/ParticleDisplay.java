import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

/*
 * Display for sand project from http://nifty.stanford.edu/2017/feinberg-falling-sand/
 */
public class ParticleDisplay {

    private final Image image;
    private final int cellSize;
    private int tool;
    private final int numRows;
    private final int numCols;
    private Location mouseLoc;
    private final JRadioButton[] buttons;
    private int speed;
    private final JPanel display;

    /** Record for row/col position */
    public record Location(int row, int col) {}

    /**
     * Create SandDisplay window of given size and tools
     *
     * @param title     The window title
     * @param toolNames Names of the tools for user to choose from.
     * @param numRows   Number of rows
     * @param numCols   Number of columns
     */
    public ParticleDisplay(String title, String[] toolNames, int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        tool = 1;
        mouseLoc = null;
        speed = computeSpeed(50);

        //determine cell size
        cellSize = Math.max(1, 600 / Math.max(numRows, numCols));
        image = new BufferedImage(numCols * cellSize, numRows * cellSize, BufferedImage.TYPE_INT_RGB);

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        frame.getContentPane().add(topPanel);

        display = new JPanel() {

            public void paintComponent(Graphics g) {
                g.drawImage(image, 0, 0, null);
            }
        };

        display.setPreferredSize(new Dimension(numCols * cellSize, numRows * cellSize));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseLoc = toLocation(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseLoc = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseLoc = toLocation(e);
            }
        };
        display.addMouseListener(mouseHandler);
        display.addMouseMotionListener(mouseHandler);
        topPanel.add(display);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        topPanel.add(buttonPanel);

        buttons = new JRadioButton[toolNames.length];
        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JRadioButton(toolNames[i]);
            buttonGroup.add(buttons[i]);
            final int finalI = i;
            buttons[i].addActionListener(e -> {
                tool = finalI;
            });
            buttonPanel.add(buttons[i]);
        }

        buttons[tool].setSelected(true);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        slider.addChangeListener(e -> speed = computeSpeed(slider.getValue()));
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(0, new JLabel("Slow"));
        labelTable.put(100, new JLabel("Fast"));
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);

        frame.getContentPane().add(slider);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Repaint the display and pause slightly to allow for mouse input.
     */
    public void refresh() {
        display.repaint();
        try {
            // take a very short nap
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get last mouse click location
     * @return Location object containing row and column of mouse click.
     */
    public Location getMouseLocation() {
        return mouseLoc;
    }

    /**
     * Get name of currently selected tool
     * @return Tool String
     */
    public String getToolString() {
        return buttons[tool].getText();
    }

    /**
     * Set color for given cell location.
     *
     * @param color The new color for the cell
     * @param row   The cell row
     * @param col   The cell column
     */
    public void setColor(Color color, int row, int col) {
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.fillRect(col * cellSize, (numRows-row-1)* cellSize, cellSize, cellSize);
    }

    /**
     * Find cell location of a mouse event.
     * @param e The mouse event
     * @return Location of (row, col)
     */
    private Location toLocation(MouseEvent e) {
        int row = numRows - (e.getY() / cellSize) - 1;
        int col = e.getX() / cellSize;
        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            return null;
        }
        return new Location(row, col);
    }

    /**
     * Get simulation speed.
     * @return number of times to step between repainting and processing mouse input
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Returns speed based on sliderValue
     *     speed of 0 returns 10^3
     *     speed of 100 returns 10^6
     * @param sliderValue Value from slider
     * @return Speed value
     */
    private static int computeSpeed(int sliderValue) {
        return (int) Math.pow(10, 0.03 * sliderValue + 3);
    }
}