
import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis;

import java.util.ArrayList;
import java.util.List;

public class JShellWrapper {
    private JShell jShell;
    private SourceCodeAnalysis srcAnalyzer;



    static public void main(String[] args) {

        JShellWrapper js = new JShellWrapper();
        System.out.println(js.runCommand("3+4"));
        System.out.println();
        System.out.println(js.runCommand("int a = 4;"));
        System.out.println();
        System.out.println(js.runCommand("3+;"));
        System.out.println();
        //        System.out.println(js.runCommand("int b = a;;"));

    }
    public JShellWrapper() {
        jShell = JShell.builder().err(System.out).build();
        srcAnalyzer = jShell.sourceCodeAnalysis();
    }

    private List<SnippetEvent> eval(String command) {
        return jShell.eval(command + ";");
    }

    public String runCommand(String command) {

        List<SnippetEvent> res = this.eval(command);
//        try {
//            res = this.eval(command);
//        } catch (IllegalStateException e) {
//            System.out.println(e);
//            e.printStackTrace();
//
//            return "";
//        }

        System.out.println(res.get(0).exception());
        return res.toString();
//        if (Snippet.Status.REJECTED.equals(res.status())) {
//        }

//        return res.value();
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