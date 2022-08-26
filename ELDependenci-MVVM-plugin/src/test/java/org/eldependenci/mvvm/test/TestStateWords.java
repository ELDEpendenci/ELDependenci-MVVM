package org.eldependenci.mvvm.test;


public class TestStateWords {

    public static void main(String[] args){
        String a = "ChatList";
        String b = "InputMessages";

        String[] al = a.split("(?=\\p{Upper})");

        System.out.println(String.join(", ", al));

        String[] bl = b.split("(?=\\p{Upper})");

        System.out.println(String.join(", ", bl));

    }




}
