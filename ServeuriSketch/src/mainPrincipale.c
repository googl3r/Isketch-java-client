#include <stdio.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#include<pthread.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<netdb.h>
#include<time.h>
#include<signal.h>
#define TAILLE_BUFFER 50
char buffer[TAILLE_BUFFER];
char *fichier="mesMots.txt";
FILE* monFichier=NULL;
FILE* maBase=NULL;
int MaxJoueur=4,tour=10;
int timeout=15;
int flagTimeout=0;
int flagScore=0;
int flagCourbe=0;
int flagMotTrouver=0;
int flagPass=0;
int nbCheat=0;
int mesSpectateurs[10];
int nbSpectateur=0;
char* nomGagnant;
/*structure du joueur */
char *aDessiner=NULL;
typedef struct Joueur Joueur;
struct Joueur{
  char *nom;
  char *type;
  int score;
  int socket_id;
  struct Joueur *suivant; 
};

typedef struct FileJoueur FileJoueur;
struct FileJoueur
{
  int TourDessinateur;
  int taille;
  Joueur *premier;
};

/*Structure d'un dessin */
typedef struct Dessin Dessin;
struct Dessin{
  char *couleur;
  char *ligne;
  char * size;
};

Joueur *dessinateur=NULL;
FileJoueur *initialiser()
{
  FileJoueur *fileJoueur=malloc(sizeof(*fileJoueur));
  fileJoueur->taille=0;
  fileJoueur->TourDessinateur=1;
  fileJoueur->premier=NULL;
  return fileJoueur;
}

FileJoueur *mesJoueurs;
void creerJoueur(char *nom,int num,FileJoueur *fileJoueur)
{
  Joueur *joueur=malloc(sizeof(*joueur));
  if(joueur==NULL || fileJoueur==NULL)
    {
      perror("impossible d'allouer de la memoire");
      exit(EXIT_FAILURE);
    }
  joueur->nom=nom;
  joueur->socket_id=num;
  joueur->type="chercheur";
  joueur->score=0;
  joueur->suivant=NULL;
  if(fileJoueur->premier!=NULL) /*le cas ou la file n'est pas vide */
    {
      /*on se positionne a la fin de la file */
      Joueur *joueurActuel=fileJoueur->premier;
      while(joueurActuel->suivant!=NULL)
	{
	  joueurActuel=joueurActuel->suivant;
	}
      joueurActuel->suivant=joueur;
      fileJoueur->taille++;
    }
  else /*le cas ou la file est vide */
    {
      fileJoueur->premier=joueur;
      fileJoueur->taille++;
    }
  printf("Le joueur a eté ajouter avec succé il a pour nom :%s \n",joueur->nom);

}
Joueur * getDessinateur()
{
  return dessinateur;
}
void setScore(FileJoueur *fileJoueur,int socket_id,int score)
{
  if(fileJoueur==NULL)
    {
      perror("file vide");exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueur->premier;
  while(joueur!=NULL)
    {
      if(joueur->socket_id==socket_id)
	{
	 
	  joueur->score+=score;
	}
      joueur=joueur->suivant;
    }
}






char* getNomJoueur(FileJoueur *fileJoueur,int socket_id)
{
  if(fileJoueur==NULL)
    {
      perror("file vide");exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueur->premier;
  while(joueur!=NULL)
    {
      if(joueur->socket_id==socket_id)
	{
	  return joueur->nom;
	}
      joueur=joueur->suivant;
    }
  return NULL;
}

Joueur *getJoueur(FileJoueur *fileJoueur,int socket)
{
if(fileJoueur==NULL)
    {
      perror("file vide");exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueur->premier;
  while(joueur!=NULL)
    {
      if(joueur->socket_id==socket)
	{
	  return joueur;
	}
      joueur=joueur->suivant;
    }
  return NULL;
}
void declencherTimeOut(FileJoueur *fileJoueur)
{
 
  if(fileJoueur==NULL)
    {
      perror("file Vide ");exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueur->premier;
  while(joueur!=NULL)
    {
      char *message;
      message=(char *)malloc(sizeof(char *));
      strcat(message,"WORD_FOUND_TIMEOUT/30/\n");
      write(joueur->socket_id,message,strlen(message));
      joueur=joueur->suivant;
    }
}

void DeclencherMotTrouver(FileJoueur *fileJoueur, int socket_id)
{
  if(fileJoueur==NULL)
    {
      perror("file vide");exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueur->premier;
  while(joueur!=NULL)
    {
      char *message;
      
      message=(char *)malloc(sizeof(char *));
      strcat(message,"WORD_FOUND/");
      
      strcat(message,getNomJoueur(fileJoueur,socket_id));
      strcat(message,"/\n");     
      write(joueur->socket_id,message,strlen(message));
      joueur=joueur->suivant;
    }
}


void finRound(FileJoueur *fileJoueur,char *motChercher,char *nomVainqueur)
{
  if(fileJoueur==NULL)
    {
      perror("file vide");exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueur->premier;
  while(joueur!=NULL)
    {
      char *message;
      message=(char *)malloc(sizeof(char *));
      strcat(message,"END_ROUND/");
      strcat(message,nomVainqueur);
      strcat(message,"/");
      strcat(message,motChercher);
      strcat(message,"/\n");
      write(joueur->socket_id,message,strlen(message));
      joueur=joueur->suivant;
    }
}

void setDessinateur(FileJoueur  *fileJoueur)
{
  int i=1;
  Joueur *joueur=fileJoueur->premier;
  while(joueur!=NULL)
    {
      if(i==fileJoueur->TourDessinateur)
	{
	  printf("%s : joueur tiret nom \n", joueur->nom);
	  joueur->type="dessinateur";
	  dessinateur=joueur;
	  printf("le dessinateur %s : joueur :%s \n",dessinateur->nom,joueur->nom);
	}
      else
	{
	  joueur->type="chercheur";
	  printf("le chercheur %s : joueur :%s \n",dessinateur->nom,joueur->nom);
	}
      i++;
      joueur=joueur->suivant;
    }
  fileJoueur->TourDessinateur++;
}
void EnvoyerScore(FileJoueur *fileJoueur)
{
  if(fileJoueur==NULL)
    {
      perror("file vide");
      exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueur->premier;
  Joueur *jou=fileJoueur->premier;
  char *message;
  message=(char *)malloc(sizeof(char *));
  strcat(message,"SCORE_ROUND/");
  while(joueur!=NULL)
    {
      int scoreJoueur=joueur->score;
      char str[10];
      sprintf(str,"%d",scoreJoueur);
      strcat(message,joueur->nom);
      strcat(message,"/");   
      strcat(message, str);
      strcat(message,"/");
      joueur=joueur->suivant;
    }
  strcat(message,"\n");
  while(jou!=NULL)
    {
      write(jou->socket_id,message,strlen(message));
      jou=jou->suivant;
    }
}
void Talk(FileJoueur *fileJoueur,int socket,char *msgText)
{
  int i=0;
  

  if(fileJoueur==NULL)
    {
      perror("file vide");
      exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueur->premier;
  char *nom=getNomJoueur(fileJoueur,socket);
  while(joueur!=NULL)
    {
      char *message;
      message=(char *)malloc(sizeof(char *));
      strcat(message,"LISTEN/");
      strcat(message,nom);
      strcat(message,"/");
      strcat(message,msgText);
      strcat(message,"/");
      strcat(message,"\n");
      write(joueur->socket_id,message,strlen(message));
      joueur=joueur->suivant;
    }

}



void EnvoyerRole(FileJoueur  *fileJoueur,char *aDessiner)
{
  int i=0;
  

  if(fileJoueur==NULL)
    {
      perror("file vide");
      exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueur->premier;
  printf("le premier joueur a pour nom :%s \n",joueur->nom);
  while(joueur!=NULL)
    {
      char *message;
      message=(char *)malloc(sizeof(char *)*8);
      strcat(message,"NEW_ROUND/");
      strcat(message,"dessinateur");
      strcat(message,"/");
      strcat(message,dessinateur->nom);
      strcat(message,"/");
      printf("%s:message \n",message);
      if(strcmp(joueur->type,"dessinateur")==0)
	{
	  strcat(message,aDessiner);
	  strcat(message,"/");
	}
      strcat(message,"\n");
      write(joueur->socket_id,message,strlen(message));
      joueur=joueur->suivant;
    }
  
}
void EnvoyerDessin(FileJoueur  *fileJoueurs,char *ligne,char *taille,char *coul)
{/* pour le moment pas de traitement exceptionnel */
  if(fileJoueurs==NULL)
    {
      perror("file ou dessin Vide \n");
      exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueurs->premier;
  while(joueur!=NULL)
    {
      char *message;
      printf("dans envoi de dessin \n");
      message=(char *)malloc(sizeof(char *));
      strcat(message,"LINE/");
      strcat(message,ligne);
      
      strcat(message,"/");
      strcat(message,coul);
      strcat(message,"/");
      strcat(message,taille);
      strcat(message,"/\n");  
      printf("aprés structuration  %s \n",message);
      if(strcmp(joueur->type,"chercheur")==0)
	{
	  printf("envoi dessin  pour un chercheur \n");
	write(joueur->socket_id,message,strlen(message));
	}

joueur=joueur->suivant;
    }
}
void supprimerJoueur(FileJoueur *fileJoueurs,int socket)
{
  if(fileJoueurs==NULL)
    {
      perror("File ou dessin vide \n");exit(EXIT_FAILURE);
    }
  Joueur *joueur=getJoueur(fileJoueurs,socket);
  if(joueur==fileJoueurs->premier)
    {
      fileJoueurs->premier=joueur->suivant;
    }
  else
    {
      Joueur *precedent;
      for(precedent=fileJoueurs->premier;precedent->suivant!=joueur;precedent=precedent->suivant);
      precedent->suivant=joueur->suivant;
    }
  /*envoyer exited */
  printf("je joueur a supprimer est %s \n",joueur->nom);
  free(joueur);
}
void EnvoyerCourbe(FileJoueur *fileJoueurs,char *ligne,char *taille,char *coul)
{
if(fileJoueurs==NULL)
    {
      perror("file ou dessin Vide \n");
      exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueurs->premier;
  while(joueur!=NULL)
    {
      char msg[350];
      strcat(msg,"COURBE/");
      strcat(msg,ligne);
      printf("%s :ligne \n");
      strcat(msg,"/");
      strcat(msg,coul);
      strcat(msg,"/");
      strcat(msg,taille);
      strcat(msg,"/\n");      
      printf("%s", msg);
      if(strcmp(joueur->type,"chercheur")==0)
	write(joueur->socket_id,msg,strlen(msg));
      joueur=joueur->suivant;
    }
}
void EnvoyerMotDeviner(FileJoueur  *fileJoueurs,char *motDeviner,int socket,int flag)
{/* pour le moment pas de traitement exceptionnel */
  if(fileJoueurs==NULL)
    {
      perror("file ou dessin Vide \n");
      exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueurs->premier;
  while(joueur!=NULL)
    {
      char *message;
      message=(char *)malloc(sizeof(char *));
      strcat(message,"GUESSED/");
      strcat(message,getNomJoueur(mesJoueurs,socket));
	     strcat(message,"/");
	     if(flag==0)
	       {
      strcat(message,motDeviner);
	       }
      strcat(message,"/\n");
      write(joueur->socket_id,message,strlen(message));
      joueur=joueur->suivant;
    }
}
void deconnexionJoueur(FileJoueur  *fileJoueurs,int socket,char *nomJoueur)
{/* pour le moment pas de traitement exceptionnel */
  if(fileJoueurs==NULL)
    {
      perror("file ou dessin Vide \n");
      exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueurs->premier;
  while(joueur!=NULL)
    {
      char *message;
      message=(char *)malloc(sizeof(char *));
      strcat(message,"EXITED/");
      strcat(message,nomJoueur);	     
      strcat(message,"/\n");
      write(joueur->socket_id,message,strlen(message));
      joueur=joueur->suivant;
    }
}

void notifierConnexion(FileJoueur  *fileJoueurs,char *motDeviner,int socket)
{/* pour le moment pas de traitement exceptionnel */
  int i=0;
  if(fileJoueurs==NULL)
    {
      perror("file ou dessin Vide \n");
      exit(EXIT_FAILURE);
    }
  Joueur *joueur=fileJoueurs->premier;
  Joueur *joueurEncoure=fileJoueurs->premier;
  while(joueurEncoure!=NULL) {
    char *msg;
    msg=(char *)malloc(sizeof(char *));
    strcat(msg,"CONNECTED/");
    strcat(msg,joueurEncoure->nom);
    strcat(msg,"/\n");
    write(socket,msg,strlen(msg)); 
    joueurEncoure=joueurEncoure->suivant;
  }
  while(joueur!=NULL)
    {
      char *message;
      message=(char *)malloc(sizeof(char *));
      strcat(message,"CONNECTED/");
      strcat(message,motDeviner);
      strcat(message,"/\n");
      if(joueur->socket_id!=socket)
	{
      write(joueur->socket_id,message,strlen(message));
	}
      joueur=joueur->suivant;
    }
}






pthread_mutex_t mutex=PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t notifierJoueurs=PTHREAD_COND_INITIALIZER;
pthread_cond_t demarrerPartie=PTHREAD_COND_INITIALIZER;
int nbConnecter=0;

char** fStrSplit(char *str, const char *delimiters)
{
  char * token; 
  char **tokenArray;
  int count=0;
  token = (char *)strtok(str, delimiters); // Get the first token
  tokenArray = (char**)malloc(1 * sizeof(char*)*20);
  tokenArray[0] = NULL;
  if (!token) {       
    return tokenArray;  
  } 
  while (token != NULL) { // While valid tokens are returned
    tokenArray[count] = (char*)strdup(token);
    //printf ("%s", tokenArray[count]);
    count++;
    token = (char *)strtok(NULL, delimiters); // Get the next token
  }
 tokenArray = (char **)realloc(tokenArray, sizeof(char *)*14);
  tokenArray[count] = NULL;  /* Terminate the array */
  return tokenArray;
}

int inscription(char *login,char *password)
{
  char *message;
  message=(char *)malloc(sizeof(char *));
 while(fgets(buffer,TAILLE_BUFFER,maBase)!=NULL)
    {
      int n=strlen(buffer);
      buffer[n-1]='\0';
      char** decouper=(char **)fStrSplit(buffer,":");
      if(strcmp(decouper[0],login)==0 && strcmp(decouper[1],password)==0)
	{
	  return 1;
	}
    }
 strcat(message,login);
 strcat(message,":");
 strcat(message,password);
 fprintf(maBase,"%s\n",message);
 fclose(maBase);
 /* if(fputs(message,maBase)==EOF)
   {
     perror("impossible d'ecrire ");
     return 1;
     }*/
 return 0;

}
int authentification(char *login,char *password)
{
 char *message;
  message=(char *)malloc(sizeof(char *));
 while(fgets(buffer,TAILLE_BUFFER,maBase)!=NULL)
    {
      int n=strlen(buffer);
      buffer[n-1]='\0';
      char** decouper=(char **)fStrSplit(buffer,":");
      if(strcmp(decouper[0],login)==0 && strcmp(decouper[1],password)==0)
	{
	  return 1;
	}
    }

 return 0;
}





Joueur *dessinateur;

void* handlerConnexion(void *args)
{
  int sock=*(int *)args;
  int read_size;
  char *reponse;
  char *message,client_message[2000];
  char **decouper;
  Dessin *dessin=malloc(sizeof(*dessin));
  dessin->couleur="0/0/0";
  dessin->size="1";
  char *size="1";
  char *couleur="0/0/0";

  int i=0;
  if((read_size=recv(sock,client_message,2000,0))>0)
    {
      decouper=(char **)fStrSplit(client_message,"/");
      if(strcmp(decouper[0],"CONNECT")==0)
	{
	  pthread_mutex_lock(&mutex);
	  nbConnecter++;
	  pthread_mutex_unlock(&mutex);
	  creerJoueur(decouper[1],sock,mesJoueurs);
	  reponse=(char *)malloc(sizeof(char *));
	  strcat(reponse,"WELCOME/");
	  strcat(reponse,decouper[1]);
	  strcat(reponse,"/\n");
	  printf("la reponse est %s \n",reponse);
	  write(sock,reponse,strlen(reponse));
	  notifierConnexion(mesJoueurs,decouper[1],sock);
	}
      if(strcmp(decouper[0],"SPECTATOR")==0)
	{
	  printf("il y'a un spectateur qui veut se connecter \n");
	  mesSpectateurs[nbSpectateur]=sock;
	  pthread_mutex_lock(&mutex);
	  nbSpectateur++;
	  pthread_mutex_unlock(&mutex);
	  
	}
      if(strcmp(decouper[0],"REGISTER")==0)
	{
	  int insc=inscription(decouper[1],decouper[2]);
	  if(insc==0)
	    {
	      char *reponse=(char *)malloc(sizeof(char *));
	  strcat(reponse,"WELCOME/");
	  strcat(reponse,decouper[1]);
	  strcat(reponse,"/\n");
	  close(sock);
	    }
	  else
	    {
	      char *access="ACCESSDENIED/\n";
	      write(sock,access,strlen(access));
	      close(sock);
	    }
	}
      if(strcmp(decouper[0],"LOGIN")==0)
	{
	  int auth=authentification(decouper[1],decouper[2]);
	  if(auth==0)
	    {
	      printf("authentification reussi \n");
	      pthread_mutex_lock(&mutex);
	      nbConnecter++;
	      pthread_mutex_unlock(&mutex);
	      creerJoueur(decouper[1],sock,mesJoueurs);
	      reponse=(char *)malloc(sizeof(char *));
	      strcat(reponse,"WELCOME/");
	      strcat(reponse,decouper[1]);
	      strcat(reponse,"/\n");
	      printf("la reponse est %s \n",reponse);
	      write(sock,reponse,strlen(reponse));
	      notifierConnexion(mesJoueurs,decouper[1],sock);
	    }
	  else
	    {
	      char *access="ACCESSDENIED/\n";
	      write(sock,access,strlen(access));
	      close(sock);
	      printf("echec authentification \n");
	    }

	}

      
    }
  while(nbConnecter!=MaxJoueur)
    {
    }
  pthread_cond_signal(&notifierJoueurs);
  while(tour!=0)
    {
      
      while((read_size=recv(sock,client_message,2000,0))>0)
	{printf("une commande est venu qd meme \n");
	  char** decouper=malloc(sizeof(char *));
	  decouper=(char **)fStrSplit(client_message,"/");
	  printf("command recu :%s \n",client_message);
	  if(strcmp(decouper[0],"SET_COLOR")==0)
	    {
	      printf("il veut definir la couleur \n");
	      char *maCouleur;
	      maCouleur=(char*)malloc(sizeof(char *));
	      strcat((char *)maCouleur,decouper[1]);
	      strcat(maCouleur,"/");
	      strcat(maCouleur,decouper[2]);
	      strcat(maCouleur,"/");
	      strcat(maCouleur,decouper[3]);
	      dessin->couleur=maCouleur;
	    }
	  if(strcmp(decouper[0],"EXIT")==0)
	    {
	      deconnexionJoueur(mesJoueurs,sock,decouper[1]);
	      supprimerJoueur(mesJoueurs,sock);
	      if(dessinateur->socket_id==sock)
		{close(sock);
		  finRound(mesJoueurs,buffer,"NO_ONE");
		  setDessinateur(mesJoueurs);
		  if(fgets(buffer,TAILLE_BUFFER,monFichier)!=NULL)
		    {
		      int n=strlen(buffer);
		      buffer[n-1]='\0';
		      aDessiner=buffer;
		      printf(" le mot a dessiner est %s lol ",aDessiner);
		      EnvoyerRole(mesJoueurs,buffer);
		      flagTimeout=0;
		      break;
		    }
		}
	      close(sock);
	    }
	 
	  if(strcmp(decouper[0],"GUESS")==0)
	    {
	      printf("veut deviner \n");
	      if(strcmp(decouper[1],aDessiner)==0)
		{
		  /*EnvoyerMotDeviner(mesJoueurs,decouper[1],sock,1);*/
		  printf("bien deviner mon pote \n");
		  DeclencherMotTrouver(mesJoueurs,sock);
		  if(flagScore==0)
		    {
		      setScore(mesJoueurs,sock,10);
		      setScore(mesJoueurs,dessinateur->socket_id,10);
		      flagScore++;
		    }
		  else
		    {
		      setScore(mesJoueurs,sock,10-flagScore);
		      setScore(mesJoueurs,dessinateur->socket_id,1);
		      flagScore++;
		    }
		    
		  if(flagTimeout==0)
		    {
		      declencherTimeOut(mesJoueurs);
		      flagTimeout=1; 
		      sleep(timeout);
		      if(flagPass==0)
			{
			  nomGagnant=getNomJoueur(mesJoueurs,sock);
			  EnvoyerScore(mesJoueurs);
			  finRound(mesJoueurs,buffer,nomGagnant);
			  
			  setDessinateur(mesJoueurs);
			  if(fgets(buffer,TAILLE_BUFFER,monFichier)!=NULL)
			    {
			      int n=strlen(buffer);
			      buffer[n-1]='\0';
			      aDessiner=buffer;
			      printf(" le mot a dessiner est %s lol ",aDessiner);
			      EnvoyerRole(mesJoueurs,buffer);
			      flagTimeout=0;
			      break;
			    }
			}
		      else
			{
			  finRound(mesJoueurs,buffer,"NO_ONE");
			  
			  setDessinateur(mesJoueurs);
			  if(fgets(buffer,TAILLE_BUFFER,monFichier)!=NULL)
			    {
			      int n=strlen(buffer);
			      buffer[n-1]='\0';
			      aDessiner=buffer;
			      printf(" le mot a dessiner est %s lol ",aDessiner);
			      EnvoyerRole(mesJoueurs,buffer);
			      flagTimeout=0;
			      break;
			    }
			}
		    }
		 
		    
		}
	      else
		{
		  EnvoyerMotDeviner(mesJoueurs,decouper[1],sock,0);
		  printf("incorrect le mot deviner \n");
		}
	
	
	    }
	   
	  if(strcmp(decouper[0],"TALK")==0)
	    {
	      Talk(mesJoueurs,sock,decouper[1]);
	    }

	  if(strcmp(decouper[0],"PASS")==0)
	    {
	      if(flagMotTrouver==0)
		{
		  /*personne n'a trouver alors on abondonne tranquillement */
		  finRound(mesJoueurs,buffer,"NO_ONE");
			  setDessinateur(mesJoueurs);
			  if(fgets(buffer,TAILLE_BUFFER,monFichier)!=NULL)
			    {
			      int n=strlen(buffer);
			      buffer[n-1]='\0';
			      aDessiner=buffer;
			      printf(" le mot a dessiner est %s lol ",aDessiner);
			      EnvoyerRole(mesJoueurs,buffer);
			      flagTimeout=0;
			      break;
			    }
		}
	      else 
		{
		  /*quelqu'un a deja trouver on attend la fin du timeout */
		  flagPass=1;
		}
	    }

	  if(strcmp(decouper[0],"CHEAT")==0)
	    {
	      pthread_mutex_lock(&mutex);
	      nbCheat++;
	      pthread_mutex_unlock(&mutex);
	      if(nbCheat==3)
		{
		  finRound(mesJoueurs,buffer,"NO_ONE");
		  setDessinateur(mesJoueurs);
		  if(fgets(buffer,TAILLE_BUFFER,monFichier)!=NULL)
		    {
		      int n=strlen(buffer);
		      buffer[n-1]='\0';
		      aDessiner=buffer;
		      printf(" le mot a dessiner est %s lol ",aDessiner);
		      EnvoyerRole(mesJoueurs,buffer);
		      break;
		    }
		}
	    }


	  if(strcmp(decouper[0],"SET_LINE")==0)
	    {
	      printf("il veut faire un setLine \n");
	      char *maLigne;
	      maLigne=(char *)malloc(sizeof(char *));
	      strcat(maLigne,decouper[1]);
	      strcat(maLigne,"/");
	      strcat(maLigne,decouper[2]);
	      strcat(maLigne,"/");
	      strcat(maLigne,decouper[3]);
	      strcat(maLigne,"/");
	      strcat(maLigne,decouper[4]);
	      /*dessin->ligne=maLigne;*/
	      EnvoyerDessin(mesJoueurs,maLigne,size,couleur);
	    }

	  if(strcmp(decouper[0],"SET_COURBE")==0)
	    {
  printf("il veut faire une courbe \n");
	      char *maLigne;
	      maLigne=(char *)malloc(sizeof(char *));
	      strcat(maLigne,decouper[1]);
	      strcat(maLigne,"/");
	      strcat(maLigne,decouper[2]);
	      strcat(maLigne,"/");
	      strcat(maLigne,decouper[3]);
	      strcat(maLigne,"/");
	      strcat(maLigne,decouper[4]);
	      strcat(maLigne,"/");
	      strcat(maLigne,decouper[5]);
	      strcat(maLigne,"/");
	      strcat(maLigne,decouper[6]);
	      strcat(maLigne,"/");
	      strcat(maLigne,decouper[7]);
	      strcat(maLigne,"/");
	      strcat(maLigne,decouper[8]);
	      EnvoyerCourbe(mesJoueurs,maLigne,size,couleur);
	      /* dessin->ligne=maLigne;
	      flagCourbe=1;
	      printf(" la courbe est  %s \n",dessin->ligne);*/
	      
	    }
	  if(strcmp(decouper[0],"SET_SIZE")==0)
	    {
	      printf("il veut definir la taille \n");
	      dessin->size=decouper[1];
		
	    }
	  if(strcmp(decouper[0],"SET_TEST")==0)
	    {
	      printf("on fait le test \n");
	    }
	  /* if(dessin->ligne!=NULL)
	    {
	      printf("le dessin est pres on l'envoi maintenant \n");
	      if(flagCourbe==1)
		{
		  EnvoyerCourbe(mesJoueurs,dessin);
		  flagCourbe=0;
		}
	      else
		{
	      EnvoyerDessin(mesJoueurs,dessin);
		}	     
	      dessin->ligne="0/0/0/0";
	
	      
	      }*/
	} 
      pthread_mutex_lock(&mutex);
      tour--;
      pthread_mutex_unlock(&mutex);
      printf("round numero %d \n",tour);

    }
}

int main(int argc, char ** argv )
{ mesJoueurs=initialiser();
  int port=2013;
  int i=0;
  int sock_server,sock_client,c,*nouveauSocket,*spectateur;
  int on=1;
  struct sockaddr_in server,client;
  /*recuperer les arguments qui existent pour initialiser le timeout port et max*/
  for(i=0;i<argc;i++)
    {
      if(strcmp(argv[i],"-timeout")==0)
	{
	  printf(" %s detecté \n",argv[i]);
	  timeout=atoi(argv[i+1]);    
	}

      if(strcmp(argv[i],"-max")==0)
	{
	  printf(" %s detecté \n",argv[i]);
	  MaxJoueur=atoi(argv[i+1]);    
	}

      if(strcmp(argv[i],"-port")==0)
	{
	  printf(" %s detecté \n",argv[i]);
	  port=atoi(argv[i+1]);    
	}
      if(strcmp(argv[i],"-dico")==0)
	{
	  printf("%s detecté \n",argv[i]);
	  fichier=argv[i+1];
	    
	  if((monFichier=fopen(fichier,"r"))==NULL)
	    {
	      perror("<<fichier>>");exit(EXIT_FAILURE);
	    }
	}

    }
  if(monFichier==NULL)
    {
      printf("vous devez specifier le dictionnaire de mot -dico \n");
      exit(EXIT_FAILURE);
    }
  if((maBase=fopen("database.txt","a+"))==NULL)
    {
      perror("erreur creation de la database");
    }
  pthread_t monThread[MaxJoueur];
  Joueur joueurs[MaxJoueur];
  printf("timeout vaut %d MaxJoueur vaut %d et le port vaut %d \n",timeout,MaxJoueur,port);

  /*creaction de la socket de connection et attendre nbMax de connection */

  if((sock_server=socket(AF_INET,SOCK_STREAM,0))<0)
    {
      perror("erreur <<socket>>");
      exit(EXIT_FAILURE);
    }
  /*parametrage du port du serveur et de l'IP */
  printf("Socket creer avec succée \n");
  server.sin_family=AF_INET;
  server.sin_addr.s_addr=INADDR_ANY;
  server.sin_port=htons(port);


  /*parametre pour pouvoir reutiliser le port */
  setsockopt(sock_server,SOL_SOCKET,SO_REUSEADDR,(void*)&on,sizeof(on));

  /*on bind la socket */

  if(bind(sock_server,(struct sockaddr *)&server,sizeof(server))<0)
    {
      perror("bind <<socket_server>>");
      exit(EXIT_FAILURE);
    }
  printf("bind reussi \n");
  listen(sock_server,MaxJoueur);

  /*accepter la connexion des max des joueurs */
  c=sizeof(struct sockaddr_in);
  nouveauSocket=malloc(MaxJoueur);
  for(i=0;i<MaxJoueur;i++)
    {
      nouveauSocket[i]=accept(sock_server,(struct sockaddr *)&client,(socklen_t *)&c);
      if(pthread_create(&monThread[i],NULL,handlerConnexion,(void *)&nouveauSocket[i])<0)
	{
	  perror("pthread_create <<monThread>>");
	  exit(EXIT_FAILURE);
	}
    }
  pthread_cond_wait(&notifierJoueurs,&mutex);
  setDessinateur(mesJoueurs); 
  /* dessinateur=getDessinateur(mesJoueurs); */
  if(fgets(buffer,TAILLE_BUFFER,monFichier)!=NULL)
    {
      int n=strlen(buffer);
      buffer[n-1]='\0';
      aDessiner=buffer;
      printf(" le mot a dessiner est %s lol ",aDessiner);
      sleep(1);
      EnvoyerRole(mesJoueurs,buffer);
    }
  spectateur=malloc(12);
  while((spectateur[i]=accept(sock_server,(struct sockaddr *)&client,(socklen_t *)&c)))
    {
pthread_t spectateur_thread;
 if(pthread_create(&spectateur_thread,NULL,handlerConnexion,(void *)&spectateur[i])<0)
   {
     perror("erreur dans thread spectateur");exit(EXIT_FAILURE);
   }
 i++;
    }

 
  for(i=0;i<MaxJoueur;i++)
    {
      pthread_join(monThread[i],NULL);
    }
  return EXIT_SUCCESS;
}
