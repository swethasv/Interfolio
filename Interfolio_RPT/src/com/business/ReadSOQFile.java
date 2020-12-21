package com.business;

import java.io.File;

public class ReadSOQFile {
	public static void main(String[] ar){
        File file = new File("\\\\Ccsoq3\\soqfiles$\\200804_ACCT_Fall_SOQ_Summary.pdf");
        System.out.println(file.getAbsolutePath());
      //followed by printing the contents of file   
    }
}
