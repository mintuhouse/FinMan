#!/bin/sh

g++ imp.cpp -o app `pkg-config --cflags --libs opencv`
./app $1
g++ main.cpp `pkg-config --cflags --libs opencv`-I /usr/include -I /usr/local/include/leptonica -I /usr/include/leptonica -I /usr/local/include -I /usr/local/include/tesseract -L/usr/local/lib -I ./ -ltesseract_api -ltesseract_textord -ltesseract_main -ltesseract_wordrec -ltesseract_classify -ltesseract_dict -ltesseract_viewer -ltesseract_ccutil -ltesseract_ccstruct -ltesseract_cutil -ltesseract_image -ltiff -lpthread -L ./ -g -O0 -o deneme2346
./deneme2346 temp.jpg

