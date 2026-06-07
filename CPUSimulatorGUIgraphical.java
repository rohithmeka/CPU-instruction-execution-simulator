import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CPUSimulatorGUIgraphical{

    static int R1 = 0, R2 = 0, R3 = 0;
    static int Rt = 0;  
    static int PC = 0, stage = 0;
    static String currentInst = "";
    static java.util.List<String> memory = new ArrayList<>();

    static final int MEM_X = 65,  CU_X = 315, REG_X = 565, ALU_X = 790;
    static final int MID_Y = 135, R1_Y = 35,  R2_Y  = 135, R3_Y  = 235;

    static JTextArea memArea         = new JTextArea(10, 20);
    static JTextArea logArea         = new JTextArea(10, 40);
    static JTextArea machineCodeArea = new JTextArea(5,  40);

    static JLabel pcLabel = new JLabel("PC = 0");
    static JLabel r1Label = new JLabel("$1 = 0");
    static JLabel r2Label = new JLabel("$2 = 0");
    static JLabel r3Label = new JLabel("$3 = 0");

    static JPanel memPanel = new JPanel();
    static JPanel cuPanel  = new JPanel();
    static JPanel regPanel = new JPanel();
    static JPanel aluPanel = new JPanel();

    static JLabel       instructionBox = new JLabel("", SwingConstants.CENTER);
    static JLayeredPane layeredPane    = new JLayeredPane();

    public static void main(String[] args) {

        JFrame frame = new JFrame("CPU Instruction Execution Simulator");
        frame.setSize(1000, 650);
        frame.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JButton load = new JButton("Load Program");
        JButton next = new JButton("Next Step");
        top.add(pcLabel); top.add(load); top.add(next);
        frame.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 4));
        memPanel.setBorder(BorderFactory.createTitledBorder("Instruction Memory"));
        memPanel.add(new JScrollPane(memArea));
        cuPanel.setBorder(BorderFactory.createTitledBorder("Control Unit"));
        regPanel.setBorder(BorderFactory.createTitledBorder("Register File"));
        regPanel.setLayout(new GridLayout(3, 1));
        regPanel.add(r1Label); regPanel.add(r2Label); regPanel.add(r3Label);
        aluPanel.setBorder(BorderFactory.createTitledBorder("ALU"));
        center.add(memPanel); center.add(cuPanel);
        center.add(regPanel); center.add(aluPanel);

        layeredPane.setLayout(null);
        center.setBounds(0, 0, 1000, 300);
        layeredPane.add(center, Integer.valueOf(0));
        instructionBox.setBounds(MEM_X, MID_Y, 120, 30);
        instructionBox.setOpaque(true);
        instructionBox.setBackground(Color.LIGHT_GRAY);
        instructionBox.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        layeredPane.add(instructionBox, Integer.valueOf(1));
        frame.add(layeredPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2));
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Fetch --> Decode --> Execute"));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        JPanel machinePanel = new JPanel(new BorderLayout());
        machinePanel.setBorder(BorderFactory.createTitledBorder("Machine Code"));
        machineCodeArea.setEditable(false);
        machineCodeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        machinePanel.add(new JScrollPane(machineCodeArea), BorderLayout.CENTER);
        bottom.add(logPanel); bottom.add(machinePanel);
        frame.add(bottom, BorderLayout.SOUTH);
        load.addActionListener(e -> {
            memory.clear();
            PC = 0; stage = 0;
            R1 = R2 = R3 = Rt = 0;
            updateRegisters();

            String[] options = { "Addition", "Maximum", "Subtraction",
                                  "Multiplication", "Even or Odd Check",
                                  "Swap", "Sum of N" };
            int choice = JOptionPane.showOptionDialog(null,
                "Select Operation", "Choose",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
            if (choice < 0) return;
            //destination, register, source
            if (choice == 0) {
                // ADDITION
                int a = Integer.parseInt(JOptionPane.showInputDialog("Enter A"));
                int b = Integer.parseInt(JOptionPane.showInputDialog("Enter B"));
                memory.add("addi $1,$0," + a);   // load A into $1 (addi means add immediate(directly written inside memory))
                memory.add("addi $2,$0," + b);   // load B into $2
                memory.add("add $3,$1,$2");       // $3 = $1 + $2
                memory.add("sw $3,0($0)");        // store result

            } else if (choice == 2) {
                // SUBTRACTION
                int a = Integer.parseInt(JOptionPane.showInputDialog("Enter A"));
                int b = Integer.parseInt(JOptionPane.showInputDialog("Enter B"));
                memory.add("addi $1,$0," + a);
                memory.add("addi $2,$0," + b);
                memory.add("sub $3,$1,$2");       // $3 = $1 - $2
                memory.add("sw $3,0($0)");

            } else if (choice == 3) {
                // MULTIPLICATION by repeated addition
                int a = Integer.parseInt(JOptionPane.showInputDialog("Enter A"));
                int b = Integer.parseInt(JOptionPane.showInputDialog("Enter B"));
                memory.add("addi $1,$0," + a);
                memory.add("addi $2,$0," + b);
                memory.add("add $3,$0,$0");       // $3 = 0
                memory.add("addi $t,$0," + b);    // $t = B (counter)
                // loop body repeated b times
                for (int k = 0; k < b; k++) {
                    memory.add("beq $t,$0,done");     // if counter=0 exit
                    memory.add("add $3,$3,$1");        // $3 = $3 + $1
                    memory.add("addi $t,$t,-1");       // counter--
                }
                memory.add("sw $3,0($0)");

            } else if (choice == 1) {
                // MAXIMUM using slt + beq
                int a = Integer.parseInt(JOptionPane.showInputDialog("Enter A"));
                int b = Integer.parseInt(JOptionPane.showInputDialog("Enter B"));
                memory.add("addi $1,$0," + a);
                memory.add("addi $2,$0," + b);
                memory.add("slt $t,$2,$1");       // $t=1 if $2 < $1
                memory.add("beq $t,$0,else");     // if $t=0 goto else
                memory.add("add $3,$1,$0");       // $3 = $1 (R1 is max)
                memory.add("j done");
                memory.add("add $3,$2,$0");       // else: $3 = $2
                memory.add("sw $3,0($0)");

            } else if (choice == 4) {
                // EVEN OR ODD using andi + beq
                int n = Integer.parseInt(JOptionPane.showInputDialog("Enter Number"));
                memory.add("addi $1,$0," + n);
                memory.add("andi $t,$1,1");       // isolate last bit
                memory.add("beq $t,$0,even");     // if 0 -> even
                memory.add("addi $3,$0,1");       // $3 = 1 (odd)
                memory.add("j done");
                memory.add("addi $3,$0,0");       // even: $3 = 0
                memory.add("sw $3,0($0)");

            } else if (choice == 5) {
                // SWAP using temp register $t
                int a = Integer.parseInt(JOptionPane.showInputDialog("Enter A"));
                int b = Integer.parseInt(JOptionPane.showInputDialog("Enter B"));
                memory.add("addi $1,$0," + a);
                memory.add("addi $2,$0," + b);
                memory.add("add $t,$1,$0");       // $t = $1 (save $1)
                memory.add("add $1,$2,$0");       // $1 = $2
                memory.add("add $2,$t,$0");       // $2 = old $1
                memory.add("sw $1,0($0)");        // store swapped $1
            } else if (choice == 6) {
                // SUM OF N using loop
                int n = Integer.parseInt(JOptionPane.showInputDialog("Enter N"));
                memory.add("addi $1,$0," + n);
                memory.add("add $3,$0,$0");       // $3 = 0 (sum)
                memory.add("addi $t,$0,1");       // $t = 1 (i)
                // loop body repeated n times
                for (int k = 1; k <= n; k++) {
                    memory.add("slt $k,$1,$t");       // $k=1 if N < i
                    memory.add("bne $k,$0,done");     // if $k=1 exit if true
                    memory.add("add $3,$3,$t");        // sum = sum + i
                    memory.add("addi $t,$t,1");        // i = i + 1
                }
                memory.add("sw $3,0($0)");
            }

            memory.add("halt");

            instructionBox.setLocation(MEM_X, MID_Y);
            instructionBox.setText("");
            updateMemory();
            logArea.setText("");
            machineCodeArea.setText("");
        });

        next.addActionListener(e -> executeStep());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    static void executeStep() {

        resetColors();
        if (PC >= memory.size()) return;

        // ── FETCH ──────────────────────────────────────────
        if (stage == 0) {
            currentInst = memory.get(PC);
            instructionBox.setText(currentInst);
            instructionBox.setLocation(MEM_X, MID_Y);
            memPanel.setBackground(Color.YELLOW);
            animatePath(new int[][]{ {MEM_X, MID_Y}, {CU_X, MID_Y} });
            logArea.append("\n      FETCH  -->  " + currentInst + "\n");
            stage = 1;
            return;
        }

        // ── DECODE ─────────────────────────────────────────
        if (stage == 1) {
            cuPanel.setBackground(Color.ORANGE);
            showMachineCode();
            logArea.append("        DECODE  -->  " + instrType() + "\n");
            stage = 2;
            return;
        }
        if (stage == 2) {
            String inst = currentInst;
            if (inst.startsWith("addi $1,$0,")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {REG_X, R1_Y} });
                regPanel.setBackground(Color.CYAN);
                R1 = Integer.parseInt(inst.split(",")[2]);
                logArea.append("        EXECUTE -->  $1 = " + R1 + "\n");
            } else if (inst.startsWith("addi $2,$0,")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {REG_X, R2_Y} });
                regPanel.setBackground(Color.CYAN);
                R2 = Integer.parseInt(inst.split(",")[2]);
                logArea.append("        EXECUTE -->  $2 = " + R2 + "\n");
            } else if (inst.startsWith("addi $t,$0,")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                Rt = Integer.parseInt(inst.split(",")[2]);
                logArea.append("        EXECUTE -->  $t = " + Rt + "  (counter)\n");

            // addi $t,$t,-1  — decrement counter
            } else if (inst.equals("addi $t,$t,-1")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                Rt = Rt - 1;
                logArea.append("        EXECUTE -->  $t = $t - 1 = " + Rt + "\n");

            // addi $t,$t,1  — increment counter (SUMN loop)
            } else if (inst.equals("addi $t,$t,1")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                Rt = Rt + 1;
                logArea.append("        EXECUTE -->  $t = $t + 1 = " + Rt + "\n");

            // addi $3,$0,0  — set $3 = 0 (even result)
            } else if (inst.equals("addi $3,$0,0")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {REG_X, R3_Y} });
                regPanel.setBackground(Color.CYAN);
                R3 = 0;
                logArea.append("        EXECUTE -->  $3 = 0  (EVEN)\n");

            // addi $3,$0,1  — set $3 = 1 (odd result)
            } else if (inst.equals("addi $3,$0,1")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {REG_X, R3_Y} });
                regPanel.setBackground(Color.CYAN);
                R3 = 1;
                logArea.append("        EXECUTE -->  $3 = 1  (ODD)\n");

            // add $3,$0,$0  — $3 = 0
            } else if (inst.equals("add $3,$0,$0")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                R3 = 0;
                logArea.append("        EXECUTE -->  $3 = 0  (initialize sum)\n");

            // add $3,$1,$2  — addition
            } else if (inst.equals("add $3,$1,$2")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                R3 = R1 + R2;
                logArea.append("        EXECUTE -->  $3 = $1 + $2 = " + R1 + " + " + R2 + " = " + R3 + "\n");

            // add $3,$3,$1  — accumulate in MUL
            } else if (inst.equals("add $3,$3,$1")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                R3 = R3 + R1;
                logArea.append("        EXECUTE -->  $3 = $3 + $1 = " + R3 + "\n");

            // add $3,$3,$t  — accumulate in SUMN
            } else if (inst.equals("add $3,$3,$t")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                R3 = R3 + Rt;
                logArea.append("        EXECUTE -->  $3 = $3 + $t = " + R3 + "\n");
            } else if (inst.equals("add $3,$1,$0")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                R3 = R1;
                logArea.append("        EXECUTE -->  $3 = $1 = " + R3 + "  (Maximum)\n");
            } else if (inst.equals("add $3,$2,$0")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                R3 = R2;
                logArea.append("        EXECUTE -->  $3 = $2 = " + R3 + "  (Maximum)\n");
            } else if (inst.equals("sub $3,$1,$2")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                R3 = R1 - R2;
                logArea.append("        EXECUTE -->  $3 = $1 - $2 = " + R1 + " - " + R2 + " = " + R3 + "\n");

            // slt $t,$2,$1  — MAX check
            } else if (inst.equals("slt $t,$2,$1")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                Rt = (R2 < R1) ? 1 : 0;
                logArea.append("        EXECUTE -->  slt: $t = " + Rt + "  (" + (Rt == 1 ? "$1 > $2" : "$2 >= $1") + ")\n");

            // slt $k,$1,$t  — SUMN loop check
            } else if (inst.equals("slt $k,$1,$t")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                int k = (R1 < Rt) ? 1 : 0;
                logArea.append("        EXECUTE -->  slt: $k = " + k + "  ($1=" + R1 + ", $t=" + Rt + ")\n");

            // andi $t,$1,1  — even/odd check
            } else if (inst.equals("andi $t,$1,1")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                Rt = R1 & 1;
                logArea.append("        EXECUTE -->  andi: $t = $1 AND 1 = " + Rt + "  (last bit of " + R1 + ")\n");

            // beq $t,$0,done  — MUL loop exit check
            } else if (inst.equals("beq $t,$0,done")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                logArea.append("        EXECUTE -->  beq: $t=" + Rt + "  " + (Rt == 0 ? "(taken → jump to done)" : "(not taken → continue loop)") + "\n");

            // beq $t,$0,else  — MAX branch
            } else if (inst.equals("beq $t,$0,else")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                logArea.append("        EXECUTE -->  beq: $t=" + Rt + "  " + (Rt == 0 ? "(taken → go to else)" : "(not taken → $1 is max)") + "\n");

            // beq $t,$0,even  — EVENODD branch
            } else if (inst.equals("beq $t,$0,even")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                logArea.append("        EXECUTE -->  beq: $t=" + Rt + "  " + (Rt == 0 ? "(taken → number is even)" : "(not taken → number is odd)") + "\n");

            // bne $k,$0,done  — SUMN loop exit
            } else if (inst.equals("bne $k,$0,done")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                int k = (R1 < Rt) ? 1 : 0;
                logArea.append("        EXECUTE -->  bne: $k=" + k + "  " + (k != 0 ? "(taken → loop done)" : "(not taken → continue)") + "\n");

            // j done / j done (label, no real jump needed — simulator is sequential)
            } else if (inst.equals("j done")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                logArea.append("        EXECUTE -->  j done  (jump to end)\n");

            // SWAP: add $t,$1,$0  — save $1 in temp
            } else if (inst.equals("add $t,$1,$0")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {ALU_X, MID_Y} });
                aluPanel.setBackground(Color.PINK);
                Rt = R1;
                logArea.append("        EXECUTE -->  $t = $1 = " + Rt + "  (save $1 in temp)\n");

            // SWAP: add $1,$2,$0  — $1 = $2
            } else if (inst.equals("add $1,$2,$0")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {REG_X, R1_Y} });
                regPanel.setBackground(Color.CYAN);
                R1 = R2;
                logArea.append("        EXECUTE -->  $1 = $2 = " + R1 + "\n");

            // SWAP: add $2,$t,$0  — $2 = old $1
            } else if (inst.equals("add $2,$t,$0")) {
                animatePath(new int[][]{ {CU_X, MID_Y}, {REG_X, R2_Y} });
                regPanel.setBackground(Color.CYAN);
                R2 = Rt;
                logArea.append("        EXECUTE -->  $2 = $t = " + R2 + "  (old $1)\n");
            } else if (inst.equals("sw $3,0($0)")) {
                animatePath(new int[][]{
                    {ALU_X, MID_Y}, {700, MID_Y}, {700, R3_Y}, {REG_X, R3_Y}
                });
                regPanel.setBackground(Color.GREEN);
                logArea.append("        EXECUTE -->  WRITE BACK: $3 = " + R3 + "\n");
            } else if (inst.equals("sw $1,0($0)")) {
                animatePath(new int[][]{
                    {CU_X, MID_Y}, {REG_X, R1_Y}
                });
                regPanel.setBackground(Color.GREEN);
                logArea.append("        EXECUTE -->  WRITE BACK: $1 = " + R1 + "  $2 = " + R2 + "\n");
            } else if (inst.equals("halt")) {
                logArea.append("        HALT  -->  Program complete.\n");
                JOptionPane.showMessageDialog(null,
                    "Program Executed Successfully!\nResult = " + R3);
                return;
            }

            stage = 3;
            return;
        }

        // ── WRITE BACK — update registers, advance PC ──────
        if (stage == 3) {
            updateRegisters();
            PC++;
            pcLabel.setText("PC = " + PC);
            stage = 0;
            updateMemory();
        }
    }

    // =======================================================
    //  ANIMATION — unchanged
    // =======================================================
    static void animatePath(int[][] pts) {
        instructionBox.setLocation(pts[0][0], pts[0][1]);
        final int[] seg = {0};
        Timer t = new Timer(12, null);
        t.addActionListener(new ActionListener() {
            int x = pts[0][0], y = pts[0][1];
            public void actionPerformed(ActionEvent e) {
                if (seg[0] >= pts.length - 1) { t.stop(); return; }
                int tx = pts[seg[0]+1][0], ty = pts[seg[0]+1][1];
                if (Math.abs(x-tx) <= 3 && Math.abs(y-ty) <= 3) {
                    x = tx; y = ty;
                    instructionBox.setLocation(x, y);
                    seg[0]++; return;
                }
                if (x < tx) x += 3; else if (x > tx) x -= 3;
                if (y < ty) y += 3; else if (y > ty) y -= 3;
                instructionBox.setLocation(x, y);
            }
        });
        t.start();
    }

    // =======================================================
    //  MACHINE CODE PANEL
    // =======================================================
    static void showMachineCode() {
        String inst = currentInst;

        if (inst.startsWith("addi $1,$0,")) {
            String imm = inst.split(",")[2];
            machineCodeArea.setText(
                "Instruction : addi $1, $0, " + imm + "\n" +
                "Type        : I-Type\n\n" +
                "opcode | rs    | rt    | immediate\n" +
                "001000 | 00000 | 00001 | " + imm + "\n\n" +
                "Loads the value " + imm + " into register $1"
            );
        } else if (inst.startsWith("addi $2,$0,")) {
            String imm = inst.split(",")[2];
            machineCodeArea.setText(
                "Instruction : addi $2, $0, " + imm + "\n" +
                "Type        : I-Type\n\n" +
                "opcode | rs    | rt    | immediate\n" +
                "001000 | 00000 | 00010 | " + imm + "\n\n" +
                "Loads the value " + imm + " into register $2"
            );
        } else if (inst.equals("add $3,$1,$2")) {
            machineCodeArea.setText(
                "Instruction : add $3, $1, $2\n" +
                "Type        : R-Type\n\n" +
                "opcode | rs    | rt    | rd    | shamt | funct\n" +
                "000000 | 00001 | 00010 | 00011 | 00000 | 100000"
            );
        } else if (inst.equals("sub $3,$1,$2")) {
            machineCodeArea.setText(
                "Instruction : sub $3, $1, $2\n" +
                "Type        : R-Type\n\n" +
                "opcode | rs    | rt    | rd    | shamt | funct\n" +
                "000000 | 00001 | 00010 | 00011 | 00000 | 100010"
            );
        } else if (inst.equals("add $3,$3,$1")) {
            machineCodeArea.setText(
                "Instruction : add $3, $3, $1\n" +
                "Type        : R-Type\n\n" +
                "opcode | rs    | rt    | rd    | shamt | funct\n" +
                "000000 | 00011 | 00001 | 00011 | 00000 | 100000\n\n" +
                "Repeated addition step (MUL)"
            );
        } else if (inst.equals("add $3,$3,$t")) {
            machineCodeArea.setText(
                "Instruction : add $3, $3, $t\n" +
                "Type        : R-Type\n\n" +
                "opcode | rs    | rt    | rd    | shamt | funct\n" +
                "000000 | 00011 | 01000 | 00011 | 00000 | 100000\n\n" +
                "Accumulate sum (SUMN loop)"
            );
        } else if (inst.equals("slt $t,$2,$1")) {
            machineCodeArea.setText(
                "Instruction : slt $t, $2, $1\n" +
                "Type        : R-Type\n\n" +
                "opcode | rs    | rt    | rd    | shamt | funct\n" +
                "000000 | 00010 | 00001 | 01000 | 00000 | 101010\n\n" +
                "Set $t=1 if $2 < $1 (find maximum)"
            );
        } else if (inst.equals("andi $t,$1,1")) {
            machineCodeArea.setText(
                "Instruction : andi $t, $1, 1\n" +
                "Type        : I-Type\n\n" +
                "opcode | rs    | rt    | immediate\n" +
                "001100 | 00001 | 01000 | 0000000000000001\n\n" +
                "AND $1 with 1 to isolate the last bit"
            );
        } else if (inst.startsWith("beq")) {
            machineCodeArea.setText(
                "Instruction : " + inst + "\n" +
                "Type        : I-Type (Branch)\n\n" +
                "opcode | rs    | rt    | offset\n" +
                "000100 | rs    | rt    | offset\n\n" +
                "Branch if two registers are equal"
            );
        } else if (inst.startsWith("bne")) {
            machineCodeArea.setText(
                "Instruction : " + inst + "\n" +
                "Type        : I-Type (Branch)\n\n" +
                "opcode | rs    | rt    | offset\n" +
                "000101 | rs    | rt    | offset\n\n" +
                "Branch if two registers are not equal"
            );
        } else if (inst.equals("j done")) {
            machineCodeArea.setText(
                "Instruction : j done\n" +
                "Type        : J-Type\n\n" +
                "opcode | target address\n" +
                "000010 | target\n\n" +
                "Unconditional jump to done label"
            );
        } else if (inst.startsWith("sw")) {
            machineCodeArea.setText(
                "Instruction : " + inst + "\n" +
                "Type        : I-Type\n\n" +
                "opcode | rs    | rt    | offset\n" +
                "101011 | 00000 | rt    | 0000000000000000\n\n" +
                "Store register value back to memory"
            );
        } else if (inst.equals("halt")) {
            machineCodeArea.setText(
                "Instruction : halt\n" +
                "Type        : J-Type (custom)\n\n" +
                "Stops execution of the program"
            );
        } else if (inst.startsWith("addi")) {
            machineCodeArea.setText(
                "Instruction : " + inst + "\n" +
                "Type        : I-Type\n\n" +
                "opcode | rs    | rt    | immediate\n" +
                "001000 | rs    | rt    | immediate\n\n" +
                "Add immediate value to register"
            );
        } else if (inst.startsWith("add") || inst.startsWith("slt")) {
            machineCodeArea.setText(
                "Instruction : " + inst + "\n" +
                "Type        : R-Type\n\n" +
                "opcode | rs    | rt    | rd    | shamt | funct\n" +
                "000000 | rs    | rt    | rd    | 00000 | funct"
            );
        } else {
            machineCodeArea.setText("");
        }
    }
    static String instrType() {
        String inst = currentInst;
        if (inst.startsWith("addi") || inst.startsWith("sw") ||
            inst.startsWith("beq")  || inst.startsWith("bne") || inst.startsWith("andi"))
            return "I-Type";
        if (inst.startsWith("add") || inst.startsWith("sub") || inst.startsWith("slt"))
            return "R-Type";
        if (inst.startsWith("j ") || inst.equals("halt"))
            return "J-Type";
        return "Unknown";
    }

    static void updateRegisters() {
        r1Label.setText("$1 = " + R1);
        r2Label.setText("$2 = " + R2);
        r3Label.setText("$3 = " + R3);
    }

    static void updateMemory() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < memory.size(); i++) {
            s.append(i == PC ? "-> " : "   ");
            s.append(i).append("  ").append(memory.get(i)).append("\n");
        }
        memArea.setText(s.toString());
    }

    static void resetColors() {
        memPanel.setBackground(null); cuPanel.setBackground(null);
        regPanel.setBackground(null); aluPanel.setBackground(null);
    }
}