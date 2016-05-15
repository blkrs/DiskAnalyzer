#!/bin/bash

mvn clean compile assembly:single

cp target/DiskSpaceScanner*.jar bin/DiskAnalyzer.jar
