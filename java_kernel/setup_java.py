#!/usr/bin/env python3

import subprocess

sp = subprocess.Popen("javac -d bin -classpath java2py/target/py4j-0.10.6.jar\
 java2py/Py4JExample.java java2py/JShellWrapper.java")
