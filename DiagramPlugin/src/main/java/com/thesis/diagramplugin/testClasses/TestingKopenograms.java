package com.thesis.diagramplugin.testClasses;

public class TestingKopenograms extends Vehicle{
    private int testingReturnAndVeryVeryVeryExtremlyLongName(int a)
    {
        if(a > 3)
        {
            a++;
            a++;
            return a+5;
        }
        else if(a == 3)
        {
            a++;
            a++;
            a++;
            a++;
            return a+2;
        }
        else if(a == 8)
        {
            a++;
            a++;
            a++;
            a++;
            return a+2;
        }
        else
        {
            return 1;
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
            return;
        }
        else if(a == 9){
            if(a + 1== 10 )
            {
                breakWithLabelStatemente();
            }
        }
        else
        {
            if(a == 0)
            {
                return;
            }
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

    public int abcd() {
        int a = 5;
        try {
            // Nějaký riskantní kód
            int[] pole = new int[5];
            System.out.println(pole[10]); // Vyvolá ArrayIndexOutOfBoundsException
        } catch (Exception e) {
            System.out.println("Nastala chyba: " + e.getMessage());
        }
        finally {
            System.out.println("ahoj");
            a++;
            if(a > 4)
            {
                a--;
            }
        }
        return 5;
    }

}
