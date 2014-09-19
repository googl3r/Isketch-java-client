###################################Makefile-Serveur iSketch-#################################
###Dossiers
SRC=src
BIN=bin
INCLUDE=include

#Parametre du compilateur 
CC=gcc
RMBIN=@rm -f $(serveur)
CFLAGS=-Wall -ansi -lpthread

#Fichier a generer par le makefile

serveur=$(BIN)/serveurisketch

all: run $(serveur) 

run:$(serveur)
	./$< -dico $(INCLUDE)/dico

$(serveur):$(SRC)/mainPrincipale.c
	$(CC) -o $@ $< $(CFLAGS)

clean:
	$(RMBIN)