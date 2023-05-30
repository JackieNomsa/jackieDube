package com.eviro.assessment.grad001.jackieDube;

import org.springframework.stereotype.Service;
import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

@Service
public class FileParserImp implements FileParser{
    List<String[]> values = new ArrayList<>();
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
        try{
            Connection connection = createConnection();
            this.createTable(connection);
            this.addDataToDB(connection,dataFile);

            Statement statement = connection.createStatement();
            String getTableRecord = "SELECT image_link FROM account_records WHERE name = "+
                    name +"AND surname = "+surname;
            path = (Path) statement.executeQuery(getTableRecord);
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
                "CREATE TABLE IF NOT EXISTS account_records (account_holder_name CHAR," +
                        " account_Holder_surname CHAR, http_image_link TEXT)";
        statement.executeUpdate(createTableQuery);
        connection.close();

    }

    public void addDataToDB(Connection connection,File csvFile) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
        String line;
        while ((line = reader.readLine()) != null) {
            values.add(line.split(","));
        }
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO account_records (name, surname, image_link) VALUES (?, ?, ?)");
        for (String[] val : this.values) {
            for (int i = 0; i < val.length-1; i++) {
                val[2] = String.valueOf(createImageLink(convertCSVDataToImage(val[2])));
                preparedStatement.setString(i + 1, val[i]);
            }
        }
        reader.close();
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }



}
