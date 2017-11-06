# java-jupyter-kernel

## Setup
First of all, you should install ```jupyter``` : [installing jupyter](http://jupyter.readthedocs.io/en/latest/install.html). 

Then launch (root privilegies might requried)
```
./setup.py install
``` 
If all goes fine, setup.py will find path, where to specify Java kernel. (It calls "jupyter kernelspec list"  and takes path from it.) If finding path fails, you'll be asked to specify
path manually (i.e. ```/usr/local/share/jupyter/kernels```).

## Development
Files in repository include file ```java_src/target/jserver.jar``` . If you want to change or/and rebuild
it, run 
```
cd java_src
mvn clean install
``` 

## Authors
- [@pvktk](https://github.com/pvktk)
- [@aleksZubakov](https://github.com/aleksZubakov)
- [@cripson1994](https://github.com/cripson1994)
