package com.thesis.diagramplugin.testClasses;

public class TestingKopenograms {
    private int testingReturnAndVeryVeryVeryExtremlyLongName(int a)
    {
        a = 2;
            do {
                if(true) {
                    break;
                }
            } while (true);
        if(a > 3)
        {
            return a +1;
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
            if(a > 1) {
                System.out.println("");
                break;
            }
        } while (true);

    }
    public void breaking()
    {
        while(true){
        break;}
    }

    public void switchTest() {
        int number = 2;

        switch (number) {
            case 1:
                System.out.println("Jedna");
                break;
            case 2:
                System.out.println("Dva");
                break;
            case 3:
                System.out.println("Tři");
                break;
            default:
                System.out.println("Neznámé číslo");
        }
    }


}
