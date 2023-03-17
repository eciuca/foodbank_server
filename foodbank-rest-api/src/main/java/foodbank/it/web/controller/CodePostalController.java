package foodbank.it.web.controller;

import foodbank.it.persistence.model.CodePostal;
import foodbank.it.service.ICodePostalService;
import foodbank.it.web.dto.CodePostalDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
public class CodePostalController {
    private ICodePostalService CodePostalService;

    public CodePostalController(ICodePostalService CodePostalService) {
        this.CodePostalService = CodePostalService;
    }
    @GetMapping("zipcode/{zipCode}")
    public CodePostal findOne(@PathVariable int zipCode) {
        return CodePostalService.findByZipCode(zipCode) .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    @GetMapping("zipcodes/")
    public Collection<CodePostalDto> find(@RequestParam String offset, @RequestParam String rows,
                                    @RequestParam String sortField, @RequestParam String sortOrder,
                                    @RequestParam(required = false) String searchField, @RequestParam(required = false) String searchValue) {
        int intOffset = Integer.parseInt(offset);
        int intRows = Integer.parseInt(rows);
        int pageNumber=intOffset/intRows; // Java throws away remainder of division
        int pageSize = intRows;
        Pageable pageRequest = null;
        if (sortOrder.equals("1")) {
            pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortField).ascending());
        }
        else {
            pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortField).descending());
        }
        Page<CodePostal> selectedCps = null;
        List<CodePostalDto> codePostalDtos = new ArrayList<>();
        selectedCps = CodePostalService.findAll(pageRequest);
        long totalRecords = selectedCps.getTotalElements();
        selectedCps.forEach(p -> codePostalDtos.add(convertToDto(p, totalRecords)));
        return codePostalDtos;
    }
    protected CodePostalDto convertToDto(CodePostal entity,long totalRecords) {
        CodePostalDto dto = new CodePostalDto();
        dto.setZipCode(entity.getZipCode());
        dto.setCity(entity.getCity());
        dto.setlCpas(entity.getLCpas());
        dto.setCityCpas(entity.getCityCpas());
        dto.setZipCodeCpas(entity.getZipCodeCpas());
        dto.setMailCpas(entity.getMailCpas());
        dto.setRemCpas(entity.getRemCpas());
        dto.setTotalRecords(totalRecords);
        return dto;
    }
    protected CodePostal convertToEntity(CodePostalDto dto) {
        CodePostal entity = new CodePostal();
        entity.setZipCode(dto.getZipCode());
        entity.setCity(dto.getCity());
        entity.setLCpas(dto.getlCpas());
        return entity;
    }

        @PutMapping("zipcode/{zipcode}")
    public CodePostal updateCodePostal(@PathVariable("zipcode") Integer zipcode, @RequestBody CodePostal updatedCodePostal) {
        return CodePostalService.save(updatedCodePostal);
    }

    @DeleteMapping("zipcode/{zipcode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCodePostal(@PathVariable("zipcode") Integer zipcode) {
        CodePostalService.delete(zipcode);
    }

    @PostMapping("zipcode/")
    @ResponseStatus(HttpStatus.CREATED)
    public CodePostal create(@RequestBody CodePostal newCodePostal) {
        CodePostal CodePostal = this.CodePostalService.save(newCodePostal);
        return CodePostal;
    }

}
