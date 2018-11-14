package xyz.mameng.tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Cmd {
    public String CMDCommand(String cmd){
        String result = null;
        String cmd1 = "cmd /c\"  "+cmd;
       // System.out.println(cmd);
        try {
            Process p = Runtime.getRuntime().exec(cmd1);
            BufferedReader	bufferedReader = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
         //       System.out.println(line);
                if (!line.equals("")) {
                    if (result == null || result.equals("")) {
                        result = line;
                    } else {
                        result = String.format("%s\n%s", result, line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

