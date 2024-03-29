/**
 * @author Palazuelos Alvarado Saul Alonso
 * @version 1.0
 */

import dataStructure.GraphT;
import dataStructure.NodeT;
import domain.model.Person;
import domain.model.Sex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Palazuelos Alvarado Saul Alonso
 * @version 1.0
 */

/**
 * Clase principal
 * Al iniciar el programa, lee el archivo de entrada
 * valida cada linea de manera independiente con regex
 * separa los elementos de informacion de cada linea
 * crea objetos, relaciones y busquedas en base a la los elementos de la informacion
 */
public class Main {
    private static GraphT<Person> graph;

    /**
     * Funcion de entrada principal
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        graph = new GraphT<Person>();

        int lineCounter = 1;

        FileReader fileReaderCatalog = new FileReader("src/data/inputCatalog");
        BufferedReader inCatalog = new BufferedReader(fileReaderCatalog);

        FileReader fileReaderQuestions = new FileReader("src/data/inputActions");
        BufferedReader inQuestions = new BufferedReader(fileReaderQuestions);

        String inLineData = "([{][\\s]*([A-z]+[[\\s]*[A-z]+]*)[\\s]*,[\\s]*([A-z]+[[\\s]*[A-z]+]*)[\\s]*,[\\s]*(M|F|U)[\\s]*,[\\s]*([0-9]{2}[/][0-9]{2}[/][0-9]{4})[\\s]*[}])";
        String pos = "([0-9]+)";
        String nivel = "([0-9]+)";

        String commandAtCenter = "(amigo|eliminar|amigos)";
        String commandAtStart = "(amigo)";

        Pattern dataPattern = Pattern.compile(inLineData);

        String lineCatalog;
        while ((lineCatalog = inCatalog.readLine()) != null) {
            String dataline = lineCatalog;
            Matcher m = dataPattern.matcher(dataline);

            if(m.find()){
                showMactherGroups(m);
                String firstName1 = m.group(2);
                String lastName1 = m.group(3);
                Sex sex1 = Sex.valueOf(m.group(4).toUpperCase());
                LocalDate date1 = parseStringDate(m.group(5));

                Person person = new Person(firstName1, lastName1, sex1, date1);
                graph.addNode(person);
            }
        }
        // graph.getNodes().forEach(node -> System.out.println(node));
        //todo change to 2 regex
        String stringPattern1 = "[\\s]*" + inLineData + "[\\s]*" + commandAtCenter + "[\\s]*" + inLineData + "(.*)";
        String stringPattern2 = "[\\s]*" + inLineData + "[\\s]*" + commandAtCenter + "[\\s]*" + pos + "(.*)";
        String stringPattern3 = "[\\s]*" + pos + "[\\s]*" + commandAtCenter + "[\\s]*" + inLineData + "(.*)";
        String stringPattern4 = "[\\s]*" + pos + "[\\s]*" + commandAtCenter + "[\\s]*" + pos + "(.*)";
        String stringPattern5 = "[\\s]*" + commandAtStart + "[\\s]*" + inLineData + "[\\s]*" + nivel + "(.*)";
        String stringPattern6 = "[\\s]*" + commandAtStart + "[\\s]*" + pos + "[\\s]*" + nivel + "(.*)";

        Pattern pattern1 = Pattern.compile(stringPattern1, Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile(stringPattern2, Pattern.CASE_INSENSITIVE);
        Pattern pattern3 = Pattern.compile(stringPattern3, Pattern.CASE_INSENSITIVE);
        Pattern pattern4 = Pattern.compile(stringPattern4, Pattern.CASE_INSENSITIVE);
        Pattern pattern5 = Pattern.compile(stringPattern5, Pattern.CASE_INSENSITIVE);
        Pattern pattern6 = Pattern.compile(stringPattern6, Pattern.CASE_INSENSITIVE);

        Pattern[] patterns = {pattern1, pattern2, pattern3, pattern4, pattern5, pattern6};

        String line;
        while ((line = inQuestions.readLine()) != null) {
            String dataline = line;

            Optional<Pattern> patternMatch = Arrays.stream(patterns)
                    .filter(pattern -> pattern.matcher(dataline).find())
                    .findFirst();

            if (patternMatch.isPresent()) {
                Pattern pattern = patternMatch.get();
                Matcher m = pattern.matcher(dataline);
                m.find();

                showMactherGroups(m);

                if(pattern.toString().equals(stringPattern1)){
                    processMatchedPattern1(m);
                }
                else if(pattern.toString().equals(stringPattern2)){
                    processMatchedPattern2(m);
                }
                else if(pattern.toString().equals(stringPattern3)){
                    processMatchedPattern3(m);
                }
                else if(pattern.toString().equals(stringPattern4)){
                    processMatchedPattern4(m);
                }
                else if(pattern.toString().equals(stringPattern5)){
                    processMatchedPattern5(m);
                }
                else if(pattern.toString().equals(stringPattern6)){
                    processMatchedPattern6(m);
                }
            }
        }

        graph.getNodes().forEach(node -> System.out.println(node));
    }

    private static void processAction(Person personA, Person personB, String action){
        NodeT<Person> NodeA = new NodeT<Person>(personA);
        NodeT<Person> NodeB = new NodeT<Person>(personB);

        switch (action){
            case "amigo":
                graph.setBiDirectionalHedge(NodeA,NodeB);
                break;
            case "eliminar":
                graph.unSetBiDirectionalHedge(NodeA,NodeB);
                break;
            case "amigos":
                graph.checkBidirectionalHedge(NodeA,NodeB);
                break;
        }
    }

    private static void processAction(Person person, String searchLevelString){
        NodeT<Person> node = new NodeT<Person>(person);
        int searchLevel = Integer.parseInt(searchLevelString);
        graph.executeSearchByLevel(node, searchLevel);
    }

    private static void processMatchedPattern1(Matcher m) {
        String firstName1 = m.group(2);
        String lastName1 = m.group(3);
        Sex sex1 = Sex.valueOf(m.group(4).toUpperCase());
        LocalDate date1 = parseStringDate(m.group(5));
        String command = m.group(6);
        Person personA = new Person(firstName1, lastName1, sex1, date1);
        String firstName2 = m.group(8);
        String lastName2 = m.group(9);
        Sex sex2 = Sex.valueOf(m.group(10).toUpperCase());
        LocalDate date2 = parseStringDate(m.group(11));
        Person personB = new Person(firstName2, lastName2, sex2, date2);

        System.out.println("Person ( " + firstName1 + " , " + lastName1 + " , " + sex1 + " , " + date1 + " )");
        System.out.println("Person ( " + firstName2 + " , " + lastName2 + " , " + sex2 + " , " + date2 + " )");

        processAction(personA, personB, command);
    }

    private static void processMatchedPattern2(Matcher m) {
        String firstNameA = m.group(2);
        String lastNameA = m.group(3);
        Sex sex1 = Sex.valueOf(m.group(4).toUpperCase());
        LocalDate dateA = parseStringDate(m.group(5));
        Person personA = new Person(firstNameA, lastNameA, sex1, dateA);
        String command = m.group(6);
        String posB = m.group(7);
        Person personB = graph.getInternalNodeByPositionInCollection(Integer.parseInt(posB)-1).getInstance();

        System.out.println("Person ( " + firstNameA + " , " + lastNameA + " , " + sex1 + " , " + dateA + " )");
        System.out.println("Person : pos " +  posB );

        processAction(personA, personB, command);
    }

    private static void processMatchedPattern3(Matcher m) {
        String posA = m.group(1);
        Person personA = graph.getInternalNodeByPositionInCollection(Integer.parseInt(posA)-1).getInstance();
        String command = m.group(2);
        String firstName2 = m.group(4);
        String lastName2 = m.group(5);
        Sex sex2 = Sex.valueOf(m.group(6).toUpperCase());
        LocalDate date2 = parseStringDate(m.group(7));
        Person personB = new Person(firstName2, lastName2, sex2, date2);

        System.out.println("Person : pos " +  posA );
        System.out.println("Person ( " + firstName2 + " , " + lastName2 + " , " + sex2 + " , " + date2 + " )");

        processAction(personA, personB, command);
    }

    private static void processMatchedPattern4(Matcher m) {
        String posA = m.group(1);
        Person personA = graph.getInternalNodeByPositionInCollection(Integer.parseInt(posA)-1).getInstance();
        String command = m.group(2);
        String posB = m.group(3);
        Person personB = graph.getInternalNodeByPositionInCollection(Integer.parseInt(posB)-1).getInstance();

        System.out.println("Person : pos " +  posA );
        System.out.println("Person : pos " +  posB );

        processAction(personA, personB, command);
    }

    private static void processMatchedPattern5(Matcher m) {
        String command = m.group(1);

        String firstName1 = m.group(3);
        String lastName1 = m.group(4);
        Sex sex1 = Sex.valueOf(m.group(5).toUpperCase());
        LocalDate date1 = parseStringDate(m.group(6));
        String level = m.group(7);

        System.out.println("Person ( " + firstName1 + " , " + lastName1 + " , " + sex1 + " , " + date1 + " )");
        System.out.println("Person : level " +  level );
    }

    private static void processMatchedPattern6(Matcher m) {
        String command = m.group(1);
        String pos = m.group(2);
        String level = m.group(3);

        System.out.println("Person : pos " +  pos );
        System.out.println("Person : level " +  level );
    }

    private static void showMactherGroups(Matcher m){
        System.out.println("--------");
        for (int i = 0; i <= m.groupCount(); i++) {
            System.out.println(i + " : " + m.group(i));
        }
    }

    private static LocalDate parseStringDate(String date){
        int day = Integer.parseInt(date.substring(0,2));
        int month = Integer.parseInt(date.substring(3,5));
        int year = Integer.parseInt(date.substring(6,10));
        return LocalDate.of(year, month, day);
    }
}
