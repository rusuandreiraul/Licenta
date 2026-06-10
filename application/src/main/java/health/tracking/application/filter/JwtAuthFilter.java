package health.tracking.application.filter;


import health.tracking.application.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter { //acesta clasa prinde cererea de la vue
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthFilter(UserDetailsService userDetailsService, JwtService jwtService){
        this.jwtService=jwtService;
        this.userDetailsService=userDetailsService;
    }

    @Override //functie suprascrisa care primeste cererea, lantul de filtre
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); //se extrage header-ul "Authorization"
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) { //se verifica daca nu incepe cu "Bearer " si nu exista
            filterChain.doFilter(request, response);//aplica filtrul standard Spring
            return;
        }

        try {
            jwt = authHeader.substring(7); //extrage token de dupa bearer
            username = jwtService.extractUsername(jwt); //extrage username

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { //verifica daca exsita in contextul de securitate
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); //cauta utilizatorul folosind functia din userDetailsService

                if (jwtService.isTokenValid(jwt, userDetails)) { //verificare validitate token
                    //creare obiect de tip UsernamePasswordAuthenticationToken care va fi folosit in Contextul de securitate.
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("DEBUG: Autentificare reușită pentru: " + username);
                }
            }
        } catch (Exception e) {
            // Dacă token-ul e invalid, se va lasa ca Spring sa decida prin filtrele standard daca trebuie autentificare sau nu.
            System.out.println("JWT Error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

}
