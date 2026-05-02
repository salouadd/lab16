# Chronomètre Service - Android (Java)

Cette application est un laboratoire pratique sur l'utilisation des **Services Android** en Java. Elle démontre l'implémentation d'un chronomètre persistant capable de fonctionner en arrière-plan.

##  Objectifs du projet
- Implémenter un **Foreground Service** (obligatoire depuis Android 8.0).
- Utiliser une **Notification persistante** pour afficher le temps en temps réel.
- Utiliser un **Bound Service** pour permettre à l'Activity de communiquer avec le Service.
- Gérer les restrictions modernes d'Android (API 34+ / Android 14).

##  Fonctionnalités
- **Démarrage du service** : Lance le chronomètre et affiche une notification.
- **Mise à jour en direct** : L'interface de l'application se met à jour chaque seconde via une connexion (binding) au service.
- **Persistance** : Le chronomètre continue de tourner même si l'application est fermée ou mise en arrière-plan.
- **Arrêt propre** : Un bouton permet d'arrêter le service, de supprimer la notification et de réinitialiser l'interface.

##  Prérequis techniques
- **Minimum SDK** : API 26 (Android 8.0) recommandé.
- **Permissions** : 
    - `POST_NOTIFICATIONS` (Android 13+).
    - `FOREGROUND_SERVICE`.
    - `FOREGROUND_SERVICE_DATA_SYNC` (Android 14+).

## Structure du Code
- `ChronometreService.java` : Gère la logique du temps, l'exécuteur de tâches (`ScheduledExecutorService`) et la gestion des notifications.
- `MainActivity.java` : Gère l'interface utilisateur, la demande de permissions au runtime et la connexion (`ServiceConnection`) au service.
- `AndroidManifest.xml` : Déclaration du service avec le type `dataSync` et les permissions nécessaires.

## Comment tester ?
1. Lancez l'application sur un émulateur ou un appareil réel.
2. Autorisez les notifications si demandé (Android 13+).
3. Cliquez sur **DÉMARRER SERVICE**.
4. Observez le chronomètre sur l'écran et dans le tiroir de notifications.
5. Quittez l'application : le temps continue de défiler dans la notification.
6. Revenez dans l'app et cliquez sur **ARRÊTER SERVICE** pour tout stopper.


