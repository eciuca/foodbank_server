package foodbank.it.web.controller;
import foodbank.it.persistence.model.*;
import foodbank.it.service.IMovementService;
import foodbank.it.service.SearchMovementCriteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@RestController
public class MovementController {
    private IMovementService movementService;
    public MovementController(IMovementService movementService) {
        this.movementService = movementService;
    }
    @GetMapping("movementsdaily/")
    public Iterable<MovementDaily> findAllDaily(
            @RequestParam(required = false) String idCompany,
            @RequestParam(required = false) String idDepot,
            @RequestParam(required = false) String lowRange,
            @RequestParam(required = false) String highRange,
            @RequestParam(required = false) String lastDays) {
        SearchMovementCriteria searchCriteria = new SearchMovementCriteria();
        searchCriteria.setIdCompany(idCompany);
        Integer idDepotInteger = Optional.ofNullable(idDepot).filter(str -> !str.isEmpty()).map(Integer::parseInt).orElse(null);
        searchCriteria.setIdDepot(idDepotInteger);
        searchCriteria.setLowRange(lowRange);
        searchCriteria.setHighRange(highRange);
        Integer lastDaysInteger = Optional.ofNullable(lastDays).filter(str -> !str.isEmpty()).map(Integer::parseInt).orElse(null);
        searchCriteria.setLastDays(lastDaysInteger);
        return movementService.findDailyMovements(searchCriteria);
    }
    @GetMapping("movementsmonthly/")
    public Iterable<MovementMonthly> findAllMonthly(
            @RequestParam(required = false) String idCompany,
            @RequestParam(required = false) String idDepot,
            @RequestParam(required = false) String lowRange,
            @RequestParam(required = false) String highRange
           ) {
        SearchMovementCriteria searchCriteria = new SearchMovementCriteria();
        searchCriteria.setIdCompany(idCompany);
        Integer idDepotInteger = Optional.ofNullable(idDepot).filter(str -> !str.isEmpty()).map(Integer::parseInt).orElse(null);
        searchCriteria.setIdDepot(idDepotInteger);
        searchCriteria.setLowRange(lowRange);
        searchCriteria.setHighRange(highRange);
        return movementService.findMonthlyMovements(searchCriteria);
    }

}
