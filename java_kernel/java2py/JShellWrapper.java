import jdk.jshell.*;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.*;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.toIntExact;

public class JShellWrapper {
    private JShell jShell;
    private ByteArrayOutputStream outStream;
    private SourceCodeAnalysis srcAnalyzer;

    public JShellWrapper() {
        outStream = new ByteArrayOutputStream();
        PrintStream prtStr = new PrintStream(outStream);
        jShell = JShell.builder().out(prtStr).err(prtStr).build();
        srcAnalyzer = jShell.sourceCodeAnalysis();

    }

    private List<SnippetEvent> eval(String code) {
        return jShell.eval(code);
    }

    public String evalSnippet(String code) {
        String[] commands = code.split(";(?=([^\"]*\"[^\"]*\")*[^\"]*$)(?=[^'][^;][^'])");

        StringBuilder evalResult = new StringBuilder();
        StringBuilder tmpCmd = new StringBuilder();
        List<String> comamnds = new ArrayList<>();

        for (String cmd: commands) {
            boolean ifErr[] = {false};

            tmpCmd.append(cmd);
            if (!this.isComplete(tmpCmd.toString())) {
                tmpCmd.append(";");
                continue;
            }

            if (!tmpCmd.toString().endsWith(";"))
                tmpCmd.append(";");

            String cmdResult = runCommand(tmpCmd.toString(), ifErr);
            if (ifErr[0]) {
                return cmdResult;
            }

            evalResult.append(outStream.toString());
            outStream.reset();
            tmpCmd.setLength(0);
            if (cmdResult.length() > 0) {
                evalResult.append(cmdResult);
                evalResult.append("\n");
            }
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
        for (SourceCodeAnalysis.Suggestion sug: suggestions
             ) {
            buf.append(sug.continuation());
            buf.append("\n");
        }
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
