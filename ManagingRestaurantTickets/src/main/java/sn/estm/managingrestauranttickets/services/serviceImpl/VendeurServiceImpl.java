package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.entities.Vendeur;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.VendeurService;

@Service
public class VendeurServiceImpl implements VendeurService{

    @Override
    public List<Vendeur> getAllVendeurs() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllVendeurs'");
    }

    @Override
    public void createVendeur(Vendeur vendeur) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createVendeur'");
    }

    @Override
    public Vendeur getVendeurById(Long idVendeur) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getVendeurById'");
    }

    @Override
    public void updateVendeur(Long idVendeur, Vendeur vendeur) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateVendeur'");
    }

    @Override
    public void deleteVendeurById(Long idVendeur) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteVendeurById'");
    }
    /*
     * @Autowired
	private EtudiantRepo etudiantRepo;
	
	@Override
	public List<Etudiant> getallEtudiants()
	{
		//Récup de l'ensble des étudiants de la bdd
		return etudiantRepo.findAll();
	}

	@Override
	public void addEtudiant(Etudiant etu) 
	{
	   //ajout d'une instance d'étudiant dans la bdd
	   etudiantRepo.save(etu);	
	}

	@Override
	public Etudiant getEtudiantById(Integer id_etudiant)
	{
	   Optional<Etudiant> optional = etudiantRepo.findById(id_etudiant);
	   Etudiant etu = null;
		if(optional.isPresent())
		{
			etu = optional.get(); 
		}
		else
		{
			throw new RuntimeException("Cet étudiant n'existe pas" +id_etudiant);
		}
		   return etu;
	}

	@Override
	public void deleteEtudiantById(Integer id_etudiant) 
	{
		etudiantRepo.deleteById(id_etudiant);
	}
     */
}
