import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvBeanIntrospectionException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String filename = "data.csv";

        // Получим список сотрудников из csv-файла
        List<Employee> listOfEmployees = parseCSV(columnMapping, filename);
        // Получим строку в формате json
        String jsonStr = listToJSON(listOfEmployees);
        // Запишем её в файл
        writeString(jsonStr, "data.json");

        // Получим список сотрудников из xml-файла
        List<Employee> listOfEmployees2 = parseXML("data.xml");
        // Получим строку в формате json
        String jsonStr2 = listToJSON(listOfEmployees2);
        // Запишем в файл
        writeString(jsonStr2, "data2.json");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String filename) throws FileNotFoundException, IOException {
        List<Employee> staffList = new LinkedList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staffList = csv.parse();
        }
        catch(CsvBeanIntrospectionException e){
            e.printStackTrace();
        }
        return staffList;
    }

    public static List<Employee> parseXML(String filename) throws SAXException, ParserConfigurationException, IOException {
        List<Employee> staffList = new LinkedList<>();
        int employeeCount = 0;
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(filename));
        // Метод возвращает корневой элемент
        Node root = doc.getDocumentElement();
        // nodeList.Length = 5
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++){
            Node currentNode = nodeList.item(i);
            if (currentNode.ELEMENT_NODE == currentNode.getNodeType()){
                Element elem = (Element) currentNode;
                staffList.add(new Employee());
                NodeList nl;
                nl = elem.getElementsByTagName("id");
                staffList.get(employeeCount).id = Long.parseLong(nl.item(0).getTextContent());
                nl = elem.getElementsByTagName("firstName");
                staffList.get(employeeCount).firstName = nl.item(0).getTextContent();
                nl = elem.getElementsByTagName("lastName");
                staffList.get(employeeCount).lastName = nl.item(0).getTextContent();
                nl = elem.getElementsByTagName("country");
                staffList.get(employeeCount).country = nl.item(0).getTextContent();
                nl = elem.getElementsByTagName("age");
                staffList.get(employeeCount).age = Integer.parseInt(nl.item(0).getTextContent());
                employeeCount++;
            }
        }

        return staffList;
    }

    public static String listToJSON(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String jsonString, String newFileName) {
        try (FileWriter fw = new FileWriter(newFileName, false)){
            fw.write(jsonString);
            fw.flush();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
