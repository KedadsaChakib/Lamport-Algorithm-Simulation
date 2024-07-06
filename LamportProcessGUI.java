import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LamportProcessGUI {
    private JFrame frame;
    private JTable logTable;
    private DefaultTableModel logTableModel;
    private Map<Integer, JLabel> processLabels;
    private Map<Integer, JLabel> timestampLabels;
    private Map<Integer, JLabel> queueLabels;
    private int totalProcesses;

    public LamportProcessGUI(int totalProcesses) {
        this.totalProcesses = totalProcesses;
        processLabels = new HashMap<>();
        timestampLabels = new HashMap<>();
        queueLabels = new HashMap<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Lamport Process Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(totalProcesses, 3));
        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);

        for (int i = 1; i <= totalProcesses; i++) {
            JLabel idLabel = new JLabel("Process " + i + ": Active");
            idLabel.setBorder(border);
            processLabels.put(i, idLabel);

            JLabel timestampLabel = new JLabel("Timestamp: 0");
            timestampLabel.setBorder(border);
            timestampLabels.put(i, timestampLabel);

            JLabel queueLabel = new JLabel("Queue: []");
            queueLabel.setBorder(border);
            queueLabels.put(i, queueLabel);

            panel.add(idLabel);
            panel.add(timestampLabel);
            panel.add(queueLabel);
        }

        logTableModel = new DefaultTableModel(new Object[]{"Timestamp", "Message"}, 0);
        logTable = new JTable(logTableModel);
        logTable.setDefaultRenderer(Object.class, new LogTableCellRenderer());
        JScrollPane scrollPane = new JScrollPane(logTable);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void updateProcessStatus(int id, String status) {
        SwingUtilities.invokeLater(() -> {
            JLabel label = processLabels.get(id);
            if (label != null) {
                label.setText("Process " + id + ": " + status);
            }
        });
    }

    public void updateTimestamp(int id, long timestamp) {
        SwingUtilities.invokeLater(() -> {
            JLabel timestampLabel = timestampLabels.get(id);
            if (timestampLabel != null) {
                timestampLabel.setText("Timestamp: " + timestamp);
            }
        });
    }

    public void updateQueue(int id, String queue) {
        SwingUtilities.invokeLater(() -> {
            JLabel queueLabel = queueLabels.get(id);
            if (queueLabel != null) {
                queueLabel.setText("Queue: " + queue);
            }
        });
    }

    public void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            long timestamp = System.currentTimeMillis();
            logTableModel.addRow(new Object[]{timestamp, message});
        });
    }

    private static class LogTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String message = table.getModel().getValueAt(row, 1).toString();
            if (message.contains("failed")) {
                cell.setBackground(Color.RED);
            } else if (message.contains("recovered")) {
                cell.setBackground(Color.GREEN);
            } else if (message.contains("entered")) {
                cell.setBackground(Color.YELLOW);
            } else if (message.contains("exited")) {
                cell.setBackground(Color.ORANGE);
            } else {
                cell.setBackground(Color.WHITE);
            }
            return cell;
        }
    }
}
