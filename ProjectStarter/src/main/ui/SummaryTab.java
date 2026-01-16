package ui;

import model.MoodEntry;
import model.MoodLevel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Tab panel that provides an overview of the user's moods.
 *
 * Responsibilities:
 * - Displays a monthly calendar view where each day may show a mood color/icon
 * based on the corresponding MoodEntry stored in the model.
 * - Displays a simple bar chart that summarizes the mood distribution for
 * the currently selected month.
 * - Allows navigation between months using previous/next buttons.
 */
public class SummaryTab extends JPanel {
    private MoodTrackerGUI parent;
    private JPanel calendarPanel;
    private JPanel chartPanel;
    private YearMonth currentMonth;

    // MODIFIES: this
    // EFFECTS:
    // - constructs summary tab with a calendar on the left and a chart on the right
    // - initializes the currentMonth to the present month
    // - builds UI components and performs an initial refresh
    public SummaryTab(MoodTrackerGUI parent) {
        this.parent = parent;
        this.currentMonth = YearMonth.now();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initializeComponents();
        refresh();
    }

    // MODIFIES: this
    // EFFECTS: initializes and lays out the title label and split pane
    private void initializeComponents() {
        add(createTitleLabel(), BorderLayout.NORTH);
        add(createSplitPane(), BorderLayout.CENTER);
    }

    // EFFECTS: creates and returns the title label for this tab
    private JLabel createTitleLabel() {
        JLabel title = new JLabel("📅 Mood Overview", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        return title;
    }

    // MODIFIES: this
    // EFFECTS:
    // - creates a JSplitPane where the left side holds the calendar
    // and the right side holds the chart
    // - initializes calendarPanel and chartPanel containers
    private JSplitPane createSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        calendarPanel = createCalendarPanel();
        chartPanel = createChartPanel();

        splitPane.setLeftComponent(new JScrollPane(calendarPanel));
        splitPane.setRightComponent(chartPanel);

        return splitPane;
    }

    // EFFECTS: creates and returns the container panel for the calendar
    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Mood Calendar"));
        return panel;
    }

    // EFFECTS: creates and returns the container panel for the chart
    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Mood Distribution"));
        return panel;
    }

    // MODIFIES: this
    // EFFECTS: refreshes both the calendar and chart based on the currentMonth
    // and the current contents of the MoodLog
    public void refresh() {
        updateCalendar();
        updateChart();
    }

    // =================== Calendar ===================

    // MODIFIES: this
    // EFFECTS:
    // - rebuilds the calendarPanel to show the currentMonth
    // - includes a header with month navigation and a grid of day cells
    private void updateCalendar() {
        calendarPanel.removeAll();
        calendarPanel.setLayout(new BorderLayout(5, 5));

        JPanel header = createCalendarHeader();
        JPanel grid = createDayGrid(currentMonth);

        calendarPanel.add(header, BorderLayout.NORTH);
        calendarPanel.add(grid, BorderLayout.CENTER);

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    // EFFECTS:
    // - creates a header panel that shows the current month/year and
    // contains "<" and ">" buttons to move backward/forward one month
    private JPanel createCalendarHeader() {
        JPanel header = new JPanel(new BorderLayout());

        JButton prev = new JButton("<");
        JButton next = new JButton(">");

        prev.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refresh();
        });

        next.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refresh();
        });

        String text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        JLabel monthLabel = new JLabel(text, SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 16));

        header.add(prev, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);

        return header;
    }

    // EFFECTS:
    // - creates a grid panel containing day-of-week headers, leading empty cells,
    // and one cell per day of the given yearMonth
    private JPanel createDayGrid(YearMonth yearMonth) {
        JPanel grid = new JPanel(new GridLayout(0, 7, 3, 3));

        addDayHeaders(grid);
        addEmptyCellsBeforeMonth(grid, yearMonth);
        addDayCells(grid, yearMonth);

        return grid;
    }

    // MODIFIES: grid
    // EFFECTS: adds the day-of-week headers (Sun, Mon, ..., Sat) to the grid
    private void addDayHeaders(JPanel grid) {
        String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            grid.add(label);
        }
    }

    // MODIFIES: grid
    // EFFECTS:
    // - computes the weekday index of the first day of yearMonth
    // - adds that many empty components to align the first real date under
    // the appropriate day-of-week header
    private void addEmptyCellsBeforeMonth(JPanel grid, YearMonth yearMonth) {
        LocalDate firstDay = yearMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7;

        for (int i = 0; i < dayOfWeek; i++) {
            grid.add(new JLabel(""));
        }
    }

    // MODIFIES: grid
    // EFFECTS: adds one calendar cell for each day of the given month
    private void addDayCells(JPanel grid, YearMonth yearMonth) {
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            grid.add(createDayCell(date));
        }
    }

    // EFFECTS:
    // - creates a single day cell panel for the given date
    // - if a MoodEntry exists for that date, colors the cell and shows icon/emoji
    // - otherwise renders a plain white cell with just the day number
    private JPanel createDayCell(LocalDate date) {
        JPanel dayPanel = new JPanel(new BorderLayout());
        dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        MoodEntry entry = parent.getMoodLog().getEntryByDate(date);

        if (entry != null) {
            styleFilledDayCell(dayPanel, entry);
        } else {
            dayPanel.setBackground(Color.WHITE);
        }

        addDayNumber(dayPanel, date.getDayOfMonth());
        return dayPanel;
    }

    // MODIFIES: dayPanel
    // EFFECTS:
    // - sets background color based on the entry's mood level
    // - adds an icon if available, otherwise falls back to showing the emoji
    private void styleFilledDayCell(JPanel dayPanel, MoodEntry entry) {
        MoodLevel level = entry.getMoodLevel();
        dayPanel.setBackground(parent.getMoodColor(level));

        Icon moodIcon = parent.createMoodIcon(level, 24);
        JLabel centerLabel;

        if (moodIcon != null) {
            centerLabel = new JLabel(moodIcon, SwingConstants.CENTER);
        } else {
            String emoji = level.getLabel().split(" ")[0];
            centerLabel = new JLabel(emoji, SwingConstants.CENTER);
            centerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        }

        dayPanel.add(centerLabel, BorderLayout.CENTER);
    }

    // MODIFIES: dayPanel
    // EFFECTS: adds a small day-of-month number label to the top-right of the cell
    private void addDayNumber(JPanel dayPanel, int day) {
        JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.RIGHT);
        dayLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        dayPanel.add(dayLabel, BorderLayout.NORTH);
    }

    // =================== Chart ===================

    // MODIFIES: this
    // EFFECTS:
    // - rebuilds the chartPanel to show the mood distribution for the currentMonth
    // - queries the model for distribution and total entries in that month
    private void updateChart() {
        chartPanel.removeAll();
        chartPanel.setLayout(new BorderLayout());

        LocalDate from = currentMonth.atDay(1);
        LocalDate to = currentMonth.atEndOfMonth();

        Map<MoodLevel, Integer> distribution = parent.getMoodLog().getMoodDistribution(from, to);
        int monthlyTotal = parent.getMoodLog().countEntriesBetween(from, to);

        MoodChartPanel chart = new MoodChartPanel(distribution, monthlyTotal);

        chartPanel.add(chart, BorderLayout.CENTER);

        chartPanel.revalidate();
        chartPanel.repaint();
    }

    // =================== Inner Chart Panel ===================

    /**
     * Inner panel that draws a simple bar chart representing the number of
     * entries for each MoodLevel over the selected month.
     */
    private class MoodChartPanel extends JPanel {
        private Map<MoodLevel, Integer> distribution;
        private int totalForPeriod;

        // MODIFIES: this
        // EFFECTS:
        // - constructs a chart panel with the given mood distribution and total count
        // - sets a preferred size for layout purposes
        public MoodChartPanel(Map<MoodLevel, Integer> distribution, int totalForPeriod) {
            this.distribution = distribution;
            this.totalForPeriod = totalForPeriod;
            setPreferredSize(new Dimension(300, 400));
        }

        // MODIFIES: g
        // EFFECTS: paints the background and calls helper to draw the bar chart
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawChart(g);
        }

        // MODIFIES: g
        // EFFECTS:
        // - draws a bar for each MoodLevel using distribution data
        // - spaces bars horizontally and adds a label for total entries at the top
        private void drawChart(Graphics g) {
            int maxCount = getMaxCount();
            int x = 30;

            for (MoodLevel level : MoodLevel.values()) {
                x = drawBar(g, level, x, maxCount);
                x += 60;
            }

            drawTotalLabel(g);
        }

        // MODIFIES: g
        // EFFECTS:
        // - draws a single bar for the given MoodLevel at x, scaled relative
        // to maxCount, and returns the x position used for this bar
        private int drawBar(Graphics g, MoodLevel level, int x, int maxCount) {
            int count = distribution.getOrDefault(level, 0);
            int barHeight = calculateBarHeight(count, maxCount);
            int posY = getHeight() - barHeight - 60;

            drawBarRectangle(g, level, x, posY, barHeight);
            drawBarLabels(g, level, x, posY, count);

            return x;
        }

        // EFFECTS:
        // - computes and returns the pixel height of a bar for the given count,
        // scaled by the maximum count; if maxCount is 0, effectively returns 0
        private int calculateBarHeight(int count, int maxCount) {
            int maxHeight = getHeight() - 100;
            return (int) ((double) count / maxCount * maxHeight);
        }

        // MODIFIES: g
        // EFFECTS: draws the filled colored bar and its outline for the given level
        private void drawBarRectangle(Graphics g, MoodLevel level, int x, int y, int height) {
            g.setColor(parent.getMoodColor(level));
            g.fillRect(x, y, 50, height);

            g.setColor(Color.BLACK);
            g.drawRect(x, y, 50, height);
        }

        // MODIFIES: g
        // EFFECTS:
        // - draws the numeric count above the bar
        // - draws the textual label of the mood level below the chart area
        private void drawBarLabels(Graphics g, MoodLevel level, int x, int y, int count) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(String.valueOf(count), x + 20, y - 5);

            String label = level.getLabel();
            g.setFont(new Font("Dialog", Font.PLAIN, 11));

            FontMetrics fm = g.getFontMetrics();
            int labelWidth = fm.stringWidth(label);

            int labelX = x + (50 - labelWidth) / 2;
            int labelY = getHeight() - 18;

            g.drawString(label, labelX, labelY);
        }

        // MODIFIES: g
        // EFFECTS: draws a label showing the total number of entries in the current
        // month
        private void drawTotalLabel(Graphics g) {
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Total this month: " + totalForPeriod, 10, 20);
        }

        // EFFECTS:
        // - returns the maximum count across all mood levels,
        // or 1 if all values are zero (to avoid division-by-zero)
        private int getMaxCount() {
            return Math.max(1, distribution.values().stream()
                    .mapToInt(Integer::intValue).max().orElse(1));
        }
    }
}