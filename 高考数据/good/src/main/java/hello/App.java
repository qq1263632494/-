package hello;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Pattern;

class Tool{
    public static List<CSVRecord> readSchool(String year, String type){
        String[] headers = new String[]{"院校代号", "院校名称", "计划", "实际投档人数", "投档最低分","语文", "数学", "外语"};
        String csvPath = "data/" + year + type + ".csv";
        CSVFormat format = CSVFormat.DEFAULT.withHeader(headers);
        List<CSVRecord> records = null;
        try(FileReader fileReader = new FileReader(csvPath)){
            records = new CSVParser(fileReader, format).getRecords();
        }catch (Exception e){
            e.printStackTrace();
        }
        return records;
    }
    public static List<CSVRecord> readGrade(String year, String type){
        String[] headers = new String[]{"分数", "考生人数"};
        String csvPath = "data/" + year + "分" + type + ".csv";
        CSVFormat format = CSVFormat.DEFAULT.withHeader(headers);
        List<CSVRecord> records = null;
        try(FileReader fileReader = new FileReader(csvPath)){
            records = new CSVParser(fileReader, format).getRecords();
        }catch (Exception e){
            e.printStackTrace();
        }
        return records;
    }
    public static void writeResult(List<String[]> data, String fileName){
        CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator("\n");
        try(FileWriter fileWriter = new FileWriter(fileName)){
            CSVPrinter printer = new CSVPrinter(fileWriter, format);
            for(String[] line : data){
                printer.printRecord(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void merge(String y){
        Tool t = new Tool();
        List<CSVRecord> levels = t.readGrade(y, "文");
        List<CSVRecord> schools = t.readSchool(y, "文");
        List<String[]> l = new ArrayList<>();
        String[] s = new String[4];
        s[0] = "代码";
        s[1] = "名称";
        s[2] = "分数";
        s[3] = "排名";
        l.add(s);
        int len_levels = levels.size();
        int len_schools = schools.size();
        System.out.println(levels.get(70).get("分数").equals( schools.get(1).get("投档最低分")));
        for(int i=1; i<len_schools; i++){
            String[] m = new String[4];
            m[0] = schools.get(i).get("院校代号");
            m[1] = schools.get(i).get("院校名称");
            m[2] = schools.get(i).get("投档最低分");
            for(int j=1; j<len_levels; j++){
                if(levels.get(j).get("分数").equals(m[2])){
                    m[3] = levels.get(j).get("考生人数");
                    break;
                }else {
                    m[3] = "FAIL";
                }
            }
            l.add(m);
        }
        t.writeResult(l, y + ".csv");
    }
}
class NTool{
    public static List<CSVRecord> readCSV(String year){
        String[] headers = {"代码", "名称", "分数", "排名"};
        CSVFormat format = CSVFormat.DEFAULT.withHeader(headers);
        List<CSVRecord> records = null;
        try(FileReader fileReader = new FileReader(year + ".csv")){
            records = new CSVParser(fileReader, format).getRecords();
        }catch (Exception e){
            e.printStackTrace();
        }
        return records;
    }
}
class School implements Comparable{
    public String code;
    public String name;
    public String level2018;
    public String level2017;
    public String level2016;
    public String level2015;
    public School(String code, String name, String level2018, String level2017, String level2016, String level2015){
        this.code = code;
        this.name = name;
        this.level2018 = level2018;
        this.level2017 = level2017;
        this.level2016 = level2016;
        this.level2015 = level2015;
    }
    public School(CSVRecord record){
        this.code = record.get("代码");
        this.name = record.get("名称");
        this.level2018 = record.get("2018名次");
        this.level2017 = record.get("2017名次");
        this.level2016 = record.get("2016名次");
        this.level2015 = record.get("2015名次");
    }
    public int compareTo(Object obj){
        return Integer.valueOf(this.level2018) - Integer.valueOf(((School)obj).level2018);
    }
    public String toString(){
        return "|" + code + "|" + name + "|" + level2018 + "|" + level2017 + "|" + level2016 + "|" + level2015;
    }
}
public final class App {
    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws Exception{
        String[] headers = {"代码", "名称", "2018名次", "2017名次", "2016名次", "2015名次"};
        CSVFormat format = CSVFormat.DEFAULT.withHeader(headers);
        try(FileReader fileReader = new FileReader("final.csv")){
            CSVParser parser = new CSVParser(fileReader, format);
            List<CSVRecord> records = parser.getRecords();
            Scanner sc = new Scanner(System.in);
            while(true){
                System.out.println("输入命令：1查询学校，2估计学校，0退出");
                int cmd = sc.nextInt();
                if(cmd==1){
                    System.out.print("输入学校：");
                    String schoolName = sc.next();
                    Pattern pattern = Pattern.compile("\\S*"+schoolName+"\\S*");
                    for(int i=1; i<records.size(); i++){
                        if(pattern.matcher(records.get(i).get("名称")).matches()){
                            System.out.println("|"+records.get(i).get("代码")+"|"+records.get(i).get("名称")+"|"+records.get(i).get("2018名次")+"|"+records.get(i).get("2017名次")+"|"+records.get(i).get("2016名次")+"|"+records.get(i).get("2015名次"));
                        }
                    }
                }else if(cmd==2){
                    System.out.print("输入名次：");
                    int level = sc.nextInt();
                    List<School> l = new ArrayList<>();
                    for(int i=1; i<records.size(); i++){
                        try{
                        if(Integer.valueOf(records.get(i).get("2018名次"))>=level){
                            l.add(new School(records.get(i)));
                        }}
                        catch (Exception e){
                            System.out.println(records.get(i).get("2018名次"));
                            System.out.println(i);
                        }
                    }
                    Collections.sort(l);
                    System.out.println("|招生代码|学校名称|2018年排名|2017年排名|2016年排名|2015年排名");
                    for(School s:l){
                        System.out.println(s);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
