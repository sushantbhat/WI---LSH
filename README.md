# WI---LSH
Map reduce implementation of Locality Sensitive Hashing
Input to mapper - Bin number followed by bin values of each signature in following format
                  1 <1,2,3,4> <2,3,4,5> ....
                  2 <4,3,4,5> ........
Output of mapper - Coloumn and bucket value.
Output of reducer - Bucket and coloumn vector hashed to it.
