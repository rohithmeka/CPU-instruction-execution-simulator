# CPU-instruction-execution-simulator
1. Introduction
The project presents a GUI based CPU Instruction Execution Simulator developed using Java Swing. The simulator demonstrates how machine level instructions are executed inside a CPU through a step by step visual representation. The system models important CPU components such as Instruction Memory, Control Unit, Register File, ALU, and Program Counter. The project aims to bridge the gap between theoretical understanding of computer architecture and practical visualization of instruction execution.
2. Problem Statement
Understanding CPU instruction execution is difficult because the internal operations of a processor are not directly visible to users. Most students learn concepts such as registers, ALU operations, and control flow only theoretically. This project addresses the problem by creating an interactive simulator that visually demonstrates how instructions move through different CPU components during execution.
3. Objectives
The primary objective of the project is to visually simulate CPU instruction execution using Java GUI. The system demonstrates the Fetch Decode Execute cycle step by step using simplified machine level instructions. The project also aims to provide instruction format visualization, machine code representation, and real time register updates for better understanding of CPU architecture.
4. System Architecture
The simulator is designed around a custom set ISA inspired MIPS architecture. 
The architecture consists of Instruction Memory, Control Unit, Register File, ALU, and Program Counter. Instructions are fetched from memory using the Program Counter, decoded by the Control Unit, executed in the ALU or Registers, and finally written back to the Register File.

5. Methodology
• The project is implemented in Java using Java Swing to simulate CPU instruction execution through a graphical user interface.
• A simplified Instruction Set Architecture (ISA) is designed using instructions such as LOAD1, LOAD2, ADD, SUB, MUL, MAX, STORE, HALT, EVENODD, SWAP, and SUMN.
• Instructions selected by the user are stored sequentially inside an Instruction Memory structure using an ArrayList.
• The simulator follows the Fetch Decode Execute Write Back cycle step by step using the Program Counter (PC) and stage-based execution logic.
• During the Fetch stage, the current instruction is fetched from Instruction Memory and visually moved to the Control Unit through animation.
• During the Decode stage, the Control Unit interprets the instruction type and displays its corresponding machine code representation such as R Type, I Type, or Custom ISA format.
• During the Execute stage, instructions are routed either to the Register File or ALU based on the operation being performed. Arithmetic and logical operations are executed using register values R1 and R2, while the result is stored in R3.
• Operations such as Multiplication and Sum of N Numbers are implemented using iterative looping logic to clearly demonstrate step by step execution inside the CPU.
• A graphical animation system is implemented using Java Swing Timer and layered components to visually represent instruction movement between Instruction Memory, Control Unit, Register File, and ALU.
• The simulator also maintains execution logs, register updates, machine code visualization, and Program Counter tracking to provide a clear understanding of internal CPU instruction flow.

6. Instruction Set Design
A simplified instruction set is designed to represent machine level operations. 
The implemented instructions include LOAD1, LOAD2, ADD, SUB, MUL, MAX, EVENODD, SWAP, SUMN, STORE, and HALT. Arithmetic and logical operations are performed using the ALU while register operations are handled through the Register File.

7. GUI Design and Visualization
The graphical user interface is developed using Java Swing components such as JFrame, JPanel, JTextArea, JLabel, and JButton. Different CPU components are represented using separate panels. Instruction movement is visualized using an animated instruction box that travels between Instruction Memory, Control Unit, Registers, and ALU. Color highlighting is also used to indicate active components during execution.

8. Program Execution Flow
The simulator follows the Fetch Decode Execute Write Back cycle. 
During Fetch, the instruction is retrieved from memory using the Program Counter. 
During Decode, the Control Unit interprets the instruction type and displays its machine code representation. During Execute, the ALU or Registers perform the required operation. Finally, the result is written back into the Register File and the Program Counter is incremented.

9. Machine Code Representation
The simulator displays simplified machine code representations inspired by Instruction Set Architecture formats such as R Type, I Type, and J Type instructions. 
Each instruction displays fields such as opcode, rs, rt, rd, shamt, funct values depending on the instruction type. Custom instructions such as MAX, SWAP, and SUMN are also represented using custom opcode formats.

10. Operations Implemented
The simulator supports Addition, Subtraction, Multiplication, Maximum of Two Numbers, Even or Odd Checking, Swapping of Two Numbers, and Sum of N Numbers. 
Multiplication is implemented using repeated addition to better demonstrate CPU level execution. 
Sum of N Numbers is implemented using iterative looping for clear visualization of repeated operations.

11. Results and Observations
The simulator successfully demonstrates instruction execution visually and interactively. 
Register updates, ALU operations, machine code display, and instruction movement are clearly represented during execution. 
The project effectively improves conceptual understanding of CPU architecture and instruction flow.

12. Advantages
The project provides an interactive approach for learning computer architecture concepts. 
It improves understanding of registers, ALU operations, instruction decoding, and execution flow. 
The visual representation makes the learning process more intuitive and engaging compared to purely theoretical explanations.

13. Limitations
The simulator is based on a simplified ISA inspired architecture and does not implement a complete real world processor architecture. 
Advanced concepts such as pipelining, cache memory, interrupts, branch prediction, and parallel execution are not included in the current version.

14. Future Enhancements
Future enhancements may include implementation of pipelining, branching instructions, memory addressing, stack operations, flag registers, and advanced datapath visualization. 
The project can also be extended to support additional instruction formats and more realistic processor simulation.

15. Conclusion
The project successfully demonstrates CPU instruction execution using a Java based graphical simulator. 
The simulator clearly visualizes the Fetch Decode Execute cycle along with instruction movement, register updates, ALU operations, and machine code representation. 
The project serves as an effective educational tool for understanding the internal working of CPU instruction execution.
