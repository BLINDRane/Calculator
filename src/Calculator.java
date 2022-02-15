import java.util.Stack;
import acm.graphics.GLabel;
import acm.program.GraphicsProgram;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Calculator extends GraphicsProgram {

    private GLabel display = new GLabel(""); // a GLabel that represents the "display of the calculator"

    @Override
    public void init() {
        display.setVisible(true); //possibly pointless

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new acm.gui.TableLayout(5, 4)); //make a 5 tall, 4 wide button spread

        JButton oneButton = new JButton("1");
        buttonPanel.add(oneButton); //note this is adding the button to a group which will then be added

        JButton twoButton = new JButton("2");
        buttonPanel.add(twoButton);

        JButton threeButton = new JButton("3");
        buttonPanel.add(threeButton);

        JButton plusButton = new JButton("+");
        buttonPanel.add(plusButton);

        JButton fourButton = new JButton("4");
        buttonPanel.add(fourButton);

        JButton fiveButton = new JButton("5");
        buttonPanel.add(fiveButton);

        JButton sixButton = new JButton("6");
        buttonPanel.add(sixButton);

        JButton minusButton = new JButton("-");
        buttonPanel.add(minusButton);

        JButton sevenButton = new JButton("7");
        buttonPanel.add(sevenButton);

        JButton eightButton = new JButton("8");
        buttonPanel.add(eightButton);

        JButton nineButton = new JButton("9");
        buttonPanel.add(nineButton);

        JButton multiplicationButton = new JButton("*");
        buttonPanel.add(multiplicationButton);

        JButton zeroButton = new JButton("(");
        buttonPanel.add(zeroButton);

        JButton openButton = new JButton("0");
        buttonPanel.add(openButton);

        JButton closeButton = new JButton(")");
        buttonPanel.add(closeButton);

        JButton divisionButton = new JButton("/");
        buttonPanel.add(divisionButton);

        JButton clearButton = new JButton("C");
        buttonPanel.add(clearButton);

        JButton backspaceButton = new JButton("<");
        buttonPanel.add(backspaceButton);

        JButton equalsButton = new JButton("=");
        buttonPanel.add(equalsButton);

        JButton decimalButton = new JButton(".");
        buttonPanel.add(decimalButton);

        add(buttonPanel, CENTER); //adds the panel and all its buttons

        addActionListeners(); //hits all buttons with an action listener

        // glabel stuff
        display.setFont("Times-bold-24");
        add(display, 0, 18);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        switch (ae.getActionCommand()) {

            case "=": // cause an evaluation

                System.out.flush(); //clear the console

                int response = evaluate(display.getLabel());

                StringBuilder ops = new StringBuilder(); // use a string builder to collect output for the display
                ops.append(response);
                String output = ops.toString();
                display.setLabel(output);

                break;

            case "C": // clear the display
                display.setLabel("");
                break;

            case "<": // delete a character from the display if applicable

                if (display.getLabel().length()>0){
                    StringBuilder sb = new StringBuilder(display.getLabel()); // save the display String to a builder
                    sb = sb.deleteCharAt(display.getLabel().length()-1); // delete the appropriate char from the String
                    display.setLabel(sb.toString()); // replace the label
                }

                break;

            default: // any input number or symbol gets added to the display string

                display.setLabel(display.getLabel() + ae.getActionCommand());

        }
    }

    private boolean precedence(char peek, char ch) {//System.out.println(x+" "+y);

        if ((peek == '*' || peek == '/') && (ch == '+' || ch == '-')) {
            return true;
        }

        // in the case of different chars, force left to right evaluation
        if(peek != ch && peek != '('){
            return true;
        }

        return false;
    }

    private int calculate(char c, int a, int b) {
        if (c == '*') {
            return a * b;
        }
        if (c == '/') {
            return b / a;
        }
        if (c == '+') {
            return a + b;
        }
        if (c == '-') {
            return b - a;
        }
        return 0;
    }

    private int evaluate(String input) {

        int strlen = input.length();

        Stack<Integer> ns = new Stack<Integer>();
        Stack<Character> op = new Stack<Character>();

        int i = 0; // an iteration marker for the first while loop
        char ch;

        StringBuffer s;

        if (!input.isEmpty()) { // ensure there is actually some input to process

            postMessage("Beginning evaluation...", 100);

            while (i < strlen) { // loop across the String

                ch = input.charAt(i); // store the character found at a point

                if (ch == ' ') { // skip over blank spaces (should not occur)
                    i++;
                    continue;
                }

                /*
                    Processing the input String should take the following steps:

                    1) Check for numbers. Question to ask yourself: how do I handle multi-digit numbers?

                    2) Check for an opening parenthesis, which would indicate the start of a block that must be evaluated before all others.

                    3) Check for a closing parenthesis, which indicates the end of a block that must be evaluated before all others.
                        When one of these is seen, we must evaluate the contents of the parenthesis set right away, and put the results back on the number stack.

                    4) Check for any non parenthesis operator, and determine if it has a higher precedence than the operator that is currently on top of the op stack.
                        When we find an op that has precedence ( '*' or '/' vs '+' or '-'), we should calculate the result of that combo immediately,
                        and push the result onto the number stack.

                        If we do not find an op that has higher precedence, simply push the symbol onto the op stack.

                    5) As the primary evaluation loop reaches the end of an iteration, don't forget to increment your counter ('a' in this case)

                    6)
                 */

                postMessage("Searching for symbols...");

                if (ch <= '9' && ch >= '0') { // check for digits to add to the number stack
                    postMessage("Found number.");
                    // here we are trying to make sure we push the correct digits onto the stack.

                    s = new StringBuffer();
                    s.append(ch); // add the digit to the string buffer
                    i++; // increment a so that the next while loop won't add the same number twice

                    // this while loop will continue as long as digits are found (not symbols)
                    // which will allow us to build a string that contains a complete number such as 26, or 123486
                    while (i < strlen && input.charAt(i) <= '9' && input.charAt(i) >= '0') {
                        s.append(input.charAt(i));
                        i++;
                    }


                    ns.push(Integer.parseInt(new String(s))); // get the number that the string builder contains, and put it on the number stack
                    continue; // the continue will skip to the next loop iteration.

                } else if (ch == '(') { // if we see an opening parenthesis, put it on the operator stack

                    postMessage("Found opening parenthesis. Adding to op stack.");

                    op.push(ch);

                } else if (ch == ')') { // if we see a closing parenthesis, we have to check some things.

                    postMessage("Found closing parenthesis. Beginning parenthesis set evaluation...");

                    // if the op stack is not empty, and we don't see an opening paren at the top, we will need to evaluate the parentheses
                    while (!op.empty() && op.peek() != '(') {
                        ns.push(calculate(op.pop(), ns.pop(), ns.pop())); //pop the op stack once to get a math symbol, then pop the number stack twice to get the numbers to work on.
                    }

                    postMessage("Removing parenthesis from op stack.");

                    op.pop(); // after a parenthesis set is processed, pop the op stack to remove the opening paren

                } else {

                    postMessage("Operator found. Checking precedence...");

                    while (!op.empty() && precedence(op.peek(), ch)) {

                        postMessage("Operator with precedence found on op stack. Evaluating precedent operator...");

                        ns.push(calculate(op.pop(), ns.pop(), ns.pop()));
                    }

                    postMessage("Adding " + ch + " to op stack.");

                    op.push(ch);
                }

                i++;
            } // end of main parsing loop

            postMessage("Main parsing complete. Finalizing...");

            // check for any remaining operators on the op stack, and evaluate them with the number stack accordingly.
            while (!op.empty()) {
                postMessage("Op stack not empty. Evaluating...");
                ns.push(calculate(op.pop(), ns.pop(), ns.pop()));
            }

            postMessage("Evaluation complete. Final answer: " + ns.peek());
            postMessage("");
            return ns.pop();

        } else { // if there was nothing to evaluate, return zero.
            return 0;
        }
    }

    private void postMessage(String s, int time){
        System.out.println(s);
        pause(time);
    }

    private void postMessage(String s){
        System.out.println(s);
        pause(250);
    }

    private int eval(String input){

        int strlen = input.length();

        Stack<Integer> ns = new Stack<Integer>();
        Stack<Character> op = new Stack<Character>();

        int i = 0; // an iteration marker for the first while loop
        char ch;

        StringBuilder s;

        if(!input.isEmpty()){

            while(i < strlen) {

                ch = input.charAt(i); // store the character found at a point

                if (ch == ' ') { // skip over blank spaces (should not occur)
                    i++;
                    continue;
                }

                if (ch <= '9' && ch >= '0') {
                    //number stuff
                    s = new StringBuilder();
                    s.append(ch);
                    ns.push(Integer.parseInt(s.toString()));

                } else if(ch == '(') {

                } else if(ch == ')') {


                } else {
                    //operator stuff
                    op.push(ch);
                }

                i++;
            } // end of while loop

            //evaluate pairs
            while (!op.empty()) {
                ns.push(calculate(op.pop(), ns.pop(), ns.pop()));
            }

            return ns.pop();
        }

        return 0;
    }

    private int calc(char c, int a, int b){
        if(c == '+'){
            return b+a;
        }

        if(c == '-'){
            return b-a;
        }

        if(c == '*'){
            return b*a;
        }

        if(c == '/'){
            return b/a;
        }

        return 0;
    }

    public static void main(String[] args) {

        new Calculator().start();
    }
}