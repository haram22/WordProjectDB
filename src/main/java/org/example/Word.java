package org.example;

// data를 다루기 위해 만든 클래스
// 단어들에 대한 변수를 만들어둔다.
public class Word {
    private int id; // member 변수 접근을 제한하기 위해 private으로 만들어둠.
    private int level;
    private String word;
    private String meaning;

    // 생성자 만들기 - 기본형
    Word(){} // 기본형
    // 파라미터를 모두 가지고 있는 생성자
    Word(int id, int level, String word, String meaning){
        this.id = id;
        this.level = level;
        this.word = word;
        this.meaning = meaning;
    }

    // 외부에서 private 데이터에 접근하게 하기 위해 getter와 setter를 만든다.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    @Override
    public String toString() {
        String slevel = "";
        for(int i=0; i<level; i++) slevel += "*";
        String str = String.format("%-3s", word) // 왼쪽 정렬
                + String.format("%15s", word) + "  " + meaning; //15칸 오른쪽 정렬
        return str;
    }
}
