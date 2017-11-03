import jdk.jshell.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.toIntExact;

public class JShellWrapper {
    private JShell jShell;
    private SourceCodeAnalysis srcAnalyzer;

    static public void main(String[] args) {
        JShellWrapper js = new JShellWrapper();
        System.out.println(js.evalSnippet("char a = 'a';"));
        System.out.println(js.evalSnippet("int abc = 3; int bas = 3; String a = \";\"; char b = ';';"));
    }
    public JShellWrapper() {
        jShell = JShell.builder().err(System.out).build();
        srcAnalyzer = jShell.sourceCodeAnalysis();

    }

    private List<SnippetEvent> eval(String code) {
        return jShell.eval(code);
    }

    public String evalSnippet(String code) {
        String[] commands = code.split(";(?=([^\"]*\"[^\"]*\")*[^\"]*$)(?=[^'][^;][^'])");

        StringBuilder evalResult = new StringBuilder();
        for (String cmd: commands) {
            boolean ifErr[] = {false};
            String cmdResult = runCommand(cmd + ";", ifErr);
            if (ifErr[0]) {
                return cmdResult;
            }

            evalResult.append(cmdResult);
            evalResult.append("\n");
        }
        return evalResult.toString();
    }

    private String runCommand(String code, boolean ifErr[]) {
        SnippetEvent res = this.eval(code).get(0);

        String codeLines[] = code.split("\\r?\\n");

        if (Snippet.Status.REJECTED.equals(res.status())) {
            ifErr[0] = true;
            List<Diag> diags = jShell.diagnostics(res.snippet()).collect(Collectors.toList());

            StringBuilder errBuf = new StringBuilder();


            int lineNum = 0;
            long alreadyPassed = 0;
            int extraSymbols = 0;


            for (Diag d: diags
                 ) {
                errBuf.append("Error :\n");
                errBuf.append(d.getMessage(Locale.US));
                errBuf.append("\n");

                long cursorPosition = d.getPosition();
                while (alreadyPassed + codeLines[lineNum].length() < cursorPosition) {
                    alreadyPassed += codeLines[lineNum++].length();
                    extraSymbols++;
                }

                int lineCursorPosition = toIntExact(cursorPosition - alreadyPassed - extraSymbols);
                errBuf.append(codeLines[lineNum]);
                errBuf.append("\n");
                errBuf.append(String.join("", Collections.nCopies(Math.max(lineCursorPosition, 0), " ")));
                errBuf.append("^");

                if (d.getEndPosition() - cursorPosition > 1) {
                    errBuf.append(String.join("-", Collections.nCopies(toIntExact(d.getEndPosition() - cursorPosition) - 1, "")));
                    errBuf.append("^");
                }
                errBuf.append("\n");
            }
            return errBuf.toString();
        }

        return res.value();
    }

    public String getSuggestions(String code, int cursor) {
        StringBuilder buf = new StringBuilder();

        int ar[] = {1};
        List<SourceCodeAnalysis.Suggestion> suggestions = srcAnalyzer.completionSuggestions(code, cursor, ar);
      //   buf.append(ar[0]);
        for (SourceCodeAnalysis.Suggestion sug: suggestions
             ) {
            buf.append(sug.continuation());
            buf.append("\n");
        }
//        System.out.println(buf.toString());
        return buf.toString();
    }

    public boolean isComplete(String msg) {
        return srcAnalyzer.analyzeCompletion(msg).completeness().isComplete();
    }
}
