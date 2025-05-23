package com.thesis.diagramplugin.testClasses;

public class TestingKopenograms extends Vehicle implements Iface{
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

    @Override
    public void hi() {

    }

    public boolean breakReturnAndContinue(int k)
    {
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                System.out.println("i=" + i + ", j=" + j);
                if (i * j == 4) {
                    System.out.println("Nalezena 4 – ukončuji oba cykly pomocí break s labelem");
                }
                else
                {
                    System.out.println("4 se zatím nenašla.");
                }
            }
        }

        for (int j = 1; j <= k; j++) {
            if (j % 2 == 0) {
                System.out.println("Číslo " + j + " je sudé, pokračuji pomocí continue");
                continue;
            }
            System.out.println("Číslo " + j + " je liché");
        }

        switch (k) {
            case 1:
                if(true)
                {
                    breakWithLabelStatement();
                    breakWithLabelStatemente();

                }
                break;
            case 2:
                System.out.println("Zvolil jsi 2");
                break;
            case 3:
                System.out.println("Zvolil jsi 3");
                break;
            default:
                System.out.println("Zvolil jsi něco jiného");
                break;
        }
        return false;
    }
    public static void countdown(int n) {
        if (n <= 0) {
            return;
        }
        System.out.println(n);
        countdown(n - 1);
    }
    public void ifElseIfElse()
    {
        int cislo = 0;
        if (cislo > 0) {
            // první větev: číslo je kladné
            System.out.println("Číslo je kladné");
        } else if (cislo < 0) {
            // druhá větev: číslo je záporné
            System.out.println("Číslo je záporné");
        } else {
            // fallback: číslo je nula
            System.out.println("Číslo je nula");
        }
    }

    public void tryCatchFinally()
    {
        String s = "ABC";  // špatný formát čísla

        try {
            int x = Integer.parseInt(s);            // může vyvolat NumberFormatException
            System.out.println("Parsed number: " + x);
        } catch (NumberFormatException e) {
            System.out.println("Chyba: '" + s + "' není platné číslo.");
        } finally {
            System.out.println("Konec pokusu o parsování.");
        }
    }



}
