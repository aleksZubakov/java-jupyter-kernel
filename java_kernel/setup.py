#!/usr/bin/env python3

from setuptools import setup, find_packages

import subprocess, re, os

if __name__ == "__main__":
    subprocess.call("mvn clean install",shell = True)
    
    pth_re = re.compile(r'\S*jupyter/kernels/')
    path = pth_re.search(str(subprocess.check_output(['jupyter', 'kernelspec', 'list']))).group() + "java/"
    
    if not os.path.exists(path):
    	os.makedirs(path)
    
    f = open(path+'kernel.json', 'w')
    
    f.write('{"argv":["python","'+path+'kernel.py", "-f", "{connection_file}"],"display_name":"Java"}')
    f.close()
    
    print(path)
    
    setup(name="javaForJupyter",
          author="Devdays",
          description="A Jupyter kernel for Java.",
          
          python_requires=">=3.5",
          install_requires=[
              "py4j",
          ],
          data_files=[(path, ['kernel.py','target/jserver.jar'])]
 	)
    
    print("Setup, maybe, done")
