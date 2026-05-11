import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.Toolkit;

class Elevator {
    double position = 0;
    boolean goingUp = true;
    boolean doorOpen = false;

    TreeSet<Integer> requests = new TreeSet<>();

    public void addRequest(int floor) {
        requests.add(floor);
    }

    public void move() {
        if (requests.isEmpty()) return;

        int currentFloor = (int)Math.round(position);

        Integer next = null;

        if (goingUp) {
            next = requests.ceiling(currentFloor);
            if (next == null) {
                goingUp = false;
                next = requests.floor(currentFloor);
            }
        } else {
            next = requests.floor(currentFloor);
            if (next == null) {
                goingUp = true;
                next = requests.ceiling(currentFloor);
            }
        }

        if (next == null) return;

        if (Math.abs(position - next) < 0.05) {
            position = next;
            doorOpen = true;
            Toolkit.getDefaultToolkit().beep();
            requests.remove(next);
        } else {
            doorOpen = false;
            if (position < next) position += 0.08;
            else position -= 0.08;
        }
    }
}

public class SingleLiftElevatorSystem extends JFrame {

    Elevator elevator = new Elevator();
    boolean[] requestLights = new boolean[10];

    boolean moving = false;

    JLabel status = new JLabel("System Ready");
    BuildingPanel panel = new BuildingPanel();

    public SingleLiftElevatorSystem() {
        setTitle("Real Smart Elevator System");
        setSize(450, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(panel, BorderLayout.CENTER);

        // Controls
        JPanel controls = new JPanel(new GridLayout(3, 4));
        controls.setBackground(Color.BLACK);

        JButton goBtn = new JButton("GO 🚀");
        JButton emergencyBtn = new JButton("EMERGENCY 🚨");

        goBtn.setBackground(Color.GREEN);
        emergencyBtn.setBackground(Color.RED);
        emergencyBtn.setForeground(Color.WHITE);

        for (int i = 0; i < 10; i++) {
            int f = i;
            JButton btn = new JButton("F" + i);
            btn.setBackground(Color.DARK_GRAY);
            btn.setForeground(Color.WHITE);

            btn.addActionListener(e -> {
                requestLights[f] = true;
                elevator.addRequest(f);
                status.setText("Selected Floor " + f);
            });

            controls.add(btn);
        }

        controls.add(goBtn);
        controls.add(emergencyBtn);

        add(controls, BorderLayout.SOUTH);

        // Status bar
        status.setForeground(Color.GREEN);
        status.setBackground(Color.BLACK);
        status.setOpaque(true);
        add(status, BorderLayout.NORTH);

        // GO button
        goBtn.addActionListener(e -> {
            if (!elevator.requests.isEmpty()) {
                moving = true;
                status.setText("🚀 Elevator Running (" + (elevator.goingUp ? "UP ↑" : "DOWN ↓") + ")");
            } else {
                status.setText("⚠ No Requests");
            }
        });

        // Emergency button
        emergencyBtn.addActionListener(e -> {
            moving = false;
            elevator.requests.clear();
            Arrays.fill(requestLights, false);
            status.setText("🚨 EMERGENCY STOP");
        });

        // Timer
        new javax.swing.Timer(40, e -> updateSystem()).start();
    }

    void updateSystem() {
        if (moving && !elevator.requests.isEmpty()) {
            elevator.move();

            status.setText("Moving " + (elevator.goingUp ? "↑ UP" : "↓ DOWN") +
                    " | Floor: " + (int)Math.round(elevator.position));
        } else if (elevator.requests.isEmpty()) {
            moving = false;
            status.setText("✅ All Requests Completed");
        }

        // Turn off lights when reached
        for (int i = 0; i < 10; i++) {
            if (Math.abs(elevator.position - i) < 0.05) {
                requestLights[i] = false;
            }
        }

        panel.repaint();
    }

    class BuildingPanel extends JPanel {

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            setBackground(Color.BLACK);

            int h = getHeight();
            int floorH = h / 10;

            g.setColor(Color.GRAY);
            for (int i = 0; i <= 10; i++) {
                g.drawLine(0, i * floorH, getWidth(), i * floorH);
                if (i < 10) {
                    g.drawString("F" + (9 - i), 10, i * floorH + 15);
                }
            }

            // Request lights
            for (int i = 0; i < 10; i++) {
                if (requestLights[i]) {
                    g.setColor(Color.RED);
                    g.fillOval(350, getHeight() - (i + 1) * floorH + 10, 10, 10);
                }
            }

            drawElevator(g, elevator, 180, floorH, getHeight());
        }

        void drawElevator(Graphics g, Elevator e, int x, int floorH, int h) {
            int y = (int)(h - (e.position + 1) * floorH);

            g.setColor(Color.GREEN);
            g.fillRect(x, y, 60, floorH - 5);

            if (e.doorOpen) {
                g.setColor(Color.BLACK);
                g.fillRect(x + 25, y, 10, floorH - 5);
            }
        }
    }

    public static void main(String[] args) {
        new SingleLiftElevatorSystem().setVisible(true);
    }
} 