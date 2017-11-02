from ipykernel.kernelbase import Kernel
#from IPython.kernel.zmq.kernelbase import Kernel
import re

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

def get_lexems(code):
    return re.findall(r'\w+', code);
    

class JavaKernel(Kernel):
    lexems = []
    
    #def __init__(self, profile_dir, log, session, stdin_socket, parent, shell_streams, iopub_socket, iopub_thread, user_ns): 
    #    lexems = []  
    #    pass
    
    implementation = 'Python'
    implementation_version = '1.0'
    language = 'Java'
    language_version = 'Java 9'
    language_info = {'mimetype': 'text/plain'}
    banner = "Java kernel for Jupyter"

    def do_execute(self, code, silent, store_history=True, user_expressions=None,
                   allow_stdin=False):
        self.lexems += get_lexems(code);
        self.lexems = list(set(self.lexems))
        if not silent:
            stream_content = {'name': 'stdout', 'text': "hello"}
            self.send_response(self.iopub_socket, 'stream', stream_content)

        return {'status': 'ok',
                # The base class increments the execution count
                'execution_count': self.execution_count,
                'payload': [],
                'user_expressions': {},
               }


    def do_complete(self, code, cursor_pos):

        #v = ["int aef_er = 10", "string cer = 'fdsf'", "float f565 = 4.567", "int i = 6", "int aef_er1 = 10"]
        
        v = self.lexems
            
        #v = match(v)
        code = last_word(code)
        v = search(v,code)

        content = {
            # The list of all matches to the completion request, such as
            # ['a.isalnum', 'a.isalpha'] for the above example.
            'matches' : v,

            # The range of text that should be replaced by the above matches when a completion is accepted.
            # typically cursor_end is the same as cursor_pos in the request.
            'cursor_start' : cursor_pos - len(code),
            'cursor_end' : cursor_pos,

            # Information that frontend plugins might use for extra display information about completions.
            'metadata' : {},

            # status should be 'ok' unless an exception was raised during the request,
            # in which case it should be 'error', along with the usual error message content
            # in other messages.
            'status' : 'ok'
        }

        return content
        
    def do_inspect(self, code, cursor_pos, detail_level=0):
        """Override in subclasses to allow introspection.
        """
        return {'status': 'ok', 'data': {code : "Oh, that's great command!"}, 'metadata': {}, 'found': True}
      	
    def do_history(self, hist_access_type, output, raw, session=None, start=None, stop=None, n=None, pattern=None, unique=False):
        """Override in subclasses to access history.
        """
        return {'status': 'ok', 'history': []}
        
    def do_shutdown(self, restart):
        """
        Override in subclasses to do things when the frontend shuts down the
        kernel.
        """
        return {'status': 'ok', 'restart': restart}

    def do_is_complete(self, code):
        """Override in subclasses to find completions.
        """
        return {'status' : 'complete', 'indent' : '!>##$$##>>'}
    
if __name__ == '__main__':
    from IPython.kernel.zmq.kernelapp import IPKernelApp
    IPKernelApp.launch_instance(kernel_class=JavaKernel)
