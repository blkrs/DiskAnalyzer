package com.diskscanner;

import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Created by krzych on 15.05.16.
 */
public class DuplicateFinder {

    private static DuplicateFinder _instance = null;
    private long duplicatedSpace = 0;
    Map<FileInfo, List<String>> totalMap = new HashMap<>();
    Set<FileInfo> duplicates = new HashSet<>();
    Set<String> processed = new HashSet<>();

    private DuplicateFinder() {};

    public static DuplicateFinder getInstance() {
        if (_instance == null) {
            _instance = new DuplicateFinder();
        }
        return _instance;
    }

    public void insert(FileInfo info, String node) {
        if (totalMap.get(info) == null) {
            totalMap.put(info, new ArrayList<String>());
            totalMap.get(info).add(node);
        } else {
            totalMap.get(info).add(node);
            duplicates.add(info);
        }
    }

    public void countDuplicates() {
        for (FileInfo info : duplicates) {
            for (String filePath1 : totalMap.get(info)) {
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

    private void checkFile(FileInfo info, String filePath1) throws IOException {
        //System.out.println("Checking file: " + filePath1);
        for (String filePath2 : totalMap.get(info)) {
            if (processed.contains(filePath2)) continue;
            if (!filePath1.equals(filePath2)  &&
                    (quickSum(filePath1) == quickSum(filePath2)) &&
            (md5(filePath1).equals(md5(filePath2)))) {
                duplicatedSpace += info.getSize();
                if (info.getSize() > 1000000) {
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

    public void clear() {
        processed.clear();
        duplicates.clear();
        totalMap.clear();
        duplicatedSpace = 0;
    }

}
