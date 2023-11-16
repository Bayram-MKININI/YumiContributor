package net.noliaware.yumi_contributor.commun.util

sealed interface ServiceError{
    data object ErrNone : ServiceError
    data class ErrSystem(val errorMessage: String? = null) : ServiceError
    data object ErrNetwork : ServiceError
}

/*
E-10 : tu appelles une méthode d'API inexistante (ex. init_v2) => suspect
E-11 : tu appelles une méthode d'API mais avec des paramètres manquants (ex. pas de login) => suspect
E-20 : le login est inexistant (utilisateur supprimé puisque c préenregistré)
E-21 : mot de passe ne correspond pas au shuffle => suspect
E-22 : mot de passe pas bon
E-30 : plus de session => reinit
E-31 : token envoyé différent du token attendu => reinit
E-40 : token envoyé inexistant (ex. token fabriqué de toute pièce) => suspect
E-41 : token incorrect après vérification (ex. token périmé) => reinit
E-50 : le compte est verrouillé
 */