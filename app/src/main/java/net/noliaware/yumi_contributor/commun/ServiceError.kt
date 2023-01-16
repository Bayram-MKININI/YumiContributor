package net.noliaware.yumi_contributor.commun

enum class ServiceError(errorCode: Int) {

    ErrDEFAULT(-99),
    ErrSYSTEM(-1),
    ErrNETWORK(-11),
    Err10(10),
    Err11(11),
    Err20(20),
    Err21(21),
    Err22(22),
    Err30(30),
    Err31(31),
    Err40(40),
    Err41(41),
    Err50(50);

    companion object {

        fun fromString(name: String): ServiceError =
            when (name) {
                "E-10" -> Err10
                "E-11" -> Err11
                "E-20" -> Err20
                "E-21" -> Err21
                "E-22" -> Err22
                "E-30" -> Err30
                "E-31" -> Err31
                "E-40" -> Err40
                "E-41" -> Err41
                "E-50" -> Err50
                else -> ErrDEFAULT
            }
    }
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