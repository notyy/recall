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
* just input following example data structure,and feel it   

```
notyy.name=notyy
     .tel=138xxxxx
     .mobile=8xxxxxx
     .address.city=shanghai
             .country=china
             .                         //press enter
     .wife.name=connie
     .wife.mother.name=zhangmuniang
                                       //press enter till save
 ```
 
 now try recall notyy, notyy.wife, notyy.*  , see what you got

* how to install
- you should have jdk1.6 installed  
- download sourcecode,make sure you have sbt installed,run sbt one-jar in the folder in command line,after some times(if first time,may cause several minutes),entering target\scala-2.9.0.1 folder  
- run "java -jar recall_2.9.0-1-0.1.0-one-jar.jar"   
- note this version is in experimental stage, not for serious usage!

todo:  a web shell,better syntax, data modeling pattern 

that's all, enjoy! 

