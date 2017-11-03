#!/usr/bin/env python3

import subprocess

sp = subprocess.Popen("javac -d bin -classpath java2py/target/py4j-0.10.6.jar java2py/JavaBridge.java \
java2py/JShellWrapper.java",shell = True)

# import time

# time.sleep(5)
