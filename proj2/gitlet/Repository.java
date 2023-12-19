package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  Notice: things to do with persistence should all be done here
 *
 *  @author Ziyue Shen
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMIT_MAP = join(GITLET_DIR, "commit_map");
    public static final File STAGE_AREA = join(GITLET_DIR, "stage_area");
    // public static final File FILE_STAGE = join(STAGE_DIR, "file_stage"); not necessary
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File OBJECTS = join(GITLET_DIR, "objects");

            /** Note: File obj is actually a string storing the path */

    public static void initCommand() throws IOException {  // static method
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();    // create .gitlet
            OBJECTS.mkdir();

            Commit initialCommit = new Commit();  // create initial commit obj

            byte[] commitBlob = serialize(initialCommit);
            String commitID = sha1(commitBlob);

            // COMMIT_MAP.createNewFile();
            Map<String, Commit> commitMap = new TreeMap<>(); // create commit tree(map)

            commitMap.put(commitID, initialCommit);

            writeObject(COMMIT_MAP, (Serializable) commitMap);
            writeObject(HEAD, (Serializable) commitID);


                    // File initialCommitFile = join(GITLET_DIR, "initialCommit");  // create file obj
            // Utils.writeObject(initialCommitFile, initialCommit);  // can store all in the map
            // writeObject() can create new or overwrite local file, write the commit obj into file
            // if (!COMMIT_MAP.exists()) {
                // COMMIT_LIST.createNewFile();  // will store a linked list obj
//            List<Commit> commitList = new LinkedList<>();   // need to be persistent, map is better
//            commitList.add(initialCommit);
//            writeObject(COMMIT_MAP, (Serializable) commitList);
            //}

        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }


    /** no need to make linked list from scratch, just add the parent field in commit  */
//    private class commitNode {
//        private Commit val;
//        private commitNode next;  // can't use commit.next directly, due to its private access
//        commitNode(Commit newCommit) {
//            this.val = newCommit;
//        }
//    }
//    private class commitList {
//        private commitNode head;
//        private commitList() {
//            commitNode head = null;
//        }
//
//        private void insert(commitNode newCommit) {
//            newCommit.next = this.head;
//            this.head = newCommit;
//        }
//
//    }

    /** add the file to the stage area;  */
    public static void add(String fileName) {

        File fileToAdd = join(CWD, fileName);
        if (!fileToAdd.exists()) {
            System.out.println("File does not exist.");
        } else {
            byte[] fileContent = readContents(fileToAdd);  // create blob
            String fileContentID = sha1(fileContent);      // create sha1 ID
            File blobFile = join(OBJECTS, fileContentID);

            Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);
            String headPointer = readObject(HEAD, String.class);
            Commit lastCommit = commitMap.get(headPointer);
            Map<String, String> lastCommitMap = lastCommit.getFile();

//            Commit lastCommit = lastADD.get(0);
//            Map<String, String> lastCommitMap = lastCommit.getFile();

            if (lastCommitMap != null && lastCommitMap.containsKey(fileName)) {     // the file has been committed before
                String lastVersionID = lastCommitMap.get(fileName);

                if (!lastVersionID.equals(fileContentID)) {         // if newly added version is different
                    Map<String, String> addMap = new TreeMap<>();
                    addMap.put(fileName, fileContentID);

                    writeObject(STAGE_AREA, (Serializable) addMap);
                    writeObject(blobFile, (Serializable) fileContent);
                }
            } else {
                Map<String, String> addMap = new TreeMap<>();
                addMap.put(fileName, fileContentID);

                writeObject(STAGE_AREA, (Serializable) addMap);
                writeObject(blobFile, (Serializable) fileContent);
            }

            // File fileStage = join(STAGE_DIR, fileContentID); // use SHA1 as file name in stage area
            // if the file is already in the stage area, overwrites the previous;
            // If the current version is identical to the version in the current commit, do not stage it to be added
            // writeContents(fileStage, fileContent);
        }
    }

    public static void commit(String msg) throws IOException {

        // read the last commit
        Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);
        String headPointer = readObject(HEAD, String.class);
        Commit lastCommit = commitMap.get(headPointer);

        Map<String, String> lastCommitMap = lastCommit.getFile();
        // String lastCommitID = lastCommit.getParent();

        // read from the stage area
        Map<String, String> justAdd = readObject(STAGE_AREA, TreeMap.class);
        Set<String> justAddkeySet = justAdd.keySet();

        for (String keyAdd : justAddkeySet) {
            if (lastCommitMap != null && lastCommitMap.containsKey(keyAdd)) {   // replace the old file reference ID
                lastCommitMap.replace(keyAdd, justAdd.get(keyAdd));  // mutate lastCommitMap
            } else if (lastCommitMap == null) {        // create CommitMap
                lastCommitMap = new TreeMap<>();
                lastCommitMap.put(keyAdd, justAdd.get(keyAdd));
            } else {                                   // add new files
                lastCommitMap.put(keyAdd, justAdd.get(keyAdd));
            }

        }
        Commit newCommit = new Commit(msg, lastCommitMap, headPointer);  // pass in the parentID
        byte[] commitBlob = serialize(newCommit);
        String newCommitID = sha1(commitBlob);
        commitMap.put(newCommitID ,newCommit);

        writeObject(COMMIT_MAP, (Serializable) commitMap);
        writeObject(HEAD, (Serializable) newCommitID);  // renew the HEAD pointer

        STAGE_AREA.delete();//clear the stage_area

//        File fileToCommit = join(GITLET_DIR, justAdd.get(keyAdd));
//        // add the new file blob; (don't delete the old blob, need to keep it)
//        File fileFetch = join(CWD, keyAdd);
//        byte[] fileContent = readContents(fileFetch);  // create blob
//        writeContents(fileToCommit, fileContent);      // store the blob
    }

    public static void log() {
        // read the last commit
        Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);
        String headPointer = readObject(HEAD, String.class);
        Commit lastCommit = commitMap.get(headPointer);
        printCommit(headPointer, lastCommit);

        String parentID = lastCommit.getParent();
        // System.out.println(parentID);  // debugging
        while (parentID != null) {
            lastCommit = commitMap.get(parentID);
            printCommit(parentID, lastCommit);
            parentID = lastCommit.getParent();
        }

    }

    public static void printCommit(String commitID, Commit commitToPrint) {
        System.out.println("===");
        System.out.println("commit " + commitID);

        Date timestamp = commitToPrint.getDate();
        System.out.println("Date: " + timestamp);

        String message = commitToPrint.getMessage();
        System.out.println(message);
        System.out.println();
    }

}
