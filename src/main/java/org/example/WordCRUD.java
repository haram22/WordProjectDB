package org.example;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
// ICRUD를 구현한 구현체가 WordCRUD
public class WordCRUD implements ICRUD{
    final String selectall = "select * from dictionary";
    // 각 데이터는 순서대로 들어간다.
    final String WORD_INSERT = "insert into dictionary (level, word, meaning, regdate)" + "values (?,?,?,?) ";
    final String WORD_UPDATE = "update dictionary set meaning=? where id=? ";
    ArrayList<Word> list;
    Scanner s;
    final String fname = "Dictionary.txt";
    Connection conn;
    WordCRUD(Scanner s) {
        list = new ArrayList<>();
        this.s = s;
        // getConnection 함수 호출
        conn = DBConnection.getConnection();
    }
    // query문을 만들어야 함
    public void loadData() throws SQLException {
        list.clear(); // 기존에 있던 리스트 초기화

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(selectall);
        while(true){
            if(!rs.next()) break; //다음 데이터가 없다면 break
            int id = rs.getInt("id");
            int level = rs.getInt("level");
            String word = rs.getString("word");
            String meaning = rs.getString("meaning");
            list.add(new Word(id, level, word, meaning));
        }
        rs.close(); // 반복문을 나왔다는 것은 데이터를 모두 로드했다는 뜻이므로 닫아줘도 된다.
        stmt.close();
    }
    // 현재 날짜 가져오기
    public String getCurrentDate(){
        LocalDate now = LocalDate.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return f.format(now);
    }
    @Override
    public int add(Word one) {
        int retval = 0;
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(WORD_INSERT);
            pstmt.setInt(1, one.getLevel());
            pstmt.setString(2, one.getWord());
            pstmt.setString(3, one.getMeaning());
            pstmt.setString(4, getCurrentDate());
            retval = pstmt.executeUpdate();
            pstmt.close(); // 클래스 종료
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return retval;
    }
    public void addItem(){
        System.out.print("=> 난이도(1,2,3) & 새 단어 입력 : ");
        int level = s.nextInt();
        String word = s.nextLine().trim();

        System.out.print("뜻 입력 : ");
        String meaning = s.nextLine();

        Word one = new Word(0, level, word, meaning);
//        list.add(one);
        int retval = add(one);
        if (retval > 0) {
            System.out.println(one);
            System.out.println("새 단어가 추가되었습니다.");
        } else {
            System.out.println("단어 추가 중 에러 발생 ⚠️");
        }
    }

    public void listAll() {
        try {
            loadData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("------------------------------------");
        for(int i=0; i<list.size(); i++){
            System.out.print((i+1) + " ");
            System.out.println(list.get(i).toString());
        }
        System.out.println("------------------------------------");
    }
    public ArrayList<Integer> listAll(String keyword) {
        ArrayList<Integer> idlist = new ArrayList<>();
        int j= 0;
        System.out.println("------------------------------------");
        for(int i=0; i<list.size(); i++){
            String word = list.get(i).getWord();
            if(!word.contains(keyword)) continue;
            System.out.print((j+1) + " ");
            System.out.println(list.get(i).toString());
            idlist.add(i);
            j++;
        }
        System.out.println("------------------------------------");
        return idlist;
    }
    public void listAll(int level){
        int j= 0;
        System.out.println("------------------------------------");
        for(int i=0; i<list.size(); i++){
            int ilevel = list.get(i).getLevel();
            if(ilevel != level) continue;
            System.out.print((j+1) + " ");
            System.out.println(list.get(i).toString());
            j++;
        }
        System.out.println("------------------------------------");
    }

    public void updateItem() {
        System.out.print("=> 수정할 단어 검색 : ");
        String keyword = s.next();
        ArrayList<Integer> idlist = this.listAll(keyword);
        System.out.print("=> 수정할 번호 선택 : ");
        int id = s.nextInt();
        s.nextLine();
        System.out.print("=> 뜻 입력 : ");
        String meaning = s.nextLine();
        Word word = list.get(idlist.get(id-1));
        word.setMeaning(meaning);
        System.out.println("단어가 수정 되었습니다. ");
    }

    public void deleteItem() {
        System.out.print("=> 삭제할 단어 검색 : ");
        String keyword = s.next();
        ArrayList<Integer> idlist = this.listAll(keyword);
        System.out.print("=> 삭제할 번호 선택 : ");
        int id = s.nextInt();
        s.nextLine();

        System.out.print("=> 정말로 삭제하시겠습니까?(Y/n) : ");
        String ans = s.nextLine();
        if (ans.equalsIgnoreCase("y")) {
            list.remove((int)idlist.get(id-1));
            System.out.println("단어가 삭제되었습니다. ");
        } else
            System.out.println("취소되었습니다. ");
    }
//    public void loadFile() {
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(fname));
//            String line;
//            int count = 0;
//
//            while (true) {
//                line = br.readLine();
//                if (line == null) break;
//                String data[] = line.split("\\|");
//                int level = Integer.parseInt(data[0]);
//                String word = data[1];
//                String meaning = data[2];
//                list.add(new Word(0, level, word, meaning));
//                count++;
//            }
//            br.close();
//            System.out.println("==> " + count + "개 데이터 로딩 완료 !");
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void saveFile() {
        try {
            PrintWriter pr = new PrintWriter(new FileWriter(fname));
            for(Word one : list) {
                pr.write(one.toFileString() + "\n");
            }
            pr.close();
            System.out.println("==> 데이터 저장 완료 !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchLevel() {
        System.out.print("=> 원하는 레벨은? (1~3) : ");
        int level = s.nextInt();
        listAll(level);
    }

    public void searchWord() {
        System.out.print("=> 원하는 단어는? ");
        String keyword = s.next();
        listAll(keyword);
    }


}
