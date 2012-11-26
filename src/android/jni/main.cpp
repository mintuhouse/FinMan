// This actually supposed to be the main code with the image processing stuff which fills \
// billDetails object. But here filled with dummy data

#include "billDetails.h"
#include "stdlib.h"
#include <string.h>
using namespace std;


// billDetail - structure containing all the details, path - image location

int getBillDetails(struct billDetail * bill, const char* path){
	//TODO: Dummy Data
	strcpy(bill->name,"RR Towers");
	bill->nItems = 3;
	for(int i=0;i<bill->nItems;i++){
		strcpy(bill->itemName[i],"ItemName");
		bill->itemPrice[i]=i*100+12;
	}
	bill->total=1213;

	return 0;
};


void myitoa(int a, char* b, int n){
	int i=0;
	while(n>0){
		b[i]=(char)(a%10+47);
		n--;
		a=a/10;
	}
}
