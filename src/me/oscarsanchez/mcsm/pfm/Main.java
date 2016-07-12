package me.oscarsanchez.mcsm.pfm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.System.out;
import static java.lang.System.err;

/**
 * Created by osanchezmon on 9/7/16.
 */
public class Main {
    public static void main(String[] args) {
        StegoDouble number;

        Path file = FileSystems.getDefault().getPath("output.txt");
        Charset charset = Charset.forName("UTF-8");


        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            int i = 0;
            int j = i + 4;
            while (j < StegoDouble.TEST.length()) {
                number = new StegoDouble(Double.parseDouble((10+j) + ".79"));
                number.setPayload(StegoDouble.TEST.substring(i, j));

                // Write
                String s = number.getStegoNumber().toString();
                writer.write(s, 0, s.length());
                writer.newLine();

                //
                i += 4;
                j = i + 4;
            }
        } catch (IOException x) {
            err.format("IOException: %s%n", x);
        }

        // Read
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line = null;
            String word = null;
            StringBuilder message = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                // Reveal hide information
                word = StegoDouble.getPayload(Double.parseDouble(line));
                message.append(word);
                out.println(line + " --> " + word);
            }
            out.println("Hide message --> " + message.toString());
        } catch (IOException x) {
            err.format("IOException: %s%n", x);
        }
    }
}
