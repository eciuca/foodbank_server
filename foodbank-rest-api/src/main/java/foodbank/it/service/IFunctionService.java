package foodbank.it.service;

import foodbank.it.persistence.model.Function;

import java.util.Optional;

public interface IFunctionService {
    Optional<Function> findByFuncId(int funcId);
    Function save(Function function, boolean booCreateMode);

    Iterable<Function> findAll(Integer lienBanque,Boolean actif);

    void delete(int funcId) throws Exception;
}
