package com.bwz6jk2227esl89ahj34;

public class Main {

    public static void main(String[] args) {
	  CLI cli = new CLI();
        Option helpOption = new HelpOption();
        Option lexOption = new LexOption();
        cli.execute(args);

    }

}
