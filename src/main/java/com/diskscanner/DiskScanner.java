package com.diskscanner;

import com.diskscanner.duplicates.DuplicateFinder;
import com.diskscanner.duplicates.FileInfo;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by krzych on 26.02.17.
 */
public class DiskScanner {
    private boolean scanning = false;

    private Consumer<Long> updateFunction;

    public DiskScanner(Consumer<Long> updatingFunction) {
        this.updateFunction = updatingFunction;
    }

    public long nonRecursiveScan(DiskNode rootNode) {
        List<DiskNode> filesToScan = new LinkedList<>();
        int fileCounter = 0;
        filesToScan.add(rootNode);
        scanning = true;
        while (!filesToScan.isEmpty()) {
            if (!scanning) break;
            if (fileCounter > 100) {
                fileCounter = 0;
                refreshView(rootNode);
            }
            fileCounter++;

            DiskNode currentNode = filesToScan.remove(0);
            File file = new File(currentNode.getAbsolutePath());
            if (file.isDirectory()) {
                String[] subDirs = file.list();
                if (subDirs != null) {
                    Arrays.stream(subDirs).forEach( (fileName) -> {
                        File fileInFolder = new File(file, fileName);
                        DiskNode newNode = new DiskNode.Builder()
                                .setName(fileName)
                                .setParent(currentNode)
                                .setAbsolutePath(fileInFolder.getAbsoluteFile().toString())
                                .build();
                        currentNode.getChildren().add(newNode);
                        filesToScan.add(newNode);
                    });
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

    public void stop() {
        scanning = false;
    }

    private void refreshView(DiskNode rootNode) {
        updateFunction.accept(rootNode.getSize());
        Collections.sort(rootNode.getChildren(), DiskNode.getComparator());
    }

}
