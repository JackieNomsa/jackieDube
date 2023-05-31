package com.eviro.assessment.grad001.jackieDube;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

@Service
public class FileParserImp implements FileParser{
    List<List<String>> values = new ArrayList<List<String>>();
    private File dataFile = new File("src/main/resources/1672815113084-GraduateDev_AssessmentCsv_Ref003.csv");

    @Override
    public void parseCSV(File csvFile) {
        try {
            Connection connection = this.createConnection();
            this.createTable(connection);
            this.addDataToDB(connection,csvFile);

        }catch (SQLException | IOException e){
            System.out.println("Failed to create table");
        }
    }



    @Override
    public File convertCSVDataToImage(String base64ImageData) {
        byte[] imageData = Base64.getDecoder().decode(base64ImageData);
        return new File(Arrays.toString(imageData));
    }

    @Override
    public URI createImageLink(File fileImage) {
        String fileName = fileImage.getName();
        String fileAbsolutePath = fileImage.getAbsolutePath();

        URI fileLink = URI.create("<a href=\"file://" + fileAbsolutePath + "\">" + fileName + "</a>");
        return fileLink;
    }

    public Path getImageLink (String name, String surname){
        Path path = null;
        ResultSet d;
        String imageData = "";
        try{
            Connection connection = createConnection();
            this.createTable(connection);
            this.addDataToDB(connection,dataFile);

            Statement statement = connection.createStatement();
            String getTableRecord = "SELECT * FROM account_records";
            d = statement.executeQuery(getTableRecord);
            while (d.next()) {
                List<String> account = Arrays.asList(
                d.getString("name"),
                d.getString("surname"),
                d.getString("image_type"),
                d.getString("image_link"));
                values.add(account);
            }
            for (List<String> li:values) {
                if(Objects.equals(li.get(0), name) && Objects.equals(li.get(1), surname)){
                    imageData = li.get(3);
                    break;
                }
            }
            File image = convertCSVDataToImage(imageData);
            URI uri = createImageLink(image);
            path = Path.of(uri);

        }catch (IOException | SQLException e){
            System.out.println("Failed to connect to DB");
        }
        return path;
    }

        private Connection createConnection() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        Connection connection = DriverManager.getConnection(
                properties.getProperty("spring.datasource.url"),
                properties.getProperty("spring.datasource.username"),
                properties.getProperty("spring.datasource.password"));

        return  connection;
    }

    public void createTable(Connection connection) throws SQLException, IOException {
        Statement statement = connection.createStatement();
        String createTableQuery =
                "CREATE TABLE IF NOT EXISTS account_records (name TEXT," +
                        "surname TEXT, image_type TEXT, image_link TEXT)";
        statement.executeUpdate(createTableQuery);

    }

    public void addDataToDB(Connection connection,File csvFile) throws IOException, SQLException {

        FileReader filereader = new FileReader(csvFile);
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        List<String[]> allData = csvReader.readAll();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO account_records (name, surname, image_type, image_link) VALUES (?, ?, ?, ?)");
        for (String[] val : allData) {
            for (int i = 0; i < val.length; i++) {
                preparedStatement.setString(i + 1, val[i]);
            }
        }

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }



}
