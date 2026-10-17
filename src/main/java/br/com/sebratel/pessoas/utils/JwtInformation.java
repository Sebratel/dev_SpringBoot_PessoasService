package br.com.sebratel.pessoas.utils;

import br.com.sebratel.pessoas.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Slf4j
public class JwtInformation {
    public static Employee retrieveUserData() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaim("email");
            String nome = jwt.getClaim("name");

            log.info("Processando requisição para: {} ({})", nome, email);
            return new Employee(email, nome);
        }
        log.warn("Nenhum usuário JWT encontrado no contexto de segurança da requisição");
        return new Employee("", "");
    }
}
