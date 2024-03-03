package com.tcblauweiss.getraenkeabrechner.util;

import android.util.Log;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.bean.comparator.LiteralComparator;
import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;

public class CsvImporter {
    public static List<Member> importMembersFromCsv(String path){
        FileReader fileReader;
        List<Member> members;
        try {
             fileReader = new FileReader(new File(path));
        } catch (Exception e){
            Log.e("CsvImporter", "Error while reading file: " + e.getMessage());
            return members = new ArrayList<>();
        }
        members = new CsvToBeanBuilder<Member>( fileReader )
                .withType(Member.class)
                .withSeparator(';')
                .build()
                .parse();

        return members;
    }

    public static void exportEntriesToCsv(List<Entry> entries, String path){
        Writer writer;
        try{
        writer = new FileWriter(path);
            HeaderColumnNameMappingStrategy<Entry> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Entry.class);
            strategy.setColumnOrderOnWrite(new LiteralComparator<String>(new String[]{"ID", "DATUM", "NACHNAME", "VORNAME", "GETRAENK", "PREIS", "ANZAHL", "GESAMTPREIS"}));
            StatefulBeanToCsv<Entry> beanToCsv = new StatefulBeanToCsvBuilder<Entry>(writer)
                    .withSeparator(';')
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(entries);
            writer.close();
        } catch (Exception e){
            Log.e("CsvImporter", "Error while writing file: " + e.getMessage());
        }
    }
}
