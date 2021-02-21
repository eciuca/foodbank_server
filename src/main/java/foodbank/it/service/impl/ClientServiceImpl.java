package foodbank.it.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import foodbank.it.persistence.model.Client;
import foodbank.it.persistence.repository.IClientRepository;
import foodbank.it.service.IClientService;
@Service
public class ClientServiceImpl implements IClientService{

	private IClientRepository ClientRepository;
	
	public ClientServiceImpl(IClientRepository ClientRepository) {
        this.ClientRepository = ClientRepository;
    }
	@Override
    public Optional<Client> findByIdClient(int idClient) {
        return ClientRepository.findByIdClient(idClient);
    }

    @Override
    public Client save(Client Client) {        
        return ClientRepository.save(Client);
    }

    

    @Override
    @Transactional
    public void delete(int idClient) {
        ClientRepository.deleteByIdClient(idClient);
        
    }

	@Override
	public Page<Client> findAll(Pageable pageable) {
		return ClientRepository.findAll(pageable);
	}
	@Override
	public Page<Client> findByBanqueObjectBankShortName(String bankShortName, Pageable pageable) {
		// TODO Auto-generated method stub
		return ClientRepository.findByBanqueObjectBankShortName(bankShortName, pageable);
	}
	@Override
	public Page<Client> findByLienDis(Integer lienDis, Pageable pageable) {
		return ClientRepository.findByLienDis(lienDis, pageable);
	}
	@Override
	public Page<Client> findByNomContaining(String search, Pageable pageRequest) {
		return  ClientRepository.findByNomContaining(search, pageRequest);
	}
	@Override
	public Page<Client> findByLienDisAndNomContaining(Integer lienDis, String search, Pageable pageRequest) {
		return  ClientRepository.findByLienDisAndNomContaining(lienDis, search, pageRequest);
	}
	@Override
	public Page<Client> findByBanqueObjectBankShortNameAndNomContaining(String bankShortName, String search,
			Pageable pageRequest) {
		return  ClientRepository.findByBanqueObjectBankShortNameAndNomContaining(bankShortName, search, pageRequest);
	}
	@Override
	public Page<Client> findByPrenomContaining(String search, Pageable pageRequest) {
		return  ClientRepository.findByPrenomContaining(search, pageRequest);
	}
	@Override
	public Page<Client> findByLienDisAndPrenomContaining(Integer lienDis, String search, Pageable pageRequest) {
		return  ClientRepository.findByLienDisAndPrenomContaining(lienDis, search, pageRequest);
	}
	@Override
	public Page<Client> findByBanqueObjectBankShortNameAndPrenomContaining(String bankShortName, String search,
			Pageable pageRequest) {
		return  ClientRepository.findByBanqueObjectBankShortNameAndPrenomContaining(bankShortName, search, pageRequest);
	}
	@Override
	public Page<Client> findByAdresseContaining(String search, Pageable pageRequest) {
		return  ClientRepository.findByAdresseContaining(search, pageRequest);
	}
	@Override
	public Page<Client> findByLienDisAndAdresseContaining(Integer lienDis, String search, Pageable pageRequest) {
		return  ClientRepository.findByLienDisAndAdresseContaining(lienDis, search, pageRequest);
	}
	@Override
	public Page<Client> findByBanqueObjectBankShortNameAndAdresseContaining(String bankShortName, String search,
			Pageable pageRequest) {
		return  ClientRepository.findByBanqueObjectBankShortNameAndAdresseContaining(bankShortName, search, pageRequest);
	}
	@Override
	public Page<Client> findByCpStartsWith(String search, Pageable pageRequest) {
		return  ClientRepository.findByCpStartsWith(search, pageRequest);
	}
	@Override
	public Page<Client> findByLienDisAndCpStartsWith(Integer lienDis, String search, Pageable pageRequest) {
		return  ClientRepository.findByLienDisAndCpStartsWith(lienDis, search, pageRequest);
	}
	@Override
	public Page<Client> findByBanqueObjectBankShortNameAndCpStartsWith(String bankShortName, String search,
			Pageable pageRequest) {
		return  ClientRepository.findByBanqueObjectBankShortNameAndCpStartsWith(bankShortName, search, pageRequest);
	}
	@Override
	public Page<Client> findByLocaliteContaining(String search, Pageable pageRequest) {
		return  ClientRepository.findByLocaliteContaining(search, pageRequest);
	}
	@Override
	public Page<Client> findByLienDisAndLocaliteContaining(Integer lienDis, String search, Pageable pageRequest) {
	return  ClientRepository.findByLienDisAndLocaliteContaining(lienDis, search, pageRequest);
	}
	@Override
	public Page<Client> findByBanqueObjectBankShortNameAndLocaliteContaining(String bankShortName, String search,
			Pageable pageRequest) {
		return  ClientRepository.findByBanqueObjectBankShortNameAndLocaliteContaining(bankShortName, search, pageRequest);
	}
	
	
}
