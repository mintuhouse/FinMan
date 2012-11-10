/******************************************     imp.cpp     ***********************************************/





/******************************************     main.cpp    **********************************************/
CHALLENGES

1) To find the text lines and text sections in the image.
	- Tesseract won't output the text in the image in order i.e. text which are inline in the image won't be inline in the tesseract output. Therefore
	  in order to associate items with prices they must be send each line at a time to tesseract.
2) Have to remove salt and pepper noise on the image.
	- Presence of noise and small blobs will impede the detection of text lines in the image.
3) Remove vertical lines in the binary image.
	- Necessary step to recognize text lines in the image.
4) Extract prices, items purchased, tool from those text lines.
	- There will be lot of errors in the text output given by tesseract because of varying intensity of characters in the image. Lot of error tolerance work
	  has to be in order to detect the total or prices or names of items.
