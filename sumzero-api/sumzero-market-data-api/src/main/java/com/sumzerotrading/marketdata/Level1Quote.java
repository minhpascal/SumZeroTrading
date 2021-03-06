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

package com.sumzerotrading.marketdata;

import com.sumzerotrading.data.Ticker;
import java.math.BigDecimal;
import java.time.ZonedDateTime;


/**
 * Implementation of an ILevel1 quote
 * 
 * @author RobTerpilowski
 */
public class Level1Quote extends AbstractQuote implements ILevel1Quote {

	protected BigDecimal value;
	
        /**
         * Builds a new Level1Quote
         * 
         * @param ticker The ticker this quote is for
         * @param type The type of quote (bid/ask/last/etc)
         * @param timeStamp The time of the quote
         * @param value The price (or volume) of this quote
         */
	public Level1Quote( Ticker ticker, QuoteType type, ZonedDateTime timeStamp, BigDecimal value ){
		 super( ticker, type, timeStamp );
		 this.value = value;
	}
	
	

        /**
         * The price or volume of this quote
         * @return The price or volume of this quote.
         */
        @Override
	public BigDecimal getValue() {
		return value;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(value.doubleValue());
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Level1Quote other = (Level1Quote) obj;
                if( ! value.equals(other.value) )
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "Level1Quote [ticker=" + ticker.getSymbol() + ",type=" + type + ",value=" + value + ",timestamp=" + timeStamp + "]";
	}

	
	
	
}
