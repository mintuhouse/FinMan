#include <stdint.h>
#include <string.h>

struct billDetail {
	char	name[50];
	int 	nItems;		//Assuming No. of Items in Bill less than 100
	char 	itemName[100][50];
	int 	itemPrice[100];
	int 	total;
} billDetails;
