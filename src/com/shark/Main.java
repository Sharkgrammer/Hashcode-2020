package com.shark;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    static float bookCount, libCount, days;
    static boolean libProcessing;
    static library lowestLib = null;
    static float lastLibInd = Float.MAX_VALUE, indCurrent;
    static List<library> libs = new ArrayList<>();
    static List<library> processedLibs = new ArrayList<>();
    static List<books> processedBooks = new ArrayList<>();
    static List<score> bookScore = new ArrayList<>();



    public static void main(String[] args) {

        String fileName;

        try {
            fileName = "b_read_on.txt";
            /*

            fileName = "a_example.txt";
            fileName = "b_read_on.txt";
            fileName = "c_incunabula.txt";
            fileName = "d_tough_choices.txt";
            fileName = "e_so_many_books.txt";
            fileName = "f_libraries_of_the_world.txt";

            //*/

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

            String line = "";
            String[] datatemp = br.readLine().split(" ");
            String[] bookstemp = br.readLine().split(" ");


            bookCount = Integer.parseInt(datatemp[0]);
            libCount = Integer.parseInt(datatemp[1]);
            days = Integer.parseInt(datatemp[2]);

            int tempScore = 0;
            score score;
            for (int x = 0; x < bookstemp.length; x++) {
                tempScore = Integer.parseInt(bookstemp[x]);

                score = new score(tempScore);

                bookScore.add(score);
            }

            List<books> bookList;
            library library;
            books book;
            int b, si, sh, bookValue;
            String[] libTemp;
            int counter = 0;
            while ((line = br.readLine()) != null) {
                libTemp = line.split(" ");

                b = Integer.parseInt(libTemp[0]);
                si = Integer.parseInt(libTemp[1]);
                sh = Integer.parseInt(libTemp[2]);

                library = new library(b, si, sh);
                library.setLibID(counter++);

                line = br.readLine();

                bookstemp = line.split(" ");

                bookList = new ArrayList<>();
                score tempBookScore;
                boolean tempIsUsed;
                for (String x : bookstemp) {

                    bookValue = Integer.parseInt(x);

                    tempBookScore = bookScore.get(bookValue);
                    tempIsUsed = tempBookScore.isUsed();

                    book = new books(bookValue, tempBookScore.getScore(), tempIsUsed);

                    if (!tempIsUsed) {
                        bookScore.set(bookValue, tempBookScore);
                    }

                    bookList.add(book);
                }

                library.setBookList(bookList);

                libs.add(library);
            }

            checkSignedUp(1);
            boolean isBookSelected;

            for (int currentDay = 0; currentDay < days; currentDay++) {
                checkSignedUp(currentDay);
                getLowestLib(currentDay);

                books bookToProcess = null;
                score scoreToProcess = null;
                for (library l : processedLibs) {

                    for (int x = 0; x < l.getShipCount(); x++) {
                        isBookSelected = true;

                        if (l.getBookList().size() == 0) {
                            break;
                        }

                        try {
                            while (isBookSelected) {
                                bookToProcess = l.getMaxBook();

                                scoreToProcess = bookScore.get(bookToProcess.getBookID());

                                isBookSelected = scoreToProcess.isSelected();


                            }

                            scoreToProcess.setSelected(true);
                            bookScore.set(bookToProcess.getBookID(), scoreToProcess);
                            processedBooks.add(bookToProcess);
                            l.addBook(bookToProcess);

                        } catch (Exception e) {

                        }

                    }

                }
                //System.out.println((currentDay + 1) + " " + processedLibs.size() + " " + processedBooks.size());
            }


            int tempCOunt = 0;

            for (library l : processedLibs) {

                if (l.howManyBooksSent() == 0) {
                    tempCOunt++;
                }
            }

            //COMPILE ANSWER DATA
            System.out.println(processedLibs.size() - tempCOunt);

            for (library l : processedLibs){

                if (l.howManyBooksSent() == 0){
                    continue;
                }
                System.out.println(l.getLibID() + " " + l.howManyBooksSent());

                List<books> bList = l.getBookSentList();

                StringBuilder boutput = new StringBuilder();
                for (books bTemp :  bList){
                    boutput.append(bTemp.getBookID() + " ");
                }

                System.out.println(boutput.toString());

            }


            System.out.println();
            System.out.println();
            System.out.println();


            int checkCount = 0, yourScore = 0;
            for (score s : bookScore){

                checkCount += s.getValue();

            }
            for (books bs : processedBooks){
                yourScore += bs.getPoint();
            }
            System.out.println(yourScore + "/" + checkCount + "    diff: " + (checkCount - yourScore));



        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

    }

    public static void checkSignedUp(int currentDay) {
        if (lowestLib != null) {
            if (lowestLib.checkSignedUp()) {
                libProcessing = false;
                processedLibs.add(lowestLib);

                //System.out.println("check signed up ran on day " + (currentDay + 1));

                lowestLib = null;
                lastLibInd = Float.MAX_VALUE;
            } else {
                //System.out.println((currentDay + 1) + " " + lowestLib.getSignUp());
            }
        }
    }

    public static void getLowestLib(int currentDay) {
        if (!libProcessing) {

            for (library l : libs) {
                indCurrent = l.getIndicator();
                ;

                if (indCurrent < lastLibInd) {
                    lastLibInd = indCurrent;

                    lowestLib = l;
                }

            }

            if (lowestLib != null) {
                lowestLib.signUpLib();
                lowestLib.checkSignedUp();
                libs.remove(lowestLib);
                libProcessing = true;

                //checkSignedUp(currentDay);
            }
        }
    }
}


class library {

    private float bookCount;
    private float signUp;
    private float shipCount;
    private List<books> bookList;
    private List<books> bookSentList = new ArrayList<>();
    private boolean signedUp;
    private int totalBooksSent = 0;
    private int libID;

    public library(int b, int si, int sh) {
        this.bookCount = (float) b;
        this.signUp = (float) si;
        this.shipCount = (float) sh;
    }

    public int getBookCount() {
        return (int) bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = (float) bookCount;
    }

    public int getSignUp() {
        return (int) signUp;
    }

    public void setSignUp(int signUp) {
        this.signUp = (float) signUp;
    }

    public int getShipCount() {
        return (int) shipCount;
    }

    public void setShipCount(int shipCount) {
        this.shipCount = (float) shipCount;
    }

    public List<books> getBookList() {
        return bookList;
    }

    public books getMaxBook() {
        books tempBook = null;

        for (books b : bookList) {

            if (tempBook == null) {
                tempBook = b;
            }

            if (b.getPoint() > tempBook.getPoint()) {
                tempBook = b;
            }
        }

        bookList.remove(tempBook);

        return tempBook;
    }

    public void setBookList(List<books> bookList) {
        this.bookList = bookList;
    }

    public int getTotalValue() {
        int res = 0;
        for (books b : bookList) {
            res += b.getPoint();
        }

        return res;
    }

    public float getIndicator() {
        float signInd = (Main.days / signUp);
        float shipInd = (bookCount / shipCount);
        float totalInd = (float) getTotalValue();

        float res;
        res = (totalInd / shipInd) / signInd;

        return res;
    }

    public boolean isSignedUp() {
        return signedUp;
    }

    public void signUpLib() {
        signedUp = true;
    }

    public boolean checkSignedUp() {
        if (signedUp) {
            if (signUp > 0) {
                signUp--;

                return false;
            }

            return true;
        }

        return false;
    }

    public int howManyBooksSent(){

        return bookSentList.size();

    }

    public void addBook(books b){

        bookSentList.add(b);

    }

    public List<books> getBookSentList() {
        return bookSentList;
    }

    public void setBookSentList(List<books> bookSentList) {
        this.bookSentList = bookSentList;
    }

    public int getLibID() {
        return libID;
    }

    public void setLibID(int libID) {
        this.libID = libID;
    }
}

class books {

    private int bookID;
    private int point;
    private boolean used;

    public books(int b, int p, boolean u) {
        this.bookID = b;
        this.point = p;
        this.used = u;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public int getScore(){
        score current = Main.bookScore.get(this.bookID);

        if (current.isSelected()){
            return 0;
        }else{
            return getPoint();
        }

    }
}

class score {
    private int value;
    private boolean used;
    private boolean selected;

    public score(int v) {
        this.setValue(v);
        this.selected = false;
        this.used = false;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public int getScore() {

        if (!used) {
            used = true;
        }

        return value;

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
