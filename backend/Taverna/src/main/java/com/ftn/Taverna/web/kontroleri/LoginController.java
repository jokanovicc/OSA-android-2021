package com.ftn.Taverna.web.kontroleri;
import com.ftn.Taverna.web.kontroleri.DTO.KorisnikDTO;
import com.ftn.Taverna.web.kontroleri.DTO.Login;
import com.ftn.Taverna.model.Korisnik;
import com.ftn.Taverna.security.TokenUtils;
import com.ftn.Taverna.servisi.KorisnikServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("users")
@CrossOrigin("*")
public class LoginController {

    @Autowired
    KorisnikServis korisnikServis;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    TokenUtils tokenUtils;


    @PostMapping("/login")
    public ResponseEntity<KorisnikDTO> login(@RequestBody Login userDto) {

        Korisnik korisnik = korisnikServis.findByUsername(userDto.getKorisnicko());
        if (korisnik!=null && korisnik.isBlokiran()) {
            return ResponseEntity.notFound().build();
        }


        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDto.getKorisnicko(), userDto.getSifra());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("sam ovde_ ");
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getKorisnicko());
            Korisnik korisnik1 = korisnikServis.findByUsername(userDto.getKorisnicko());
            KorisnikDTO korisnikDTO = new KorisnikDTO(korisnik1);
            korisnikDTO.setToken(tokenUtils.generateToken(userDetails));

            return ResponseEntity.ok(korisnikDTO);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
