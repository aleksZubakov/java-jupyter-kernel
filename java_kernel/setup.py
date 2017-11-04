#!/usr/bin/env python3

from setuptools import setup, find_packages

import subprocess

if __name__ == "__main__":
    setup(name="javaForJupyter",
          author="Devdays",
          description="A Juyter kernel for Java.",
          
          python_requires=">=3.5",
          install_requires=[
              "py4j",
          ],
          data_files=[('/usr/local/share/jupyter/kernels/java', ['kernel.json','kernel.py'])]
 	)
    subprocess.call("mvn clean compile assembly:single",shell = True)
    print("Setup, maybe, done")
