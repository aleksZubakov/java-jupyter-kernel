# java-jupyter-kernel

# How to install

First of all, you should install Jupyter. Then launch "./setup.py install" (sudo
privilegies might requried). If all goes fine, setup.py will find path,
where to specify Java kernel. (It calls "jupyter kernelspec list" 
and takes path from it.) If finding path fails, you'll be asked to specify
path manually (i.e. /usr/local/share/jupyter/kernels).


Files in repository include file ./target/jserver.jar . If you want to rebuild
it, run "mvn clean install" in directory java_kernel.

### Existing repos

 - https://github.com/scijava/scijava-jupyter-kernel — конвертит в js(_не ок_), но работает:)
 - https://github.com/Bachmann1234/java9_kernel — написан на python, не работает (The kernel as of now is not working. Unclear on why.)

 - https://github.com/imatlab/imatlab — matlab, написан на python
 
 - https://github.com/ligee/kotlin-jupyter — kotlin, написан на kotlin:)
