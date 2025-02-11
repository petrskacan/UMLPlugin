package com.thesis.diagramplugin.testClasses;

public class TestingKopenograms {
    private int testingReturnAndVeryVeryVeryExtremlyLongName(int a)
    {
        Label:
        {
            do {
                break Label;
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
                break Label;
            } while (true);
        }
    }


}
