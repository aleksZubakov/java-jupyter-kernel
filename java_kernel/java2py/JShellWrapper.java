import jdk.jshell.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

public class JShellWrapper {
    private JShell jShell;
    private SourceCodeAnalysis srcAnalyzer;



    static public void main(String[] args) {
        JShellWrapper js = new JShellWrapper();
        System.out.println(js.runCommand("3 >\na + a"));
        System.out.println(js.runCommand("3+;"));
        System.out.println(js.runCommand("10;"));
        System.out.println(js.runCommand("10"));
    }
    public JShellWrapper() {
        jShell = JShell.builder().err(System.out).build();
        srcAnalyzer = jShell.sourceCodeAnalysis();
    }

    private List<SnippetEvent> eval(String code) {
        return jShell.eval(code + ";");
    }

    public String runCommand(String code) {

        SnippetEvent res = this.eval(code).get(0);

        if (Snippet.Status.REJECTED.equals(res.status())) {
            List<Diag> diags = jShell.diagnostics(res.snippet()).collect(Collectors.toList());
            
            StringBuilder buf = new StringBuilder();


            int lineNum = 0;
            long alreadyPassed = 0;

            int extraSymbols = 0;

            String codeLines[] = code.split("\\r?\\n");

            for (Diag d: diags
                 ) {
                buf.append("Error :\n");
                buf.append(d.getMessage(Locale.US));
                buf.append("\n");

                long cursorPosition = d.getPosition();

                while (alreadyPassed + codeLines[lineNum].length() < cursorPosition) {
                    alreadyPassed += codeLines[lineNum++].length();
                    extraSymbols++;
                }

                int lineCursorPosition = toIntExact(cursorPosition - alreadyPassed - extraSymbols);
                buf.append(codeLines[lineNum]);
                buf.append("\n");
                buf.append(String.join("", Collections.nCopies(lineCursorPosition, " ")));
                buf.append("^");

                if (d.getEndPosition() - cursorPosition > 1) {
                    buf.append(String.join("", Collections.nCopies(toIntExact(d.getPosition() - cursorPosition - 1), " ")));
                    buf.append("^");
                }
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
