package staysplit.hotel_reservation.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import staysplit.hotel_reservation.reservation.service.UsernameAutocompleteService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/autocomplete")
public class AutoCompleteController {
    private final UsernameAutocompleteService usernameAutocompleteService;

    @GetMapping("/username")
    public ResponseEntity<List<String>> autocomplete(@RequestParam String prefix) {
        List<String> matches = usernameAutocompleteService.searchByPrefix(prefix);
        return ResponseEntity.ok(matches);
    }

}
