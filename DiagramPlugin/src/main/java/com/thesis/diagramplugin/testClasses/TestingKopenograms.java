package com.thesis.diagramplugin.testClasses;

public class TestingKopenograms {
    private int testingReturnAndVeryVeryVeryExtremlyLongName(int a)
    {
        Label:
        {
            do {
                break ;
            } while (true);
        }
        return a;
    }
    private void continueTest()
    {
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continue;
            }
            System.out.println("Number: " + i);
        }
    }
    public void breakWithLabelStatement()
    {
        Label:
        {
            do {
                if(true)
                break Label;
            } while (true);
        }
    }
    public void breakWithLabelStatemente() {
        int a = 10;
        do {
            System.out.println("TestingKopenograms.breakWithLabelStatemente");
            System.out.println("TestingKopenograms.breakWithLabelStatemente");
            System.out.println("TestingKopenograms.breakWithLabelStatemente");
            System.out.println("TestingKopenograms.breakWithLabelStatemente");
            break;
        } while (true);

    }
    public void breaking()
    {
        while(true){
        break;}
    }


}
