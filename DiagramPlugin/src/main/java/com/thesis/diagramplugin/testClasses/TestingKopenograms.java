package com.thesis.diagramplugin.testClasses;

public class TestingKopenograms {
    private int testingReturnAndVeryVeryVeryExtremlyLongName(int a)
    {
        a = 2;
        if(a < 1) {
            do {
                for (int i = 1; i <= 10; i++) {
                    if (i % 2 == 0) {
                        continue;
                    }
                    System.out.println("Number: " + i);
                }
                if (a > 7) {
                    break;
                }
            } while (true);
            Label:
            {
                do {
                    if (true)
                        break Label;
                } while (true);
            }
        }
        if(a > 3)
        {
            return a+5;
        }
        else
        {
            a++;
            return a+2;
        }

    }
    private void continueTest(int a)
    {
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continueTest(2);
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

        if(a >= 10)
        {
        }
        else {
            a++;
            breakWithLabelStatemente();
        }

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

    public int abcd()
    {
        return 5;
    }

}
