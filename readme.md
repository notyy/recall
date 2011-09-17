Extremely simple personal database  
=========================================
  
basic usage:
------------------
1. input abc=123 will record it to database   , in this case, I call abc as a hint, and 123 is the content, I name the whole thing as a Memory
2. input abc will give 123
3. if you forget full name of a hint, recall support fuzzy search
input ab  will give abc  
4. input abc=None to delete this memory

advanced usage:
------------------
* one hint can have multiple content, seperated by ",;"  
eg:

```
recall>abc=123,;xyz
recall>abc
 hint:abc
 content:
      (1)456
      (2)xyz
 ```

* you can use _ to reference last recalled memory  
recall>_  after above example will reproduce same output  

* you can use _+ to append more content to last memory

```
recall>_+123  
recall>_  
hint:abc  
content:  
  (1)456  
  (2)xyz  
  (3)123  
```

* you can use _1 to reference sub memory

```
recall>_+123  
recall>_  
hint:abc  
content:  
  (1)456  
  (2)xyz  
  (3)123  
```
  
* you can use _1 to reference sub memory

```
recall>_1
456
recall>_2
xyz
```

* you can use _1= to make a content as a "subhint",NOTE this usage won't change value of _

```
recall>_1=content11,;content12
456=content11,;content12 marked in my memory
recall>_2=content22,;content22
xyz=content22,;content22 marked in my memory
recall>_
hint:abc
content:
  (1)456
  (2)xyz
  (3)123
```

* you can use :r hint to get a recursive view

```
recall>:r abc
hint:abc
content:
    456
        content11
        content12
    xyz
        content22
        content22
    123
```

*Yes! you get a Tree, although recall's command is extremely simple(very few commands), it do support complex data structure such as a tree  

*how to install
-you should have jdk1.6 installed  
-download sourcecode,make sure you have sbt installed,run sbt one-jar in the folder in command line,after some times(if first time,may cause several minutes),entering target\scala-2.9.0.1 folder  
-run "java -jar recall_2.9.0-1-0.1.0-one-jar.jar"   

that's all, enjoy! 

