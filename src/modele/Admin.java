package modele;

public class Admin extends Utilisateur {
    public Admin() {
        super();
        setRole("admin");
    }
    public Admin(int id, String login, String motDePasse, String email) {
        super(id, login, motDePasse, email, "admin", true);
    }


    
}
