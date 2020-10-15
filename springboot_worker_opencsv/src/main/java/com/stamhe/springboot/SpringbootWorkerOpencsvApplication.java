package com.stamhe.springboot;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class SpringbootWorkerOpencsvApplication implements CommandLineRunner {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringbootWorkerOpencsvApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        /*
        String csvFile = "/Users/hequan/Downloads/202001.csv";
    
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            while ((line = reader.readNext()) != null) {
//                System.out.println("Country [id= " + line[0] + ", code= " + line[1] + " , name=" + line[2] + "]");
                Arrays.asList(line).stream().forEach(System.out::println);
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    
        String fileName = "/Users/hequan/Downloads/202001.csv";
        Path myPath = Paths.get(fileName);
    
        CSVParser parser = new CSVParserBuilder().withSeparator('\t').build();
    
        try (BufferedReader br = Files.newBufferedReader(myPath,  StandardCharsets.UTF_8);
             CSVReader reader = new CSVReaderBuilder(br).withCSVParser(parser)
                     .build()) {
            
            int count = 0;
            String[] line;
            while ((line = reader.readNext()) != null) {
//                System.out.println(line[0]);
//                String[] s = line[0].split(" ");
//                Arrays.asList(s).stream().forEach(System.out::println);

//                Arrays.asList(line).stream().forEach(System.out::println);
                System.out.println(line[4]);
                count++;
                if(count > 10) {
                    System.exit(0);
                }
            }
        }
    }
}
