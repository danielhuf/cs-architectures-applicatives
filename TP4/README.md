# TP Kafka

Membres du trinôme:
- GONZALEZ Tomas
- STULBERG HUF Daniel
- OBEID Hanna

Lien du livre utilisé : https://ws-export.wmcloud.org/?lang=fr&title=Le_Livre_de_jade_(1867)
Le fichier doit être nommé `fichier.txt` et placé dans le dossier racine.

## Bonus implementés

Tous les bonus ont été réalisés : 
- **Créer topics avec l'API d'administration de Kafka** : les fichiers `CreateTopics.sh` et `DeletTopics.sh` sont chargés de la création et élimination des topics Kafka avec son API d'administration.
- **Rendre le dernier composant générique par rapport à la catégorie** : le composant CategoryCount a été rendu générique, et son exécution a été modifié pour une utilisation plus simple :

  `$ java -cp target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.CategoryCount {CATEGORY}`

- **Ajout des commandes au topic `command-topic`** : Les commandes `DISPLAY 10` et `RESET` ont été ajoutées (méthode `processCommand`, ligne 32 du fichier du composant CategoryCount)



## Résultats:

- `END`

      Top 20 VER:
      être: 154
      avoir: 148
      voir: 27
      regarder: 23
      tasser: 20
      aller: 20
      luire: 18
      tomber: 18
      venir: 14
      faire: 13
      aimer: 13
      pouvoir: 12
      boire: 12
      laisser: 11
      partir: 11
      maintenir: 11
      entendre: 10
      ombrer: 10
      passer: 10
      croire: 9
      ----------
      Exiting...
- `DISPLAY 10`
  
      Top 10 VER:
      être: 154
      avoir: 148
      voir: 27
      regarder: 23
      tasser: 20
      aller: 20
      luire: 18
      tomber: 18
      venir: 14
      faire: 13
      ----------

## Annexe

Pour faciliter l'exécution, quelques script ont été créés. Ils se trouvent dans le dossier `scripts`. Malheureusement, il y a quelques bugs que nous n'avons pas pu corriger.

Si vous voulez tester ces fichiers, l'ordre d'execution est le suivant :
1. `setup-and-run.sh` : fichier chargé de créer les topics Kafka, `mvn package` 
2. `interactive-producer.sh` : Ouvre un producer interactif dans le topic `command-topic`.
3. `clean.sh` : chargé de nettoyer les fichiers créés par mvn, terminer les processus kafka et les applications java.
