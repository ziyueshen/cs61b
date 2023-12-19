package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ziyue Shen
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {    // must all be static methods
            case "init":
                Repository.initCommand();
                // TODO: handle the `init` command
                break;
            case "add":
                Repository.add(args[1]);
                break;
            case "commit":
                if (args.length >= 2) {
                    Repository.commit(args[1]);
                } else {
                    System.out.println("Please enter a commit message.");
                }
            case "log":
                Repository.log();
            // TODO: FILL THE REST IN
        }
    }
}
