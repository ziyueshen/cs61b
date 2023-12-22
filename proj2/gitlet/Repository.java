package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.text.SimpleDateFormat;


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
    public static final File REMOVE_STAGE_AREA = join(GITLET_DIR, "remove_stage_area");
    // public static final File FILE_STAGE = join(STAGE_DIR, "file_stage"); not necessary
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File OBJECTS = join(GITLET_DIR, "objects");
    public static final File BRANCHES = join(GITLET_DIR, "branches");

            /** Note: File obj is actually a string storing the path */

    public static void initCommand() {  // static method
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

            String branchName = "master";
            writeContents(HEAD, branchName);  // don't use writeObject

            Map<String, String> branchMap = new TreeMap<>();
            branchMap.put(branchName, commitID);
            writeObject(BRANCHES, (Serializable) branchMap);


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

//            Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);
//            String headPointer = readObject(HEAD, String.class);
//            Commit lastCommit = commitMap.get(headPointer);
            Commit lastCommit = getActiveLatestCommit();
            Map<String, String> lastCommitMap = lastCommit.getFile();

//            Commit lastCommit = lastADD.get(0);
//            Map<String, String> lastCommitMap = lastCommit.getFile();

            if (lastCommitMap != null && lastCommitMap.containsKey(fileName)) {     // the file has been committed before
                String lastVersionID = lastCommitMap.get(fileName);

                if (!lastVersionID.equals(fileContentID)) {         // if newly added version is different
                    Map<String, String> addMap;
                    if (!STAGE_AREA.exists()) {
                        addMap = new TreeMap<>(); // should read from stage first
                    } else {
                        addMap =  readObject(STAGE_AREA, TreeMap.class);
                    }
                    addMap.put(fileName, fileContentID);

                    writeObject(STAGE_AREA, (Serializable) addMap);
                    writeContents(blobFile, fileContent);
                }
            } else {
                Map<String, String> addMap;
                if (!STAGE_AREA.exists()) {
                    addMap = new TreeMap<>(); // should read from stage first
                } else {
                    addMap =  readObject(STAGE_AREA, TreeMap.class);
                }
                addMap.put(fileName, fileContentID);

                writeObject(STAGE_AREA, (Serializable) addMap);
                writeContents(blobFile, fileContent);
            }

            // File fileStage = join(STAGE_DIR, fileContentID); // use SHA1 as file name in stage area
            // if the file is already in the stage area, overwrites the previous;
            // If the current version is identical to the version in the current commit, do not stage it to be added
            // writeContents(fileStage, fileContent);
        }
    }

    public static Commit commit(String msg) {

        // read the last commit
        Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);
        String headPointer = getActiveBranchHEAD();
//        String headPointer = readObject(HEAD, String.class);
//        Commit lastCommit = commitMap.get(headPointer);
        Commit lastCommit = getActiveLatestCommit();
        Map<String, String> lastCommitMap;

        Map<String, String> originalMap = lastCommit.getFile();
        if (originalMap != null) {
            lastCommitMap = new TreeMap<>(originalMap);
        } else {
            lastCommitMap = null;  // to avoid NullPointerException
        }
        // must create a copied map, don't mutate originalMap; otherwise would cause bug!!!
        // String lastCommitID = lastCommit.getParent();

        // read from the stage area
        if (!STAGE_AREA.exists() && !REMOVE_STAGE_AREA.exists()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        // adding stage
        if (STAGE_AREA.exists()) {
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
            STAGE_AREA.delete();//clear the stage_area
        }
        // read from the removal stage area
        if (REMOVE_STAGE_AREA.exists()) {
            Map<String, String> justRemove = readObject(REMOVE_STAGE_AREA, TreeMap.class);
            Set<String> justRemovekeySet = justRemove.keySet();

            for (String keyRemove : justRemovekeySet) {
                if (lastCommitMap != null && lastCommitMap.containsKey(keyRemove)) {   // replace the old file reference ID
                    lastCommitMap.remove(keyRemove);  // mutate lastCommitMap
                }
            }
            REMOVE_STAGE_AREA.delete();
        }
        Commit newCommit = new Commit(msg, lastCommitMap, headPointer);  // pass in the parentID
        byte[] commitBlob = serialize(newCommit);
        String newCommitID = sha1(commitBlob);
        commitMap.put(newCommitID ,newCommit);

        writeObject(COMMIT_MAP, (Serializable) commitMap);

        mutateActiveBranchHEAD(newCommitID);
        // writeContents(HEAD, (Serializable) newCommitID);  // renew the HEAD pointer

        return newCommit;

//        File fileToCommit = join(GITLET_DIR, justAdd.get(keyAdd));
//        // add the new file blob; (don't delete the old blob, need to keep it)
//        File fileFetch = join(CWD, keyAdd);
//        byte[] fileContent = readContents(fileFetch);  // create blob
//        writeContents(fileToCommit, fileContent);      // store the blob
    }

    public static void log() {
        // read the last commit
        Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);
//        String headPointer = readObject(HEAD, String.class);
//        Commit lastCommit = commitMap.get(headPointer);
        String headPointer = getActiveBranchHEAD();
        Commit lastCommit = getActiveLatestCommit();
        printCommit(headPointer, lastCommit);

        String parentID = lastCommit.getParent();
        // System.out.println(parentID);  // debugging
        while (parentID != null) {
            lastCommit = commitMap.get(parentID);
            printCommit(parentID, lastCommit);
            parentID = lastCommit.getParent();
        }

    }

    public static void status() {
        System.out.println("=== Branches ===");  // println contains \n implicitly
        String activeBranch = readContentsAsString(HEAD);
        Map<String, String> branchMap = readObject(BRANCHES, TreeMap.class);
        for (String branch : branchMap.keySet()) {
            if (branch.equals(activeBranch)) {
                System.out.println("*" + activeBranch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        if (STAGE_AREA.exists()) {
            Map<String, String> addMap =  readObject(STAGE_AREA, TreeMap.class);
            for (String addFile : addMap.keySet()) {
                System.out.println(addFile);
            }
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        if (REMOVE_STAGE_AREA.exists()) {
            Map<String, String> addMap =  readObject(REMOVE_STAGE_AREA, TreeMap.class);
            for (String addFile : addMap.keySet()) {
                System.out.println(addFile);
            }
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkout(String commitID, String filename) {
        if (commitID.equals("HEAD")) {
            // read the last commit
            String headPointer = getActiveBranchHEAD();
            // String headPointer = readObject(HEAD, String.class);
            replaceFile(filename, headPointer);

        } else {
            replaceFile(filename, commitID);
        }
    }

    public static void rm(String fileName) {
        // read the last commit
        Commit lastCommit = getActiveLatestCommit();
        // String headPointer = readObject(HEAD, String.class);
//        Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);
//        Commit lastCommit = commitMap.get(headPointer);
        Map<String, String> lastCommitMap = lastCommit.getFile();

        if (lastCommitMap.containsKey(fileName)) {
            File fileRemove = join(CWD, fileName);
            restrictedDelete(fileRemove);
            // lastCommitMap.remove(fileName); don't change history
            // stage for removal
            Map<String, String> removeMap;
            if (!REMOVE_STAGE_AREA.exists()) {
                removeMap = new TreeMap<>(); // should read from stage first
            } else {
                removeMap =  readObject(REMOVE_STAGE_AREA, TreeMap.class);
            }
            removeMap.put(fileName, lastCommitMap.get(fileName));

            writeObject(REMOVE_STAGE_AREA, (Serializable) removeMap);

        } else if (STAGE_AREA.exists()) {
            Map<String, String> justAdd = readObject(STAGE_AREA, TreeMap.class);
            if (justAdd.containsKey(fileName)) {
                // STAGE_AREA.delete();
                justAdd.remove(fileName);
                writeObject(STAGE_AREA, (Serializable) justAdd);
            } else {  // no such file in STAGE_AREA
                System.out.println("No reason to remove the file.");
            }
        } else {  // STAGE_AREA is empty
            System.out.println("No reason to remove the file.");
        }
    }

    public static void branch(String branchName) {
        String headPointer = getActiveBranchHEAD();
        Map<String, String> branchMap = readObject(BRANCHES, TreeMap.class);
        if (branchMap.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branchMap.put(branchName, headPointer);
        writeObject(BRANCHES, (Serializable) branchMap);
    }

    /** dangerous!!! don't test in proj2 dir  */
    public static void checkoutBranch(String branchName) {
        Map<String, String> branchMap = readObject(BRANCHES, TreeMap.class);
        if (!branchMap.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        String activeBranch = readContentsAsString(HEAD);
        if (branchName.equals(activeBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        List<String> fileCWD = plainFilenamesIn(CWD);
        Commit lastCommit = getActiveLatestCommit();
        Map<String, String> lastCommitMap = lastCommit.getFile();

        String givenBranch = branchMap.get(branchName);
        Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);
        Map<String, String> givenFileMap = commitMap.get(givenBranch).getFile();

        if (fileCWD != null) {
            for (String fileName : fileCWD) {
                // must do the check before changing CWD
                if (!lastCommitMap.containsKey(fileName) && givenFileMap.containsKey(fileName)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }

        // don't bother to compare, just delete all, then copy all
//        if (fileCWD != null) {
//            for (String fileName : fileCWD) {
//                File fileToDelete = join(CWD, fileName);
//                fileToDelete.delete();
//            }
//        }

        writeContents(HEAD, branchName);  // move the HEAD
        lastCommit = getActiveLatestCommit(); // after the HEAD moves
        lastCommitMap = lastCommit.getFile();
        if (lastCommitMap != null) {
            for (Map.Entry<String, String> entry : lastCommitMap.entrySet()) {
                File fileToWrite = join(CWD, entry.getKey());
                File fileWanted = join(OBJECTS, entry.getValue());
                byte[] fileText = readContents(fileWanted);
                writeContents(fileToWrite, fileText);  // overwrite if exists
            }
        }

        // clear the stage area
        if (STAGE_AREA.exists()) {
            STAGE_AREA.delete();
        }

        if (REMOVE_STAGE_AREA.exists()) {
            REMOVE_STAGE_AREA.delete();
        }
    }

    public static void merge(String branchName) {
        Set<String> currentBranchSet = new HashSet<>();
        Set<String> givenBranchSet = new HashSet<>();

        Map<String, String> branchMap = readObject(BRANCHES, TreeMap.class);
        Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);
        String activeBranch = getActiveBranchHEAD();
        String givenBranch = branchMap.get(branchName);

        String ancestorCommitID = findAncestor(branchName, currentBranchSet, givenBranchSet);

        if (currentBranchSet.contains(givenBranch)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (activeBranch.equals(ancestorCommitID)) {
            System.out.println("Current branch fast-forwarded.");
            checkoutBranch(branchName);
            System.exit(0);
        }

        // compare file one by one
        Map<String, String> ancestorFileMap = commitMap.get(ancestorCommitID).getFile();
        Map<String, String> currentFileMap = commitMap.get(activeBranch).getFile();
        Map<String, String> givenFileMap = commitMap.get(givenBranch).getFile();

        for (String fileName : ancestorFileMap.keySet()) {
            if (currentFileMap.containsKey(fileName)) {
                // not deleted in current branch
                if (ancestorFileMap.get(fileName).equals(currentFileMap.get(fileName))) {
                    // if current file remains unchanged vs ancestor
                    if (!givenFileMap.containsKey(fileName)) {
                        // deleted in given branch, should stage for removal
                        // rm(fileName); this may change CWD
                        // currentFileMap.remove(fileName); don't change history
                        Map<String, String> removeMap;
                        if (!REMOVE_STAGE_AREA.exists()) {
                            removeMap = new TreeMap<>(); // should read from stage first
                        } else {
                            removeMap =  readObject(REMOVE_STAGE_AREA, TreeMap.class);
                        }
                        removeMap.put(fileName, currentFileMap.get(fileName));

                        writeObject(REMOVE_STAGE_AREA, (Serializable) removeMap);
                    } else {
                        // not deleted in given branch, should update and stage for addition
                        // currentFileMap.replace(fileName, givenFileMap.get(fileName)); don't change history
                        Map<String, String> addMap;
                        if (!STAGE_AREA.exists()) {
                            addMap = new TreeMap<>(); // should read from stage first
                        } else {
                            addMap =  readObject(STAGE_AREA, TreeMap.class);
                        }
                        addMap.put(fileName, givenFileMap.get(fileName));
                        writeObject(STAGE_AREA, (Serializable) addMap);
                    }
                } else {
                    // file content changed in current branch
                    if (!givenFileMap.containsKey(fileName)) {
                        // deleted in given branch, conflict
                        mergeContent(fileName, currentFileMap.get(fileName), "empty");
                        System.out.println("Encountered a merge conflict.");
                    } else {
                        // not deleted in given branch, compare content
                        if (!givenFileMap.get(fileName).equals(currentFileMap.get(fileName))) {
                            // different changes in 2 branches, conflict
                            mergeContent(fileName, currentFileMap.get(fileName), givenFileMap.get(fileName));
                            System.out.println("Encountered a merge conflict.");
                        }
                    }
                }
            } else {
                // deleted in current branch
                if (!givenFileMap.get(fileName).equals(ancestorFileMap.get(fileName))) {
                    // changed in given branch, conflict
                    mergeContent(fileName, "empty", givenFileMap.get(fileName));
                    System.out.println("Encountered a merge conflict.");
                }
            }
        }

        // deal with newly added files in given branch
        for (String fileName : givenFileMap.keySet()) {
            if (!ancestorFileMap.containsKey(fileName)) {
                // newly added
                if (currentFileMap.containsKey(fileName)) {
                    // also added in current branch, compare
                    if (!givenFileMap.get(fileName).equals(currentFileMap.get(fileName))) {
                        // add diff contents, conflict
                        mergeContent(fileName, currentFileMap.get(fileName), givenFileMap.get(fileName));
                        System.out.println("Encountered a merge conflict.");
                    }
                } else {
                    // not added in current branch, add to stage area
                    // add Map must contain multiple files
                    Map<String, String> addMap;
                    if (!STAGE_AREA.exists()) {
                        addMap = new TreeMap<>(); // should read from stage first
                    } else {
                        addMap =  readObject(STAGE_AREA, TreeMap.class);
                    }
                    addMap.put(fileName, givenFileMap.get(fileName));
                    writeObject(STAGE_AREA, (Serializable) addMap);
                }
            }
        }
        String activeBranchName = readContentsAsString(HEAD);
        Commit mergedCommit = commit("Merged " + branchName +" into " + activeBranchName + ".");
        mergedCommit.setSecondParent(givenBranch);
    }

    /** helper function  */
    public static void replaceFile(String fileName, String commitID) {
        // delete the old version
        File fileReplaced = join(CWD, fileName);
        // restrictedDelete(fileReplaced); // not necessary, will be replaced

        // get the blob name
        Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);
//        for (Map.Entry<String, Commit> entry : commitMap.entrySet()) {
//            System.out.println(entry.getKey());
//            Commit lastCommit = entry.getValue();
//            Map<String, String> lastCommitMap = lastCommit.getFile();
//            System.out.println(lastCommitMap);
//        } // debugging
        // Commit lastCommit = getActiveLatestCommit();
        Commit lastCommit = commitMap.get(commitID);
        if (lastCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Map<String, String> lastCommitMap = lastCommit.getFile();
        // System.out.println(lastCommitMap);


        String fileBlobName = lastCommitMap.get(fileName);
        if (fileBlobName == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        // System.out.println(fileBlobName);

        // replace the content
        File fileWanted = join(OBJECTS, fileBlobName);
        byte[] fileText = readContents(fileWanted);
        writeContents(fileReplaced, fileText);
    }

    public static void printCommit(String commitID, Commit commitToPrint) {
        System.out.println("===");
        System.out.println("commit " + commitID);

        Date timestamp = commitToPrint.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        String formattedDate = dateFormat.format(timestamp);  // formatting
        System.out.println("Date: " + formattedDate);

        String message = commitToPrint.getMessage();
        System.out.println(message);
        System.out.println();
    }

    public static void checkInit() {
        if (!GITLET_DIR.exists() || !GITLET_DIR.isDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static Commit getActiveLatestCommit() {
        String activeBranch = readContentsAsString(HEAD);
        Map<String, String> branchMap = readObject(BRANCHES, TreeMap.class);
        String latestCommitID = branchMap.get(activeBranch);

//        System.out.println(activeBranch);
//        System.out.println(branchMap);
//        System.out.println(latestCommitID);

        Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);

        // System.out.println(commitMap);
        // avoid null pointer
        Commit lastCommit = commitMap.get(latestCommitID);
        // System.out.println(lastCommit);
        return lastCommit;
    }

    public static String getActiveBranchHEAD() {
        String activeBranch = readContentsAsString(HEAD);  // don't use readObject
        Map<String, String> branchMap = readObject(BRANCHES, TreeMap.class);
        String latestCommitID = branchMap.get(activeBranch);
        return latestCommitID;
    }

    public static void mutateActiveBranchHEAD(String commitId) {
        String activeBranch = readContentsAsString(HEAD);  // don't use readObject
        Map<String, String> branchMap = readObject(BRANCHES, TreeMap.class);
        // System.out.println(branchMap);
        branchMap.replace(activeBranch, commitId);
        // System.out.println(branchMap);
        writeObject(BRANCHES, (Serializable) branchMap);
    }

    public static String findAncestor(String givenBranchName, Set<String> currentBranchSet, Set<String> givenBranchSet) {
        // may need to think about second parent
//        Set<String> currentBranchSet = new HashSet<>();
//        Set<String> givenBranchSet = new HashSet<>();
        Commit activeBranchCommit;
        String activeParentID;
        Commit givenBranchCommit;
        String givenParentID;

        Map<String, String> branchMap = readObject(BRANCHES, TreeMap.class);
        Map<String, Commit> commitMap = readObject(COMMIT_MAP, TreeMap.class);

        String activeBranch = getActiveBranchHEAD();
        String givenBranch = branchMap.get(givenBranchName);
        currentBranchSet.add(activeBranch);
        givenBranchSet.add(givenBranch);

        Set<String> intersection = new HashSet<>(currentBranchSet);
        intersection.retainAll(givenBranchSet);

        while (intersection.isEmpty()) {
            activeBranchCommit = commitMap.get(activeBranch);
            activeParentID = activeBranchCommit.getParent();
            if (activeParentID != null) {
                currentBranchSet.add(activeParentID);
                // Commit activeParentCommit = commitMap.get(activeParentID);
                activeBranch = activeParentID;
            }

            givenBranchCommit = commitMap.get(givenBranch);
            givenParentID = givenBranchCommit.getParent();
            if (givenParentID != null) {
                givenBranchSet.add(givenParentID);
                givenBranch = givenParentID;
            }

            intersection = new HashSet<>(currentBranchSet);
            intersection.retainAll(givenBranchSet);
        }
        Iterator<String> iterator = intersection.iterator();
        return iterator.next();
    }

    public static void mergeContent(String fileName, String fileNameCurrent, String fileNameGiven) {
        File fileCurrent = join(OBJECTS, fileNameCurrent);
        File fileGiven = join(OBJECTS, fileNameGiven);
        String currentContents;
        String givenContents;
        if (fileCurrent.equals("empty")) {
            currentContents = "";
        } else {
            currentContents = readContentsAsString(fileCurrent);
        }
        if (fileGiven.equals("empty")) {
            givenContents = "";
        } else {
            givenContents = readContentsAsString(fileGiven);
        }

        String text = "<<<<<<< HEAD" + "\n" + currentContents + "=======" + "\n" + givenContents + ">>>>>>>";
        byte[] fileContent = serialize(text);

        String fileContentID = sha1(fileContent);      // create sha1 ID
        File blobFile = join(OBJECTS, fileContentID);


        // read from the stage area
        Map<String, String> addMap;
        if (!STAGE_AREA.exists()) {
            addMap = new TreeMap<>(); // should read from stage first
        } else {
            addMap =  readObject(STAGE_AREA, TreeMap.class);
        }
        // add to stage area
        addMap.put(fileName, fileContentID);  // may overwrite sth in stage area

        writeObject(STAGE_AREA, (Serializable) addMap);
        writeContents(blobFile, fileContent);
    }
}
