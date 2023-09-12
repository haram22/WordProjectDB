package org.example;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
// ICRUD를 구현한 구현체가 WordCRUD
public class WordCRUD implements ICRUD{
    final String selectall = "select * from dictionary limit 5";
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
    @Override
    public Object add() {
        System.out.print("=> 난이도(1,2,3) & 새 단어 입력 : ");
        int level = s.nextInt();
        String word = s.nextLine().trim();

        System.out.print("뜻 입력 : ");
        String meaning = s.nextLine();

        return new Word(0, level, word, meaning);
    }
    public void addItem(){
        Word one = (Word)add();
        list.add(one);
        System.out.println(one);
        System.out.println("새 단어가 추가되었습니다.");
    }
    @Override
    public int update(Object obj) {
        return 0;
    }

    @Override
    public int delete(Object obj) {
        return 0;
    }

    @Override
    public void selectOne(int id) {}
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
