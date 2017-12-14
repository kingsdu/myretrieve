package com.retrieve.util;

import java.security.SecureRandom;


public class BloomFilter_utils {

    /** The number of weights used to create hash functions. */
    final public static int NUMBER_OF_WEIGHTS = 2083; // CHANGED FROM 16
    /** The number of bits in this filter. */
    final public long m; 
    /** The number of hash functions used by this filter. */
    final public int d;
    /** The underlying bit vectorS. */
//    final private int[] bits;
    final private int[][] bits;
    /** The random integers used to generate the hash functions. */
    final private int[][] weight;

    /** The number of elements currently in the filter. It may be
     * smaller than the actual number of additions of distinct character
     * sequences because of false positives.
     */
    private int size;

    /** The natural logarithm of 2, used in the computation of the number of bits. */
    private final static double NATURAL_LOG_OF_2 = Math.log( 2 );

    /** number of ints in 1MB. */
    private final static int ONE_MB_INTS = 1 << 18; // 

    private final static boolean DEBUG = false;
	
	
    /** Creates a new Bloom filter with given number of hash functions and expected number of elements.
    *
    * @param n the expected number of elements.
    * @param d the number of hash functions; if the filter add not more than <code>n</code> elements,
    * false positives will happen with probability 2<sup>-<var>d</var></sup>.
    */
	public BloomFilter_utils( final int n, final int d ) {
        this.d = d;
        int len =
        	(int)Math.ceil( ( (long)n * (long)d / NATURAL_LOG_OF_2 ) / 32 );
        // round up to ensure divisible into 1MiB chunks
        len = ((len / ONE_MB_INTS)+1)*ONE_MB_INTS;
        this.m = len*32L;
        if ( m >= 1L<<54 ) {
        	throw new IllegalArgumentException( "This filter would require " + m + " bits" );
        }
//        bits = new int[ len ];
        bits = new int[ len/ONE_MB_INTS ][ONE_MB_INTS];//生成bloom数组

        if ( DEBUG ) System.err.println( "Number of bits: " + m );

        // seeded for reproduceable behavior in repeated runs; BUT: 
        // SecureRandom's default implementation (as of 1.5) 
        // seems to mix in its own seeding.
        final SecureRandom random = new SecureRandom(new byte[] {19,96});
        weight = new int[ d ][];
        //生成d行[NUMBER_OF_WEIGHTS]列的随机数
        for( int i = 0; i < d; i++ ) {
            weight[ i ] = new int[ NUMBER_OF_WEIGHTS ];
            for( int j = 0; j < NUMBER_OF_WEIGHTS; j++ )
                 weight[ i ][ j ] = random.nextInt();
        }
    }
	
	
	
	public int size() {
        return size;
    }

    /** Hashes the given sequence with the given hash function.
     * 计算hash函数
     * @param s a character sequence.
     * @param l the length of <code>s</code>.
     * @param k a hash function index (smaller than {@link #d}).
     * @return the position in the filter corresponding to <code>s</code> for the hash function <code>k</code>.
     */
	private long hash( final CharSequence s, final int l, final int k ) {
		final int[] w = weight[ k ];
		int h = 0, i = l;
		while( i-- != 0 ) h ^= s.charAt( i ) * w[ i % NUMBER_OF_WEIGHTS ];
		return ((long)h-Integer.MIN_VALUE) % m; 
	}

	
	
	 public boolean contains( final CharSequence s ) {
	        int i = d, l = s.length();
	        while( i-- != 0 ) if ( ! getBit( hash( s, l, i ) ) ) return false;
	        return true;
	    }

	    /** Adds a character sequence to the filter.
	     *
	     * @param s a character sequence.
	     * @return true if the character sequence was not in the filter (but see {@link #contains(CharSequence)}).
	     */

	    public boolean add( final CharSequence s ) {
	        boolean result = false;
	        int i = d, l = s.length();
	        long h;
	        while( i-- != 0 ) {
	            h = hash( s, l, i );
	            if ( ! setGetBit( h ) ) result = true;
	        }
	        if ( result ) size++;//不同的url
	        return result;
	    }
	    
	    
	    
	    protected final static long ADDRESS_BITS_PER_UNIT = 5; // 32=2^5
	    protected final static long BIT_INDEX_MASK = 31; // = BITS_PER_UNIT - 1;

	    /**
	     * Returns from the local bitvector the value of the bit with 
	     * the specified index. The value is <tt>true</tt> if the bit 
	     * with the index <tt>bitIndex</tt> is currently set; otherwise, 
	     * returns <tt>false</tt>.
	     *
	     * (adapted from cern.colt.bitvector.QuickBitVector)
	     * 
	     * @param     bitIndex   the bit index.
	     * @return    the value of the bit with the specified index.
	     */
	    protected boolean getBit(long bitIndex) {
	        long intIndex = (bitIndex >>> ADDRESS_BITS_PER_UNIT);
	        return ((bits[(int)(intIndex / ONE_MB_INTS)][(int)(intIndex % ONE_MB_INTS)] 
	            & (1 << (bitIndex & BIT_INDEX_MASK))) != 0);
	    }

	    /**
	     * Changes the bit with index <tt>bitIndex</tt> in local bitvector.
	     *
	     * (adapted from cern.colt.bitvector.QuickBitVector)
	     * 
	     * @param     bitIndex   the index of the bit to be set.
	     */
	    protected void setBit(long bitIndex) {
	        long intIndex = (bitIndex >>> ADDRESS_BITS_PER_UNIT);
	        bits[(int)(intIndex / ONE_MB_INTS)][(int)(intIndex % ONE_MB_INTS)] 
	            |= 1 << (bitIndex & BIT_INDEX_MASK);
	    }

	    /**
	     * Sets the bit with index <tt>bitIndex</tt> in local bitvector -- 
	     * returning the old value. 
	     *
	     * (adapted from cern.colt.bitvector.QuickBitVector)
	     * 
	     * @param     bitIndex   the index of the bit to be set.
	     * 将计算出的hash函数存入bits数组中
	     */
	    protected boolean setGetBit(long bitIndex) {
	        long intIndex = (int) (bitIndex >>> ADDRESS_BITS_PER_UNIT);
	        int a = (int)(intIndex / ONE_MB_INTS);
	        int b = (int)(intIndex % ONE_MB_INTS);
	        int mask = 1 << (bitIndex & BIT_INDEX_MASK);
	        boolean ret = ((bits[a][b] & (mask)) != 0);
	        bits[a][b] |= mask;
	        return ret;
	    }
	    
		/* (non-Javadoc)
		 * @see org.archive.util.BloomFilter#getSizeBytes()
		 */
		public long getSizeBytes() {
			return bits.length*bits[0].length*4;
		}
		
		
		
		public static void main(String args[]){
			BloomFilter_utils boom = new BloomFilter_utils(2000,24);
			boom.add("www.baidu.com");
			boom.add("www.baidu.com");
			boom.add("www.baiii.com");
			
			
			System.out.println(boom.contains("www.baidu.com"));
			System.out.println(boom.contains("www.abaidu.com"));
		}
		
}
