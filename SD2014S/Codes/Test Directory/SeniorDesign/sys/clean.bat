cd C:/Users/Caleb/SeniorDesign/
rmdir encryptThese /s /q
robocopy C:/Users/Caleb/encryptThese C:/Users/Caleb/SeniorDesign/encryptThese/ /mir

cd sys
del Index.index
copy /y NUL Index.index >NUL
cd bulks 
del /S *.bulk
cd ../decrypted 
del /S *.pdf
cd ../ivs
del /S *.txt
cd ../pieces
del /S *.piece
cd ../temp
del /S *.txt 
exit