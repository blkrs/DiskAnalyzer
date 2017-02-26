package com.diskscanner.duplicates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Created by krzych on 15.05.16.
 */
public class DuplicateFinder {
    private static final int LARGE_FILE_SIZE = 1000000;
    private static DuplicateFinder _instance = null;
    private long duplicatedSpace = 0;
    private Map<FileInfo, List<String>> allFiles = new HashMap<>();
    private Set<FileInfo> potentialDuplicates = new HashSet<>();
    private Set<String> processed = new HashSet<>();
    private DuplicateFinder() {};

    public static DuplicateFinder getInstance() {
        if (_instance == null) {
            _instance = new DuplicateFinder();
        }
        return _instance;
    }

    public void insert(FileInfo info, String node) {
        if ( info.getSize() < LARGE_FILE_SIZE ) return;
        if (allFiles.get(info) == null) {
            allFiles.put(info, new ArrayList<String>());
            allFiles.get(info).add(node);
        } else {
            allFiles.get(info).add(node);
            potentialDuplicates.add(info);
        }
    }

    public void countDuplicates() {
        for (FileInfo info : potentialDuplicates) {
            for (String filePath1 : allFiles.get(info)) {
                try {
                    checkFile(info, filePath1);
                    processed.add(filePath1);
                } catch (IOException e) {
                    System.out.println("Cannot read file: " + filePath1);
                }
            }
        }
        System.out.println("Total redundant space: " + duplicatedSpace + " bytes");
    }

    public void clear() {
        processed.clear();
        potentialDuplicates.clear();
        allFiles.clear();
        duplicatedSpace = 0;
    }

    private void checkFile(FileInfo info, String filePath1) throws IOException {
        //System.out.println("Checking file: " + filePath1);
        for (String filePath2 : allFiles.get(info)) {
            if (processed.contains(filePath2)) continue;
            if (!filePath1.equals(filePath2)  &&
                    (quickSum(filePath1) == quickSum(filePath2)) &&
            (md5(filePath1).equals(md5(filePath2)))) {
                duplicatedSpace += info.getSize();
                if (info.getSize() > LARGE_FILE_SIZE) {
                    System.out.println("Duplicate files: " + filePath1 + " ," + filePath2);
                }
            }
         }
    }


    private String md5(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(new File(filePath));
        String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        fis.close();
        return md5;
    }

    private long quickSum(String filePath) throws IOException {
        FileChannel fc = new FileInputStream(new File(filePath)).getChannel();
        long qsum = 0;
        ByteBuffer bb = ByteBuffer.allocate(32);
        fc.read(bb);
        for (byte b : bb.array()){
            qsum += b;
        }
        fc.close();
        return qsum;
    }



}
