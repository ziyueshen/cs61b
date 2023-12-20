package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ziyue Shen
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {    // must all be static methods
            case "init":
                Repository.initCommand();
                break;
            case "add":
                Repository.checkInit();
                if (args.length == 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.add(args[1]);
                break;
            case "commit":
                Repository.checkInit();
                if (args.length >= 2) {
                    Repository.commit(args[1]);
                } else {
                    System.out.println("Please enter a commit message.");
                }
                break; // must have, otherwise will go to the next block
            case "log":
                Repository.checkInit();
                Repository.log();
                break;
            case "checkout":
                Repository.checkInit();
                if (args.length == 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                } else if (args[1].equals("--")) {
                    if (args.length == 2) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkout("HEAD", args[2]);
                } else {
                    Repository.checkout(args[1], args[3]);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
            // TODO: FILL THE REST IN
        }
    }
}
