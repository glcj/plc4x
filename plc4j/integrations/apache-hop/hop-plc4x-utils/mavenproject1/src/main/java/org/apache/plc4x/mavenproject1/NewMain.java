/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.apache.plc4x.mavenproject1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author cgarcia
 */
public class NewMain {

    public static final Pattern ADDRESS_PATTERN = Pattern.compile("(?<address>[\\%a-zA-Z_\\.0-9]+)(:(?<datatype>[a-zA-Z_]+))?(\\[(?<quantity>\\d+)])?");    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String addressString = "%MX2.0:BOOL";
        
        Matcher matcher = ADDRESS_PATTERN.matcher(addressString); 
        
        matcher.matches();
        String address = matcher.group("address");
        System.out.println(address);
        
    }
    
}
