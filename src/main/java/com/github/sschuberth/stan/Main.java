package com.github.sschuberth.stan;

import com.github.sschuberth.stan.data.BookingItem;
import com.github.sschuberth.stan.data.Statement;
import com.github.sschuberth.stan.parsers.PostbankPDFParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.nio.file.Files.newDirectoryStream;

public class Main {
    public static void main(String[] args) {
        List<Statement> statements = new ArrayList<>();

        for (String arg : args) {
            File file = new File(arg);

            try (DirectoryStream<Path> stream = newDirectoryStream(file.getParentFile().toPath(), file.getName())) {
                stream.forEach(filename -> {
                    try {
                        Statement st = PostbankPDFParser.parse(filename.toString());
                        System.out.println("Successfully parsed statement '" + file.getName() + "' dated from " + st.fromDate + " to " + st.toDate + ".");
                        statements.add(st);
                    } catch (ParseException e) {
                        System.err.println("Error parsing '" + filename + "'.");
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                System.err.println("Error opening '" + arg + "'.");
            }
        }

        Collections.sort(statements);

        Iterator<Statement> statementIterator = statements.iterator();
        if (!statementIterator.hasNext()) {
            System.err.println("No statements found.");
            System.exit(1);
        }

        Statement curr = statementIterator.next(), next;
        while (statementIterator.hasNext()) {
            next = statementIterator.next();

            if (!curr.toDate.plusDays(1).equals(next.fromDate)) {
                System.err.println("Statements '" + curr.filename + "' and '" + next.filename + "' are not consecutive.");
                System.exit(1);
            }

            if (curr.balanceNew != next.balanceOld) {
                System.err.println("Balances of statements '" + curr.filename + "' and '" + next.filename + "' are not consistent.");
                System.exit(1);
            }

            curr = next;
        }

        statementIterator = statements.iterator();
        while (statementIterator.hasNext()) {
            System.out.println("[");

            Iterator<BookingItem> bookingItemIterator = statementIterator.next().bookings.iterator();
            while (bookingItemIterator.hasNext()) {
                BookingItem item = bookingItemIterator.next();
                System.out.println(item + (bookingItemIterator.hasNext() ? "," : ""));
            }

            System.out.println("]");
        }
    }
}