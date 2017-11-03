from ipykernel.kernelbase import Kernel
# from IPython.kernel.zmq.kernelbase import Kernel
import re
import time
import subprocess
import socket

from py4j.java_gateway import JavaGateway, GatewayParameters


class JavaKernel(Kernel):
    implementation = 'Python'
    implementation_version = '1.0'
    language = 'Java'
    language_version = 'Java 9'
    language_info = {'mimetype': 'text/plain'}
    banner = "Java kernel for Jupyter"

    def __init__(self, **kwargs):
        super(JavaKernel, self).__init__(**kwargs)
        port = 25222
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        while(True):
            result = sock.connect_ex(('127.0.0.1',port))
            if result != 0:
                break
            port += 1

        #self.__sp = subprocess.Popen("java -classpath bin:java2py/target/py4j-0.10.6.jar JavaBridge " + str(port),
        self.__sp = subprocess.Popen("java -classpath bin:target/jserver-jar-with-dependencies.jar JavaBridge " + str(port),
        shell = True)
        time.sleep(5)

        self.history = ['']
        self.__java_bridge = JavaGateway(gateway_parameters=GatewayParameters(port=port)) \
            .jvm.JShellWrapper()

    def __last_word(self, var):
        result = re.findall(r'\w+$', var)
        return result[0]

    def do_execute(self, code, silent, store_history=True, user_expressions=None,
                   allow_stdin=False):
        if not silent:
            self.history.append(code)
            self.history = (self.history)[-255:]
            if code == r'%h':
                stream_content = {'name': 'stdout', 'text': '\n'.join(self.history)}
            elif code == r'/vars':
                stream_content = {'name': 'stdout', 'text': self.__java_bridge.getVariables()}
            else:
                stream_content = {'name': 'stdout', 'text': self.__java_bridge.runCommand(code)}

            self.send_response(self.iopub_socket, 'stream', stream_content)

        return {'status': 'ok',
                'execution_count': self.execution_count,
                'payload': [],
                'user_expressions': {},
                }

    def do_is_complete(self, code):
        if self.__java_bridge.isComplete(code):
            return {"status": "complete"}
        else:
            return {"status": "incomplete", "indent": "  "}

    def do_complete(self, code, cursor_pos):
        mask = self.__last_word(code)
        v = self.__java_bridge.getSuggestions(code, cursor_pos).split("\n")[:-1]
        content = {
            'matches': v,
            'cursor_start': cursor_pos - len(mask),
            'cursor_end': cursor_pos,
            'metadata': {},
            'status': 'ok'
        }

        return content

    def do_shutdown(self, restart):
        self.__sp.kill()


if __name__ == '__main__':
    from ipykernel.kernelapp import IPKernelApp
    IPKernelApp.launch_instance(kernel_class=JavaKernel)
