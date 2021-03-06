/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentinel.Engines;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sentinel.Security.Security;
import sentinel.Utility.ToolBox;

/**
 *
 * @author Shalin Kulawardane
 */
public class Affire extends Thread {
    private final char[] PASSWORD;
    private final int PASSWORD_LENGTH = 15;
    private final String FIRST_NAME, LAST_NAME, USERNAME, TEST_TYPE;
    public static final int QUICK_TEST_COUNT = 8, DEEP_TEST_COUNT = 11;
    
    public Affire(char[] passwordToTest, String firstName, String lastName, String username, String testType) { 
        this.PASSWORD = passwordToTest; 
        this.FIRST_NAME = firstName;
        this.LAST_NAME = lastName;
        this.USERNAME = username;
        this.TEST_TYPE = testType;
        
    }
        
    public static File getPath(String fileName) {   
        switch(fileName.toLowerCase()) {            
            case "rockyou":
                return new File("src\\Assets\\PasswordTesting\\rockyou.txt");
            
            case "wordlist":
                return new File("src\\Assets\\PasswordTesting\\WordList.txt");
        
        }
        
        return null;
        
    }
    
    public ArrayList<Boolean> characterCheck() {
        /*  This method will check if a password has digits, special characters, 
            uppercase characters and lowercase characters. 
            It uses regular expressions to look for such patterns.
        
            It will return a boolean array. A boolean value at a specific index 
            is determined by a successful match with an appropriate pattern. 
            If a value of false is provided, then the password failed that 
            part of the test.
        
            The meaning of each position:
            index 0: has digits.
            index 1: has lowercase characters.
            index 2: has uppercase characters.
            index 3: has special characters.
        */
        
        ArrayList<Boolean> result = new ArrayList<>();
        final String[] PATTERNS = {".*\\d.*", ".*[a-z]+.*", ".*[A-Z]+.*", ".*[\\W_]+.*"};
        Pattern pattern;
        Matcher matcher;        
        
        for (String expression : PATTERNS) {
            pattern = Pattern.compile(expression);
            matcher = pattern.matcher(new String(this.PASSWORD));
            result.add(matcher.matches());
        
        }        
        
        return result;
        
    }

    public static boolean characterCheck(String username, String password) {
        /*  This method will check if a password has digits, special characters, 
            uppercase characters and lowercase characters. 
            It uses regular expressions to look for such patterns.
        
            It will return a boolean array. A boolean value at a specific index 
            is determined by a successful match with an appropriate pattern. 
            If a value of false is provided, then the password failed that 
            part of the test.
        
            The meaning of each position:
            index 0: has digits.
            index 1: has lowercase characters.
            index 2: has uppercase characters.
            index 3: has special characters.
        */
        
        boolean[] result = new boolean[4];
        final String[] PATTERNS = {".*\\d.*", ".*[a-z]+.*", ".*[A-Z]+.*", ".*[\\W_]+.*", ".*" + username +".*"};
        Pattern pattern;
        Matcher matcher;        
        
        for (int i = 0; i < result.length; i++) {
            pattern = Pattern.compile(PATTERNS[i]);
            matcher = pattern.matcher(password);
            result[i] = matcher.matches();

        }        

        return result[0] && result[1] && result[2] && result[3];

    }

    public ArrayList<Boolean> compositionCheck() {
        /*  This method will check if a password contains 
            a person's first name, last name and username.
            It will return false if it detected any one of the 
            factors (this means it did not fully pass the test)
        */
    
        ArrayList<Boolean> result = new ArrayList<>();
        final String[] PATTERNS = {
            ".*" + this.FIRST_NAME.toLowerCase() + ".*", 
            ".*" + this.LAST_NAME.toLowerCase() + "+.*", 
            ".*" + this.USERNAME.toLowerCase() + "+.*" 
        };
        Pattern pattern;
        Matcher matcher;        
        
        for (String expression : PATTERNS) {
            pattern = Pattern.compile(expression);
            matcher = pattern.matcher(new String(this.PASSWORD).toLowerCase());
            result.add(!matcher.matches());

        }        

        return result;
                
    }
    
    public boolean executePasswordSprayingAtack() {
        /*  It will brute force a password using breached passwords
            Will return false if the password was cracked
        */   
        
        try (Scanner scanner = new Scanner(new FileInputStream(getPath("rockyou")))) {
            while (scanner.hasNextLine()) { if(Arrays.equals(this.PASSWORD, 
                    scanner.nextLine().toCharArray())){return false; } }
            
        }  catch (FileNotFoundException ex) { ToolBox.triggerException(ex); }

        return true;

    }
    
    public boolean executeDictionaryBasedBruteForceAttack() {
        /*  It will perform a dictionary-based brute force attack and will 
            return false if the password was cracked.
        */
        
        try(Scanner scanner = new Scanner(new FileInputStream(getPath("wordlist")))) {
            while(scanner.hasNextLine()) { if(Arrays.equals(this.PASSWORD,  scanner.nextLine().toCharArray())) 
            { return false; } }

        } catch (FileNotFoundException ex) { ToolBox.triggerException(ex); }

        return true;

    }
    
    public boolean consecutiveNumbersCheck() {
        /*  This method checks if a password has consecutive numbers. If the password
            passes this test (it does not have consecutive numbers), 
            it will return true, otherwise false.
        */

        boolean foundNum;
        
        for(int i = 0; i < this.PASSWORD.length; i++) {
            int num1 = 0;                        
            foundNum = (Character.isDigit(this.PASSWORD[i]) && i != this.PASSWORD.length-1);

            if(foundNum) { num1 = (int)this.PASSWORD[i]; }
            
            if(foundNum && Character.isDigit(this.PASSWORD[i + 1])) {
                int num2 = (int)this.PASSWORD[i + 1];
                return !(num2 == num1 + 1);
                
            }
            
        }        

        return true;

    }
    
    public boolean checkLength() { return this.PASSWORD.length >= this.PASSWORD_LENGTH; }
    
    public ArrayList<Boolean> startQuickTest() {        
        /*  This method will evaluate th strength of the password by performing various tests.
            It will return an array list of boolean values. Each position in the array list represents a test, and 
            the boolean value at that position denotes the result of that test.
        
            Index meanings:
            index 0: length test
            index 1: digit test
            index 2: lowercase character test
            index 3: uppercase character test
            index 4: special character test
            index 5: first name presence check
            index 6: last name presence check
            index 7: username presence check
        */
        
        final ArrayList<Boolean> RESULT = new ArrayList<>();
        RESULT.add(checkLength());
        RESULT.addAll(characterCheck());
        RESULT.addAll(compositionCheck());
        return RESULT;
        
    }
    
    public ArrayList<Boolean> startDeepTest() {        
        /*  This method will evaluate th strength of the password by performing various tests.
            It will return an array list of boolean values. Each position in the array list represents a test, and 
            the boolean value at that position denotes the result of that test.
        
            Index meanings:
            index 0: length test
            index 1: digit test
            index 2: lowercase character test
            index 3: uppercase character test
            index 4: special character test
            index 5: first name presence check
            index 6: last name presence check
            index 7: username presence check
            index 8: consecutive number test
            index 9: password spraying test
            index 10: brute force test
        */
        
        ArrayList<Boolean> result1 = new ArrayList<>();
        result1.add(checkLength());
        result1.addAll(characterCheck());
        result1.addAll(compositionCheck());
        result1.add(consecutiveNumbersCheck());
        result1.add(executePasswordSprayingAtack());
        result1.add(executeDictionaryBasedBruteForceAttack());
        return result1;
        
    }
    
    @Override
    public void run() {         
        if(this.TEST_TYPE.toLowerCase().equals("quick")) { ToolBox.getPasswordTester().displayResult(startQuickTest()); }
        else { ToolBox.getPasswordTester().displayResult(startDeepTest()); }    
        
    }
    
}
