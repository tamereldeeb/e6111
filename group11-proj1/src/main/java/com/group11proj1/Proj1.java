package com.group11proj1;

import com.group11proj1.services.QueryService;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Arrays;

public class Proj1 {

    public static void main(String[] args) throws Exception {
        String accountKey = args[0];
        Double precisionTarget = Double.parseDouble(args[1]);

        String query = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");
        if (query.startsWith("'")) {
            query = query.substring(1);
        }
        if (query.endsWith("'")) {
            query = query.substring(0, query.length()-1);
        }

        QueryService service = new QueryService(accountKey);
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("transcript.txt"), "utf-8"));
            writer.write(System.lineSeparator());
            service.search(query, precisionTarget, writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {//ignore}
            }
        }
    }
}