package com.diskscanner;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileInfo {
	
	long size;
	long last_used;

}
