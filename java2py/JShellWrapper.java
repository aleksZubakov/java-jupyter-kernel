
import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis;


import java.util.ArrayList;
import java.util.List;


public class JShellWrapper {
    private JShell jShell;
    private SourceCodeAnalysis srcAnalyzer;


    public JShellWrapper() {
        jShell = JShell.create();
        srcAnalyzer = jShell.sourceCodeAnalysis();
    }

    public SnippetEvent eval(String command) {
        return jShell.eval(command + ";").get(0);
    }

    public String runCommand(String command) {
        SnippetEvent res = this.eval(command);

        if (Snippet.Status.REJECTED.equals(res.status())) {
            return "Evaluation failed: " + res.snippet().toString();
        }
        return res.value();
    }


    public List<String> getSuggestions(String code, int cursor) {
        List<String> res = new ArrayList<>();

        int ar[] = {0};
        for (SourceCodeAnalysis.Suggestion sug: srcAnalyzer.completionSuggestions(code, cursor, ar)
             ) {
            res.add(sug.continuation());
        }
        return res;
    }

    public boolean isComplete(String msg) {
        return srcAnalyzer.analyzeCompletion(msg).completeness().isComplete();
    }
}
