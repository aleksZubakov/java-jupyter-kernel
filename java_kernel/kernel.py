from ipykernel.kernelbase import Kernel
# from IPython.kernel.zmq.kernelbase import Kernel
import re

from py4j.java_gateway import JavaGateway, GatewayParameters

def match(var):
            res = []
            for v in var:
                find = re.findall(r'\w+', v)[1]
                res.append(find)
            return res


def search(var, mask):
    mask = "^"+mask
    res = []
    for v in var:
        find = re.search(mask, v)
        if find is not None:
            res.append(v)
    return res


def last_word(var):
    result = re.findall(r'\w+$', var)
    return result[0]


def generate(k,code):
    if k == 0:
        return [match([code])[0] + " ==> " + "44\n",1]
    if k == 1:
        return [match([code])[0] + " ==> " + "20.0\n",1]
    if k == 2:
        return [match([code])[0] + " ==> " + "'Hello'\n",1]
    if k == 3:
        return [match([code])[0] + " ==> " + "' world!'\n",1]
    if k == 4:
        return ["Hello world!\n", 0]
    if k == 5:
        res = ""
        for i in range(10):
            res += str(i) + '\n'
        return [res]


i = 0
names = []


class JavaKernel(Kernel):

    implementation = 'Python'
    implementation_version = '1.0'
    language = 'Java'
    language_version = 'Java 9'
    language_info = {'mimetype': 'text/plain'}
    banner = "Java kernel for Jupyter"

    def __init__(self, **kwargs):
        super(JavaKernel, self).__init__(**kwargs)

        self.__java_bridge = JavaGateway(gateway_parameters=GatewayParameters(port=25333))\
            .jvm.JShellWrapper()

    def do_execute(self, code, silent, store_history=True, user_expressions=None,
                   allow_stdin=False):
        # print(self.__java_bridge.runCommand("int a = 5"))
        if not silent:
            # global i
            # v = generate(i,code)
            stream_content = {'name': 'stdout', 'text': self.__java_bridge.runCommand("int a = 5")}
            # i += 1
            self.send_response(self.iopub_socket, 'stream', stream_content)

        return {'status': 'ok',
                # The base class increments the execution count
                'execution_count': self.execution_count,
                'payload': [],
                'user_expressions': {},
               }

    def do_complete(self, code, cursor_pos):

        v = ["test1", "test2"]
        # v = match(v)
        code = last_word(code)
        v = search(v, code)
        v = [code] + v

        content = {
            # The list of all matches to the completion request, such as
            # ['a.isalnum', 'a.isalpha'] for the above example.
            'matches': v,

            # The range of text that should be replaced by the above matches when a completion is accepted.
            # typically cursor_end is the same as cursor_pos in the request.
            'cursor_start': cursor_pos - len(code),
            'cursor_end': cursor_pos,

            # Information that frontend plugins might use for extra display information about completions.
            'metadata': {},

            # status should be 'ok' unless an exception was raised during the request,
            # in which case it should be 'error', along with the usual error message content
            # in other messages.
            'status': 'ok'
        }

        return content


if __name__ == '__main__':

    from ipykernel.kernelapp import IPKernelApp
    IPKernelApp.launch_instance(kernel_class=JavaKernel)
