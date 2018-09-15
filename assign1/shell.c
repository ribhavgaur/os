#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <stdbool.h>
#include <limits.h>

//cd ke baad waale spaces 
//ls mein quotes waale spaces


typedef struct node
{
    char* n;
    struct node* next;
}node;

node* insertend(char* data, node* head)
{
    node* p;
    p=(node *) malloc (sizeof(node));
    p->n=(char *)malloc(1000*sizeof(char));
    p->n=data;
    p->next=head;
    return p;
    
}

void print (node * head)
{
    int sizeoflist=1;
    node* q=head;
    while(q!=NULL){
        printf("%d ",sizeoflist);
        
        printf("%s \n", q->n);
        sizeoflist++;
        q=q->next;
    }
    //printf("\n");
    return ;
}

char* parser(char* str)
{
  
   int i=0;
   char* x=(char *)malloc(1000*sizeof(char));
   int quote=0;
   for(i=0; i<strlen(str); i++)
   {
       if(str[i]=='"')
       {
           quote++;
       }
       else
       x[i-quote]=str[i];
   }
   return x;
}

char* cdhistoryparser(char* str)
{
  //printf(" %s \n ", str);
   int i=0;
   char* x=(char *)malloc(1000*sizeof(char));
   int count=0;
   bool firstlettercame=false;
   for(i=0; i<strlen(str); i++)
   {
       
      if(str[i]!=32)
      {
         
          break;
      }
       else 
       {
           
           count++;
       }
       
   }
    i = count;
   for (i=count;i<strlen(str);i++)
   {
       x[i-count] = str[i];
   }
   //printf(" %d\n ", count);
   return x;
}

bool hasquote(char* str)
{
     int i=0;
   
  
   for(i=0; i<strlen(str); i++)
   {
       if(str[i]=='"')
       return true;
   }
   return false;
}




int main()
{
    char* str;
    char* token,* string;
    bool quote=false;
        bool isbalanced=true;
    
    //char * ptr = (char *)malloc(PATH_MAX*sizeof(char));
    char ptr[PATH_MAX];
    getcwd(ptr,sizeof(ptr));
    

    strcat(ptr,"/history.txt");
    
    //printf("%s\n",ptr);
    
    node* head=NULL;
    while(1)
    {
        
        str=(char *)malloc(1000*sizeof(char));
        char cdr[PATH_MAX];
        getcwd(cdr,sizeof(cdr));
        strcat(cdr," MTL458 > ");
        printf("%s",cdr);
        
        //printf("ls");
       
        gets(str);
        //printf("ls");
        char * str3 = (char *)malloc(1000*sizeof(char));
        strcpy(str3,str);
        strcat(str3,"\n");
        // printf("we are prinintg",str);
        // str=parser(str);
        //printf("%s\n",str);
        head=insertend(str,head);
        
        int rc=fork();
        if(rc<0)
            printf(stderr,"fork failed\n");
        
        if (rc==0)
        {
            string = strdup(str);
            char* myargs[100];
            myargs[0] = (char *)malloc(1000*sizeof(char));
            myargs[1] = (char *)malloc(1000*sizeof(char));
            myargs[2] = (char *)malloc(1000*sizeof(char));
            
            int count=0;
            
            
            while ((token = strsep(&string, " ")) != NULL)
            {
                //printf("%s\n",token);
                 

                quote=hasquote(token);
                token=parser(token);
                if(quote)
                isbalanced=!(isbalanced);
                //printf("%d \n",quote);
                  //printf("%d\n",isbalanced);
                
                if((token[0]==0)&&(!isbalanced))
                    {
                        //printf("came");
                        strcat(myargs[count]," ");
                        continue;
                    }
                else if ((isbalanced)&&(token[0]==0))
                    {
                        continue;
                    }
                else if ((quote)&&(isbalanced))
                {
                    strcat(myargs[count]," ");
                    strcat(myargs[count],token);
                    count++;
                    continue;

                        
                } 
                else if ((!quote)&&(!isbalanced))  
                {
                    strcat(myargs[count]," ");
                    strcat(myargs[count],token);
                    continue;
                } 
                

                    
                strcpy(myargs[count],token);
                if(isbalanced)
                count++;
               
                // }
            }
            myargs[count]=NULL;
               myargs[3]=NULL;
            // }

            count = 0;
            //printf("%s\n",myargs[1]);
            //myargs[2]=NULL;
            if((strcmp(myargs[0],"cd")==0)||(strcmp(myargs[0],"history")==0))
            {
                return 0;
            }
            else
            {
                if(execvp(myargs[0],myargs)<0)
                {
                    //add str3 to the file
                    FILE* f1;
                    f1 = fopen(ptr,"a");
                    if(f1==NULL)
                    {
                        //printf(" file ot ehre");
                        f1 = fopen(ptr,"w");
                    }
                        printf("Invalid System Call\n");
                    fprintf(f1,str3);
                    fclose(f1);
                }
            }
        }  
        else
        {
            int rc_wait=wait(NULL);
            //printf("Came to parent");
            FILE* f1;
            f1 = fopen(ptr,"a");
            

            if(f1==NULL)
            {
                //printf(" file ot ehre");
                f1 = fopen(ptr,"w");
            }
            char * str2 = (char *)malloc(1000*sizeof(char));
            strcpy(str2,str);
            strcat(str2,"\n");
            fprintf(f1,str2);
            fclose(f1);
            str=parser(str);
            str=cdhistoryparser(str);
            string = strdup(str);
            token = strsep(&string, " ");
            //string = cdhistoryparser(string);
            if(token[0]=='c' && token[1] == 'd')
            {
                //printf("Here");
                char* myargs;
                myargs = (char *)malloc(1000*sizeof(char));
                strcpy(myargs,string);
                if(chdir(myargs)==-1)
                    printf("No such file or directory \n");
            }
            else if (strcmp(token,"history")==0)
            {
                FILE* f2;
                char ch;
                f2 = fopen(ptr,"r");
                   while((ch = fgetc(f2)) != EOF)
                    printf("%c", ch);
 
                fclose(f2);
                //print(head);
            }
            
        }     
    }
}