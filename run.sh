#!/bin/sh
cd "$(dirname "$0")"
javac src/HostelServer.java && java -cp src HostelServer
