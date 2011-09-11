#Everyone need a personal database  
#Expert using command line  

-you should have jdk1.6 installed  
-download sourcecode,run sbt one-jar in the folder in command line,after some times(if first time,may cause several minutes),entering target\scala-2.9.0.1 folder  
-run "java -jar recall_2.9.0-1-0.1.0-one-jar.jar"   

usage is simple:  
input abc=123 to rember it, then input abc will recall 123  
it supports fuzzy search, eg, you have rembered testhint=something,hintlong=something,hintshort=something  
input hint will give you testhint,hintlong,hintshort to help you recall what you want  

that's all, enjoy! 

