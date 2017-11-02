import jdk.jshell.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class JShellWrapper {
    private JShell jShell;
    private SourceCodeAnalysis srcAnalyzer;



    static public void main(String[] args) {
        JShellWrapper js = new JShellWrapper();
        System.out.println(js.runCommand("3+;"));
    }
    public JShellWrapper() {
        jShell = JShell.builder().err(System.out).build();
        srcAnalyzer = jShell.sourceCodeAnalysis();
    }

    private List<SnippetEvent> eval(String command) {
        return jShell.eval(command + ";");
    }

    public String runCommand(String command) {

        SnippetEvent res = this.eval(command).get(0);

//        System.out.println(res.get(0).exception());
//        return res.toString();
        if (Snippet.Status.REJECTED.equals(res.status())) {
            List<Diag> diags = jShell.diagnostics(res.snippet()).collect(Collectors.toList());
            
//            String err = "";
            StringBuilder buf = new StringBuilder();
            for (Diag d: diags
                 ) {
                buf.append("Error :\n");
                buf.append(d.getMessage(Locale.US));
//                " ".repeat(3);
//                if (buf.append())
                buf.append("\n");
            }
            return buf.toString();
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
