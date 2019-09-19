import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DBUtil {
    public static void insertData(String path){
        String[] headers = {"代码", "名称", "2018名次", "2017名次", "2016名次", "2015名次"};
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers);
        try(FileReader fileReader = new FileReader(path)){
            CSVParser csvParser = new CSVParser(fileReader, csvFormat);
            List<CSVRecord> records = csvParser.getRecords();
            int length = records.size();
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:database/test.db");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test values (?, ?, ?, ?, ?, ?)");
            for(int i=1; i < length; i++){
                CSVRecord record = records.get(i);
                preparedStatement.setInt(1, Integer.valueOf(record.get("代码")));
                preparedStatement.setString(2, record.get("名称"));
                preparedStatement.setInt(3, Integer.valueOf(record.get("2018名次")));
                String level2017 = record.get("2017名次");
                String level2016 = record.get("2016名次");
                String level2015 = record.get("2015名次");
                if(level2017.equals("null")){
                    preparedStatement.setInt(4, -1);
                }else {
                    preparedStatement.setInt(4, Integer.valueOf(level2017));
                }
                if(level2016.equals("null")){
                    preparedStatement.setInt(5, -1);
                }else {
                    preparedStatement.setInt(5, Integer.valueOf(level2016));
                }
                if(level2015.equals("null")){
                    preparedStatement.setInt(6, -1);
                }else {
                    preparedStatement.setInt(6, Integer.valueOf(level2015));
                }
                preparedStatement.executeUpdate();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static List<String[]> readResultSet(ResultSet resultSet) throws Exception{
        List<String[]> list = new ArrayList<>();
        while (resultSet.next()){
            String[] line = new String[6];
            line[0] = String.valueOf(resultSet.getInt(1));
            line[1] = resultSet.getString(2);
            line[2] = String.valueOf(resultSet.getInt(3));
            line[3] = String.valueOf(resultSet.getInt(4));
            line[4] = String.valueOf(resultSet.getInt(5));
            line[5] = String.valueOf(resultSet.getInt(6));
            list.add(line);
        }
        resultSet.close();
        return list;
    }
    public static List<String[]> searchSchool(String name){
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:database/test.db");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM test WHERE name LIKE ?")){
            Class.forName("org.sqlite.JDBC");
            preparedStatement.setString(1, "%" + name + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            return readResultSet(resultSet);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static List<String[]> suggest(int level){
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:database/test.db");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM test WHERE level2018 > ? AND level2018 - ? < 1500 ORDER BY level2018 ASC ")){
            Class.forName("org.sqlite.JDBC");
            preparedStatement.setInt(1, level);
            preparedStatement.setInt(2, level);
            ResultSet resultSet = preparedStatement.executeQuery();
            return readResultSet(resultSet);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static void printResult(List<String[]> list){
        System.out.println("|代码|名称|2018名次|2017名次|2016名次|2015名次");
        for(String[] line : list){
            System.out.println(String.format("|%s|%s|%s|%s|%s|%s|", line[0], line[1], line[2], line[3], line[4], line[5]));
        }
    }
    public static void start(){
        Scanner sc = new Scanner(System.in);
        while (true){
            System.out.print("输入命令：1查询学校，2报考建议，0退出");
            int cmd = sc.nextInt();
            if(cmd==1){
                System.out.print("输入名称：");
                String name = sc.next();
                List<String[]> result = DBUtil.searchSchool(name);
                DBUtil.printResult(result);
            }else if(cmd==2){
                System.out.print("输入排名：");
                int level = sc.nextInt();
                List<String[]> result = DBUtil.suggest(level);
                DBUtil.printResult(result);
            }else {
                break;
            }
        }
    }
}
