package com.diskscanner;

import com.diskscanner.duplicates.DuplicateFinder;
import com.diskscanner.duplicates.FileInfo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Created by krzych on 26.02.17.
 */
public class DiskScanner {
    public static final int REFRESH_RATE = 100;
    private boolean scanning = false;

    private Consumer<Long> updateFunction;

    public DiskScanner(Consumer<Long> updatingFunction) {
        this.updateFunction = updatingFunction;
    }

    public long nonRecursiveScan(DiskNode rootNode) {
        List<DiskNode> filesToScan = new LinkedList<>();
        int refreshCounter = 0;
        filesToScan.add(rootNode);
        scanning = true;
        while (!filesToScan.isEmpty()) {
            if (!scanning) break;
            if (refreshCounter > REFRESH_RATE) {
                refreshCounter = 0;
                refreshView(rootNode);
            }
            refreshCounter++;

            DiskNode currentNode = filesToScan.remove(0);
            File file = new File(currentNode.getAbsolutePath());
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files != null) {
                    Arrays.stream(files).forEach( (fileName) ->
                        addToQueueIfValid(filesToScan, currentNode, file, fileName)
                    );
                }
            } else {
                DuplicateFinder.getInstance().insert(new FileInfo(file.length()), file.getAbsolutePath());
                currentNode.increaseSize(file.length());
            }
        }
        Collections.sort(rootNode.getChildren(), DiskNode.getComparator());
        refreshView(rootNode);
        return rootNode.getSize();
    }

    private void addToQueueIfValid(List<DiskNode> filesQueue, DiskNode parentNode, File parent, String fileName) {
        File fileInConcern = new File(parent, fileName);
        try {
            if ((fileInConcern.isFile() || fileInConcern.isDirectory() )
                    && !isProcFS(fileInConcern)
                    && !isSymlink(fileInConcern)
                    )

            {
                DiskNode newNode = new DiskNode.Builder()
                        .setName(fileName)
                        .setParent(parentNode)
                        .setAbsolutePath(fileInConcern.getAbsoluteFile().toString())
                        .build();
                parentNode.getChildren().add(newNode);
                filesQueue.add(newNode);
            }
        } catch (IOException e) {
            System.out.println("Skipping not existing file: " + fileName) ;
        }
    }

    public void stop() {
        scanning = false;
    }

    private void refreshView(DiskNode rootNode) {
        updateFunction.accept(rootNode.getSize());
        Collections.sort(rootNode.getChildren(), DiskNode.getComparator());
    }

    public static boolean isSymlink(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File must not be null");
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }

    public static boolean isProcFS(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File must not be null");
        if (file.getAbsolutePath().startsWith("/proc/")) return true;
        return false;
    }

}
