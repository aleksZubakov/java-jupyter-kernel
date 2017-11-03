import jdk.jshell.*;

import java.util.*;
import java.util.stream.*;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.toIntExact;

public class JShellWrapper {
    private JShell jShell;
    private SourceCodeAnalysis srcAnalyzer;



    static public void main(String[] args) {
        JShellWrapper js = new JShellWrapper();
        System.out.println(js.runCommand("3 >\na + a"));
        System.out.println(js.runCommand("3+;"));
        System.out.println(js.runCommand("int abc = 2;"));
        System.out.println(js.runCommand("int abe = 10;"));
        System.out.println(js.runCommand("int abo = 3;"));
        System.out.println(js.runCommand("abo"));
        String snippet = "ab";

        System.out.println(js.getSuggestions(snippet, 0));
    }
    public JShellWrapper() {
        jShell = JShell.builder().err(System.out).build();
        srcAnalyzer = jShell.sourceCodeAnalysis();
    }

    private List<SnippetEvent> eval(String code) {
        return jShell.eval(code + ";");
    }

    public String runCommand(String code) {

        SnippetEvent res= this.eval(code).get(0);
        String codeLines[] = code.split("\\r?\\n");
//        List<String> codeLines = new ArrayList<>();
//        StringBuilder buf = new StringBuilder();
//
//        for (String line: codeLines
//             ) {
//            if (this.isComplete(buf.toString())) {
//                codeLines.add(buf.toString());
//                buf.setLength(0);
//            }
//
//        }


        if (Snippet.Status.REJECTED.equals(res.status())) {
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
//                System.out.println();
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
        buf.append(ar[0]);
        for (SourceCodeAnalysis.Suggestion sug: suggestions
             ) {
            buf.append(sug.continuation());
            buf.append("\n");
        }
//        System.out.println(buf.toString());
        return buf.toString();
    }


    public String getVariables(){
      List<VarSnippet> vars = jShell.variables().collect(Collectors.toList());

      StringBuilder result = new StringBuilder();
      for (VarSnippet v: vars){
        result.append(v.typeName());
        result.append(" ");
        result.append(v.name());
        result.append(" = ");
        result.append(jShell.varValue​(v));
        result.append("\n");
      }
      return result.toString();
    }

    public String getMethods(){
      List<MethodSnippet> methods = jShell.methods().collect(Collectors.toList());
      StringBuilder result = new StringBuilder();

      for (MethodSnippet m: methods){
        result.append(m.name​());
        result.append(" ");
        result.append(m.signature​());
        result.append("\n");
      }
      return result.toString();
    }

    public boolean isComplete(String msg) {
        return srcAnalyzer.analyzeCompletion(msg).completeness().isComplete();
    }
}
