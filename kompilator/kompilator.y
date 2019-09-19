/**
Jan Sieradzki
236441
*/

%{
#include <math.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <stdarg.h>
#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <vector>
#include <algorithm>
#define YYDEBUG 1
using namespace std;


struct structures {
	string declare;
    string type;
	bool initialized; //Array  Num

	char regist;
	long long int value;
	long long int memory;
	//for array
	long long int start;
	long long int end;
	long long int size;
	map<long long int,bool> initializedArray;

	bool variable;
	long long int lastTarget;
	string declareTarget;
	
	int num;
	bool isIterator = true;
	char lastRegist;
	string lastDeclare;
} ;

typedef struct {
	char regist;
	bool hasStructure = false;
	bool hasIterator = false;
	string declare;
	long long int value=-1;
} regist;

int yyerror(char const* str);
int yylex(void);
string yytext;
bool endProgram;
int err = 0;
extern int yylineno;
long long int iterators;
long long int memory;
long long int temp1;
long long int temp2;
int declarcounter = 0;
int flag;
int loopCounter;
char actualRegist;

map<string, structures> declarationSet;
map<char, regist> regists;
vector<string> output;
vector<structures*> values;
vector<int> commendsNum;
vector<long long int> commands;
vector<string> commandss;

structures createNum(string declare,char regist,long long int memory);
structures createArray(string declare,long long int start,long long int end,long long int memory);
void initialize(structures *s);
void createArray(structures *s,string declare,long long int start,long long int end,long long int memory);
void forHelper(string hole,char reg);
void setRegist(char regist,long long int value);
void createCommand2(string s, char s1,char s2);
void createCommand1(string s, char s1);
void createCommand2(string s, char s1,string s2);
void createCommand1(string s, string s1);
void load(structures *s, char s1);
void load(long long int mem, char s1);
void store(structures *s, char s1);
void store(long long int mem, char s1);
string prepereIterator();
void multiply(structures *s, structures *s1, structures *s2);
void divide(structures *s, structures *s1, structures *s2);
void mod(structures *s, structures *s1, structures *s2);
void putToRegist(string declare,char s1);
void putToMemory(string declare);
%}
%define parse.error verbose
%define parse.lac full
%union {
    char* str;
    long long int num;
}

%token <str> DECLARE IN END IF THEN ENDIF ELSE   FROM   WHILE DO ENDWHILE ENDDO FOR ENDFOR WRITE READ ';' PIDENTIFIER TO DOWNTO NUM 
%token '+' '-' 
%token '*' '/' '%' ASSIGN GRE LWE EQ NE LW GR ':' '(' ')'

%%

program:
DECLARE declarations IN commands END {
	output.push_back("HALT");
	  return 0;
}
;
declarations:
declarations PIDENTIFIER ';' {
	if(declarationSet.find($2)==declarationSet.end()) {
		structures structure;
		if(declarcounter<3){
			structure = createNum($2,char(declarcounter+'F'),memory);
			declarcounter++;
		}
		else{
			structure = createNum($2,'A',memory);
			memory++;
		}
		declarationSet.insert(make_pair($2, structure));
    }
	else {
		cout << endl << "ERROR near line : " << yylineno << " : That variable is already declared : " << $2 << " ." << endl;
	exit(1);   
	}
}
| declarations PIDENTIFIER '(' NUM ':' NUM ')' ';'{
	if(declarationSet.find($2)==declarationSet.end()) {
		if(atoll($6)>=atoll($4)){
			structures structure = createArray($2,atoll($4),atoll($6),memory);
			int sizeOfArray = abs(atoll($6))-abs(atoll($4))+1;
			declarationSet.insert(make_pair($2, structure));
			memory = memory + sizeOfArray;
		}
		else{
			cout << endl<< "ERROR near line : " << yylineno << " : Wrong bounds of array (proper way -> tab(n:m) where n=<m) " << endl;
			exit(1);
		}
    }
	else {
		cout << endl << "ERROR near line : " << yylineno << " : That variable is already declared : " << $2 << " ." << endl;
		exit(1);  
	}
}
|
;
commands:
commands command
| command
;
command: 
identifier {
	
	structures *s = values.back();
	
	initialize(s);
	
} ASSIGN expression ';' 
| IF condition THEN commands {
	int iterator = commendsNum.back();
	commendsNum.pop_back();
	
	long long int num;
	string com;
	while(iterator>0){
		
		com = commandss.back();
		commandss.pop_back();

		num = commands.back();
		commands.pop_back();
		

		output.insert(output.begin()+(num),com + " " + to_string(output.size()+loopCounter+1));
		iterator--;
		loopCounter--;
	} 
		commands.pop_back();
		commands.push_back(output.size());
		commandss.push_back("JUMP");
		loopCounter++;
}
ELSE commands {
	string com = commandss.back();
	commandss.pop_back();
	//commandss.erase(commandss.begin());

	long long int num = commands.back();
	commands.pop_back();
	//commands.erase(commands.begin());

	output.insert(output.begin()+(num),com + " " + to_string(output.size()+loopCounter));
	loopCounter--;
} ENDIF
| IF condition THEN commands {
	int iterator = commendsNum.back();
	commendsNum.pop_back();
	
	long long int num;
	string com;
	while(iterator>0){
		com = commandss.back();
		commandss.pop_back();

		num = commands.back();
		commands.pop_back();
		

		output.insert(output.begin()+(num),com + " " + to_string(output.size()+loopCounter));
		iterator--;
		loopCounter--;
	} 
	commands.pop_back();
} ENDIF
| WHILE condition DO commands {
	
	int iterator = commendsNum.back();
	commendsNum.pop_back();
	
	long long int num;
	string com;
	while(iterator>0){
		
		com = commandss.back();
		commandss.pop_back();

		num = commands.back();
		commands.pop_back();
		

		output.insert(output.begin()+(num),com + " " + to_string(output.size()+loopCounter+1));
		iterator--;
		loopCounter--;
	} 

	num = commands.back();
	commands.pop_back();
	output.push_back("JUMP " + to_string(num+loopCounter));
	
} ENDWHILE
| DO {
	commands.push_back(output.size());
} commands WHILE condition {
	int iterator = commendsNum.back();
	commendsNum.pop_back();
	
	long long int num;
	string com;
	while(iterator>0){
		
		com = commandss.back();
		commandss.pop_back();

		num = commands.back();
		commands.pop_back();
		

		output.insert(output.begin()+(num),com + " " + to_string(output.size()+loopCounter+1));
		iterator--;
		loopCounter--;
	} 
	commands.pop_back();
	num = commands.back();
	commands.pop_back();
	output.push_back("JUMP " + to_string(num+loopCounter));
	
} ENDDO
| FOR PIDENTIFIER FROM value TO value DO {
	if(declarationSet.find($2)!=declarationSet.end()) {
		cout << endl << "ERROR near line : " << yylineno << " : That variable is declared, can't be used as iterator : " << $2 << " ." << endl;
		exit(1);   
	} 

	string dec = prepereIterator();
	string hole = $2;
	hole += "-1";

	structures iterator;
	structures* piterator;
	char reg ='D';
	iterator = createNum(hole,reg,memory);
	iterator.initialized=true;
	iterator.isIterator=false;
	iterator.memory=memory;
	iterator.lastDeclare="";
	memory++;
	iterator.regist='A';
	declarationSet.insert(make_pair(hole, iterator));
	piterator = &iterator;
	
	structures itVar;
	structures* pitVar;

	if(dec != "-1"){
		itVar = createNum($2,actualRegist,memory);
		itVar.initialized=true;
		itVar.lastDeclare = dec;
		itVar.isIterator=true;
		declarationSet.insert(make_pair($2, itVar));
		pitVar = &itVar;
	} else {
		itVar = createNum($2,reg,memory);
		itVar.initialized=true;
		itVar.isIterator=true;
		itVar.memory=memory;
		itVar.lastDeclare = "";
		memory++;
		itVar.regist='A';
		declarationSet.insert(make_pair($2, itVar));
		pitVar = &itVar;
	}



	if(flag == 11){

		setRegist('C',temp1);
		store(&declarationSet.at($2),'C');
		
		setRegist(reg,temp2);
		createCommand2("SUB",reg,'C');
		createCommand1("INC",reg);
		createCommand1("INC",reg);
		forHelper(hole,reg);

	} else if(flag == 22){

		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());
	
		if(s1->regist=='A'){
			load(s1, 'B');
			createCommand2("COPY",'C','B');
			store(&declarationSet.at($2),'C');
			if(s2->regist=='A'){
				load(s2, reg);
				
				createCommand2("SUB",reg,'C');
				createCommand1("INC",reg);
				createCommand1("INC",reg);
				forHelper(hole,reg);
			}else {
				createCommand2("COPY",reg,s2->regist);
				createCommand2("SUB",reg,'C');
				createCommand1("INC",reg);
				createCommand1("INC",reg);
				forHelper(hole,reg);
			}		

		}else {
			createCommand2("COPY",'C',s1->regist);
			store(&declarationSet.at($2),'C');
			if(s2->regist=='A'){
				load(s2, reg);
				
				createCommand2("SUB",reg,'C');
				createCommand1("INC",reg);
				createCommand1("INC",reg);
				forHelper(hole,reg);
			}else {
				createCommand2("COPY",reg,s2->regist);
				createCommand2("SUB",reg,'C');
				createCommand1("INC",reg);
				createCommand1("INC",reg);
				forHelper(hole,reg);
			}		 
		}
			
		

	} else if(flag == 12) {

		structures *s2 = values.front();
		values.erase(values.begin());

		
		setRegist('C',(temp1));
		store(&declarationSet.at($2),'C');

		if(s2->regist=='A'){
			load(s2, reg);
			
			createCommand2("SUB",reg,'C');
			createCommand1("INC",reg);
			createCommand1("INC",reg);
			forHelper(hole,reg);
		}else {

			createCommand2("COPY",reg,s2->regist);
			createCommand2("SUB",reg,'C');
			createCommand1("INC",reg);
			createCommand1("INC",reg);
			forHelper(hole,reg);
		}
								
			

	} else if(flag == 21) {

		structures *s1 = values.front();
		values.erase(values.begin());
		setRegist(reg,(temp2));

		if(s1->regist=='A'){
			load(s1, 'B');
			createCommand2("COPY",'C','B');	
			store(&declarationSet.at($2),'C');

			createCommand2("SUB",reg,'C');
			createCommand1("INC",reg);
			createCommand1("INC",reg);
			forHelper(hole,reg);

		}else {
			createCommand2("COPY",'C',s1->regist);
			store(&declarationSet.at($2),'C');

			createCommand2("SUB",reg,'C');
			createCommand1("INC",reg);
			createCommand1("INC",reg);
			forHelper(hole,reg);						 
		}

	}
	loopCounter++;
	flag = -1;

} commands {

	if(declarationSet.at($2).regist=='A'){
		load(&declarationSet.at($2),'B');
		createCommand1("INC",'B');
		store(&declarationSet.at($2),'B');
	} else {
		createCommand1("INC",declarationSet.at($2).regist);
	}

	string hole = $2;
	hole += "-1";	
	long long int num = commands.back();
	commands.pop_back();
	if(declarationSet.at(hole).regist=='A'){
		output.insert(output.begin()+(num),"JZERO D " + to_string(output.size()+loopCounter+1));
	} else {
		string helper(1,declarationSet.at(hole).regist);
		output.insert(output.begin()+(num),"JZERO " + helper + " " + to_string(output.size()+loopCounter+1));
	}
	loopCounter--;
	if(declarationSet.at(hole).regist=='A'){
		num = commands.back()+1;
		commands.pop_back();
	}
	output.push_back("JUMP " + to_string(num+loopCounter-1));
	string x = declarationSet.at($2).lastDeclare;
	if(x!="")
		putToRegist(declarationSet.at(x).declare,declarationSet.at($2).regist);
	else {
		regists.at(declarationSet.at($2).regist).hasIterator=false;
		regists.at(declarationSet.at($2).regist).hasStructure=false;
		regists.at(declarationSet.at($2).regist).declare="";
	}
	declarationSet.erase(declarationSet.find($2));
	declarationSet.erase(declarationSet.find(hole));


} ENDFOR
| FOR PIDENTIFIER FROM value DOWNTO value DO {
	if(declarationSet.find($2)!=declarationSet.end()) {
		cout << endl << "ERROR near line : " << yylineno << " : That variable is declared, can't be used as iterator : " << $2 << " ." << endl;
		exit(1);   
	} 

	string dec = prepereIterator();
	string hole = $2;
	hole += "-1";

	structures iterator;
	structures* piterator;
	char reg ='D';
	iterator = createNum(hole,reg,memory);
	iterator.initialized=true;
	iterator.isIterator=false;
	iterator.memory=memory;
	iterator.lastDeclare="";
	memory++;
	iterator.regist='A';
	declarationSet.insert(make_pair(hole, iterator));
	piterator = &iterator;
	
	structures itVar;
	structures* pitVar;

	if(dec != "-1"){
		itVar = createNum($2,actualRegist,memory);
		itVar.initialized=true;
		itVar.lastDeclare = dec;
		itVar.isIterator=true;
		declarationSet.insert(make_pair($2, itVar));
		pitVar = &itVar;
	} else {
		itVar = createNum($2,reg,memory);
		itVar.initialized=true;
		itVar.isIterator=true;
		itVar.memory=memory;
		memory++;
		itVar.regist='A';
		itVar.lastDeclare = "";
		declarationSet.insert(make_pair($2, itVar));
		pitVar = &itVar;
	}


	if(flag == 11){
		
		setRegist(reg,temp1);
		store(&declarationSet.at($2),reg);
		
		setRegist('C',temp2);
		createCommand2("SUB",reg,'C');
		createCommand1("INC",reg);
		createCommand1("INC",reg);
		forHelper(hole,reg);
	} else if(flag == 22){

		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());
	
		if(s1->regist=='A'){
			load(s1, 'B');
			createCommand2("COPY",reg,'B');
			store(&declarationSet.at($2),reg);
			if(s2->regist=='A'){
				load(s2, 'C');
				
				createCommand2("SUB",reg,'C');
				createCommand1("INC",reg);
				createCommand1("INC",reg);
				forHelper(hole,reg);
			}else {

				createCommand2("SUB",reg,s2->regist);
				createCommand1("INC",reg);
				createCommand1("INC",reg);
				forHelper(hole,reg);
			}		

		}else {
			createCommand2("COPY",reg,s1->regist);
			store(&declarationSet.at($2),reg);
			if(s2->regist=='A'){
				load(s2, 'C');
				
				createCommand2("SUB",reg,'C');
				createCommand1("INC",reg);
				createCommand1("INC",reg);
				forHelper(hole,reg);
			}else {
				createCommand2("SUB",reg,s2->regist);
				createCommand1("INC",reg);
				createCommand1("INC",reg);
				forHelper(hole,reg);	
			}								 
		}
			
		

	} else if(flag == 12) {

		structures *s2 = values.front();
		values.erase(values.begin());

		
		setRegist(reg,(temp1));
		store(&declarationSet.at($2),reg);

		if(s2->regist=='A'){
			load(s2, 'C');
			
			createCommand2("SUB",reg,'C');
			createCommand1("INC",reg);
			createCommand1("INC",reg);
			forHelper(hole,reg);
		}else {

			createCommand2("SUB",reg,s2->regist);
			createCommand1("INC",reg);
			createCommand1("INC",reg);
			forHelper(hole,reg);
		}
								
			

	} else if(flag == 21) {

		structures *s1 = values.front();
		values.erase(values.begin());
		setRegist('C',(temp2));

		if(s1->regist=='A'){
			load(s1, reg);
			store(&declarationSet.at($2),reg);

			createCommand2("SUB",reg,'C');
			createCommand1("INC",reg);
			createCommand1("INC",reg);
			forHelper(hole,reg);

		}else {
			createCommand2("COPY",reg,s1->regist);
			store(&declarationSet.at($2),reg);

			createCommand2("SUB",reg,'C');
			createCommand1("INC",reg);
			createCommand1("INC",reg);
			forHelper(hole,reg);					 
		}

	}
	loopCounter++;
	flag = -1;

} commands {

	if(declarationSet.at($2).regist=='A'){
		load(&declarationSet.at($2),'B');
		createCommand1("DEC",'B');
		store(&declarationSet.at($2),'B');
	} else {
		createCommand1("DEC",declarationSet.at($2).regist);
	}

	string hole = $2;
	hole += "-1";	
	long long int num = commands.back();
	commands.pop_back();
	if(declarationSet.at(hole).regist=='A'){
		output.insert(output.begin()+(num),"JZERO D " + to_string(output.size()+loopCounter+1));
	} else {
		string helper(1,declarationSet.at(hole).regist);
		output.insert(output.begin()+(num),"JZERO " + helper + " " + to_string(output.size()+loopCounter+1));
	}
	loopCounter--;
	if(declarationSet.at(hole).regist=='A'){
		num = commands.back()+1;
		commands.pop_back();
	}
	output.push_back("JUMP " + to_string(num+loopCounter-1));
	string x = declarationSet.at($2).lastDeclare;
	if(x!="")
		putToRegist(declarationSet.at(x).declare,declarationSet.at($2).regist);
	else {
		regists.at(declarationSet.at($2).regist).hasIterator=false;
		regists.at(declarationSet.at($2).regist).hasStructure=false;
		regists.at(declarationSet.at($2).regist).declare="";
	}
	declarationSet.erase(declarationSet.find($2));
	declarationSet.erase(declarationSet.find(hole));

} ENDFOR
| READ identifier ';' {

	structures *s = values.front();
	values.erase(values.begin());

	if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
	}

	initialize(s);
	if(s->type=="Array"){
		
		if(s->regist=='A'){
			output.push_back("GET B"); 
			store(s,'B');
		} 	
	} else if (s->type=="Num"){	
		
		if(s->regist=='A'){
			output.push_back("GET B");
			store(s,'B');
		}else {
			createCommand1("GET",s->regist);
		}		
	}
	s-> value = -1;

}
| WRITE value ';' {
	if(flag==2){
		structures *s = values.front();
		
		values.erase(values.begin());
	

		if(s->type=="Array"){
			
			if(s->regist=='A'){
				load(s,'C');
				output.push_back("PUT C");
			} 	
		} else if (s->type=="Num"){
			if(s->regist=='A'){
				load(s,'C');
				output.push_back("PUT C");
			}else {
				createCommand1("PUT",s->regist);
			}		
		}
		
	}
	else if(flag == 1){
		setRegist('C', temp1);
		output.push_back("PUT C");
	}
	flag = -1;
	
}
;
expression:
value {

	structures *s = values.front();
	values.erase(values.begin());

	if(s->isIterator == true) {
		cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
		exit(1);
	}

	initialize(s);

	if(flag == 1){

		if(s->regist=='A'){
			setRegist('C',temp1);
			store(s, 'C');
		}else {			
			setRegist(s->regist,temp1);					
		}
	} else if (flag == 2){
		structures *s1 = values.front();
		
		values.erase(values.begin());
		if(s->regist=='A'){
			if(s1->regist=='A'){

			load(s1, 'B');				
			createCommand2("COPY",'C','B'); 
			}else {
				createCommand2("COPY",'C',s1->regist);								 
			}
	
			store(s, 'C');
			
		}else {			
			if(s1->regist=='A'){
				
				load(s1, 'B');
				createCommand2("COPY",s->regist,'B');	

			}else {
				createCommand2("COPY",s->regist,s1->regist);						 
			}								
		}	
	}
	flag = -1;
}
| value '+' value {
	if(flag == 11){
		structures *s = values.front();
		values.erase(values.begin());

		if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
		}

		initialize(s);
		if(s->regist=='A'){
			setRegist('C',(temp1+temp2));
			store(s, 'C');
		}else {			
			setRegist(s->regist,(temp1+temp2));					
		}			

	} else if(flag == 22){
		structures *s = values.front();
		values.erase(values.begin());

		if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
		}


		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

		initialize(s);
		if(s->regist=='A'){
			if(s1->regist=='A'){
				load(s1, 'B');
				createCommand2("COPY",'C','B'); 
				if(s2->regist=='A'){
					load(s2, 'B');
					createCommand2("ADD",'C','B'); 
				}else {
					createCommand2("ADD",'C',s2->regist);		
				}		

			}else {
				createCommand2("COPY",'C',s1->regist);
				if(s2->regist=='A'){
					load(s2, 'B');
					createCommand2("ADD",'C','B'); 
				}else {
					createCommand2("ADD",'C',s2->regist);		
				}								 
			}
			store(s, 'C');
		}else {			
			if(s1->regist=='A'){
				load(s1, 'B'); 			
				load(s2, 'C');
				createCommand2("COPY",s->regist,'B');
				createCommand2("ADD",s->regist,'C'); 
				

			}else {
				if(s2->regist=='A'){
					load(s2, 'B');
					createCommand2("COPY",s->regist,s1->regist);
					createCommand2("ADD",s->regist,'B'); 
				}else {		
					if(s->regist!=s2->regist){
						createCommand2("COPY",s->regist,s1->regist);
						createCommand2("ADD",s->regist,s2->regist);	
					}else {
						createCommand2("COPY",'B',s->regist);
						createCommand2("COPY",s->regist,s1->regist);
						createCommand2("ADD",s->regist,'B');
					}	
				}								 
			}								
		}		
		

	} else if(flag == 12) {
		structures *s = values.front();
		values.erase(values.begin());

		if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
		}

		structures *s2 = values.front();
		values.erase(values.begin());

		initialize(s);
		if(s->regist=='A'){
			setRegist('C',(temp1));
			if(s2->regist=='A'){
				load(s2, 'B');
				createCommand2("ADD",'C','B'); 
			}else {
				createCommand2("ADD",'C',s2->regist);		
			}								 		
			store(s, 'C');
		}else {	
			if(s2->regist=='A'){
				load(s2, 'B');
				setRegist(s->regist,(temp1));
				createCommand2("ADD",s->regist,'B'); 
			}else {
				createCommand2("COPY",'B',s2->regist);
				setRegist(s->regist,(temp1));
				createCommand2("ADD",s->regist,'B');		
			}							 
		}								
			

	} else if(flag == 21) {
		structures *s = values.front();
		values.erase(values.begin());

		if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
		}

		structures *s1 = values.front();
		values.erase(values.begin());

		initialize(s);
		if(s->regist=='A'){
			if(s1->regist=='A'){
				load(s1, 'B');
				
				createCommand2("COPY",'C','B'); 
	
				setRegist('B',(temp2));
				createCommand2("ADD",'C','B'); 

			}else {
				if (s1->regist != s-> regist){
					createCommand2("COPY",'C',s1->regist);
				}
				setRegist('B',(temp2));
				createCommand2("ADD",'C','B'); 						 
			}
			store(s, 'C');
		}else {			
			if(s1->regist=='A'){
				load(s1, 'B');
				if (s1->regist != s-> regist){
					createCommand2("COPY",s->regist,'B'); 
				}
				setRegist('B',(temp2));
				createCommand2("ADD",s->regist,'B'); 

			}else {
				if (s1->regist != s-> regist){
					createCommand2("COPY",s->regist,s1->regist);
				}
				setRegist('B',(temp2));
				createCommand2("ADD",s->regist,'B'); 							 
			}								
		}
	}

	flag = -1;

}
| value '-' value {
	if(flag == 11){
		structures *s = values.front();
		values.erase(values.begin());

		if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
		}

		initialize(s);
		if(s->regist=='A'){
			long long int x = temp1-temp2;
			if (x<0)
				x=0;
			setRegist('C',x);
			store(s, 'C');
		}else {
			long long int x = temp1-temp2;
			if (x<0)
				x=0;			
			setRegist(s->regist,x);					
		}	
		flag = -1;		

	} else if(flag == 22){

		structures *s = values.front();
		values.erase(values.begin());

		if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
		}

		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

		initialize(s);
		if(s->regist=='A'){
			if(s1->regist=='A'){
				load(s1, 'B');
				createCommand2("COPY",'C','B'); 
				if(s2->regist=='A'){
					load(s2, 'B');
					createCommand2("SUB",'C','B'); 
				}else {
					createCommand2("SUB",'C',s2->regist);		
				}		

			}else {
				createCommand2("COPY",'C',s1->regist);
				if(s2->regist=='A'){
					load(s2, 'B');
					createCommand2("SUB",'C','B'); 
				}else {
					createCommand2("SUB",'C',s2->regist);		
				}								 
			}
			store(s, 'C');
		}else {			
				if(s1->regist=='A'){
				load(s1, 'B'); 				
				load(s2, 'C');
				createCommand2("COPY",s->regist,'B');
				createCommand2("SUB",s->regist,'C'); 
				
			}else {
				if(s2->regist=='A'){
					load(s2, 'B');
					createCommand2("COPY",s->regist,s1->regist);
					createCommand2("SUB",s->regist,'B'); 
				}else {					
					if(s->regist!=s2->regist){
						createCommand2("COPY",s->regist,s1->regist);
						createCommand2("SUB",s->regist,s2->regist);	
					}else {
						createCommand2("COPY",'B',s->regist);
						createCommand2("COPY",s->regist,s1->regist);
						createCommand2("SUB",s->regist,'B');
					}	
				}								 
			}								
		}		
		flag = -1;

	} else if(flag == 12) {
		structures *s = values.front();
		values.erase(values.begin());

		if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
		}

		structures *s2 = values.front();
		values.erase(values.begin());

		initialize(s);
		if(s->regist=='A'){
			setRegist('C',(temp1));
			if(s2->regist=='A'){
				load(s2, 'B');
				createCommand2("SUB",'C','B'); 
			}else {
				createCommand2("SUB",'C',s2->regist);		
			}								 		
			store(s, 'C');
		}else {			
			if(s2->regist=='A'){
				load(s2, 'B');
				setRegist(s->regist,(temp1));
				createCommand2("SUB",s->regist,'B'); 
			}else {
				if(s->regist!=s2->regist){
					setRegist(s->regist,(temp1));
					createCommand2("SUB",s->regist,s2->regist);	
				}else {
					createCommand2("COPY",'B',s->regist);
					setRegist(s->regist,(temp1));
					createCommand2("SUB",s->regist,'B');
				}	
			}								 
		}								
			
		flag = -1;
	} else if(flag == 21) {
		structures *s = values.front();
		values.erase(values.begin());

		if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
		}

		structures *s1 = values.front();
		values.erase(values.begin());

		initialize(s);
		if(s->regist=='A'){
			if(s1->regist=='A'){
				load(s1, 'B');
				
				createCommand2("COPY",'C','B'); 
			
				setRegist('B',(temp2));
				createCommand2("SUB",'C','B'); 

			}else {
				createCommand2("COPY",'C',s1->regist);
				setRegist('B',(temp2));
				createCommand2("SUB",'C','B'); 						 
			}
			store(s, 'C');
		}else {			
			if(s1->regist=='A'){
				load(s1, 'B');
				createCommand2("COPY",s->regist,'B'); 
				setRegist('B',(temp2));
				createCommand2("SUB",s->regist,'B'); 

			}else {
				createCommand2("COPY",s->regist,s1->regist);
				setRegist('B',(temp2));
				createCommand2("SUB",s->regist,'B'); 							 
			}								
		}
	}

	flag = -1;
}
| value '*'value {

	structures *s = values.front();
	values.erase(values.begin());

	if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
	}

	initialize(s);

	if(flag == 22){
		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

		multiply(s,s1,s2);

	} else if(flag == 12) {
		structures *s2 = values.front();
		values.erase(values.begin());

		structures stemp1;
		stemp1.type = "Temp";
		stemp1.value = temp1;
		structures *s1 = &stemp1;

		if(temp1==2){
			if(s->regist=='A'){
				load(s2, 'C');
				createCommand2("ADD",'C','C');
				store(s, 'C');
			}else {
				load(s2, 'C');
				createCommand2("ADD",'C','C');
				createCommand2("COPY",s->regist,'C');				
			}
		} else {
				multiply(s,s1,s2);
		}

	} else if(flag == 21){
		structures *s1 = values.front();
		values.erase(values.begin());

		structures stemp2;
		stemp2.type = "Temp";
		stemp2.value = temp2;
		structures *s2 = &stemp2;

		if(temp2==2){
			if(s->regist=='A'){
				load(s1, 'C');
				createCommand2("ADD",'C','C');
				store(s, 'C');
			}else {
				load(s1, 'C');
				createCommand2("ADD",'C','C');
				createCommand2("COPY",s->regist,'C');				
			}
		} else {
				multiply(s,s1,s2);
		}

	} else if(flag == 11){

		if(s->regist=='A'){
			long long int x;
			
			x = temp1*temp2;
			setRegist('C',x);
			store(s, 'C');
		}else {
			long long int x;
		
			x = temp1*temp2;		
			setRegist(s->regist,x);					
		}

	}

	flag = -1;
	
}
| value '/' value {
	structures *s = values.front();
	values.erase(values.begin());

	if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
	}

	initialize(s);

	if(flag == 22){
		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

		divide(s,s1,s2);

	} else if(flag == 12) {
		structures *s2 = values.front();
		values.erase(values.begin());

		structures stemp1;
		stemp1.type = "Temp";
		stemp1.value = temp1;
		structures *s1 = &stemp1;

		divide(s,s1,s2);

	} else if(flag == 21){
		structures *s1 = values.front();
		values.erase(values.begin());

		structures stemp2;
		stemp2.type = "Temp";
		stemp2.value = temp2;
		structures *s2 = &stemp2;

		if(temp2==0){
			if(s->regist=='A'){
				setRegist('C',0);
				store(s, 'C');
			}else {
				long long int x;		
				setRegist(s->regist,0);					
			}
		} else if(temp2==2){
			if(s->regist=='A'){
				load(s1, 'C');
				createCommand1("HALF",'C');
				store(s, 'C');
			}else {
				load(s1, 'C');
				createCommand1("HALF",'C');
				createCommand2("COPY",s->regist,'C');				
			}
		} else {
			divide(s,s1,s2);
		}

	} else if(flag == 11){
	
		if(s->regist=='A'){
			long long int x;
			if(temp2==0)
				x=0;
			else
				x = temp1/temp2;
			setRegist('C',x);
			store(s, 'C');
		}else {
			long long int x;
			if(temp2==0)
				x=0;
			else
				x = temp1/temp2;		
			setRegist(s->regist,x);					
		}
	}

	flag = -1;
}
| value '%' value {
	structures *s = values.front();
	values.erase(values.begin());

	if(s->isIterator == true) {
			cout << endl  << "ERROR near line : " << yylineno << " : You cant change iterator inside loop : " << s->declare << " ." << endl;
			exit(1);
	}

	initialize(s);

	if(flag == 22){
		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

		mod(s,s1,s2);

	} else if(flag == 12) {
		structures *s2 = values.front();
		values.erase(values.begin());

		structures stemp1;
		stemp1.type = "Temp";
		stemp1.value = temp1;
		structures *s1 = &stemp1;

		mod(s,s1,s2);

	} else if(flag == 21){
		structures *s1 = values.front();
		values.erase(values.begin());

		structures stemp2;
		stemp2.type = "Temp";
		stemp2.value = temp2;
		structures *s2 = &stemp2;

		if(temp2==0){
			if(s->regist=='A'){
				setRegist('C',0);
				store(s, 'C');
			}else {
				long long int x;		
				setRegist(s->regist,0);					
			}
		} else {
			mod(s,s1,s2);
		}

	} else if(flag == 11){

		if(s->regist=='A'){
			long long int x;
			if(temp2==0)
				x=0;
			else
				x = temp1%temp2;
			setRegist('C',x);
			store(s, 'C');
		}else {
			long long int x;
			if(temp2==0)
				x=0;
			else
				x = temp1%temp2;		
			setRegist(s->regist,x);					
		}

	}

	flag = -1;
}
;
condition:
value EQ value{
	commands.push_back(output.size());
	int n;
	if(flag == 11){
		if((temp1 - temp2)!=0){
			commands.push_back(output.size());
			commandss.push_back("JUMP");
			n=1;
		}
		else{
			n=0;
		}

	} else if(flag == 22){
		
		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

				
		if(s1->regist=='A'){
			load(s1, 'A');
			createCommand2("COPY",'E','A'); 
			createCommand2("COPY",'C','A'); 
			if(s2->regist=='A'){
				load(s2, 'B'); 
			} else {
				createCommand2("COPY",'B',s2->regist); 
			}
			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());
			commandss.push_back("JUMP");
			createCommand2("SUB",'B','E');
			createCommand2("JZERO",'B',to_string(output.size()+3+loopCounter));
			commands.push_back(output.size());
			commandss.push_back("JUMP");

		}else {
			createCommand2("COPY",'C',s1->regist); 

			if(s2->regist=='A'){
				load(s2, 'B'); 
			} else {
				createCommand2("COPY",'B',s2->regist); 
			}
			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());
			commandss.push_back("JUMP");
			createCommand2("SUB",'B',s1->regist);
			createCommand2("JZERO",'B',to_string(output.size()+3+loopCounter));
			commands.push_back(output.size());
			commandss.push_back("JUMP");
		}
		n=2;
	} else if(flag == 12) {
		
		structures *s2 = values.front();
		values.erase(values.begin());

		setRegist('E', temp1);
		createCommand2("COPY",'C','E'); 

		if(s2->regist=='A'){
				load(s2, 'B'); 
			} else {
				createCommand2("COPY",'B',s2->regist); 
			}
			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());
			commandss.push_back("JUMP");
			createCommand2("SUB",'B','E');
			createCommand2("JZERO",'B',to_string(output.size()+3+loopCounter));
			commands.push_back(output.size());
			commandss.push_back("JUMP");
		n=2;
	} else if(flag == 21) {

		structures *s1 = values.front();
		values.erase(values.begin());

		if(s1->regist=='A'){
			load(s1, 'E');
			createCommand2("COPY",'C','E'); 			
			setRegist('B', temp2);

			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());
			commandss.push_back("JUMP");
			createCommand2("SUB",'B','E');
			createCommand2("JZERO",'B',to_string(output.size()+3+loopCounter));
			commands.push_back(output.size());
			commandss.push_back("JUMP");

		}else {
			createCommand2("COPY",'C',s1->regist); 	
			setRegist('B', temp2); 

			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());
			commandss.push_back("JUMP");
			createCommand2("SUB",'B',s1->regist);
			createCommand2("JZERO",'B',to_string(output.size()+3+loopCounter));
			commands.push_back(output.size());
			commandss.push_back("JUMP");
		}		
		
		n=2;
	}	
	commendsNum.push_back(n);
	loopCounter+=n;
	flag = -1;	
}
| value NE value{
	commands.push_back(output.size());
	int n;
	if(flag == 11){
			
		if((temp1 - temp2)==0){
			commands.push_back(output.size());
			commandss.push_back("JUMP");
			n=1;
		}
		else{
			n=0;			
		}
	} else if(flag == 22){
		
		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

				
		if(s1->regist=='A'){
			load(s1, 'A');
			createCommand2("COPY",'C','A'); 
			createCommand2("COPY",'E','A');

			if(s2->regist=='A'){
				load(s2, 'B'); 
			} else {
				createCommand2("COPY",'B',s2->regist);
			}
				createCommand2("SUB",'C','B');
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				createCommand1("JUMP",to_string(output.size()+3+loopCounter));
				createCommand2("SUB",'B','E');
				commands.push_back(output.size());
				commandss.push_back("JZERO B");		

		}else {
			createCommand2("COPY",'C',s1->regist); 

			if(s2->regist=='A'){
				load(s2, 'B'); 
			} else {
				createCommand2("COPY",'B',s2->regist);
			}

				createCommand2("SUB",'C','B');
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				createCommand1("JUMP",to_string(output.size()+3+loopCounter));
				createCommand2("SUB",'B',s1->regist);
				commands.push_back(output.size());
				commandss.push_back("JZERO B");

		}
		n=1;
	} else if(flag == 12) {
		
		structures *s2 = values.front();
		values.erase(values.begin());

		setRegist('E', temp1);
		createCommand2("COPY",'C','E'); 

		if(s2->regist=='A'){
				load(s2, 'B'); 
			} else {
				createCommand2("COPY",'B',s2->regist);
			}
				createCommand2("SUB",'C','B');
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				createCommand1("JUMP",to_string(output.size()+3+loopCounter));
				createCommand2("SUB",'B','E');
				commands.push_back(output.size());
				commandss.push_back("JZERO B");
		n=1;
	} else if(flag == 21) {

		structures *s1 = values.front();
		values.erase(values.begin());

		if(s1->regist=='A'){
			load(s1, 'E');
			createCommand2("COPY",'C','E'); 			
			setRegist('B', temp2);

			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			createCommand1("JUMP",to_string(output.size()+3+loopCounter));
			createCommand2("SUB",'B','E');
			commands.push_back(output.size());
			commandss.push_back("JZERO B");

		}else {
			createCommand2("COPY",'C',s1->regist); 	
			setRegist('B', temp2); 

			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			createCommand1("JUMP",to_string(output.size()+3+loopCounter));
			createCommand2("SUB",'B',s1->regist);
			commands.push_back(output.size());
			commandss.push_back("JZERO B");
		}		
		n=1;
	}	
	commendsNum.push_back(n);
	loopCounter+=n;
	flag = -1;
}
| value LW value{
	commands.push_back(output.size());
	int n=0;
	if(flag == 11){
			
		if((temp1 >= temp2))
			commands.push_back(output.size());
		else{
			n--;
		}
	} else if(flag == 22){
		
		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

				
		if(s1->regist=='A'){
			load(s1, 'A');
			createCommand2("COPY",'C','A'); 
			createCommand1("INC",'C');

			if(s2->regist=='A'){
				load(s2, 'B'); 

				createCommand2("SUB",'C','B');
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				commands.push_back(output.size());

			}else {			
				createCommand2("SUB",'C',s2->regist);
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				commands.push_back(output.size());
			}		

		}else {
			createCommand2("COPY",'C',s1->regist); 
			createCommand1("INC",'C');

			if(s2->regist=='A'){
				load(s2, 'B'); 

				createCommand2("SUB",'C','B');
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				commands.push_back(output.size());

			}else {			
				createCommand2("SUB",'C',s2->regist);
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				commands.push_back(output.size());
			}	

		}
		
	} else if(flag == 12) {
		
		structures *s2 = values.front();
		values.erase(values.begin());

		setRegist('A', temp1);
		createCommand2("COPY",'C','A'); 
		createCommand1("INC",'C');

		if(s2->regist=='A'){
			load(s2, 'B'); 

			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());

		}else {			
			createCommand2("SUB",'C',s2->regist);
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());
		}		

	} else if(flag == 21) {

		structures *s1 = values.front();
		values.erase(values.begin());

		if(s1->regist=='A'){
			load(s1, 'A');
			createCommand2("COPY",'C','A'); 
			createCommand1("INC",'C');			
			setRegist('B', temp2);

			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());

		}else {
			createCommand2("COPY",'C',s1->regist);
			createCommand1("INC",'C'); 	
			setRegist('B', temp2); 

			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());
		}		
		

	}
	n++;
	if(n!=0){
		commandss.push_back("JUMP");
	}
	commendsNum.push_back(n);
	loopCounter+=n;
	flag = -1;
}
|  value GR value{
	commands.push_back(output.size());
	int n=0;
	if(flag == 11){
			
		if((temp1 <= temp2)){
			commands.push_back(output.size());
			commandss.push_back("JUMP");
		}
		else{
			n--;
		}
	} else if(flag == 22){
		
		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

				
		if(s1->regist=='A'){
			load(s1, 'A');
			createCommand2("COPY",'C','A'); 

			if(s2->regist=='A'){
				load(s2, 'B'); 

				createCommand2("SUB",'C','B');
				commands.push_back(output.size());

			}else {			
				createCommand2("SUB",'C',s2->regist);
				commands.push_back(output.size());
			}		

		}else {
			createCommand2("COPY",'C',s1->regist); 

			if(s2->regist=='A'){
				load(s2, 'B'); 

				createCommand2("SUB",'C','B');
				commands.push_back(output.size());

			}else {			
				createCommand2("SUB",'C',s2->regist);
				commands.push_back(output.size());
			}	

		}
		
	} else if(flag == 12) {
		
		structures *s2 = values.front();
		values.erase(values.begin());

		setRegist('A', temp1);
		createCommand2("COPY",'C','A'); 

		if(s2->regist=='A'){
			load(s2, 'B'); 

			createCommand2("SUB",'C','B');
			commands.push_back(output.size());

		}else {			
			createCommand2("SUB",'C',s2->regist);
			commands.push_back(output.size());
		}		

	} else if(flag == 21) {

		structures *s1 = values.front();
		values.erase(values.begin());

		if(s1->regist=='A'){
			load(s1, 'A');
			createCommand2("COPY",'C','A'); 			
			setRegist('B', temp2);

			createCommand2("SUB",'C','B');
			commands.push_back(output.size());

		}else {
			createCommand2("COPY",'C',s1->regist); 	
			setRegist('B', temp2); 

			createCommand2("SUB",'C','B');
			commands.push_back(output.size());
		}		
		
	}	
	n++;
	if(flag!=11){
		commandss.push_back("JZERO C");
	} 
	commendsNum.push_back(n);
	loopCounter+=n;
	flag = -1;
}
| value GRE value{
	commands.push_back(output.size());
	int n=0;
	if(flag == 11){
			
		if((temp1 < temp2)){
			commands.push_back(output.size());
			commandss.push_back("JUMP");
		}
		else{
			n--;
		}
	} else if(flag == 22){
		
		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

				
		if(s1->regist=='A'){
			load(s1, 'A');
			createCommand2("COPY",'C','A'); 
			createCommand1("INC",'C'); 

			if(s2->regist=='A'){
				load(s2, 'B'); 

				createCommand2("SUB",'C','B');
				commands.push_back(output.size());

			}else {			
				createCommand2("SUB",'C',s2->regist);
				commands.push_back(output.size());
			}		

		}else {
			createCommand2("COPY",'C',s1->regist); 
			createCommand1("INC",'C'); 

			if(s2->regist=='A'){
				load(s2, 'B'); 

				createCommand2("SUB",'C','B');
				commands.push_back(output.size());

			}else {			
				createCommand2("SUB",'C',s2->regist);
				commands.push_back(output.size());
			}	

		}
		
	} else if(flag == 12) {
		
		structures *s2 = values.front();
		values.erase(values.begin());

		setRegist('A', temp1);
		createCommand2("COPY",'C','A'); 
		createCommand1("INC",'C'); 

		if(s2->regist=='A'){
			load(s2, 'B'); 

			createCommand2("SUB",'C','B');
			commands.push_back(output.size());

		}else {			
			createCommand2("SUB",'C',s2->regist);
			commands.push_back(output.size());
		}		

	} else if(flag == 21) {

		structures *s1 = values.front();
		values.erase(values.begin());

		if(s1->regist=='A'){
			load(s1, 'A');
			createCommand2("COPY",'C','A'); 
			createCommand1("INC",'C'); 			
			setRegist('B', temp2);

			createCommand2("SUB",'C','B');
			commands.push_back(output.size());

		}else {
			createCommand2("COPY",'C',s1->regist); 	
			createCommand1("INC",'C'); 
			setRegist('B', temp2); 

			createCommand2("SUB",'C','B');
			commands.push_back(output.size());
		}		
		
	}	
	n++;
	if(flag!=11){
		commandss.push_back("JZERO C");
	} 
	commendsNum.push_back(n);
	loopCounter+=n;
	flag = -1;
}
| value LWE value{
	int n=0;
	commands.push_back(output.size());
	if(flag == 11){
			
		if((temp1 > temp2)){
			commands.push_back(output.size());
			commandss.push_back("JUMP");
		}
		else{
			n--;
		}
	} else if(flag == 22){
		
		structures *s1 = values.front();
		values.erase(values.begin());

		structures *s2 = values.front();
		values.erase(values.begin());

				
		if(s1->regist=='A'){
			load(s1, 'A');
			createCommand2("COPY",'C','A'); 

			if(s2->regist=='A'){
				load(s2, 'B'); 

				createCommand2("SUB",'C','B');
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				commands.push_back(output.size());

			}else {			
				createCommand2("SUB",'C',s2->regist);
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				commands.push_back(output.size());
			}		

		}else {
			createCommand2("COPY",'C',s1->regist); 

			if(s2->regist=='A'){
				load(s2, 'B'); 

				createCommand2("SUB",'C','B');
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				commands.push_back(output.size());

			}else {			
				createCommand2("SUB",'C',s2->regist);
				createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
				commands.push_back(output.size());
			}	

		}
		
	} else if(flag == 12) {
		
		structures *s2 = values.front();
		values.erase(values.begin());

		setRegist('A', temp1);
		createCommand2("COPY",'C','A'); 

		if(s2->regist=='A'){
			load(s2, 'B'); 

			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());

		}else {			
			createCommand2("SUB",'C',s2->regist);
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());
		}		

	} else if(flag == 21) {

		structures *s1 = values.front();
		values.erase(values.begin());

		if(s1->regist=='A'){
			load(s1, 'A');
			createCommand2("COPY",'C','A'); 			
			setRegist('B', temp2);

			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());

		}else {
			createCommand2("COPY",'C',s1->regist); 	
			setRegist('B', temp2); 

			createCommand2("SUB",'C','B');
			createCommand2("JZERO",'C',to_string(output.size()+2+loopCounter));
			commands.push_back(output.size());
		}		
		

	}	
	n++;
	if(flag!=11){
		commandss.push_back("JUMP");
	} 
	commendsNum.push_back(n);
	loopCounter+=n;
	flag = -1;	
}
;
value:
NUM {
	if(flag == -1){
		temp1 = atoll($1);
		flag = 1;
	} else{
		temp2 = atoll($1);
		flag = flag*10;
		flag += 1;
	}
}
| identifier {
	structures* s = values.back();
	/*if(s->type == "Array"){
		
		
		s->initializedArray.insert(make_pair(s->lastTarget,true));
		if(s->initializedArray.find(s->lastTarget) == s->initializedArray.end()){
			cout << "ERROR near line : " << yylineno << " : That variable is not initialized : " << s->declare << "(" << s->lastTarget << ")" << endl;
			exit(1);
		}	
	} else*/ if(s->type == "Num"){
		
		if(s->initialized == false){
			cout << endl << "ERROR near line : " << yylineno << " : That variable is not initialized : " << s->declare << " ." << endl;
			exit(1);
		}

	}
	if(flag == -1){
		flag = 2; 
	} else{
		flag = flag*10;
		flag += 2;
	}
}
;
identifier:
PIDENTIFIER {
	if(declarationSet.find($1)==declarationSet.end()){
		cout << endl << "ERROR near line : " << yylineno << " : That variable is not declared : " << $1 << " ." << endl;
		exit(1);
	}

	if(declarationSet.at($1).type=="Array"){
		cout << endl << "ERROR near line : " << yylineno << " : Bad usage of array : " << $1 << " ." << endl;
		exit(1);
	}

	structures *s = &declarationSet.at($1);
	values.push_back(s);
	
}
| PIDENTIFIER '(' PIDENTIFIER ')' {
	if(declarationSet.find($1)==declarationSet.end()){
		cout << endl << "ERROR near line : " << yylineno << " : That array is not declared : " << $1 << " ." << endl;
		exit(1);
	}
	if(declarationSet.find($3)==declarationSet.end()){
		cout << endl << "ERROR near line : " << yylineno << " : That is not declared variable : " << $3 << " ." << endl;
		exit(1);
	}
	if(declarationSet.at($3).type!="Num"){
		cout << endl << "ERROR near line : " << yylineno << " : Can't use arry here, use variable instead : " << $3 << " ." << endl;
		exit(1);
	}
	if(declarationSet.at($1).type=="Num"){
		cout << endl << "ERROR near line : " << yylineno << " : Bad usage of variable : " << $1 << " ." << endl;
		exit(1);
	}
	if(declarationSet.at($3).initialized==false){
		cout << endl << "ERROR near line : " << yylineno << " : That variable is not initialized : " << $3 << " ." << endl;
		exit(1);
	}
	
	structures* s = &declarationSet.at($1);
	string hole = $1;
	structures *pstr = new structures;
	createArray(pstr,s->declare,s->start,s->end,s->memory);
	pstr->initialized=true;
	pstr->variable = true;
	pstr->declareTarget = $3;
	
	values.push_back(pstr);
}
| PIDENTIFIER '(' NUM ')' {

	if(declarationSet.find($1)==declarationSet.end()){
		cout << endl << "ERROR near line : " << yylineno << " : That array is not declared : " << $1 << " ." << endl;
		exit(1);
	}
	if(declarationSet.at($1).type=="Num"){
		cout << endl << "ERROR near line : " << yylineno << " : Bad usage of variable : " << $1 << " ." << endl;
		exit(1);
	}
	if(!((declarationSet.at($1).start <= atoll($3) )&&( atoll($3) <= declarationSet.at($1).end))){
		cout << endl  << "ERROR near line : " << yylineno << " : Out of bounds array : " << $1 << " ." << endl;
		exit(1);
	}

	structures* s = &declarationSet.at($1);
	string hole = $1;
	structures *pstr = new structures;
	createArray(pstr,s->declare,s->start,s->end,s->memory);
	pstr->initialized=true;
	pstr->variable = false;
	pstr->lastTarget = atoll($3);
	


	values.push_back(pstr);
	
}
;

%%

void forHelper(string hole,char reg){
	if(declarationSet.at(hole).regist=='A'){
		store(&declarationSet.at(hole),reg);
		commands.push_back(output.size());
		load(&declarationSet.at(hole),reg);
		createCommand1("DEC",reg);
		commands.push_back(output.size());
		//store(&declarationSet.at(hole),reg);
		createCommand1("STORE", reg);
	} else {
		createCommand1("DEC",reg);
		commands.push_back(output.size());
	}
}

void divide(structures *s, structures *s1, structures *s2){
	
	if(s2->type=="Temp"){
		setRegist('B',s2->value);
	} else {
		if(s2->regist=='A'){
			load(s2,'B');
		} else {
			createCommand2("COPY",'B',s2->regist);
		}
	}

	if(s1->type=="Temp"){
		setRegist('A',s1->value);
	} else {
		if(s1->regist=='A'){
			load(s1,'A');
		} else {
			createCommand2("COPY",'A',s1->regist);
		}
	}

	createCommand2("JZERO",'B',to_string(output.size()+2+loopCounter));
	createCommand1("JUMP",to_string(output.size()+3+loopCounter)); 
	createCommand2("SUB",'C','C');
	createCommand1("JUMP",to_string(output.size()+28+loopCounter)); 

	createCommand2("SUB",'E','E');
	createCommand1("INC",'E');
	createCommand2("COPY",'C','A');

	createCommand2("SUB",'C','B');	
	createCommand2("JZERO",'C',to_string(output.size()+5+loopCounter));
	createCommand2("ADD",'C','B');
	createCommand2("ADD",'B','B');	
	createCommand2("ADD",'E','E');
	createCommand1("JUMP",to_string(output.size()-5+loopCounter)); 

	// s1 - A s2 - B multiple - E
	createCommand2("COPY",'D','A');
	createCommand1("INC",'D');
	createCommand2("SUB",'D','B');
	createCommand2("JZERO",'D',to_string(output.size()+3+loopCounter));
	createCommand2("SUB",'A','B');
	createCommand2("ADD",'C','E');

	createCommand1("HALF",'B');	
	createCommand1("HALF",'E');

	createCommand2("JZERO",'E',to_string(output.size()+10+loopCounter));

	createCommand2("COPY",'D','A');
	createCommand1("INC",'D');
	createCommand2("SUB",'D','B');
	createCommand2("JZERO",'D',to_string(output.size()+3+loopCounter));
	createCommand2("SUB",'A','B');
	createCommand2("ADD",'C','E');

	createCommand1("HALF",'B');	
	createCommand1("HALF",'E');
	createCommand1("JUMP",to_string(output.size()-9+loopCounter));
	store(s,'C');
}

void mod(structures *s, structures *s1, structures *s2){

	if(s2->type=="Temp"){
		setRegist('B',s2->value);
	} else {
		if(s2->regist=='A'){
			load(s2,'B');
		} else {
			createCommand2("COPY",'B',s2->regist);
		}
	}

	if(s1->type=="Temp"){
		setRegist('C',s1->value);
	} else {
		if(s1->regist=='A'){
			load(s1,'C');
		} else {
			createCommand2("COPY",'C',s1->regist);
		}
	}

	createCommand2("JZERO",'B',to_string(output.size()+2+loopCounter));
	createCommand1("JUMP",to_string(output.size()+3+loopCounter)); 
	createCommand2("SUB",'C','C');
	createCommand1("JUMP",to_string(output.size()+28+loopCounter)); 

	createCommand2("SUB",'A','A');
	createCommand1("INC",'A');
	createCommand2("COPY",'E','C');

	createCommand2("SUB",'E','B');	
	createCommand2("JZERO",'E',to_string(output.size()+5+loopCounter));
	createCommand2("ADD",'E','B');
	createCommand2("ADD",'B','B');	
	createCommand2("ADD",'A','A');
	createCommand1("JUMP",to_string(output.size()-5+loopCounter)); 

	// s1 - C s2 - B multiple - A
	createCommand2("COPY",'D','C');
	createCommand1("INC",'D');
	createCommand2("SUB",'D','B');
	createCommand2("JZERO",'D',to_string(output.size()+3+loopCounter));
	createCommand2("SUB",'C','B');
	createCommand2("ADD",'E','A');

	createCommand1("HALF",'B');	
	createCommand1("HALF",'A');

	createCommand2("JZERO",'A',to_string(output.size()+10+loopCounter));

	createCommand2("COPY",'D','C');
	createCommand1("INC",'D');
	createCommand2("SUB",'D','B');
	createCommand2("JZERO",'D',to_string(output.size()+3+loopCounter));
	createCommand2("SUB",'C','B');
	createCommand2("ADD",'E','A');

	createCommand1("HALF",'B');	
	createCommand1("HALF",'A');
	createCommand1("JUMP",to_string(output.size()-9+loopCounter));

	store(s,'C');
}

void multiply(structures *s, structures *s1, structures *s2){

	if(s2->type=="Temp"){
		setRegist('B',s2->value);
	} else {
		if(s2->regist=='A'){
			load(s2,'B');
		} else {
			createCommand2("COPY",'B',s2->regist);
		}
	}

	if(s1->type=="Temp"){
		setRegist('A',s1->value);
	} else {
		if(s1->regist=='A'){
			load(s1,'A');
		} else {
			createCommand2("COPY",'A',s1->regist);
		}
	}

	createCommand2("SUB",'C', 'C');
	createCommand2("JZERO",'B',to_string(output.size()+7+loopCounter));
	createCommand2("JODD",'B',to_string(output.size()+2+loopCounter)); //nieparzyste j
	createCommand1("JUMP",to_string(output.size()+2+loopCounter));
	createCommand2("ADD",'C', 'A');
	createCommand2("ADD",'A', 'A');
	createCommand1("HALF",'B');
	createCommand1("JUMP",to_string(output.size()-6+loopCounter));
	store(s,'C');
}

void makeRegists(){
	int counter = 0;
	while (counter!=8){
		regist r;
		r.declare="";
		r.regist =  char('A'+counter);
		regists.insert(make_pair(char('A'+counter),r));
		counter++;
	}
}

void setRegist(char regist,long long int value){
 
	long long int counter1=0;
	long long int counter2=0;
	long long int v1=value;
	long long int v2=value;
	
	vector<string> out1;
	vector<string> out2;
	string str1 = " ";
	str1+=regist;
	while(v1>0){
		if(v1%2==0){
			v1=v1/2;
			counter1+=5;
			out1.push_back("ADD" + str1 + str1);
		} else {
			v1=v1-1;
			counter1+=1;
			out1.push_back("INC" + str1);
		}
	}
	if(regists.at(regist).value!=-1){
		long long int v3 = regists.at(regist).value;
		if(v3>v2){
			while(v3!=v2){
				if(v3%2==0 && (v3/2)>v2){
					v3=v3/2;
					counter1+=1;
					out2.push_back("HALF" + str1);
				} else {
					v3=v3-1;
					counter1+=1;
					out2.push_back("DEC" + str1);
				}
			}
		} else {
			while(v3!=v2){
				if((v3*2)<v2){
					v3=v3*2;
					counter1+=5;
					out2.push_back("ADD" + str1 + str1);
				} else {
					v3=v3+1;
					counter1+=1;
					out2.push_back("INC" + str1);
				}
			}
		}
	}	
	string data;
	if(counter1>counter2 && regists.at(regist).value!=-1){
		int size = out2.size();
		for(int i=0;i<size;i++){
			data = out2.back();
			out2.pop_back();
			output.push_back(data);
		}
	}else {
		
		out1.push_back("SUB" + str1 + str1);
		int size = out1.size();
		for(int i=0;i<size;i++){
			data = out1.back();
			out1.pop_back();
			output.push_back(data);
		}
	}
	
}

structures createNum(string declare,char regist,long long int memory){
	 structures s;
	s.initialized = false;
	s.declare = declare;
	s.regist = regist;
	s.isIterator = false;
	s.value = -1;
	if(regist == 'A'){
		s.memory = memory;
	}
	else{
		regists.at(regist).hasStructure = true;
		regists.at(regist).declare = declare;
	}
	s.type = "Num";
	return s;
}

structures createArray(string declare,long long int start,long long int end,long long int memory){
	structures s;
	s.declare = declare;
	s.type = "Array";
	s.regist = 'A';
	s.end = end;
	s.start = start;
	s.size = abs(start)-abs(end)+1;
	s.memory = memory;
	s.isIterator = false;
	return s;
}
void createArray(structures* s,string declare,long long int start,long long int end,long long int memory){
	s->declare = declare;
	s->type = "Array";
	s->regist = 'A';
	s->end = end;
	s->start = start;
	s->size = abs(start)-abs(end)+1;
	s->memory = memory;
	s->isIterator = false;
}
void initialize(structures *s){
	if(s->type=="Num")
		s->initialized = true;
	else if(s->type == "Array"){		
		if(s->variable){
			
		} else {
			declarationSet.at(s->declare).initializedArray.insert(make_pair(s->lastTarget,true));
		}
	}
}
string prepereIterator(){
	for (int i=0; i<3;i++){
		if(regists.at(char('H'-i)).hasIterator==false){
			if(regists.at(char('H'-i)).hasStructure==true)
				putToMemory(regists.at(char('H'-i)).declare);
			regists.at(char('H'-i)).hasIterator=true;
			regists.at(char('H'-i)).hasStructure=false;
			string declare = regists.at(char('H'-i)).declare;
			regists.at(char('H'-i)).declare="";
			actualRegist = char('H'-i);
			return declare;
		}
	}
	return "-1";
}

void putToRegist(string declare,char s1){
	regists.at(s1).hasIterator=false;
	regists.at(s1).hasStructure=true;
	regists.at(s1).declare=declare;	
	if(declarationSet.at(declare).initialized==true){
		setRegist('A',declarationSet.at(declare).memory);
		createCommand1("LOAD", s1);
	}
	declarationSet.at(declare).regist=s1;
	declarationSet.at(declare).memory=-1;
	
}

void putToMemory(string declare){
	if(declarationSet.at(declare).initialized==true){
		setRegist('A',memory);
		createCommand1("STORE", declarationSet.at(declare).regist);
	}
	declarationSet.at(declare).lastRegist = declarationSet.at(declare).regist;
	declarationSet.at(declare).regist='A';
	declarationSet.at(declare).memory=memory;
	memory++;	
}

void store(structures *s, char s1){
	
	if(s->type == "Array"){
		if(s->variable){
			load(&declarationSet.at(s->declareTarget),'A');
			setRegist('E',(s->memory));
			createCommand2("ADD", 'A', 'E');
			setRegist('E',(s->start));
			createCommand2("SUB", 'A', 'E');
			createCommand1("STORE", s1);
		} else {
			setRegist('A',s->memory+s->lastTarget - s->start); 
			createCommand1("STORE", s1);
		}
	}
	else if(s->type == "Num"){
		if(s->regist=='A'){
			setRegist('A',s->memory);
			createCommand1("STORE", s1);
		} else {
			createCommand2("COPY",s->regist ,s1);
		}
	}
}

void store(long long int mem, char s1){
	setRegist('A',mem);
	createCommand1("STORE", s1);
}

void load(long long int mem, char s1){
	setRegist('A',mem);
	createCommand1("LOAD", s1);
}

void load(structures *s, char s1){
	if(s->type == "Array"){
		if(s->variable){
			load(&declarationSet.at(s->declareTarget),'A');
			setRegist('E',(s->memory));
			createCommand2("ADD", 'A', 'E');
			setRegist('E',(s->start));
			createCommand2("SUB", 'A', 'E');
			createCommand1("LOAD", s1);
	
		} else {
			setRegist('A',s->memory+s->lastTarget - s->start); 
			createCommand1("LOAD", s1);
			
		}
	}
	else if(s->type == "Num"){
		if(s->regist=='A'){
			setRegist('A',s->memory);
			createCommand1("LOAD", s1);
		} else {
			createCommand2("COPY",s1 ,s->regist);
		}
	}
}

void createCommand1(string s, char s1){
	string str =s;
	str+=" ";
	str+=s1;
	output.push_back(str);
}

void createCommand2(string s, char s1,char s2){
	string str =s;
	str+=" ";
	str+=s1;
	str+=" ";
	str+=s2;
	output.push_back(str);
}

void createCommand1(string s, string s1){
	string str =s;
	str+=" ";
	str+=s1;
	output.push_back(str);
}

void createCommand2(string s, char s1,string s2){
	string str =s;
	str+=" ";
	str+=s1;
	str+=" ";
	str+=s2;
	output.push_back(str);
}

int yyerror(char const* str){
	string x = "2";
	if(yychar<128)
		cout << endl << " ERROR near line : " << yylineno << " : Unexpected character '" << char(yychar) << "'" << endl;
	else
		cout << endl << " ERROR near line : " << yylineno << " " << str << endl;
	endProgram =true;
	return 1;
}

int main(int argc, char** argv) {
	//yydebug = 1;
	makeRegists();
	endProgram = false;
	iterators = 0;
	memory = 0;
	loopCounter=0;
	flag = -1;
	//values.reserve(1000);
	extern FILE * yyin; 
	FILE *myfile = fopen(argv[1], "r");

	if (!myfile) {
		printf( "I can't open InputFileHTML.html");
		return -1;
	}

	yyin = myfile;
	yyparse();
	if(!endProgram){
		ofstream myfile;
		myfile.open (argv[2]);
		for(int i = 0; i < output.size(); i++){
				myfile << output.at(i) << endl;
				//cout << i << " " << output.at(i) << endl;
		}
		myfile.close();
	} else {
		for(int i = 0; i < output.size(); i++){
				
				//cout << i << " " << output.at(i) << endl;
		}
	}
}
