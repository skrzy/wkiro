# WKiRO

W branchu _master_ jest projekt w wersji zawierającej biblioteki .jar i .dll z OpenCV dla Windowsa x64. W branchu _linux_ są biblioteki zbudowane na Linux Mint - powinno też działać np. na Ubuntu. W przypadku innych systemów trzeba pobrać lub zbudować bibliotekę zgodnie z [instrukcją](http://docs.opencv.org/2.4.11/doc/tutorials/introduction/desktop_java/java_dev_intro.html), podmienić pliki w folderze _lib_ i ew. zmodyfikować ścieżki w _build.gradle_.

Projekt buduje się i uruchamia poleceniem 
> gradlew run

lub na linuxie
> ./gradlew run

Pierwsze urucomienie może trochę potrwać - musi pobrać się gradle (~70mb).

![alt text](/temat.jpg "Title")
