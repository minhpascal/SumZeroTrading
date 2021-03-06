/**
 * MIT License

Copyright (c) 2015  Rob Terpilowski

Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.sumzerotrading.data;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Rob Terpilowski
 */
public class BarTest {

    public BarTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFirstConstructor() {
        LocalDateTime cal = LocalDateTime.now();
        String timeLabel = "time";
        double open = 1;
        double high = 2;
        double low = 3;
        double close = 4;
        long volume = 5;

        BarData bar = new BarData(cal, open, high, low, close, volume);
        assertEquals(cal, bar.getDateTime());
        assertEquals(open, bar.getOpen(), 0);
        assertEquals(high, bar.getHigh(), 0);
        assertEquals(low, bar.getLow(), 0);
        assertEquals(close, bar.getClose(), 0);
        assertEquals(volume, bar.getVolume());

    }

    @Test
    public void testSecondConstructor() {
        LocalDateTime cal = LocalDateTime.now();
        double open = 1;
        double high = 2;
        double low = 3;
        double close = 4;
        long volume = 5;
        long openInterest = 6;

        BarData bar = new BarData(cal, open, high, low, close, volume, openInterest);
        assertEquals(cal, bar.getDateTime());
        assertEquals(open, bar.getOpen(), 0);
        assertEquals(high, bar.getHigh(), 0);
        assertEquals(low, bar.getLow(), 0);
        assertEquals(close, bar.getClose(), 0);
        assertEquals(volume, bar.getVolume());
        assertEquals(openInterest, bar.getOpenInterest());
    }

    /**
     * Test of getDateTime method, of class BarData.
     */
    @Test
    public void testGetSetDateTime() {
        System.out.println("getDateTime");
        LocalDateTime cal = LocalDateTime.now();
        BarData instance = new BarData();
        Date expResult = new Date();
        instance.setDateTime(cal);

        LocalDateTime result = instance.getDateTime();
        assertEquals(cal, result);
    }


    /**
     * Test of setOpen method, of class BarData.
     */
    @Test
    public void testSetOpen() {
        System.out.println("setOpen");
        double open = 1.0;
        BarData instance = new BarData();
        instance.setOpen(open);
        assertEquals(open, instance.getOpen(), 0.0);
    }

    /**
     * Test of setHigh method, of class BarData.
     */
    @Test
    public void testSetHigh() {
        double high = 1.0;
        BarData instance = new BarData();
        instance.setHigh(high);
        assertEquals(high, instance.getHigh(), 0.0);
    }

    /**
     * Test of setLow method, of class BarData.
     */
    @Test
    public void testSetLow() {
        double low = 1.0;
        BarData instance = new BarData();
        instance.setLow(low);
        assertEquals(low, instance.getLow(), 0.0);
    }

    /**
     * Test of setClose method, of class BarData.
     */
    @Test
    public void testSetClose() {
        double close = 1.0;
        BarData instance = new BarData();
        instance.setClose(close);
        assertEquals(close, instance.getClose(), 0.0);
    }

    /**
     * Test of setVolume method, of class BarData.
     */
    @Test
    public void testSetVolume() {
        long volume = 1L;
        BarData instance = new BarData();
        instance.setVolume(volume);
        assertEquals(volume, instance.getVolume());
    }

    /**
     * Test of setOpenInterest method, of class BarData.
     */
    @Test
    public void testSetOpenInterest() {
        long openInterest = 1;
        BarData instance = new BarData();
        instance.setOpenInterest(openInterest);
        assertEquals(openInterest, instance.getOpenInterest());
    }


    /**
     * Test of equals method, of class BarData.
     */
    @Test
    public void testEquals() {
        BarData[] bars = getEqualBars();
        assertTrue( bars[0].equals(bars[1]) );
    }
    
    @Test
    public void testEquals_NullBar() {
        BarData bar = new BarData();
        assertFalse( bar.equals(null));
    }      
    
    public void testEquals_WrongClass() {
        BarData bar = new BarData();
        assertFalse( bar.equals( "123" ) );
    }
    
    @Test
    public void testEquals_differentClose() {
        BarData[] bars = getEqualBars();
        bars[0].setClose(0);
        assertFalse( bars[0].equals( bars[1] ) );
    }
    
    @Test
    public void testEquals_differentOpen() {
        BarData[] bars = getEqualBars();
        bars[0].setOpen(0);
        assertFalse( bars[0].equals( bars[1] ) );
    }    
    
    @Test
    public void testEquals_differentHigh() {
        BarData[] bars = getEqualBars();
        bars[0].setHigh(0);
        assertFalse( bars[0].equals( bars[1] ) );
    }       
    
    @Test
    public void testEquals_differentLow() {
        BarData[] bars = getEqualBars();
        bars[0].setLow(0);
        assertFalse( bars[0].equals( bars[1] ) );
    }    
    
    @Test
    public void testEquals_differentVolume() {
        BarData[] bars = getEqualBars();
        bars[0].setVolume(0);
        assertFalse( bars[0].equals( bars[1] ) );
    }   
    
    @Test
    public void testEquals_differentOpenInterest() {
        BarData[] bars = getEqualBars();
        bars[0].setOpenInterest(0);
        assertFalse( bars[0].equals( bars[1] ) );
    }       
    
    
    @Test
    public void testEquals_differentDate() throws Exception {
        BarData[] bars = getEqualBars();
        bars[0].setDateTime(null);
        assertFalse( bars[0].equals( bars[1] ) );
        
        
        LocalDateTime cal = LocalDateTime.of(1980, Month.MARCH, 5,0, 0);
        bars[0].setDateTime( cal );
        assertFalse( bars[0].equals( bars[1] ) );
    }         
    
    
    
    private BarData[] getEqualBars() {
        LocalDateTime cal = LocalDateTime.now();
        double open = 1.0;
        double high = 2.0;
        double low = 3.0;
        double close= 4.0;
        long volume = 5;
        long openInterest =  6;
        
        BarData[] bars = new BarData[2];
        bars[0] = new BarData(cal, open, high, low, close, volume, openInterest);
        
        bars[1] = new BarData(cal, open, high, low, close, volume, openInterest);
        
        return bars;
                
    }
}
